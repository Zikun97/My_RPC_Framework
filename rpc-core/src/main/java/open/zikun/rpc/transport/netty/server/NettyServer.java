package open.zikun.rpc.transport.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import open.zikun.rpc.codec.CommonDecoder;
import open.zikun.rpc.codec.CommonEncoder;
import open.zikun.rpc.enumeration.RpcError;
import open.zikun.rpc.exception.RpcException;
import open.zikun.rpc.hook.ShutdownHook;
import open.zikun.rpc.provider.ServiceProvider;
import open.zikun.rpc.provider.ServiceProviderImpl;
import open.zikun.rpc.registry.NacosServiceRegistry;
import open.zikun.rpc.registry.ServiceRegistry;
import open.zikun.rpc.serializer.CommonSerializer;
import open.zikun.rpc.serializer.JsonSerializer;
import open.zikun.rpc.serializer.KryoSerializer;
import open.zikun.rpc.transport.AbstractRpcServer;
import open.zikun.rpc.transport.RpcServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.plugin2.os.windows.SECURITY_ATTRIBUTES;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class NettyServer extends AbstractRpcServer {

    private final CommonSerializer serializer;

    public NettyServer(String host, int port) {
        this(host, port, DEFAULT_SERIALIZER);
    }

    public NettyServer(String host, int port, Integer serializerCode) {
        this.host = host;
        this.port = port;
        serviceProvider = new ServiceProviderImpl();
        serviceRegistry = new NacosServiceRegistry();
        // 默认设置Json作为序列化方式
        this.serializer = CommonSerializer.getByCode(serializerCode);
        scanServices();
    }

    @Override
    public void start() {
        // 自动注销服务
        ShutdownHook.getShutdownHook().addClearAllHook();

        EventLoopGroup boosGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boosGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .option(ChannelOption.SO_BACKLOG, 256)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline channelPipeline = socketChannel.pipeline();
//                            channelPipeline.addLast(new CommonEncoder(new JsonSerializer()));
                            channelPipeline.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
                            channelPipeline.addLast(new CommonEncoder(serializer));
                            channelPipeline.addLast(new CommonDecoder());
                            channelPipeline.addLast(new NettyServerHandler());
                        }
                    });
            ChannelFuture future = serverBootstrap.bind(host, port).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("启动服务器时发生错误：{}", e.getMessage());
            e.printStackTrace();
        } finally {
            boosGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

}
