package bio;

import java.io.*;
import java.net.Socket;

/**
 * @author zousy
 * @version v1.0
 * @Description
 * @date 2020-12-14 17:43
 */
public class TimeClient {
    public static void main(String[] args) {
        int port = 8080;
        if (args != null && args.length>0){
            port = Integer.parseInt(args[0]);
        }

        Socket socket = null;
        BufferedReader in = null;
        PrintWriter out = null;

        try {
            socket = new Socket("127.0.0.1", port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println("query");
            System.out.println("向服务端查询时间");
            String res= in.readLine();
            System.out.println("查询结果为: "+ res);

        } catch (IOException e) {
            e.printStackTrace();

            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

            if (out != null) {
                out.close();
            }

            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }


    }
}
