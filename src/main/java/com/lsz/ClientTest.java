package com.lsz;

import java.util.Scanner;

public class ClientTest {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        TcpClient tcpClient = new TcpClient("127.0.0.1", 8888) {
            @Override
            protected void onDataReceive(byte[] bytes, int size) {
                String content = new String(bytes, 0, size);
                System.out.println(content);
            }
        };
        ServerTest test = new ServerTest();
        test.setF(false);
        tcpClient.connect(test.getF()); 
        while (tcpClient.isConnected()) {
            //发送数据
            String outContent = in.next();
            System.out.println(test.getF());
            tcpClient.send(outContent.getBytes());
        }

        
        tcpClient.close();

    }
}
