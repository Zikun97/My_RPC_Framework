package open.zikun.rpc.transport.netty.client;

import open.zikun.rpc.entity.RpcRequest;
import open.zikun.rpc.transport.RpcClientProxy;
import open.zikun.rpc.transport.netty.client.NettyClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.UUID;

public class NettyClientProxy extends RpcClientProxy {

    private static final Logger logger = LoggerFactory.getLogger(NettyClientProxy.class);

    private final NettyClient client;

    public NettyClientProxy(NettyClient client) {
        super(client);
        this.client = client;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        logger.info("调用接口{}: {}", method.getDeclaringClass().getName(), method.getName());
        RpcRequest request = new RpcRequest(UUID.randomUUID().toString(), method.getDeclaringClass().getName(),
                method.getName(), args, method.getParameterTypes(), false);

        return client.sendRequest(request);
    }
}
