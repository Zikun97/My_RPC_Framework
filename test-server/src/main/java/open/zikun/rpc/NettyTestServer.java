package open.zikun.rpc;


import open.zikun.rpc.annotation.ServiceScan;
import open.zikun.rpc.api.HelloService;
import open.zikun.rpc.provider.ServiceProviderImpl;
import open.zikun.rpc.provider.ServiceProvider;
import open.zikun.rpc.serializer.CommonSerializer;
import open.zikun.rpc.serializer.ProtobufSerializer;
import open.zikun.rpc.transport.RpcServer;
import open.zikun.rpc.transport.netty.server.NettyServer;

@ServiceScan
public class NettyTestServer {

    public static void main(String[] args) {

        RpcServer server = new NettyServer("127.0.0.1", 5555, CommonSerializer.DEFAULT_SERIALIZER);
        server.start();


    }
}
