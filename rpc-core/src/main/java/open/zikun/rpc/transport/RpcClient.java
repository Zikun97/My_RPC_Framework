package open.zikun.rpc.transport;

import open.zikun.rpc.entity.RpcRequest;
import open.zikun.rpc.serializer.CommonSerializer;

public interface RpcClient {

    int DEFAULT_SERIALIZER = CommonSerializer.KRYO_SERIALIZER;

    Object sendRequest(RpcRequest request);

    void setSerializer(CommonSerializer serializer);
}
