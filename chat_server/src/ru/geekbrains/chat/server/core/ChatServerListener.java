package ru.geekbrains.chat.server.core;

/**
 * Created by Administrator on 27.07.2017.
 */
public interface ChatServerListener {
    void onLogChatServer(ChatServer chatServer, String msg);
}
