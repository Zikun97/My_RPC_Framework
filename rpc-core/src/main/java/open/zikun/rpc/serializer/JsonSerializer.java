package open.zikun.rpc.serializer;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import open.zikun.rpc.entity.RpcRequest;
import open.zikun.rpc.enumeration.SerializerCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class JsonSerializer implements CommonSerializer{

    private static final Logger logger = LoggerFactory.getLogger(JsonSerializer.class);

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(Object object) {
        try {
            return objectMapper.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            logger.error("序列化时发生错误：{}", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Object deSerialize(byte[] bytes, Class<?> clazz) {
        try {
            Object obj = objectMapper.readValue(bytes, clazz);
            if (obj instanceof RpcRequest) {
                obj = handleRequest(obj);
            }
            return obj;
        } catch (IOException exception) {
            logger.info("反序列化错误: {}", exception.getMessage());
            exception.printStackTrace();
            return null;
        }
    }

    private Object handleRequest(Object obj) throws IOException {
        RpcRequest request = (RpcRequest) obj;
        for (int i = 0; i < request.getParamTypes().length; ++i) {
            Class<?> clazz = request.getParamTypes()[i];
            if (!clazz.isAssignableFrom(request.getParamTypes()[i].getClass())) {
                byte[] bytes = objectMapper.writeValueAsBytes(request.getParamTypes()[i]);
                request.getParams()[i] = objectMapper.readValue(bytes, clazz);
            }
        }
        return request;
    }


    @Override
    public int getCode() {
        return SerializerCode.valueOf("JSON").getCode();
//        return SerializerCode.JSON.getCode();
    }
}
