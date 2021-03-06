package ru.geekbrains.network;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Date;

/**
 * Created by Administrator on 02.08.2017.
 */
public class SocketThread extends Thread {

    private final SocketThreadListener eventListener;
    private final Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private Long startTime;

    public SocketThread(SocketThreadListener eventListener, String name, Socket socket) {
        super(name);
        this.eventListener = eventListener;
        this.socket = socket;
        startTime = System.currentTimeMillis();
        start();
    }

    public Long getStartTime() {
        return startTime;
    }

    @Override
    public void run() {

        eventListener.onStartSocketThread(this);
        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            eventListener.onReadySocketThread(this, socket);

            while (!isInterrupted()) {
                String msg = in.readUTF();
                eventListener.onReceiveString(this, socket, msg);
            }

        } catch (IOException e) {
            eventListener.onExceptionSocketThread(this, socket, e);
        } finally {
            try {
                in.close();
                out.close();
                socket.close();
            } catch (IOException e) {
                eventListener.onExceptionSocketThread(this, socket, e);
            }
            eventListener.onStopSocketThread(this);
        }
    }

    public synchronized void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
            out.flush();
        } catch (IOException e) {
            eventListener.onExceptionSocketThread(this, socket, e);
            close();
        }
    }

    public synchronized void close() {
        interrupt();

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
