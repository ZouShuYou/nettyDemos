package netty.rpc.common.codec;

import java.io.Serializable;

/**
 * @author zousy
 * @version v1.0
 * @Description
 * @date 2021-11-18 10:32
 */
public class RpcResponse implements Serializable {
    private static final long serialVersionUID = 7895224307858438203L;

    private String requestId;
    private String error;
    private Object result;

    public boolean isError(){
        return error!= null;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
