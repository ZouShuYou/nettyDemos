package time;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author zousy
 * @version v1.0
 * @Description
 * @date 2020-12-15 9:17
 */
public class TimeClientHandler extends ChannelInboundHandlerAdapter {
    private int counter = 0;

    private byte[] req;

    public TimeClientHandler() {
        req = ("QUERY" + System.getProperty("line.separator")).getBytes();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ByteBuf message = null;
        for (int i = 0; i<100; i++){
            message = Unpooled.buffer(req.length);
            message.writeBytes(req);
            ctx.writeAndFlush(message);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //ByteBuf buf = (ByteBuf) msg;
        //byte[] req = new byte[buf.readableBytes()];
        //buf.readBytes(req);
        //String body = new String(req,"UTF-8");
        String body = (String) msg;
        System.out.println("Now is:" + body + "   counter:   " + ++this.counter);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
