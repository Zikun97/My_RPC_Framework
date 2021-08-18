package open.zikun.rpc;

import open.zikun.rpc.api.HelloObject;
import open.zikun.rpc.api.HelloService;
import open.zikun.rpc.transport.RpcClientProxy;
import open.zikun.rpc.transport.socket.client.SocketClient;

/**
 * 测试客户端启动器，socket测试
 */
public class SocketTestClient {
    public static void main(String[] args) {
        SocketClient socketClient = new SocketClient();
        RpcClientProxy proxy = new RpcClientProxy(socketClient);
        HelloService helloService = proxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "This is a message");
        for(int i = 0; i < 20; i ++) {
            String res = helloService.hello(object);
            System.out.println(res);
        }
    }
}
