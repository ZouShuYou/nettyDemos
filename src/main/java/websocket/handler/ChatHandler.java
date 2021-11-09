package websocket.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * @author zousy
 * @version v1.0
 * @Description
 * @date 2021-11-09 15:17
 */
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        String content = msg.text();
        System.out.println("接收到消息:"+content);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ChannelSupervise.addChannel(ctx.channel());
        System.out.println("客户端加入连接："+ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ChannelSupervise.removeChannel(ctx.channel());
        System.out.println("客户端断开连接："+ctx.channel());
    }


    //public static void start(){
    //
    //}

}
