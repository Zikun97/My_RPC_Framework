package open.zikun.rpc.enumeration;

/**
 * Rpc接口错误
 */
public enum RpcError {

    CLIENT_CONNECT_SERVER_FAILED("客户端连接服务器失败！"),
    ERROR_TYPE("格式错误"),
    FAILED_TO_CONNECT_TO_SERVICE_REGISTRY("连接服务注册器失败！"),
    REGISTER_SERVICE_FAILED("注册服务失败"),
    RESPONSE_NOT_MATCH("响应与请求号不匹配！"),
    SERIALIZER_NOT_FOUND("未找到序列化器！"),
    SERVICE_INVOCATION_FAILURE("服务调用失败"),
    SERVICE_NOT_FOUND("找不到相应的服务"),
    SERVICE_NOT_IMPLEMENT_ANY_INTERFACE("注册的服务未实现接口"),
    SERVER_SCAN_PACKAGE_NOT_FOUND("启动类ServiceScan缺失"),
    UNKNOW_ERROR("发生位置错误"),
    UNKNOWN_PROTOCOL("未知的协议包"),
    UNKNOWN_SERIALIZER("未识别的（反）序列化器");


    private final String message;

    RpcError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
