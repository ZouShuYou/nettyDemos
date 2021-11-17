package netty.rpc.server.core;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author zousy
 * @version v1.0
 * @Description
 * @date 2021-11-17 11:18
 */
public class RpcServerInitializer extends ChannelInitializer<SocketChannel> {
    private Map<String, Object> handlerMap;
    private ThreadPoolExecutor threadPoolExecutor;

    public RpcServerInitializer(Map<String, Object> handlerMap, ThreadPoolExecutor threadPoolExecutor) {
        this.handlerMap = handlerMap;
        this.threadPoolExecutor = threadPoolExecutor;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline cp = ch.pipeline();
    }
}