###  外设支持
1.两个网卡(一个接入外网，一个是连接虚拟机桥接网卡)；一张硬盘剩余至少200GB；
2.usb口转接网卡口（简称USB网卡）
3.连接示意图：

![Untitled Diagram](C:\Users\xiaohong1.guo\Desktop\VOD在线测试环境搭建.assets\Untitled Diagram.png)

### 搭建过程
##### 1.安装virtualBox
安装教程：https://blog.csdn.net/qq3399013670/article/details/81937412
下载地址：https://www.virtualbox.org/wiki/Downloads

##### 2.将服务器导入virtualBox
打开virtualBox->管理->导入虚拟电脑->选择hbbtvtest的所在位置

![1](C:\Users\xiaohong1.guo\Desktop\VOD在线测试环境搭建.assets\1.png)


##### 3.主机网络管理配置

![2](C:\Users\xiaohong1.guo\Desktop\VOD在线测试环境搭建.assets\2.png)

##### 4.服务器hbbtvtest设置
选择服务器hbbtvtest->设置->网络->
网卡1:不动
网卡2：勾选启用网络连接；连接方式设置为仅主机(Host-Only)网络；界面名称：virtualBox Host-Only Ethernet Adapter
网卡3：勾选启用网络连接；连接方式设置为桥接网卡；界面名称：ASTX AX88772C usb2.0 头 fast Ethernet Adapter(实际上是usb转网卡的别称)
##### 5.本地电脑快捷至服务器
计算机->网络位置映射->\\192.168.50.10\hbbtvtest\
得到一个快捷方式，可以快速访问服务器。访问时有可能会询问凭证
username:hbbtvtest
password:hbbtvtest
##### 6.本地网络环境修改
进入【网络与共享中心】，选择【更改适配器设置】，修改虚拟机网卡3对应的桥接网卡，属性中只勾选VirtualBox NDIS6 Bridged Networking Briver,其他都不勾选

![3](C:\Users\xiaohong1.guo\Desktop\VOD在线测试环境搭建.assets\3.png)

网络配置ok后，就可以点击virtualBox的【启动】图标来启动hbbtvtest服务器，输入用户名密码登录。
在本地电脑中通过smb网络，输入\\192.168.50.10，进入虚拟机的hbbtv目录，点击进入到：\\192.168.50.10\hbbtv\hbbtv_testsuite，在该目录下创建文件夹tencent，再在tencent目录下创建子目录x，将测试码流拷贝到该路径下，例如：存在的路径为./tencent/x/test.mp4；即腾讯视频访问的文件url为：http://192.168.50.10/x/test.mp4

##### 7.验证搭建结果
PC浏览器上访问http://192.168.50.10/x/test.html，出现响应的资源
TV通过串口线连接PC
secureCRT下访问：am start -a android.intent.action.VIEW -d http://192.168.50.10/x/test.html，出现响应的资源