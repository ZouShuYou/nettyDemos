package netty.rpc.client.handler;

/**
 * @author zousy
 * @version v1.0
 * @Description
 * @date 2021-12-01 16:25
 */
public interface AsyncRPCCallback {

    void success(Object result);

    void fail(Exception e);
}
