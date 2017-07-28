package ru.geekbrains.chat.client;

import ru.geekbrains.chat.library.DefaultGUIExceptionHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Created by Administrator on 27.07.2017.
 */
public class ChatClientGui extends JFrame implements ActionListener, ItemListener {

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
    private final JTextField fieldIPAddr = new JTextField("89.222.249.131");
    private final JTextField fieldPort = new JTextField("8189");
    private final JCheckBox chkAlwaysOnTop = new JCheckBox("Always on top");
    private final JTextField fieldLogin = new JTextField("login_1");
    private final JPasswordField fieldPass = new JPasswordField("pass_1");
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
        add(bottomPanel,BorderLayout.SOUTH);

        btnLogin.addActionListener(this);
        btnDisconnect.addActionListener(this);
        btnSend.addActionListener(this);
        chkAlwaysOnTop.addItemListener(this);




        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        Object src  = e.getSource();

        if(src == btnLogin){
            connect();
        }
        if (src == btnDisconnect){
            disconnect();
        }
        if (src == btnSend){
            sendMsg();
        }


    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        Object src = e.getItemSelectable();
        if (src == chkAlwaysOnTop){
            setAlwaysOnTop(true);
        }
        if(e.getStateChange() == ItemEvent.DESELECTED) setAlwaysOnTop(false);
    }

    private void connect(){
        System.out.println("Connect");
        upperPanel.setVisible(false);
        bottomPanel.setVisible(true);
    }

    private void disconnect(){
        System.out.println("disconnect");
        upperPanel.setVisible(true);
        bottomPanel.setVisible(false);
    }

    private void sendMsg(){
        String text = fieldInput.getText() + "\n";
        if (text != null) {
            fieldInput.setText(null);
            log.append(text);
        }
    }

}
