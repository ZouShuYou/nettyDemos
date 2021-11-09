package nio;

/**
 * @author zousy
 * @version v1.0
 * @Description
 * @date 2020-12-14 18:37
 */
public class TimeServer {
    public static void main(String[] args) {
        int port = 8080;
        if (args != null && args.length>0){
            port = Integer.parseInt(args[0]);
        }

        new Thread(new MultiplexerTimeServer(port)).start();
    }
}
