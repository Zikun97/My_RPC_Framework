package open.zikun.rpc.transport.socket.util;

import open.zikun.rpc.entity.RpcRequest;
import open.zikun.rpc.entity.RpcResponse;
import open.zikun.rpc.enumeration.PackageType;
import open.zikun.rpc.enumeration.RpcError;
import open.zikun.rpc.exception.RpcException;
import open.zikun.rpc.serializer.CommonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * 从输入流中读取字节，并反序列化为object
 */
public class ObjectReader {

    private static final Logger logger = LoggerFactory.getLogger(ObjectReader.class);
    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    public static Object readObject(InputStream inputStream) throws IOException {
        byte[] numberBytes = new byte[4];
        inputStream.read(numberBytes);
        int magic = bytes2Int(numberBytes);
        if (magic != MAGIC_NUMBER) {
            logger.error("不能识别的协议包");
            throw new RpcException(RpcError.UNKNOWN_PROTOCOL);
        }
        inputStream.read(numberBytes);
        int packageCode = bytes2Int(numberBytes);
        Class<?> packageClass;
        if (packageCode == PackageType.REQUEST_PACK.getCode()) {
            packageClass = RpcRequest.class;
        } else if (packageCode == PackageType.RESPONSE_PACK.getCode()) {
            packageClass = RpcResponse.class;
        } else {
            logger.error("无法识别的数据包：{}", packageCode);
            throw new RpcException(RpcError.UNKNOWN_PROTOCOL);
        }
        inputStream.read(numberBytes);
        int serializeCode = bytes2Int(numberBytes);
        CommonSerializer serializer = CommonSerializer.getByCode(serializeCode);
        if (serializer == null) {
            logger.error("未找到相应的序列化器！目标序列化器码：{}", serializeCode);
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        inputStream.read(numberBytes);
        int len = bytes2Int(numberBytes);
        byte[] bytes = new byte[len];
        inputStream.read(bytes);
        return serializer.deSerialize(bytes, packageClass);
    }

    private static int bytes2Int(byte[] bytes) {
        int value;
        value = ((bytes[0] & 0xFF) << 24) |
                ((bytes[1] & 0xFF) << 16) |
                ((bytes[2] & 0xFF) << 8) |
                (bytes[3] & 0xFF);
        return value;
    }
}
