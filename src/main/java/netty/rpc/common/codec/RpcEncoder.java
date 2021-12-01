package netty.rpc.common.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import netty.rpc.common.serializer.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zousy
 * @version v1.0
 * @Description
 * @date 2021-11-22 9:38
 */
public class RpcEncoder extends MessageToByteEncoder {
    private static final Logger logger = LoggerFactory.getLogger(RpcDecoder.class);
    private Class<?> genericClass;
    private Serializer serializer;

    public RpcEncoder(Class<?> genericClass,Serializer serializer){
        this.genericClass = genericClass;
        this.serializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if (genericClass.isInstance(msg)){
            try {
                byte[] data = serializer.serialize(msg);
                out.writeInt(data.length);
                out.writeBytes(data);
            }catch (Exception e){
                logger.error("Encode error: " + e.toString());
            }
        }
    }
}
