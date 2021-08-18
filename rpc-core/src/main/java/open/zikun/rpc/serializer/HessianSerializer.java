package open.zikun.rpc.serializer;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import open.zikun.rpc.enumeration.SerializerCode;
import open.zikun.rpc.exception.SerializeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class HessianSerializer implements CommonSerializer {

    private static final Logger logger = LoggerFactory.getLogger(HessianSerializer.class);

    @Override
    public byte[] serialize(Object object) {
        HessianOutput hessianOutput = null;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            hessianOutput = new HessianOutput(outputStream);
            hessianOutput.writeObject(object);
            return outputStream.toByteArray();
        } catch (IOException e) {
            logger.error("序列化失败！", e);
            e.printStackTrace();
            throw new SerializeException("序列化时发生错误！");
        } finally {
            if (hessianOutput != null) {
                try {
                    hessianOutput.close();
                } catch (IOException e) {
                    logger.error("关闭流时发生错误！");
                }
            }
        }
    }

    @Override
    public Object deSerialize(byte[] bytes, Class<?> clazz) {
        HessianInput hessianInput = null;
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes)) {
            hessianInput = new HessianInput(inputStream);
            return hessianInput.readObject();
        } catch (IOException e) {
            logger.error("反序列化失败，", e);
            e.printStackTrace();
            throw new SerializeException("反序列化失败！");
        } finally {
            if (hessianInput != null) {
                    hessianInput.close();
            }
        }
    }

    @Override
    public int getCode() {
        return SerializerCode.HESSIAN.getCode();
    }
}
