package ru.geekbrains.chat.server.core;

import ru.geekbrains.chat.library.Messages;
import ru.geekbrains.network.ServerSocketThread;
import ru.geekbrains.network.ServerSocketThreadListener;
import ru.geekbrains.network.SocketThread;
import ru.geekbrains.network.SocketThreadListener;

import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Vector;

/**
 * Created by Administrator on 27.07.2017.
 */
public class ChatServer implements ServerSocketThreadListener, SocketThreadListener {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss: ");
    private final ChatServerListener eventListener;
    private final AuthService authService;
    private ServerSocketThread serverSocketThread;
    private final Vector<SocketThread> clients = new Vector<>();

    public ChatServer(ChatServerListener eventListener, AuthService authService) {
        this.eventListener = eventListener;
        this.authService = authService;
    }

    public void startListening(int port){
        if(serverSocketThread != null && serverSocketThread.isAlive()){
            putLog("Поток сервера уже запущен.");
            return;
        }
        serverSocketThread = new ServerSocketThread("ServerSocketThread",port,this,2000);
        authService.start();
    }

    public void dropAllClients(){
        for (int i = 0; i < clients.size() ; i++) {
            clients.get(i).close();
        }
    }

    public void stopListening(){
        if(serverSocketThread == null || !serverSocketThread.isAlive()){
            putLog("Поток сервера не запущен.");
            return;
        }
        serverSocketThread.interrupt();
        authService.stop();
    }

    private synchronized void putLog(String msg){
        String msgLog = dateFormat.format(System.currentTimeMillis());
        msgLog = msgLog + Thread.currentThread().getState() + ": " + msg;
        eventListener.onLogChatServer(this, msgLog);
    }

    @Override
    public void onStartServerSocketThread(ServerSocketThread thread) {
        putLog("started...");
    }

    @Override
    public void onStopServerSocketThread(ServerSocketThread thread) {
        putLog("stoped.");
    }

    @Override
    public void onReadyServerSocketThread(ServerSocketThread thread, ServerSocket serverSocket) {
        putLog("ServerSocket is ready...");
    }

    @Override
    public void onTimeOutAccept(ServerSocketThread thread, ServerSocket serverSocket) {
        putLog("accept() timeout");
    }

    @Override
    public void onAcceptedSocket(ServerSocketThread thread, ServerSocket serverSocket, Socket socket) {
        putLog("Client connected: " + socket);
        String threadName = "Socket thread: " + socket.getInetAddress() + ":" + socket.getPort();
        new ChatSocketThread(this,threadName,socket);
    }

    @Override
    public void onExceptionServerSocketThread(ServerSocketThread thread, Exception e) {
        putLog("Exception: " + e.getClass().getName() + ": " + e.getMessage());
    }

    @Override
    public synchronized void onStartSocketThread(SocketThread socketThread) {
        putLog("started...");
    }

    @Override
    public synchronized void onStopSocketThread(SocketThread socketThread) {
        putLog("stoped.");
        clients.remove(socketThread);
        ChatSocketThread client = (ChatSocketThread) socketThread;
        if(client.isAuthorized() && !client.isReconnected()){
            sendToAllAuthorizedClients(Messages.getBroadcast("Server",client.getNickname() + " disconnected."));
            sendToAllAuthorizedClients(Messages.getUsersList(getAllNicknamesString()));
        }

    }

    @Override
    public synchronized void onReadySocketThread(SocketThread socketThread, Socket socket) {
        putLog("ServerSocket is ready...");
        clients.add(socketThread);
    }

    @Override
    public synchronized void onReceiveString(SocketThread socketThread, Socket socket, String value) {
        ChatSocketThread client = (ChatSocketThread) socketThread;
        if(client.isAuthorized()){
            handleAuthorizeClient(client,value);
        } else{
            handleNonAuthorizeClient(client,value);
        }
    }

    private void handleAuthorizeClient(ChatSocketThread client, String msg){
        sendToAllAuthorizedClients(Messages.getBroadcast(client.getNickname(),msg));
    }

    private void sendToAllAuthorizedClients(String msg){
        int cnt = clients.size();
        for (int i = 0; i < cnt ; i++) {
            ChatSocketThread client = (ChatSocketThread) clients.get(i);
            if(client.isAuthorized()) client.sendMsg(msg);
        }
    }

    private void handleNonAuthorizeClient(ChatSocketThread newclient, String msg){
        String[] tokens = msg.split(Messages.DELIMITER);
        if (tokens.length != 3 || !tokens[0].equals(Messages.AUTH_REQUEST)) {
            newclient.messageFormatError(msg);
            return;
        }

        String login = tokens[1];
        String password = tokens[2];
        String nickname = authService.getNickname(login, password);

        if(nickname == null){
            newclient.authError();
            return;
        }


        ChatSocketThread oldClient = getClientByNickname(nickname);
        newclient.authAcccept(nickname);
        if(oldClient == null){
            sendToAllAuthorizedClients(Messages.getBroadcast("Server", newclient.getNickname() + " connected." ));
        } else {
            oldClient.reconnected();
        }
        sendToAllAuthorizedClients(Messages.getUsersList(getAllNicknamesString()));
    }

    private ChatSocketThread getClientByNickname(String nickname){
        final int cnt = clients.size();
        for (int i = 0; i < cnt ; i++) {
            ChatSocketThread client = (ChatSocketThread) clients.get(i);
            if(!client.isAuthorized()) continue;
            if(client.getNickname().equals(nickname)) return client;
        }
        return null;
    }

    private String getAllNicknamesString(){
        StringBuilder sb = new StringBuilder();
        final int cnt = clients.size();
        final int last = cnt - 1;
        for (int i = 0; i < cnt; i++) {
            ChatSocketThread client = (ChatSocketThread) clients.get(i);
            if(!client.isAuthorized() || client.isReconnected()) continue;
            sb.append(client.getNickname());
            if(i != last) sb.append(Messages.DELIMITER);
        }
        return sb.toString();
    }

    @Override
    public synchronized void onExceptionSocketThread(SocketThread socketThread, Socket socket, Exception e) {
        putLog("Exception: " + e.getClass().getName() + ": " + e.getMessage());
    }
}
