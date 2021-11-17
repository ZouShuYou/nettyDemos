package netty.rpc.common.util;

/**
 * @author zousy
 * @version v1.0
 * @Description
 * @date 2021-11-17 11:03
 */
public class ServiceUtil {
    public static final String SERVICE_CONCAT_TOKEN = "#";

    public static String makeServiceKey(String interfaceName, String version){
        String serviceKey = interfaceName;
        if (version != null && version.trim().length() > 0){
            serviceKey += SERVICE_CONCAT_TOKEN.concat(version);
        }
        return serviceKey;
    }
}
