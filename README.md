# jetcd-register-demo
自制etcd服务注册demo
* 运用：[jetcd](https://github.com/coreos/jetcd)
* 想法:
    1. 服务都监听etcd上名为testServer的key。
    2. 若: 
            服务监听到此key，意味着有其他服务在运行，继续监听；
            服务监听到此key不存在或被删除，注册自己到etcd，服务开始。
    3. 注意：当有多个服务同时监听到key的上述变化时，每个服务都要等待特定的等待时间后，
    再次检查key的情况，若还未被其他服务注册则注册自己

**Note: 因为刚刚初步了解etcd，此demo中肯定有不当的地方，仅供参考！**
