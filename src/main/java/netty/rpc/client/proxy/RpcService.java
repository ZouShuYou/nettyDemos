package netty.rpc.client.proxy;

import netty.rpc.client.handler.RpcFuture;

/**
 * @author zousy
 * @version v1.0
 * @Description
 * @date 2021-12-08 14:33
 */
public interface RpcService<T,P, FN extends SerializableFunction<T>> {
    RpcFuture call(String funcName, Object... args) throws Exception;

    /**
     * lambda method reference
     */
    RpcFuture call(FN fn, Object... args) throws Exception;
}
