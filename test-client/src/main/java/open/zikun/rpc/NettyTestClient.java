package open.zikun.rpc;

import open.zikun.rpc.api.HelloObject;
import open.zikun.rpc.api.HelloService;
import open.zikun.rpc.transport.netty.client.NettyClient;
import open.zikun.rpc.transport.netty.client.NettyClientProxy;

public class NettyTestClient {
    public static void main(String[] args) {
        // 初始化client
        NettyClient client = new NettyClient();
        NettyClientProxy clientProxy = new NettyClientProxy(client);

        HelloService helloService = clientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(14, "5,15");
        String res = helloService.hello(object);
        System.out.println(res);


    }
}
