package ru.geekbrains.chat.client;

import ru.geekbrains.chat.library.DefaultGUIExceptionHandler;
import ru.geekbrains.chat.library.Messages;
import ru.geekbrains.network.SocketThread;
import ru.geekbrains.network.SocketThreadListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static ru.geekbrains.chat.library.Messages.*;


/**
 * Created by Administrator on 27.07.2017.
 */
public class ChatClientGui extends JFrame implements ActionListener, SocketThreadListener {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ChatClientGui();
            }
        });
    }

    private static final int WIDTH = 300;
    private static final int HEIGHT = 350;
    private static final String TITLE = "Chat client";

    private final JPanel upperPanel = new JPanel(new GridLayout(2,3));
    private final JTextField fieldIPAddr = new JTextField("127.0.0.1");
    private final JTextField fieldPort = new JTextField("8189");
    private final JCheckBox chkAlwaysOnTop = new JCheckBox("Always on top", true);
    private final JTextField fieldLogin = new JTextField("lex");
    private final JPasswordField fieldPass = new JPasswordField("lex");
    private final JButton btnLogin = new JButton("Login");

    private final JTextArea log = new JTextArea();
    private final JList<String> userList = new JList<>();

    private final JPanel bottomPanel = new JPanel(new BorderLayout());
    private final JButton btnDisconnect = new JButton("Disconnect");
    private final JTextField fieldInput = new JTextField();
    private final JButton btnSend = new JButton("Send");

    private  ChatClientGui(){
        Thread.setDefaultUncaughtExceptionHandler(new DefaultGUIExceptionHandler());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(WIDTH,HEIGHT);
        setTitle(TITLE);
        upperPanel.add(fieldIPAddr);
        upperPanel.add(fieldPort);
        upperPanel.add(chkAlwaysOnTop);
        upperPanel.add(fieldLogin);
        upperPanel.add(fieldPass);
        upperPanel.add(btnLogin);
        add(upperPanel,BorderLayout.NORTH);

        JScrollPane scrollLog = new JScrollPane(log);
        log.setEditable(false);
        add(scrollLog,BorderLayout.CENTER);

        JScrollPane scrollUsers = new JScrollPane(userList);
        scrollUsers.setPreferredSize(new Dimension(150,0));
        add(scrollUsers, BorderLayout.EAST);

        bottomPanel.add(btnDisconnect,BorderLayout.WEST);
        bottomPanel.add(fieldInput, BorderLayout.CENTER);
        bottomPanel.add(btnSend,BorderLayout.EAST);
        bottomPanel.setVisible(false);
        add(bottomPanel,BorderLayout.SOUTH);

        btnLogin.addActionListener(this);
        btnDisconnect.addActionListener(this);
        btnSend.addActionListener(this);
        chkAlwaysOnTop.addActionListener(this);

        fieldIPAddr.addActionListener(this);
        fieldPort.addActionListener(this);
        fieldLogin.addActionListener(this);
        fieldPass.addActionListener(this);
        btnLogin.addActionListener(this);
        btnDisconnect.addActionListener(this);
        fieldInput.addActionListener(this);
        btnSend.addActionListener(this);
        chkAlwaysOnTop.addActionListener(this);

        setAlwaysOnTop(chkAlwaysOnTop.isSelected());
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        boolean a = true;
        Object src = e.getSource();
        if (    src == fieldIPAddr ||
                src == fieldPort   ||
                src == fieldLogin  ||
                src == fieldPass   ||
                src == btnLogin) {
            connect();
        } else if (src == btnDisconnect) {
            disconnect();
        } else if (src == fieldInput || src == btnSend) {
            sendMsg();
        } else if (src == chkAlwaysOnTop) {
            setAlwaysOnTop(chkAlwaysOnTop.isSelected());
        } else {
            throw new RuntimeException("Unknown src = " + src);
        }
    }

    private SocketThread socketThread;

    private void connect(){

        try {
            Socket socket = new Socket(fieldIPAddr.getText(),Integer.parseInt(fieldPort.getText()));
            socketThread = new SocketThread(this,"SocketThread",socket);
        } catch (IOException e) {
            e.printStackTrace();
            log.append("Exception: " + e.getMessage() + "\n");
            log.setCaretPosition(log.getDocument().getLength());
        }
    }

    private void disconnect(){

        socketThread.close();
    }

    private void sendMsg(){
        String msg = fieldInput.getText();
        if(msg.equals("")) return;
        fieldInput.setText(null);
        fieldInput.requestFocus();
        socketThread.sendMsg(msg);

    }

    @Override
    public void onStartSocketThread(SocketThread socketThread) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append("Поток сокета запущен\n");
                log.setCaretPosition(log.getDocument().getLength());
            }
        });
    }

    @Override
    public void onStopSocketThread(SocketThread socketThread) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append("Соединение потеряно\n");
                log.setCaretPosition(log.getDocument().getLength());
                bottomPanel.setVisible(false);
                upperPanel.setVisible(true);
            }
        });
    }

    @Override
    public void onReadySocketThread(SocketThread socketThread, Socket socket) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append("Соединение установлено\n");
                log.setCaretPosition(log.getDocument().getLength());
                bottomPanel.setVisible(true);
                upperPanel.setVisible(false);
                String login = fieldLogin.getText();
                String password = new String(fieldPass.getPassword());
                socketThread.sendMsg(Messages.getAuthRequest(login, password));
            }
        });
    }

    @Override
    public void onReceiveString(SocketThread socketThread, Socket socket, String value) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                String[] message = value.split(DELIMITER);
                String output = null;
                SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss: ");

                switch(message[0]){
                    case AUTH_REQUEST: break;
                    case AUTH_ACCEPT: break;
                    case AUTH_ERROR: break;
                    case USERS_LIST:break;
                    case RECONNECT:break;
                    case BROADCAST:{
                        Long dateString = Long.parseLong(message[1]);
                        output =  df.format(dateString) + ": " + message[2] + ": " + message[3];
                        break;
                    }
                    case MSG_FORMAT_ERROR:break;
                    default: new RuntimeException("Source: " + this.getClass() + ": Unknown type of message");

                }
                if(output == null)  new RuntimeException("Source: " + this.getClass() + ": empty output string");
                log.append(output + "\n");
                log.setCaretPosition(log.getDocument().getLength());
            }
        });
    }

    @Override
    public void onExceptionSocketThread(SocketThread socketThread, Socket socket, Exception e) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                e.printStackTrace();
                log.append("Exception: " + e.getMessage() + "\n");
                log.setCaretPosition(log.getDocument().getLength());
            }
        });

    }
}
