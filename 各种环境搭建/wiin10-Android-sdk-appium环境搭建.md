#### 第一步JDK安装：

##### 安装JDK，本机如果带有1.7及以上版本的，则可忽略此安装步骤。

地址： https://www.oracle.com/technetwork/java/javase/downloads/index.html 


##### 配置环境变量（此处我是安装到D盘）：
新建 JAVA_HOME 变量          
```
JAVA_HOME  
D:\Program Files\Java\j2sdk1.5.0;  （JDK的安装路径） 
```
寻找 Path 变量→编辑            
```
PATH              
%JAVA_HOME%\bin; （注意原来Path的变量值末尾有没有;号，如果没有，先输入；号再输入上面的代码）
```
新建 CLASSPATH 变量         
```
CLASSPATH  .\;%JAVA_HOME%\lib\dt.jar;\%JAVA_HOME%\lib\tools.jar;%JAVA_HOME%\bin;（注意最前面有一点）
```

##### 检验是否安装成功：
```
cmd: java -version
```
出现如下即是成功：
java version "1.8.0_201"
Java(TM) SE Runtime Environment (build 1.8.0_201-b09)
Java HotSpot(TM) 64-Bit Server VM (build 25.201-b09, mixed mode)


#### 第二步Android SDK安装：
##### 安装
将压缩文件android-sdk_r24.4.1-windows.zip解压（我解压到D盘）
环境变量新建ANDROID_HOME，在我的电脑新建环境变量信息如下：
```
ANDROID_HOME 
D:\andriod\android-sdk-windows
```

双击解压目录后的SDK Manager（安装过程比较漫长）
必装：Tools文件夹下Android SDK Platform-tools和Android SDK Build-tools，Android SDK Tools可以选择是否更新，建议网速OK直接更新。
镜像：随便选个版本，喜欢啥版本选啥版本（本人安装的是 Android 6.0.0 Andriod8.0.0 Andriod9.0.0）

##### 环境变量
配置Path环境变量中添加ANDROID_HOME、tools、platform-tools、build-tools目录。保存修改。

```
%ANDROID_HOME%;%ANDROID_HOME%\tools;%ANDROID_HOME%\platform-tools;%ANDROID_HOME%\build-tools;
```


##### 检验：
```
cmd:adb connect "设备的ip:5555"   一些电脑不需要加端口5555就可以连上，一些连不上就得加上端口号。
```

#### 第三步：
地址 ：https://github.com/appium/appium-desktop/releases/tag/v1.2.1
双击安装appium-desktop-setup-1.8.2.exe，一直等待


