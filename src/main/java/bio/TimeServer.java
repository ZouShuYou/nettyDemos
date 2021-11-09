package bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author zousy
 * @version v1.0
 * @Description
 * @date 2020-12-14 17:27
 */
public class TimeServer {
    public static void main(String[] args) throws IOException {
        int port = 8080;
        if (args != null && args.length>0){
            port = Integer.parseInt(args[0]);
        }

        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("监听端口：  " + port);
            Socket socket =null;
            TimeServerHandlerExecutePool timeServerHandlerExecutePool = new TimeServerHandlerExecutePool(50,1000);
            while (true){
                socket = serverSocket.accept();
                //new Thread(new TimeServerHandler(socket)).start();
                timeServerHandlerExecutePool.execute(new TimeServerHandler(socket));
            }
        }finally {
            if (serverSocket!=null){
                serverSocket.close();
            }
            serverSocket = null;
        }
    }
}
