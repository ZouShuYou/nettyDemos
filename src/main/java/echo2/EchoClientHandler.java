package echo2;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author zousy
 * @version v1.0
 * @Description
 * @date 2021-01-19 16:09
 */
public class EchoClientHandler extends ChannelInboundHandlerAdapter {

    private int counter = 0;

    static final String ECHO_REQ = "HI , zsy welcome to netty.$_";



    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for (int i=0; i <10;i++){
            ctx.writeAndFlush(Unpooled.copiedBuffer(ECHO_REQ.getBytes()));
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("This is " + counter++ +"times client : [" + msg + "]");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
