package ru.geekbrains.chat.library;

import javax.swing.*;

/**
 * Created by Administrator on 27.07.2017.
 */
public class DefaultGUIExceptionHandler implements Thread.UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        e.printStackTrace();
        StackTraceElement[] stackTraceElements = e.getStackTrace();
        String msg;

        if(stackTraceElements.length == 0){
            msg = "Пусто stackTraceElements";
        } else {
            msg = e.getClass().getCanonicalName() + ": " + e.getMessage() + "\n" + stackTraceElements[0];
        }

        JOptionPane.showMessageDialog(null, msg, "Exception: ", JOptionPane.ERROR_MESSAGE);
        System.exit(1);

    }
}
