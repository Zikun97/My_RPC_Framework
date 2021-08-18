package open.zikun.rpc.transport.socket.client;

import open.zikun.rpc.entity.RpcRequest;
import open.zikun.rpc.entity.RpcResponse;
import open.zikun.rpc.enumeration.RpcError;
import open.zikun.rpc.exception.RpcException;
import open.zikun.rpc.loadbalancer.LoadBalancer;
import open.zikun.rpc.loadbalancer.RandomLoadBalancer;
import open.zikun.rpc.registry.NacosServiceDiscovery;
import open.zikun.rpc.registry.ServiceDiscovery;
import open.zikun.rpc.serializer.CommonSerializer;
import open.zikun.rpc.serializer.HessianSerializer;
import open.zikun.rpc.serializer.JsonSerializer;
import open.zikun.rpc.serializer.KryoSerializer;
import open.zikun.rpc.transport.RpcClient;
import open.zikun.rpc.transport.socket.util.ObjectReader;
import open.zikun.rpc.transport.socket.util.ObjectWriter;
import open.zikun.rpc.util.RpcMessageChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SocketClient implements RpcClient {
    private static final Logger logger = LoggerFactory.getLogger(SocketClient.class);

    private final ServiceDiscovery serviceDiscovery;

    private CommonSerializer serializer;

    public SocketClient(){
        this(DEFAULT_SERIALIZER, new RandomLoadBalancer());
    }

    public SocketClient(LoadBalancer loadBalancer) {
        this(DEFAULT_SERIALIZER, loadBalancer);
    }

    public SocketClient(Integer serializerCode) {
        this(serializerCode, new RandomLoadBalancer());
    }

    public SocketClient(Integer serializerCode, LoadBalancer loadBalancer) {
        this.serializer =  CommonSerializer.getByCode(serializerCode);
        this.serviceDiscovery = new NacosServiceDiscovery(loadBalancer);
    }


    public Object sendRequest(RpcRequest request) {
        if (serializer == null) {
            logger.error("未设置序列化器！");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(request.getInterfaceName());
        try (Socket socket = new Socket()) {
            socket.connect(inetSocketAddress);
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();
            ObjectWriter.writeObject(outputStream, request, serializer);
            Object object = ObjectReader.readObject(inputStream);
            RpcResponse response = (RpcResponse) object;
            if (response == null) {
                logger.error("调用服务失败，service: {}", request.getInterfaceName());
                throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, " service:" + request.getInterfaceName());
            }
            RpcMessageChecker.check(request, response);
            return response;
        } catch (IOException e) {
            logger.error("调用时发生错误：", e);
            return null;
        }
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }
}
