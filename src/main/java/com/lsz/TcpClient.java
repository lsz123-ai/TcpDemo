package com.lsz;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;

public abstract class TcpClient {

    private String ip;
    private int port;

    private Socket mSocket;
    private SocketAddress mSocketAddress;
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    private ReadThread mReadThread;
    private boolean _isConnected = false;


    public TcpClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }
    

    public void connect(boolean f) {

        try {
            this.mSocket = new Socket();
            this.mSocket.setTcpNoDelay(f);   //false 为开启Nagle算法（默认）   true：禁用
            this.mSocket.setKeepAlive(true);
            this.mSocketAddress = new InetSocketAddress(ip, port);
            this.mSocket.connect( mSocketAddress, 5000);// 设置连接超时时间为5秒

            this.mOutputStream = mSocket.getOutputStream();
            this.mInputStream = mSocket.getInputStream();

            this.mReadThread = new TcpClient.ReadThread();
            this.mReadThread.start();
            this._isConnected = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {

        if (this.mReadThread != null) {
            this.mReadThread.interrupt();
        }
        if (this.mSocket != null) {
            try {
                this.mSocket.close();
                this.mSocket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this._isConnected = false;
    }

    public boolean isConnected() {
        return this._isConnected;
    }


    public void send(byte[] bOutArray) {
        try {
            this.mOutputStream.write(bOutArray);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected abstract void onDataReceive(byte[] bytes, int size);

    private class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                try {
                    if (TcpClient.this.mInputStream == null) {
                        return;
                    }
                    int available = TcpClient.this.mInputStream.available();
                    if (available > 0) {
                        byte[] buffer = new byte[available];
                        int size = TcpClient.this.mInputStream.read(buffer);
                        if (size > 0) {
                            onDataReceive(buffer, size);
                        }
                    } else {
                        Thread.sleep(50);
                    }


                } catch (Throwable e) {
                    System.out.println(e.getMessage());
                    return;
                }
            }
        }
    }
}

