package com.lsz;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class TcpServer {
    private int port;
    private boolean isFinished;
    private ServerSocket serverSocket;
    private ArrayList<SocketThread> socketThreads;


    public TcpServer(int port) {
        this.port = port;
        socketThreads = new ArrayList<>();
    }


    public void start() {
        isFinished = false;
        try {
            //创建服务器套接字，绑定到指定的端口
            serverSocket = new ServerSocket(port);
            //等待客户端连接
            while (!isFinished) {
                Socket socket = serverSocket.accept();//接受连接
                //创建线程处理连接
                SocketThread socketThread = new SocketThread(socket);
                socketThreads.add(socketThread);
                socketThread.start();
            }
        } catch (IOException e) {
            isFinished = true;
        }
    }


    public void stop() {
        isFinished = true;
        for (SocketThread socketThread : socketThreads) {
            socketThread.interrupt();
            socketThread.close();
        }
        try {
            if (serverSocket != null) {
                serverSocket.close();
                serverSocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class SocketThread extends Thread {

        private Socket socket;
        private InputStream in;
        private OutputStream out;

        SocketThread(Socket socket) {
            this.socket = socket;
            try {
                in = socket.getInputStream();
                out = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while (!isInterrupted()) {
                if (in == null) {
                    return;
                }
                try {
                    int available = in.available();
                    if (available > 0) {
                        byte[] buffer = new byte[available];
                        int size = in.read(buffer);
                        if (size > 0) {
                            boolean f = false;
                            String data = new String(buffer,0,size);
                            String[] s = data.split("\\+");
                            if (s.length == 1) data = String.valueOf(0);
                            else if (s.length == 2) {
                                try {
                                    int a = Integer.parseInt(s[0]);
                                    int b = Integer.parseInt(s[1]);
                                    data = String.valueOf(a + b);
                                }catch (Exception e){
                                    f = true;
                                }finally {
                                    if (f) {
                                        data = String.valueOf(0);
                                    }
                                }
                            }
                            String response = "response:" + data;
                            out.write(response.getBytes());
                            out.flush();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        void close() {

            try {
                if (in != null) {
                    in.close();
                }

                if (out != null) {
                    out.close();
                }

                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
