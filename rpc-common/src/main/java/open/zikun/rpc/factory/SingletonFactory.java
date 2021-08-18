package open.zikun.rpc.factory;


import jdk.nashorn.internal.runtime.regexp.JoniRegExp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.InstanceNotFoundException;
import java.util.HashMap;
import java.util.Map;

/**
 * 单例工厂
 * （zikun）生成service实例的单例工厂，确保只有一个service实例生成。
 */
public class SingletonFactory {

    private static Map<Class, Object> objectMap = new HashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(SingletonFactory.class);

    private SingletonFactory(){}

    public static <T> T getInstance(Class<T> clazz) {
        Object instance = objectMap.get(clazz);
        if (instance == null) {
            synchronized (clazz) {
                if (instance == null) {
                    try{
                        instance = clazz.newInstance();
                        objectMap.put(clazz, instance);
                    } catch (IllegalAccessException | InstantiationException e) {
                        logger.error("获取服务实例失败！");
                        throw new RuntimeException(e.getMessage(), e);
                    }
                }
            }
        }
        return clazz.cast(instance);
    }

}
