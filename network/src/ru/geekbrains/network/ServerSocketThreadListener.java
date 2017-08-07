package ru.geekbrains.network;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Administrator on 02.08.2017.
 */
public interface ServerSocketThreadListener {
    void onStartServerSocketThread(ServerSocketThread thread);
    void onStopServerSocketThread(ServerSocketThread thread);

    void onReadyServerSocketThread(ServerSocketThread thread, ServerSocket serverSocket);
    void onTimeOutAccept(ServerSocketThread thread, ServerSocket serverSocket);
    void onAcceptedSocket(ServerSocketThread thread, ServerSocket serverSocket, Socket socket);

    void onExceptionServerSocketThread(ServerSocketThread thread, Exception e);
}
