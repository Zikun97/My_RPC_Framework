package open.zikun.rpc;

import open.zikun.rpc.annotation.ServiceScan;
import open.zikun.rpc.api.HelloService;
import open.zikun.rpc.provider.ServiceProviderImpl;
import open.zikun.rpc.provider.ServiceProvider;
import open.zikun.rpc.serializer.CommonSerializer;
import open.zikun.rpc.serializer.HessianSerializer;
import open.zikun.rpc.transport.RpcServer;
import open.zikun.rpc.transport.socket.server.SocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServiceScan
public class SocketTestServer {
    /**
     * 测试 服务器端启动入口，测试socket
     */



    public static void main(String[] args) {
        RpcServer server = new SocketServer("127.0.0.1", 9989, CommonSerializer.HESSIAN_SERIALIZER);
        server.start();
    }

}
