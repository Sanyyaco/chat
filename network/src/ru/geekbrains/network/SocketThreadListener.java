package ru.geekbrains.network;

import java.net.Socket;

/**
 * Created by Administrator on 03.08.2017.
 */
public interface SocketThreadListener {

    void onStartSocketThread (SocketThread socketThread);
    void onStopSocketThread (SocketThread socketThread);

    void onReadySocketThread(SocketThread socketThread, Socket socket);
    void onReceiveString(SocketThread socketThread, Socket socket, String value);

    void onExceptionSocketThread(SocketThread socketThread, Socket socket, Exception e);
}
