### 图文详见链接
https://www.cnblogs.com/rocedu/p/6012545.html

### 安装过程中可能遇到一个问题
VT-x AMD-V 硬件加速在您的系统中不可用。您的 64-位虚拟机将无法检测到 64-位处理器，从而无法启动

```
解决方法

1.进入系统BIOS，可参考链接：https://www.cnblogs.com/guoyinghome/p/11199479.html

2.Advancd找到CPU status/ CPU Configuration( 不同的电脑可能叫法不一样)进入> Intel Virtualization Technology，设置为Enabled 。

3. F10.保存退出，重启电脑，再次启动虚拟机。

```
