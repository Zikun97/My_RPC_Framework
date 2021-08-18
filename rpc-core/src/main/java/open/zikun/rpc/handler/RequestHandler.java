package open.zikun.rpc.handler;

import open.zikun.rpc.entity.RpcRequest;
import open.zikun.rpc.entity.RpcResponse;
import open.zikun.rpc.enumeration.ResponseCode;
import open.zikun.rpc.enumeration.RpcError;
import open.zikun.rpc.provider.ServiceProvider;
import open.zikun.rpc.provider.ServiceProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 进行过程调用的处理器
 */
public class RequestHandler {

    public static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    public static final ServiceProvider serviceProvider;

    static {
        serviceProvider = new ServiceProviderImpl();
    }

    public Object handle(RpcRequest request) {
        Object result = null;
        Object service;
        service = serviceProvider.getServiceProvider(request.getInterfaceName());
        try {
            result = invokeTargetService(request, service);
            logger.info("调用{}：{}成功", request.getInterfaceName(), request.getMethodName());
        } catch (InvocationTargetException | IllegalAccessException exception) {
            logger.error("调用{}：{}失败！", request.getInterfaceName(), request.getMethodName());
            exception.printStackTrace();
        }

        return result;
    }


    private Object invokeTargetService(RpcRequest request, Object service) throws InvocationTargetException, IllegalAccessException {
        Method method;
        try {
            method = service.getClass().getMethod(request.getMethodName(), request.getParamTypes());
        } catch (NoSuchMethodException e) {
            return RpcResponse.fail(ResponseCode.METHOD_NOT_FOUND);
        }
        return method.invoke(service, request.getParams());
    }
}
