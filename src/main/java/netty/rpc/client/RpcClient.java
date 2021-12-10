package netty.rpc.client;

import netty.rpc.client.discovery.ServiceDiscovery;
import netty.rpc.client.proxy.ObjectProxy;
import netty.rpc.common.annotation.RpcAutowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zousy
 * @version v1.0
 * @Description
 * @date 2021-12-01 15:15
 */
public class RpcClient implements ApplicationContextAware, DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(RpcClient.class);

    private ServiceDiscovery serviceDiscovery;

    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(16, 16, 600, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1000));

    public RpcClient(String address){
        this.serviceDiscovery = new ServiceDiscovery(address);
    }

    @Override
    public void destroy() throws Exception {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        for (String beanName : beanNames){
            Object bean = applicationContext.getBean(beanName);
            Field[] fields = bean.getClass().getDeclaredFields();
            try {
                for (Field field : fields){
                    RpcAutowired rpcAutowired = field.getAnnotation(RpcAutowired.class);
                    if (rpcAutowired != null){
                        String version = rpcAutowired.version();
                        field.setAccessible(true);
                        field.set(bean,createService(field.getType(),version));
                    }
                }
            }catch (IllegalAccessException e){
                logger.error(e.toString());
            }
        }
    }

    public static <T, P> T createService(Class<T> interfaceClass, String version) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new ObjectProxy<T, P>(interfaceClass,version)
        );
    }

    public static void submit(Runnable task){
        threadPoolExecutor.submit(task);
    }
}
