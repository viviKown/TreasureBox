1.安卓 TV 应用检索主要实现显示 TV 所有应用列表、动态显示选中应用
的内存信息、 CPU 信息，显示应用权限信息、应用存储等功能。 

apk安装至电视方法，PC与TV通过串口连接

安装指令：

130|BeyondTV:/ # pm install /data/local/tmp/demo.apk                           

Success



卸载指令

BeyondTV:/ # pm uninstall com.tcl.demo   

 

adb shell下执行monkey压测

执行100次随机用户模拟操作：monkey –p com.jianke.doctor –v 100

导出日志：monkey -p com.junte -v 100 >d:\test.txt

 

从日志中搜索：exception、crash、ANR 这3个关键词。

monkey的错误一般都是这3个，异常，崩溃，程序终止。

找到错误了日志后面会有原因的，给开发看就是了