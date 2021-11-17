package netty.rpc.server.core;

/**
 * @author zousy
 * @version v1.0
 * @Description
 * @date 2021-11-17 10:38
 */
public abstract class AbstractServer {

    public abstract void start() throws Exception;

    public abstract void stop() throws Exception;
}
