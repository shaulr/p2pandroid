package com.tikalk.p2p;


import java.io.IOException;
import java.io.InputStream;

/**
 * Created by shaulr on 24/07/2017.
 */

public class SocketReadThread extends Thread {
    private final InputStream is;
    private final IConnectionListener listener;

    public SocketReadThread(InputStream is, IConnectionListener listener){
        this.is = is;
        this.listener = listener;
    }

    @Override
    public void run() {
        byte[] buffer = new byte[1024];
        while (!Thread.currentThread().isInterrupted()) {
            try {
                is.read(buffer);
                listener.onRecieveData(buffer);
                Thread.sleep(100);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
