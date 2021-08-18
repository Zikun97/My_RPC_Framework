package open.zikun.rpc.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;
import open.zikun.rpc.enumeration.RpcError;
import open.zikun.rpc.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;

public class RandomLoadBalancer implements LoadBalancer{

    private static final Logger logger = LoggerFactory.getLogger(RandomLoadBalancer.class);

    @Override
    public Instance select(List<Instance> instances) {
        if (instances.size() < 1) {
            logger.error("服务器中没有该服务！");
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
        int index = new Random().nextInt(instances.size());
        logger.info("随机选取第[{}]个实例", index);
        return instances.get(index);
    }
}
