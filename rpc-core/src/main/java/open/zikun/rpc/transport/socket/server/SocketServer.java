package open.zikun.rpc.transport.socket.server;

import open.zikun.rpc.enumeration.RpcError;
import open.zikun.rpc.exception.RpcException;
import open.zikun.rpc.handler.RequestHandler;
import open.zikun.rpc.provider.ServiceProvider;
import open.zikun.rpc.provider.ServiceProviderImpl;
import open.zikun.rpc.registry.NacosServiceRegistry;
import open.zikun.rpc.registry.ServiceRegistry;
import open.zikun.rpc.serializer.CommonSerializer;
import open.zikun.rpc.transport.AbstractRpcServer;
import open.zikun.rpc.transport.RpcServer;
import open.zikun.rpc.factory.ThreadPoolFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class SocketServer extends AbstractRpcServer {


//    private final static int corePoolSize = 5;
//    private final static int maximumPoolSize = 50;
//    private final static long keepAliveTime = 60;

    private final ExecutorService theadPool;
    private final RequestHandler requestHandler = new RequestHandler();
    private CommonSerializer serializer;

    public SocketServer(String host, int port) {
        this(host, port, DEFAULT_SERIALIZER);
    }

    public SocketServer(String host, int port, Integer serializerCode) {
        this.host = host;
        this.port = port;

        this.serviceProvider = new ServiceProviderImpl();
        this.serviceRegistry = new NacosServiceRegistry();
        this.serializer = CommonSerializer.getByCode(serializerCode);
        this.theadPool = ThreadPoolFactory.createDefaultTheadPool("SocketServer");
        scanServices();
    }

    @Override
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket()) {
            logger.info("服务器正在启动...");
            serverSocket.bind(new InetSocketAddress(host, port));
            Socket socket;
            while ((socket = serverSocket.accept()) != null) {
                logger.info("客户端连接！IP为：" + socket.getInetAddress());
                this.theadPool.submit(new RequestHandlerThread(socket, requestHandler, serviceProvider, serializer));
//                this.threadPool.execute(new RequestHandlerThread(socket, serviceProvider));
            }
        } catch (IOException e) {
            logger.error("连接时有错误发生！");
        }
    }

}
