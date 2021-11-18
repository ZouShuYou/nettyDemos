package netty.rpc.common.serializer.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoPool;
import netty.rpc.common.serializer.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author zousy
 * @version v1.0
 * @Description
 * @date 2021-11-18 10:27
 */
public class KrypSerializer extends Serializer {
    private KryoPool kryoPool = KryoPoolFactory.getKryoPoolInstance();

    @Override
    public <T> byte[] serialize(T obj) {
        Kryo kryo = kryoPool.borrow();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);
        try {
            kryo.writeObject(output,obj);
            output.close();
            return byteArrayOutputStream.toByteArray();
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }finally {
            try {
                byteArrayOutputStream.close();
            }catch (IOException e){
                throw new RuntimeException(e);
            }
            kryoPool.release(kryo);
        }
    }

    @Override
    public <T> Object deserialize(byte[] bytes, Class<?> clazz) {
        Kryo kryo = kryoPool.borrow();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        Input input = new Input(byteArrayInputStream);
        try {
            Object result = kryo.readObject(input,clazz);
            input.close();
            return result;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }finally {
            try {
                byteArrayInputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            kryoPool.release(kryo);
        }
    }
}
