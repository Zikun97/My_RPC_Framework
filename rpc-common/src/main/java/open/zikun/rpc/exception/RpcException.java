package open.zikun.rpc.exception;

import open.zikun.rpc.enumeration.RpcError;


/**
 * 继承RuntimeException的RpcException
 */
public class RpcException extends RuntimeException {

    public RpcException(RpcError error, String detail) {
        super(error.getMessage() + ": " + detail);
    }

    public RpcException(RpcError error) {
        super(error.getMessage());
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }
}
