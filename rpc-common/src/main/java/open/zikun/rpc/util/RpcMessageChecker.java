package open.zikun.rpc.util;

import open.zikun.rpc.entity.RpcRequest;
import open.zikun.rpc.entity.RpcResponse;
import open.zikun.rpc.enumeration.ResponseCode;
import open.zikun.rpc.enumeration.RpcError;
import open.zikun.rpc.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  检查请求和相应的格式
 */
public class RpcMessageChecker {
    public static final String INTERFACE_NAME = "InterfaceName";
    private static final Logger logger = LoggerFactory.getLogger(RpcMessageChecker.class);


    public RpcMessageChecker() {
    }

    public static void check(RpcRequest request, RpcResponse<?> response) {
        if (response == null) {
            logger.error(INTERFACE_NAME + ": {}调用失败！", request.getMethodName());
            throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE,
                    INTERFACE_NAME + ": " + request.getMethodName());
        }
        if (!request.getRequestId().equals(response.getRequestId())) {
            logger.error(INTERFACE_NAME + ": 响应与请求不匹配！");
            throw new RpcException(RpcError.RESPONSE_NOT_MATCH,
                    INTERFACE_NAME + ": " + request.getMethodName());
        }
        if (response.getStatusCode() == null || !response.getStatusCode().equals(ResponseCode.SUCCESS.getCode())) {
            logger.error(INTERFACE_NAME + ": {}调用失败！", request.getMethodName());
            throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE,
                    INTERFACE_NAME + ": " + request.getMethodName());
        }
    }
}
