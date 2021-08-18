package open.zikun.rpc.transport;

import open.zikun.rpc.annotation.Service;
import open.zikun.rpc.annotation.ServiceScan;
import open.zikun.rpc.enumeration.RpcError;
import open.zikun.rpc.exception.RpcException;
import open.zikun.rpc.provider.ServiceProvider;
import open.zikun.rpc.registry.ServiceRegistry;
import open.zikun.rpc.util.ReflectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Set;

public abstract class AbstractRpcServer implements RpcServer {
    protected Logger logger = LoggerFactory.getLogger(AbstractRpcServer.class);

    protected String host;
    protected int port;

    protected ServiceRegistry serviceRegistry;
    protected ServiceProvider serviceProvider;

    public void scanServices() {
        String mainClassName = ReflectUtil.getStackTrace();
        Class<?> startClass;
        try {
            startClass = Class.forName(mainClassName);
            if (!startClass.isAnnotationPresent(ServiceScan.class)) {
                logger.error("启动类缺少 @ScanService注解");
                throw new RpcException(RpcError.SERVER_SCAN_PACKAGE_NOT_FOUND);
            }
        } catch (ClassNotFoundException e) {
            logger.error("出现未知错误：", e);
            throw new RpcException(RpcError.UNKNOW_ERROR);
        }
        String basePackage = startClass.getAnnotation(ServiceScan.class).value();
        if ("".equals(basePackage)) {
            basePackage = mainClassName.substring(0, mainClassName.lastIndexOf('.'));
        }
        Set<Class<?>> classSet = ReflectUtil.getClasses(basePackage);
        for (Class<?> clazz : classSet) {
            if (clazz.isAnnotationPresent(Service.class)) {
                String serviceName = clazz.getAnnotation(Service.class).name();
                Object object;
                try {
                    object = clazz.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    logger.error("创建[" + clazz.getName() + "]时发生错误！");
                    continue;
                }
                if ("".equals(serviceName)) {
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for (Class<?> oneInterface : interfaces) {
                        publishServer(object, oneInterface.getCanonicalName());
                    }
                }
            }
        }
    }

    @Override
    public <T> void publishServer(Object service, String serviceName) {
        serviceProvider.addServiceProvider(service);
        serviceRegistry.register(serviceName, new InetSocketAddress(host, port));
    }
}
