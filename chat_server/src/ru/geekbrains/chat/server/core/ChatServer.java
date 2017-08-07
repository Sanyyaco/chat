package ru.geekbrains.chat.server.core;

import ru.geekbrains.network.ServerSocketThread;
import ru.geekbrains.network.ServerSocketThreadListener;
import ru.geekbrains.network.SocketThread;
import ru.geekbrains.network.SocketThreadListener;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

/**
 * Created by Administrator on 27.07.2017.
 */
public class ChatServer implements ServerSocketThreadListener, SocketThreadListener {

    private final ChatServerListener eventListener;
    private ServerSocketThread serverSocketThread;
    private final Vector<SocketThread> clients = new Vector<>();

    public ChatServer(ChatServerListener eventListener) {
        this.eventListener = eventListener;
    }

    public void startListening(int port){
        if(serverSocketThread != null && serverSocketThread.isAlive()){
            putLog("Поток сервера уже запущен.");
            return;
        }
        serverSocketThread = new ServerSocketThread("ServerSocketThread",port,this,2000);
    }

    public void dropAllClients(){
        putLog("dropAllClients");
    }

    public void stopListening(){
        if(serverSocketThread == null || !serverSocketThread.isAlive()){
            putLog("Поток сервера не запущен.");
            return;
        }
        serverSocketThread.interrupt();
    }

    private synchronized void putLog(String msg){
        eventListener.onLogChatServer(this, msg);
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
        new SocketThread(this,threadName,socket);
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
    }

    @Override
    public synchronized void onReadySocketThread(SocketThread socketThread, Socket socket) {
        putLog("ServerSocket is ready...");
        clients.add(socketThread);
    }

    @Override
    public synchronized void onReceiveString(SocketThread socketThread, Socket socket, String value) {
        for (int i = 0; i < clients.size(); i++) {
            clients.get(i).sendMsg(value);
        }
    }

    @Override
    public synchronized void onExceptionSocketThread(SocketThread socketThread, Socket socket, Exception e) {
        putLog("Exception: " + e.getClass().getName() + ": " + e.getMessage());
    }
}
