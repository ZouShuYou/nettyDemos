package netty.rpc.client.connect;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import netty.rpc.client.handler.RpcClientHandler;
import netty.rpc.client.handler.RpcClientInitializer;
import netty.rpc.client.route.RpcLoadBalance;
import netty.rpc.client.route.impl.RpcLoadBalanceRoundRobin;
import netty.rpc.common.protocol.RpcProtocol;
import netty.rpc.common.protocol.RpcServiceInfo;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author zousy
 * @version v1.0
 * @Description
 * @date 2021-12-01 15:55
 */
public class ConnectionManager {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionManager.class);

    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(4);
    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(4, 8,
            600L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(1000));
    private Map<RpcProtocol, RpcClientHandler> connectedServerNodes = new ConcurrentHashMap<>();
    private CopyOnWriteArraySet<RpcProtocol> rpcProtocolSet = new CopyOnWriteArraySet<>();
    private ReentrantLock lock = new ReentrantLock();
    private Condition connected = lock.newCondition();
    private long waitTimeout = 5000;
    private RpcLoadBalance loadBalance = new RpcLoadBalanceRoundRobin();
    private volatile boolean isRunning = true;


    public void updateConnectedServer(RpcProtocol rpcProtocol, PathChildrenCacheEvent.Type type) {
        if (rpcProtocol == null){
            return;
        }
        if (type == PathChildrenCacheEvent.Type.CHILD_ADDED && !rpcProtocolSet.contains(rpcProtocol)){
            connectServerNode(rpcProtocol);
        }
    }

    private void connectServerNode(RpcProtocol rpcProtocol) {
        if (rpcProtocol.getServiceInfoList() == null || rpcProtocol.getServiceInfoList().isEmpty()) {
            logger.info("No service on node, host: {}, port: {}", rpcProtocol.getHost(), rpcProtocol.getPort());
            return;
        }
        rpcProtocolSet.add(rpcProtocol);
        logger.info("New service node, host: {}, port: {}", rpcProtocol.getHost(), rpcProtocol.getPort());
        for (RpcServiceInfo serviceInfo : rpcProtocol.getServiceInfoList()){
            logger.info("New service info, name: {}, version: {}", serviceInfo.getServiceName(), serviceInfo.getVersion());
        }
        final InetSocketAddress remotePeer = new InetSocketAddress(rpcProtocol.getHost(), rpcProtocol.getPort());
        threadPoolExecutor.submit(new Runnable() {
            @Override
            public void run() {
                Bootstrap b = new Bootstrap();
                b.group(eventLoopGroup).channel(NioSocketChannel.class).handler(new RpcClientInitializer());

                ChannelFuture channelFuture = b.connect(remotePeer);
                channelFuture.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (future.isSuccess()){
                            logger.info("Successfully connect to remote server, remote peer = " + remotePeer);
                            RpcClientHandler handler = channelFuture.channel().pipeline().get(RpcClientHandler.class);
                            connectedServerNodes.put(rpcProtocol,handler);
                            handler.setRpcProtocl(rpcProtocol);
                            signalAvailableHandler();
                        }else {
                            logger.error("Can not connect to remote server, remote peer = " + remotePeer);
                        }
                    }
                });
            }
        });
    }

    private void signalAvailableHandler() {
        lock.lock();
        try {
            connected.signalAll();
        }finally {
            lock.unlock();
        }
    }

    private static class SingletonHolder{
        private static final ConnectionManager instance = new ConnectionManager();
    }

    public static ConnectionManager getInstance() {
        return SingletonHolder.instance;
    }

    public RpcClientHandler chooseHandler(String serviceKey) throws Exception {
        int size = connectedServerNodes.values().size();
        while (isRunning && size <= 0){
            try {
                waitingForHandler();
            }catch (InterruptedException e){
                logger.error("Waiting for available service is interrupted!", e);
            }
        }
        RpcProtocol rpcProtocol = loadBalance.route(serviceKey,connectedServerNodes);
        RpcClientHandler handler = connectedServerNodes.get(rpcProtocol);
        if (handler != null){
            return handler;
        }else {
            throw new Exception("Can not get available connection");
        }
    }

    private boolean waitingForHandler() throws InterruptedException {
        lock.lock();
        try {
            logger.warn("Waiting for available service");
            return connected.await(this.waitTimeout,TimeUnit.SECONDS);
        }finally {
            lock.unlock();
        }
    }

    public void stop(){
        isRunning = false;
        for (RpcProtocol rpcProtocol : rpcProtocolSet){
            removeAndCloseHandler(rpcProtocol);
        }

    }

    private void removeAndCloseHandler(RpcProtocol rpcProtocol) {
        RpcClientHandler handler = connectedServerNodes.get(rpcProtocol);
        if (handler != null){
            handler.close();
        }
        connectedServerNodes.remove(rpcProtocol);
        rpcProtocolSet.remove(rpcProtocol);
    }
}
