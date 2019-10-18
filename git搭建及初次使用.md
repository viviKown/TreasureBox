####  安装下载
根据系统版本下载安装，一直Next默认就好了，如果需要设置就要仔细读一下安装界面上的选项。
地址：https://git-scm.com/downloads
安装成功后，鼠标右键就可以看到Git 选项
```
Git Bash：Unix与Linux风格的命令行，使用最多，推荐最多
```
#### 验证
```
cmd ：git
```
出现一堆git命令即安装成功
如果提示git非内部命令，则在环境变量Path中加上git的安装路径
我的是安装在D盘：
```
D:\Git\cmd
```
#### 初次使用
既然已经在系统上安装了 Git，你会想要做几件事来定制你的 Git 环境。 每台计算机上只需要配置一次，程序升级时会保留配置信息。 你可以在任何时候再次通过运行命令来修改它们。
##### 配置用户信息
```
$ git config --global user.name "John Doe"
$ git config --global user.email johndoe@example.com
```
##### 检查配置信息
```
$ git config --list
user.name=John Doe
user.email=johndoe@example.com
color.status=auto
color.branch=auto
color.interactive=auto
color.diff=auto
...
```
##### 检查特定的变量
```
$ git config user.name
John Doe
```
#### 初始化本地库
任意位置新建一个文件夹，在该文件夹内，右键打开git bash here命令框
输入
```
$ git init
```
#### 拉取github项目
```
$ git clone github URL

eg.
$ git clone https://github.com/viviKown/TreasureBox.git
```
#### 查看分支状态
```
$ git status
```
#### 上传本地代码或文件至远程仓库
注意每次上传前尽量先下拉一遍，有可能团队中其他人更新了代码
```
$ git status
$ git add filename/ if all file use .
$ git commit -m "log message"
$ git push origin master
```
##### 注意
工程中含有中文命名的文件是不能被add 成功的，原因git默认是不能识别中文的。需要在终端修改能识别中文。
```
$ git config --global core.quotepath false
```
core.quotepath设为false的话，就不会对0x80以上的字符进行quote，中文显示正常。，之后可以正常的git add 了，挨个add之后即可，最后commit之后再push。
#### 撤销add操作
如果发生错误可以撤销add操作
```
$ git reset HEAD filename //撤销add某个文件
$ git reset HEAD   //撤销所有
```
#### 下拉更新
Git中从远程的分支获取最新的版本到本地有这样2个命令
fetch：相当于是从远程获取最新版本到本地，不会自动merge
```
$ git fetch origin master //首先从远程的origin的master主分支下载最新的版本到origin/master分支上 
$ git log -p master..origin/master //然后比较本地的master分支和origin/master分支的差别
$ git merge origin/master  //将拉取下来的最新内容合并到当前所在的分支中
```
或者

```
$ git pull origin master //相当于git fetch 和 git merge
```

#### 查看分支
```
$ git branch -a  //查看远程分支
结果：	* master
  	remotes/origin/HEAD -> origin/master
  	remotes/origin/master

$ git branch //查看本地分支
结果：* master
```
#### 切换分支
```
git checkout 分支名
```





