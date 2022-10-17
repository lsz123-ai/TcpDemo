package com.lsz;

public class ServerTest {
    public static boolean f = false;
    public static void main(String[] args) {
        TcpServer tcpServer = new TcpServer(8888);
        new Thread(()-> tcpServer.start()).start();
        tcpServer.stop();
    }
    public boolean getF() {
        return f;
    }
    public void setF(boolean f) {
        this.f = f;
    }

}
