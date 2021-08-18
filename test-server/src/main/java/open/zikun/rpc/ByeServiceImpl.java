package open.zikun.rpc;

import open.zikun.rpc.annotation.Service;
import open.zikun.rpc.api.ByeService;

@Service
public class ByeServiceImpl implements ByeService {
    @Override
    public String bye(String name) {
        return "ByeServiceImpl: " + name + " bye !";
    }
}
