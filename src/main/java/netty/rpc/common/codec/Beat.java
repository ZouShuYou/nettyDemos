package netty.rpc.common.codec;

/**
 * @author zousy
 * @version v1.0
 * @Description
 * @date 2021-11-18 11:00
 */
public class Beat {
    public static final int BEAT_INTERVAL = 30;
    public static final int BEAT_TIMEOUT = 3 * BEAT_INTERVAL;
    public static final String BEAT_ID = "BEAD_PING_PONG";

    public static RpcRequest BEAT_PING;

    static {
        BEAT_PING = new RpcRequest(){};
        BEAT_PING.setRequestId(BEAT_ID);
    }
}
