package netty.rpc.client.discovery;

import netty.rpc.client.connect.ConnectionManager;
import netty.rpc.common.protocol.RpcProtocol;
import netty.rpc.common.zookeeper.CuratorClient;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zousy
 * @version v1.0
 * @Description
 * @date 2021-12-01 15:17
 */
public class ServiceDiscovery {
    private static final Logger logger = LoggerFactory.getLogger(ServiceDiscovery.class);
    private CuratorClient curatorClient;

    public ServiceDiscovery(String registryAddress) {
        this.curatorClient = new CuratorClient(registryAddress);
        discoveryService();
    }

    private void discoveryService(){
        try {
            logger.info("Get initial serive info");
            getServiceAndUpdateServer();
            curatorClient.watchPathChildrenNode(CuratorClient.ZK_REGISTRY_PATH, new PathChildrenCacheListener() {
                @Override
                public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                    PathChildrenCacheEvent.Type type = pathChildrenCacheEvent.getType();
                    ChildData childData = pathChildrenCacheEvent.getData();
                    switch (type){
                        case CONNECTION_RECONNECTED:
                            logger.info("Reconnected to zk, try to get latest service list");
                            getServiceAndUpdateServer();
                            break;
                        case CHILD_ADDED:
                            getServiceAndUpdateServer(childData, PathChildrenCacheEvent.Type.CHILD_ADDED);
                        case CHILD_UPDATED:
                            getServiceAndUpdateServer(childData, PathChildrenCacheEvent.Type.CHILD_UPDATED);
                        case CHILD_REMOVED:
                            getServiceAndUpdateServer(childData, PathChildrenCacheEvent.Type.CHILD_REMOVED);
                            break;
                    }
                }
            });
        } catch (Exception e) {
            logger.error("Watch node exception: " + e.getMessage());
        }
    }

    private void getServiceAndUpdateServer(){
        try {
            List<String> nodeList = curatorClient.getChildren(CuratorClient.ZK_REGISTRY_PATH);
            List<RpcProtocol> dataList = new ArrayList<>();
            for (String node : nodeList){
                logger.debug("Service node: " + node);
                byte[] bytes = curatorClient.getData(CuratorClient.ZK_REGISTRY_PATH + "/" + node);
                String json = new String(bytes);
                RpcProtocol rpcProtocol = RpcProtocol.formJson(json);
                dataList.add(rpcProtocol);
            }
            logger.debug("Service node data: {}", dataList);
            updateConnectedServer(dataList);
        }catch (Exception e){

        }
    }

    private void updateConnectedServer(List<RpcProtocol> dataList) {
        ConnectionManager.getInstance().updateConnectedServer(dataList);
    }

    private void getServiceAndUpdateServer(ChildData childData, PathChildrenCacheEvent.Type type){
        String path = childData.getPath();
        String data = new String(childData.getData(), StandardCharsets.UTF_8);
        logger.info("Child data updated, path:{},type:{},data:{},", path, type, data);
        RpcProtocol rpcProtocol = RpcProtocol.formJson(data);
        updateConnectedServer(rpcProtocol,type);
    }

    private void updateConnectedServer(RpcProtocol rpcProtocol, PathChildrenCacheEvent.Type type) {
        ConnectionManager.getInstance().updateConnectedServer(rpcProtocol, type);
    }

    public void stop(){
        this.curatorClient.close();
    }
}
