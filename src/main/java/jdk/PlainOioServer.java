package jdk;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author zousy
 * @version v1.0
 * @Description
 * @date 2020-12-09 15:14
 */
public class PlainOioServer {
    public void serve(int port) throws IOException{
        final ServerSocket serverSocket = new ServerSocket(port);
        try {
            for (;;){
                final  Socket clinetSocket = serverSocket.accept();
                System.out.println("收到连接来自： "+clinetSocket);
                new Thread(new Runnable() {
                    public void run() {
                        OutputStream out;
                        try {
                            out = clinetSocket.getOutputStream();
                            out.write("HI! \r\n".getBytes());
                            out.flush();
                            clinetSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }finally {
                            try {
                                clinetSocket.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        PlainOioServer plainOioServer = new PlainOioServer();
        try {
            plainOioServer.serve(9999);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
