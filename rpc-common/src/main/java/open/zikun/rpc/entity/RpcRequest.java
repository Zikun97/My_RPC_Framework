package open.zikun.rpc.entity;


import java.io.Serializable;

/**
 * rpc请求实体
 */
public class RpcRequest implements Serializable {

    // 请求号
    private String requestId;

    // 调用接口的名称
    private String interfaceName;

    // 调用方法的名称
    private String methodName;

    // 调用方法的参数
    private Object[] params;

    // 调用方法的参数类型
    private Class<?>[] paramTypes;

    // 是否为心跳包
    private Boolean heartBeat;


    public RpcRequest() {
    }

    public RpcRequest(String requestId, String interfaceName, String methodName,
                      Object[] params, Class<?>[] paramTypes, Boolean heartBeat) {
        this.requestId = requestId;
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.params = params;
        this.paramTypes = paramTypes;
        this.heartBeat = heartBeat;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    public Class<?>[] getParamTypes() {
        return paramTypes;
    }

    public void setParamTypes(Class<?>[] paramTypes) {
        this.paramTypes = paramTypes;
    }

    public Boolean getHeartBeat() {
        return heartBeat;
    }

    public void setHeartBeat(Boolean heartBeat) {
        this.heartBeat = heartBeat;
    }
}
