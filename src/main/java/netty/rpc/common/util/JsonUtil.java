package netty.rpc.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * @author zousy
 * @version v1.0
 * @Description
 * @date 2021-11-17 13:51
 */
public class JsonUtil {
    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        objectMapper.setDateFormat(dateFormat);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static String objectToJson(Object o){
        String json = "";
        try {
            json = objectMapper.writeValueAsString(o);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(),e);
        }
        return json;
    }

    public static <T> T jsonToObject(String json, Class<?> cls) {
        T t = null;
        JavaType javaType = objectMapper.getTypeFactory().constructType(cls);
        try {
            t = objectMapper.readValue(json,javaType);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return t;
    }
}
