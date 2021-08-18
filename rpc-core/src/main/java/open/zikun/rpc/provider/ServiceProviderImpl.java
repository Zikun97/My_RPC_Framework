package open.zikun.rpc.provider;

import open.zikun.rpc.enumeration.RpcError;
import open.zikun.rpc.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class ServiceProviderImpl implements ServiceProvider {

    private final static Logger logger = LoggerFactory.getLogger(ServiceProviderImpl.class);

    private final static Map<String, Object> serviceMap = new ConcurrentHashMap<>();
    private final static Set<String> registeredService = ConcurrentHashMap.newKeySet();


    @Override
    public synchronized <T> void addServiceProvider(T service) {
        String serviceName = service.getClass().getCanonicalName();
        if (registeredService.contains(serviceName)) {
            return;
        }
        registeredService.add(serviceName);
        Class<?>[] interfaces = service.getClass().getInterfaces();
        if (interfaces.length == 0) {
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
        for (Class<?> i : interfaces) {
            serviceMap.put(i.getCanonicalName(), service);
        }
        logger.info("向接口：{}注册服务：{}", interfaces, serviceName);
    }

    @Override
    public synchronized Object getServiceProvider(String serviceName) {
        Object service = serviceMap.get(serviceName);
        if (service == null) {
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
        return service;
    }
}
