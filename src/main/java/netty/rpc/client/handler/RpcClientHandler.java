package netty.rpc.client.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import netty.rpc.common.codec.RpcRequest;
import netty.rpc.common.codec.RpcResponse;
import netty.rpc.common.protocol.RpcProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zousy
 * @version v1.0
 * @Description
 * @date 2021-12-01 15:59
 */
public class RpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> {
    private static final Logger logger = LoggerFactory.getLogger(RpcClientHandler.class);

    private ConcurrentHashMap<String, RpcFuture> pendingRPC = new ConcurrentHashMap<>();
    private volatile Channel channel;
    private SocketAddress remotePeer;
    private RpcProtocol rpcProtocol;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception {
        String requestId = response.getRequestId();
        logger.debug("Receive response: " + requestId);
        RpcFuture rpcFuture = pendingRPC.get(requestId);
        if (rpcFuture != null){
            pendingRPC.remove(requestId);
            rpcFuture.done(response);
        }else {
            logger.warn("Can not get pending response for request id: " + requestId);
        }
    }

    public void close() {
        channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    public RpcFuture sendRequest(RpcRequest request){
        RpcFuture rpcFuture  = new RpcFuture(request);
        pendingRPC.put(request.getRequestId(), rpcFuture);
        try {
            ChannelFuture channelFuture = channel.writeAndFlush(request).sync();
            if (!channelFuture.isSuccess()){
                logger.error("Send request {} error", request.getRequestId());
            }
        } catch (InterruptedException e) {
            logger.error("Send request exception: " + e.getMessage());
        }
        return rpcFuture;
    }

    public void setRpcProtocl(RpcProtocol rpcProtocol) {
        this.rpcProtocol = rpcProtocol;
    }
}
