package ru.geekbrains.network;

/**
 * Created by Administrator on 29.07.2017.
 */
public class ServerSocketThread extends Thread {

    public ServerSocketThread(String name) {
        super(name);
        start();
    }

    @Override
    public void run() {
        System.out.println("Поток запущен");
        while(!isInterrupted()){
            System.out.println("поток ServerSocketThread работает");
            try{
                sleep(1000);
            } catch (InterruptedException e){
                break;
            }
        }
        System.out.println("Поток остановлен");
    }
}
