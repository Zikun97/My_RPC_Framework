package open.zikun.rpc;

import open.zikun.rpc.annotation.Service;
import open.zikun.rpc.api.HelloObject;
import open.zikun.rpc.api.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class HelloServiceImpl implements HelloService {
    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);
    @Override
    public String hello(HelloObject hb) {
        logger.info("接收到：{}", hb.getMessage());
        return "这是调用的返回值，id=" + hb.getId();
    }
}
