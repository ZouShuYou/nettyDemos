package nio;

/**
 * @author zousy
 * @version v1.0
 * @Description
 * @date 2020-12-14 19:14
 */
public class TimeClient {
    public static void main(String[] args) {
        int port = 8080;
        if (args != null && args.length>0){
            port = Integer.parseInt(args[0]);
        }

        new Thread(new TimeClientHandler(null, port)).start();
    }
}
