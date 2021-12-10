package netty.rpc.client.proxy;

import netty.rpc.client.connect.ConnectionManager;
import netty.rpc.client.handler.RpcClientHandler;
import netty.rpc.client.handler.RpcFuture;
import netty.rpc.common.codec.RpcRequest;
import netty.rpc.common.util.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * @author zousy
 * @version v1.0
 * @Description
 * @date 2021-12-08 14:33
 */
public class ObjectProxy<T,P> implements InvocationHandler, RpcService<T,P,SerializableFunction<T>> {
    private static final Logger logger = LoggerFactory.getLogger(ObjectProxy.class);
    private Class<T> clazz;
    private String version;

    public ObjectProxy(Class<T> clazz, String version){
        this.clazz = clazz;
        this.version = version;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class == method.getDeclaringClass()){
            String name = method.getName();
            if ("equals".equals(name)){
                return proxy == args[0];
            }else if ("hashcode".equals(name)){
                return System.identityHashCode(proxy);
            }else if ("toString".equals(name)){
                return proxy.getClass().getName() + "@" +
                        Integer.toHexString(System.identityHashCode(proxy))+
                        ", with InvocationHandler " + this;
            }else {
                throw new IllegalStateException(String.valueOf(method));
            }
        }

        RpcRequest request = new RpcRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);
        request.setVersion(version);

        if (logger.isDebugEnabled()){
            logger.debug(method.getDeclaringClass().getName());
            logger.debug(method.getName());
            for (int i = 0; i < method.getParameterTypes().length; ++i) {
                logger.debug(method.getParameterTypes()[i].getName());
            }
            for (int i = 0; i < args.length; ++i) {
                logger.debug(args[i].toString());
            }
        }

        String serviceKey = ServiceUtil.makeServiceKey(method.getDeclaringClass().getName(), version);
        RpcClientHandler handler = ConnectionManager.getInstance().chooseHandler(serviceKey);
        RpcFuture rpcFuture = handler.sendRequest(request);
        return rpcFuture.get();
    }

    @Override
    public RpcFuture call(String funcName, Object... args) throws Exception {
        return null;
    }

    @Override
    public RpcFuture call(SerializableFunction<T> tSerializableFunction, Object... args) throws Exception {
        return null;
    }
}
