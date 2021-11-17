package netty.rpc.server.core;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import netty.rpc.common.codec.RpcRequest;

/**
 * @author zousy
 * @version v1.0
 * @Description
 * @date 2021-11-17 15:18
 */
public class RpcServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {

    }
}
