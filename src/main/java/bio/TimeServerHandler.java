package bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

/**
 * @author zousy
 * @version v1.0
 * @Description
 * @date 2020-12-14 17:35
 */
public class TimeServerHandler implements Runnable{
    private Socket socket;

    public TimeServerHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        BufferedReader in = null;
        PrintWriter out = null;


        try {
            in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            String currentTime = null;
            String body = null;

            while (true){
                body = in.readLine();
                if (body == null){
                    break;
                }
                System.out.println("服务器收到请求： "+body);
                currentTime = "query".equals(body) ? new Date(System.currentTimeMillis()).toString() : "null";
                out.println(currentTime);
            }
        } catch (IOException e) {
            e.printStackTrace();

            try {
                assert in != null;
                in.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            if (out != null) {
                out.close();
            }
            try {
                this.socket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
