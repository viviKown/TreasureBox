**怎么实现一台Linux电脑连接另一台Linux电脑？**

首先查看是否安装ssh服务：systemctl status sshd.service

  启动服务：systemctl start sshd.service
  重启服务：systemctl restart sshd.service
  开机自启：systemctl enable sshd.service

若已经安装了ssh服务直接在终端输入：ssh username@IP（该IP是要连接的计算机ip）

如果没有安装，就在命令行输入：yum install openssh-server（这是ssh服务的安装命令）

**怎么实现SSH实现linux之间的免密码登陆拷贝文件？**

拷贝远程服务器的文件到本地：

 ```
 scp -r -P 端口号 用户名@IP地址:/usr/local/tomcat_airc/webapps/ /tmp/kyj/
 ```

拷贝本地文件到远程服务器：

```
scp -r  /tmp/kyj/sys.war 用户名@IP地址:/usr/local/tomcat_airc/webapps/
```

