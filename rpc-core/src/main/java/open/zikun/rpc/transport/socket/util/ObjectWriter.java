package open.zikun.rpc.transport.socket.util;


import open.zikun.rpc.entity.RpcRequest;
import open.zikun.rpc.enumeration.PackageType;
import open.zikun.rpc.serializer.CommonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;


/**
 * socket传输的文件
 */
public class ObjectWriter {

    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    private static final Logger logger = LoggerFactory.getLogger(ObjectWriter.class);

    /**
     * 将Object对象写入OutputStream
     * 按照以下顺寻写入：
     *      1、写入魔数
     *      2、写入PACKAGE_TYPE
     *      3、写入序列号编码（Serialization Code）
     *      4、写入序列化后的byte数组长度
     *      5、写入序列化后的byte数组
     * @param outputStream
     * @param object
     * @param serializer
     * @throws IOException
     */
    public static void writeObject(OutputStream outputStream, Object object, CommonSerializer serializer) throws IOException {
        outputStream.write(int2Bytes(MAGIC_NUMBER));
        if (object instanceof RpcRequest) {
            outputStream.write(int2Bytes(PackageType.REQUEST_PACK.getCode()));
        } else {
            outputStream.write(int2Bytes(PackageType.RESPONSE_PACK.getCode()));
        }
        outputStream.write(int2Bytes(serializer.getCode()));
        byte[] bytes = serializer.serialize(object);
        outputStream.write(int2Bytes(bytes.length));
        outputStream.write(bytes);
        outputStream.flush();
    }


    private static byte[] int2Bytes(int value) {
        byte[] src = new byte[4];
        src[0] = (byte) ((value >> 24) & 0xFF);
        src[1] = (byte) ((value >> 16) & 0xFF);
        src[2] = (byte) ((value >> 8) & 0xFF);
        src[3] = (byte) (value & 0xFF);
        return src;
    }
}
