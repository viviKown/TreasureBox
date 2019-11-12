#### 录制视频：

TV与PC通过adb通信
```
win + R键打开cmd，输入

连接设备
adb connect TV'IP:5555

查看是否连接成功
adb devices  

输入命令录制视频：
adb shell screenrecord /xxx/usb/62CC9CCCCC9C9BBB/111.mp4

/mnt/usb/62CC9CCCCC9C9BBB/111.mp4是保存视频的路径

ctrl + c停止录屏，如果不执行该操作，有可能导致视频无内容

输入命令将生产的视频拷贝至/xxx/usb/62CC9CCCCC9C9BBB/路径下
adb pull /mnt/usb/62CC9CCCCC9C9BBB/111.mp4
```