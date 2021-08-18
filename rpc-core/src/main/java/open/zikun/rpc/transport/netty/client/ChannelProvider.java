package open.zikun.rpc.transport.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import open.zikun.rpc.codec.CommonDecoder;
import open.zikun.rpc.codec.CommonEncoder;
import open.zikun.rpc.enumeration.RpcError;
import open.zikun.rpc.exception.RpcException;
import open.zikun.rpc.serializer.CommonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.*;

public class ChannelProvider {

    private static final Logger logger = LoggerFactory.getLogger(ChannelProvider.class);
    private static EventLoopGroup eventLoopGroup;
    private static Bootstrap bootstrap = initializeBootstrap();

    private static Map<String, Channel> channels = new ConcurrentHashMap<>();

//    private static final int MAX_RETRY_COUNT = 5;
//    private static Channel channel = null;

    public static Channel get(InetSocketAddress inetSocketAddress, CommonSerializer serializer) throws InterruptedException{
        String key = inetSocketAddress.toString() + serializer.getCode();
        if (channels.containsKey(key)) {
            Channel channel = channels.get(key);
            if (channel != null && channel.isActive()) {
                return channel;
            } else {
                channels.remove(key);
            }
        }

        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                // 自定义序列化编码器
                // RpcResponse -> ByteBuf
                socketChannel.pipeline().addLast(new CommonEncoder(serializer))
                        .addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS))
                        .addLast(new CommonDecoder())
                        .addLast(new NettyClientHandler());

            }
        });
//        CountDownLatch countDownLatch = new CountDownLatch(1);
//        try {
//            connect(bootstrap, inetSocketAddress, countDownLatch);
//            countDownLatch.await();
//        } catch (InterruptedException e) {
//            logger.error("获取channel时有错误发生");
//            e.printStackTrace();
//        }
//        return channel;
        Channel channel = null;
        try {
            channel = connect(bootstrap, inetSocketAddress);
        } catch (ExecutionException e) {
            logger.error("连接客户端时有错误发生：", e);
            return null;
        }
        channels.put(key, channel);
        return channel;
    }



    private static Channel connect(Bootstrap bootstrap, InetSocketAddress inetSocketAddress) throws ExecutionException, InterruptedException {
//        connect(bootstrap, inetSocketAddress, MAX_RETRY_COUNT, countDownLatch);
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                logger.info("客户端连接成功!");
                completableFuture.complete(future.channel());
            } else {
                logger.error("客户端连接失败！");
                throw new IllegalStateException();
            }
        });
        return completableFuture.get();
    }

//    private static void connect(Bootstrap bootstrap, InetSocketAddress inetSocketAddress,int retry, CountDownLatch countDownLatch) {
//        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
//            if (future.isSuccess()) {
//                logger.info("连接客户端成功");
//                channel = future.channel();
//                countDownLatch.countDown();
//                return;
//            }
//            if (retry == 0) {
//                logger.error("客户端连接失败：重试次数已经用完， 放弃连接！");
//                countDownLatch.countDown();
//                throw new RpcException(RpcError.CLIENT_CONNECT_SERVER_FAILED);
//            }
//            // 第几次连接
//            int order = (MAX_RETRY_COUNT - retry) + 1;;
//            // 本次重连的间隔
//            int delay = 1 << order;
//            logger.error("{}: 连接失败， 第{}次重连...", new Date(), order);
//            bootstrap.config().group().schedule(() -> connect(bootstrap, inetSocketAddress, retry - 1, countDownLatch), delay, TimeUnit.SECONDS);
//        });
//    }


    private static Bootstrap initializeBootstrap() {
        eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                // 连接的时间，超过这个时间连接不上会认为连接失败
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                // 是否开启TCP底层心跳机制
                .option(ChannelOption.SO_KEEPALIVE, true)
                // TCP默认开启了 Nagle 算法，该算法的作用是尽可能的发送大数据快，减少网络传输。TCP_NODELAY 参数的作用就是控制是否启用 Nagle 算法。
                .option(ChannelOption.TCP_NODELAY, true);
        return bootstrap;
    }
}
