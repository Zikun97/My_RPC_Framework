package open.zikun.rpc.transport.netty.client;

import open.zikun.rpc.entity.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class UnprocessedRequest {

    private static final Logger logger = LoggerFactory.getLogger(UnprocessedRequest.class);

    private static ConcurrentHashMap<String, CompletableFuture<RpcResponse>> unprocessedFutures
            = new ConcurrentHashMap<>();


    public void put(String requestId, CompletableFuture<RpcResponse> responseCompletableFuture) {
        unprocessedFutures.put(requestId, responseCompletableFuture);
    }

    public void remove(String requestId) {
        unprocessedFutures.remove(requestId);
    }

    public void complete(RpcResponse response) {
        CompletableFuture<RpcResponse> future = unprocessedFutures.remove(response.getRequestId());
        if (null != future) {
            future.complete(response);
        } else {
            throw new IllegalStateException();
        }
    }

}
