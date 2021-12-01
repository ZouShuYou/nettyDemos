package netty.rpc.common.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.List;

/**
 * @author zousy
 * @version v1.0
 * @Description
 * @date 2021-11-17 10:45
 */
public class CuratorClient {
    public static final int ZK_SESSION_TIMEOUT = 5000;
    public static final int ZK_CONNECTION_TIMEOUT = 5000;
    public static final String ZK_REGISTRY_PATH = "/registry";
    public static final String ZK_DATA_PATH = ZK_REGISTRY_PATH + "/data";
    public static final String ZK_NAMESPACE = "netty-rpc";

    private CuratorFramework client;

    public CuratorClient(String connectString,String namespace,int sessionTimeout, int connectionTimeout){
        client = CuratorFrameworkFactory.builder().namespace(namespace).connectString(connectString)
                .sessionTimeoutMs(sessionTimeout).connectionTimeoutMs(connectionTimeout)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
        client.start();
    }

    public CuratorClient(String connectString, int timeout) {
        this(connectString,ZK_NAMESPACE,timeout,timeout);
    }

    public CuratorClient(String connectString){
        this(connectString,ZK_NAMESPACE,ZK_SESSION_TIMEOUT,ZK_CONNECTION_TIMEOUT);
    }

    public String createPathData(String path, byte[] data) throws Exception {
        return client.create().creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                .forPath(path,data);
    }

    public void addConnectionStateListener(ConnectionStateListener connectionStateListener){
        client.getConnectionStateListenable().addListener(connectionStateListener);
    }

    public void deletePath(String path) throws Exception {
        this.client.delete().forPath(path);
    }

    public void close() {
        this.client.close();
    }

    public List<String> getChildren(String path) throws Exception {
        return client.getChildren().forPath(path);
    }

    public byte[] getData(String path) throws Exception {
        return client.getData().forPath(path);
    }

    public void watchPathChildrenNode(String path, PathChildrenCacheListener listener) throws Exception {
        PathChildrenCache pathChildrenCache = new PathChildrenCache(client, path, true);
        pathChildrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
        pathChildrenCache.getListenable().addListener(listener);
    }

}
