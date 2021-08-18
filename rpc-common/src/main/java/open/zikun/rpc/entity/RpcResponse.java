package open.zikun.rpc.entity;

import java.io.Serializable;

import open.zikun.rpc.enumeration.ResponseCode;


/**
 * rpc 响应实体
 * @param <T> 接口返回数据类型
 */
public class RpcResponse<T> implements Serializable {

    // 相应对应的请求号
    private String requestId;

    private Integer statusCode;

    private String message;

    private T data;

    public RpcResponse() {
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static <T> RpcResponse<T> success(T data, String requestId) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setRequestId(requestId);
        response.setData(data);
        response.setStatusCode(ResponseCode.SUCCESS.getCode());
        response.setMessage(ResponseCode.SUCCESS.getMessage());
        return response;
    }

    public static <T> RpcResponse<T> fail(ResponseCode responseCode) {
        RpcResponse<T> response = new RpcResponse<>();
//        response.setRequestId(requestId);
        response.setStatusCode(responseCode.getCode());
        response.setMessage(responseCode.getMessage());
        return response;
    }


}
