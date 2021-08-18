package open.zikun.rpc.transport.socket.server;

import open.zikun.rpc.entity.RpcRequest;
import open.zikun.rpc.entity.RpcResponse;
import open.zikun.rpc.handler.RequestHandler;
import open.zikun.rpc.provider.ServiceProvider;
import open.zikun.rpc.serializer.CommonSerializer;
import open.zikun.rpc.transport.socket.util.ObjectReader;
import open.zikun.rpc.transport.socket.util.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

public class RequestHandlerThread implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandlerThread.class);

    private final Socket socket;
    private final RequestHandler requestHandler;
    private final ServiceProvider serviceRegistry;
    private final CommonSerializer serializer;

    public RequestHandlerThread(Socket socket, RequestHandler requestHandler, ServiceProvider serviceRegistry, CommonSerializer commonSerializer) {
        this.socket = socket;
        this.serviceRegistry = serviceRegistry;
        this.requestHandler = requestHandler;
        this.serializer = commonSerializer;
    }

    @Override
    public void run() {

        try (InputStream inputStream = this.socket.getInputStream();
             OutputStream outputStream  = this.socket.getOutputStream()) {
            RpcRequest rpcRequest = (RpcRequest) ObjectReader.readObject(inputStream);
            Object resultObject = requestHandler.handle(rpcRequest);
            RpcResponse<Object> response = RpcResponse.success(resultObject, rpcRequest.getRequestId());
            ObjectWriter.writeObject(outputStream, response, serializer);
        } catch (IOException e){
            logger.error("远程调用或发送时错误！");
        }

    }
}
