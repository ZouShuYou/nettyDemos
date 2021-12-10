package netty.rpc.client.route;

import netty.rpc.client.handler.RpcClientHandler;
import netty.rpc.common.protocol.RpcProtocol;
import netty.rpc.common.protocol.RpcServiceInfo;
import netty.rpc.common.util.ServiceUtil;

import java.util.*;

/**
 * @author zousy
 * @version v1.0
 * @Description
 * @date 2021-12-10 9:56
 */
public abstract class RpcLoadBalance {
    protected Map<String, List<RpcProtocol>> getServiceMap(Map<RpcProtocol, RpcClientHandler> connectedServerNodes){
        Map<String, List<RpcProtocol>> serviceMap = new HashMap<>();
        if (connectedServerNodes != null && connectedServerNodes.size() > 0){
            for (RpcProtocol rpcProtocol : connectedServerNodes.keySet()){
                for (RpcServiceInfo serviceInfo : rpcProtocol.getServiceInfoList()){
                    String serviceKey = ServiceUtil.makeServiceKey(serviceInfo.getServiceName(),serviceInfo.getVersion());
                    List<RpcProtocol> rpcProtocolList = serviceMap.get(serviceKey);
                    if (rpcProtocolList == null){
                        rpcProtocolList = new ArrayList<>();
                    }
                    rpcProtocolList.add(rpcProtocol);
                    serviceMap.putIfAbsent(serviceKey,rpcProtocolList);
                }
            }
        }
        return serviceMap;
    }

    public abstract RpcProtocol route(String serviceKey, Map<RpcProtocol, RpcClientHandler> connectedServerNodes) throws Exception;

}
