win10下python2与python3并存配置
#### 1.分别在D盘下安装python2.7和python3.7  下载链接https://www.python.org/
#### 2.安装完毕分别在D盘下存在Python27和Python37文件夹
#### 3.配置环境
  计算机->属性->高级系统配置->环境变量-Path中分别添加
  D:\Python27;D:\Python27\Script
  D:\Python37;D:\Python37\Script
  
##### 出现问题：
  此时在命令行中输入python验证是否安装成功会提示，python不是内部命令，原因是Python27与Python37都存在一个python.exe，系统无法识别具体是哪一个
或者能识别，但默认是使用python3；
  导致另一个问题，pip无法使用，python需要安装许多包，pip install安装包时，系统可能无法识别具体是哪一个版本的python或者默认安装到Python37中，
  导致使用不便
  
#### 4.修改python.exe标识
未解决上述问题，分别修改；
  D:\Python27下的python.exe为python2.exe;
  D:\Python37下的python.exe为python3.exe；
  
#### 5.命令行验证
win+r;在命令行中分别输入python2，python3;出现对应的版本号

#### 6.注意pip安装包时，若想在python2.x环境上安装某个包，则使用pip2 install xxxx;否则使用pip3 install xxx
