package ru.geekbrains.chat.server.core;

import ru.geekbrains.chat.library.Messages;
import ru.geekbrains.network.SocketThread;
import ru.geekbrains.network.SocketThreadListener;

import java.net.Socket;

/**
 * Created by Administrator on 07.08.2017.
 */
public class ChatSocketThread extends SocketThread {

    private boolean isAuthorized;
    private boolean isReconnected;
    private String nickname;

    public ChatSocketThread(SocketThreadListener eventListener, String name, Socket socket) {
        super(eventListener, name, socket);
    }

    public boolean isAuthorized() {
        return isAuthorized;
    }

    public boolean isReconnected() {
        return isReconnected;
    }

    public String getNickname() {
        return nickname;
    }

    void authError(){
        sendMsg(Messages.getAuthError());
        close();
    }

    void reconnected(){
        isReconnected = true;
        sendMsg(Messages.getReconnect());
        close();
    }

    void messageFormatError(String msg){
        sendMsg(Messages.getMsgFormatError(msg));
    }

    void authAcccept(String nickname){
        this.isAuthorized = true;
        this.nickname = nickname;
        sendMsg(Messages.getAuthAccept(nickname));
    }

}
