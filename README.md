# jetcd-register-demo
自制etcd服务注册demo
* 运用：[jetcd](https://github.com/coreos/jetcd)
* 想法:
    1. 服务都监听etcd上名为testServer的key。
    2. 若: 
            服务监听到此key，意味着有其他服务在运行，继续监听；
            服务监听到此key不存在或被删除，注册自己到etcd，服务开始。
   
* 注意：
    1. 在Eclipse下测试App、App2、App3时发现：当有多个服务同时监听到key的上述变化时，会同时执行注册和服务开始。即两个服务都在运行，而结点上却只有后写入的服务名。目前我用的解决办法为:检测到key变化时，每个服务都要sleep((${NodeNubmer}-1)\*20ms)后，再次检查key的情况，若还未被其他服务注册则注册自己。
    2. 在Vmware上模拟Ubuntu 16.04执行jar包时发现，并不需要设置sleep。不会发生上述冲突。详见branch test。

**Note: 因为刚刚初步了解etcd，此demo中肯定有不当的地方，仅供参考！**
