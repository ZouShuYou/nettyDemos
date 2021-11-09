package websocket.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author zousy
 * @version v1.0
 * @Description
 * @date 2021-11-09 15:58
 */
public class ChannelSupervise {
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private static ConcurrentMap<ChannelId, Channel> channelMap = new ConcurrentHashMap();

    public static void addChannel(Channel channel){
        channelGroup.add(channel);
        channelMap.put(channel.id(),channel);
    }

    public static void removeChannel(Channel channel){
        channelGroup.remove(channel);
        channelMap.remove(channel.id());
    }

    public static void sendAll(TextWebSocketFrame webSocketFrame){
        //while (!channelGroup.isEmpty()){
        //    for (Channel channel : channelGroup){
        //        channel.writeAndFlush(webSocketFrame);
        //    }
        //    try {
        //        Thread.sleep(3000);
        //    } catch (InterruptedException e) {
        //        e.printStackTrace();
        //    }
        //}
        channelGroup.writeAndFlush(webSocketFrame);
    }
}
