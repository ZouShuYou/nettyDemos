package netty.rpc.common.serializer.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;
import netty.rpc.common.codec.RpcRequest;
import netty.rpc.common.codec.RpcResponse;
import org.objenesis.strategy.StdInstantiatorStrategy;

/**
 * @author zousy
 * @version v1.0
 * @Description
 * @date 2021-11-18 10:30
 */
public class KryoPoolFactory {
/*
    private static volatile KryoPoolFactory poolFactory = null;

    public static KryoPool getKryoPoolInstance(){
        if (poolFactory == null){
            synchronized (KryoPoolFactory.class){
                if (poolFactory == null){
                    poolFactory = new KryoPoolFactory();
                }
            }
        }
        return poolFactory.getPool();
    }
*/
    private static class KryoPoolFactoryHolder{
        public static KryoPoolFactory kryoPoolFactory = new KryoPoolFactory();
    }

    public static KryoPool getKryoPoolInstance(){
        return KryoPoolFactoryHolder.kryoPoolFactory.getPool();
    }


    private KryoFactory factory = new KryoFactory() {
        @Override
        public Kryo create() {
            Kryo kryo = new Kryo();
            kryo.setReferences(false);
            kryo.register(RpcRequest.class);
            kryo.register(RpcResponse.class);
            Kryo.DefaultInstantiatorStrategy strategy = (Kryo.DefaultInstantiatorStrategy) kryo.getInstantiatorStrategy();
            strategy.setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());
            return kryo;
        }
    };

    private KryoPool pool = new KryoPool.Builder(factory).build();

    private KryoPoolFactory(){
    }

    public KryoPool getPool(){
        return pool;
    }
}
