package ru.geekbrains.chat.server.gui;

import ru.geekbrains.chat.library.DefaultGUIExceptionHandler;
import ru.geekbrains.chat.server.core.ChatServer;
import ru.geekbrains.chat.server.core.ChatServerListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Administrator on 26.07.2017.
 */
public class ChatServerGui extends JFrame implements ActionListener, ChatServerListener {

    private static final int POS_X = 100;
    private static final int POS_Y = 50;
    private static final int WIDTH = 800;
    private static final int HEIGHT = 400;

    private static final String TITLE = "Chat Server";
    private static final String START_LISTENING = "Start Listening";
    private static final String DROP_ALL_CLIENTS = "Drop all clients";
    private static final String STOP_LISTENING = "Stop listening";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ChatServerGui();
            }
        });
    }

    private final ChatServer chatServer = new ChatServer(this);
    private final JButton btnStartListening = new JButton(START_LISTENING);
    private final JButton btnStopListening = new JButton(STOP_LISTENING);
    private final JButton btnDropAllClients = new JButton(DROP_ALL_CLIENTS);
    private final JTextArea log = new JTextArea();

    private ChatServerGui() {
        Thread.setDefaultUncaughtExceptionHandler(new DefaultGUIExceptionHandler());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(POS_X,POS_Y,WIDTH,HEIGHT);
        setTitle(TITLE);

        JPanel upperPanel = new JPanel(new GridLayout(1,3));
        upperPanel.add(btnStartListening);
        upperPanel.add(btnStopListening);
        upperPanel.add(btnDropAllClients);
        add(upperPanel, BorderLayout.NORTH);

        JScrollPane scrollLog= new JScrollPane(log);
        log.setEnabled(false);
        add(scrollLog, BorderLayout.CENTER);

        btnStartListening.addActionListener(this);
        btnStopListening.addActionListener(this);
        btnDropAllClients.addActionListener(this);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {

        Object src  = e.getSource();
        if (src == btnStartListening){
           chatServer.startListening(8189);
        }
        else if(src == btnDropAllClients){
            chatServer.dropAllClients();
        }
        else if(src == btnStopListening){
            chatServer.stopListening();
        }
        else {
            throw new RuntimeException("Uknown src = " + src);
        }
    }

    @Override
    public void onLogChatServer(ChatServer chatServer, String msg) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append(msg + "\n");
                log.setCaretPosition(log.getDocument().getLength());//гарантированная установка скрола
            }
        });
    }
}
