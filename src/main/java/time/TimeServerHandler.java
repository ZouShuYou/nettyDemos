package time;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Date;

/**
 * @author zousy
 * @version v1.0
 * @Description
 * @date 2020-12-15 9:06
 */
public class TimeServerHandler extends ChannelInboundHandlerAdapter {

    private int counter = 0;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //ByteBuf buf = (ByteBuf) msg;
        //byte[] req = new byte[buf.readableBytes()];
        //buf.readBytes(req);
        //String body = new String(req,"UTF-8").substring(0,req.length - System.getProperty("line.separator").length());

        String body = (String) msg;
        System.out.println("服务器收到请求:  " + body + "  counter is "+ this.counter++);
        String currentTime = new Date(System.currentTimeMillis()).toString();
        currentTime = currentTime + System.getProperty("line.separator");
        ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
        ctx.writeAndFlush(resp);

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
