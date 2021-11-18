package netty.rpc.common.serializer;

/**
 * @author zousy
 * @version v1.0
 * @Description
 * @date 2021-11-18 10:28
 */
public abstract class Serializer {
    public abstract <T> byte[] serialize(T obj);

    public abstract <T> Object deserialize(byte[] bytes,Class<?> clazz);

}
