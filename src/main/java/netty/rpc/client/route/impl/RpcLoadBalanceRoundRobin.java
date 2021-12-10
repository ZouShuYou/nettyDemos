package netty.rpc.client.route.impl;

import netty.rpc.client.handler.RpcClientHandler;
import netty.rpc.client.route.RpcLoadBalance;
import netty.rpc.common.protocol.RpcProtocol;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zousy
 * @version v1.0
 * @Description
 * @date 2021-12-10 9:55
 */
public class RpcLoadBalanceRoundRobin extends RpcLoadBalance {
    private AtomicInteger roundRobin = new AtomicInteger(0);

    public RpcProtocol doRoute(List<RpcProtocol> addressList){
        int size = addressList.size();
        int index = (roundRobin.getAndAdd(1) + size) % size;
        return addressList.get(index);
    }

    @Override
    public RpcProtocol route(String serviceKey, Map<RpcProtocol, RpcClientHandler> connectedServerNodes) throws Exception {
        Map<String, List<RpcProtocol>> serviceMap = getServiceMap(connectedServerNodes);
        List<RpcProtocol> addressList = serviceMap.get(serviceKey);
        if (addressList != null && addressList.size() > 0) {
            return doRoute(addressList);
        }else {
            throw new Exception("Can not find connection for service: " + serviceKey);
        }
    }
}
