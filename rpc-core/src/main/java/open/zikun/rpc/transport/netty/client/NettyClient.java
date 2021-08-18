package open.zikun.rpc.transport.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import open.zikun.rpc.codec.CommonDecoder;
import open.zikun.rpc.codec.CommonEncoder;
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
import open.zikun.rpc.serializer.KryoSerializer;
import open.zikun.rpc.transport.RpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class NettyClient implements RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private static final Bootstrap bootStart;
    private static final EventLoopGroup group;

    private final ServiceDiscovery serviceDiscovery;
    private CommonSerializer serializer;
    private final UnprocessedRequest unprocessedRequest;


    static {
        group = new NioEventLoopGroup();
        bootStart = new Bootstrap();
        bootStart.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        ChannelPipeline pipeline = nioSocketChannel.pipeline();
                        pipeline.addLast(new CommonDecoder())
                                .addLast(new CommonEncoder(new KryoSerializer()))
                                .addLast(new NettyClientHandler());
                    }
                });

    }

    public NettyClient() {
        this(DEFAULT_SERIALIZER, new RandomLoadBalancer());
    }

    public NettyClient(LoadBalancer loadBalancer) {
        this(DEFAULT_SERIALIZER, loadBalancer);
    }

    public NettyClient(Integer serializerCode, LoadBalancer loadBalancer) {
        this.serviceDiscovery = new NacosServiceDiscovery(loadBalancer);
        this.serializer = CommonSerializer.getByCode(serializerCode);
        this.unprocessedRequest = new UnprocessedRequest();
    }


    @Override
    public CompletableFuture<RpcResponse> sendRequest(RpcRequest request) {
        if (this.serializer == null) {
            logger.error("未发现序列化器！");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        CompletableFuture<RpcResponse> result = new CompletableFuture<>();
        try {
            InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(request.getInterfaceName());
            Channel channel = ChannelProvider.get(inetSocketAddress, serializer);
            if (!channel.isActive()) {
                group.shutdownGracefully();
                return null;
            }
            unprocessedRequest.put(request.getRequestId(), result);
            channel.writeAndFlush(request).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    logger.info(String.format("客户端发送消息：%s", request.toString()));
                } else {
                    future.channel().close();
                    result.completeExceptionally(future.cause());
                    logger.error("发送消息时有错误发生！", future.cause());
                }
            });
        } catch (InterruptedException e) {
            unprocessedRequest.remove(request.getRequestId());
            logger.error(e.getMessage());
            Thread.currentThread().interrupt();
        }
        return result;
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }
}
