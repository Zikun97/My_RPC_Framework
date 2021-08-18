package open.zikun.rpc.transport.netty.server;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import open.zikun.rpc.entity.RpcRequest;
import open.zikun.rpc.entity.RpcResponse;
import open.zikun.rpc.factory.SingletonFactory;
import open.zikun.rpc.handler.RequestHandler;
import open.zikun.rpc.provider.ServiceProviderImpl;
import open.zikun.rpc.provider.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);
    private static final RequestHandler requestHandler;

    private int lossConnectCount = 0;

//    private static final ExecutorService threadPool;

    static {
        requestHandler = SingletonFactory.getInstance(RequestHandler.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest msg) throws Exception {


        try {
            if (msg.getHeartBeat()) {
                logger.info("接收到客户端心跳包...");
                return;
            }
            logger.info("服务器接收到请求：{}", msg);
            Object result = requestHandler.handle(msg);
            if (channelHandlerContext.channel().isActive() && channelHandlerContext.channel().isWritable()) {
                channelHandlerContext.writeAndFlush(RpcResponse.success(result, msg.getRequestId()));
            } else {
                logger.error("通道不可写");
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("处理过程中，有错误发生");
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        System.out.println("已经30s未收到客户端的消息了！");
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent)evt).state();
            if (state == IdleState.READER_IDLE) {
                ++ lossConnectCount;
                if (lossConnectCount > 2) {
                    logger.info("长时间未收到心跳包，断开连接...");
                    ctx.channel().close();
                }
            } else {
                super.userEventTriggered(ctx, evt);
            }
        }
    }
}
