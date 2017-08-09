package ru.geekbrains.chat.server.core;

/**
 * Created by Administrator on 07.08.2017.
 */
public interface AuthService {

    void start();
    String getNickname(String login, String password);
    void stop();
}
