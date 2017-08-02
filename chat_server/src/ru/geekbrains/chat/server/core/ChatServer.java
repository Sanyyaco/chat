package ru.geekbrains.chat.server.core;

import ru.geekbrains.network.ServerSocketThread;

/**
 * Created by Administrator on 27.07.2017.
 */
public class ChatServer {

    private final ChatServerListener eventListener;
    private ServerSocketThread serverSocketThread;

    public ChatServer(ChatServerListener eventListener) {
        this.eventListener = eventListener;
    }

    public void startListening(int port){
        if(serverSocketThread != null && serverSocketThread.isAlive()){
            putLog("Поток сервера уже запущен.");
            return;
        }
        serverSocketThread = new ServerSocketThread("ServerSocketThread");
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

    private void putLog(String msg){
        eventListener.onLogChatServer(this, msg);
    }
}
