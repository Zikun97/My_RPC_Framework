package open.zikun.rpc.serializer;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import open.zikun.rpc.enumeration.SerializerCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ProtobufSerializer implements CommonSerializer{

    private static final Logger logger = LoggerFactory.getLogger(ProtobufSerializer.class);

    private final LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
    private final Map<Class<?>, Schema<?>> schemaCache = new ConcurrentHashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public byte[] serialize(Object object) {
        Class clazz = object.getClass();
        Schema schema = getSchema(clazz);
        byte[] data;
        try {
            data = ProtostuffIOUtil.toByteArray(object, schema, buffer);
        } finally {
            buffer.clear();
        }
        return data;
    }

    @Override
    @SuppressWarnings("unchecke")
    public Object deSerialize(byte[] bytes, Class<?> clazz) {
        Schema schema = getSchema(clazz);
        Object result = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(bytes, result, schema);
        return result;
    }

    @Override
    public int getCode() {
        return SerializerCode.PROTOBUF.getCode();
    }

    @SuppressWarnings("unchecked")
    private Schema getSchema(Class clazz) {
        Schema schema = schemaCache.get(clazz);
        if (Objects.isNull(schema)) {
            schema = RuntimeSchema.getSchema(clazz);
            if (Objects.nonNull(schema)) {
                schemaCache.put(clazz, schema);
            }
        }
        return schema;
    }
}
