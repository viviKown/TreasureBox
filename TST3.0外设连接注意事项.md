### 预防串口打印不出信息
| 机芯   | 串口线                                                       | secureCRT设置                             |
| ------ | ------------------------------------------------------------ | ----------------------------------------- |
| T962   | HDM---HDMI                                                   | 协议Seria、COM4、波特率115200、流控制不选 |
| MS838A | USB--USB,且蓝色头USB接机芯板，白色USB头接主机口；注意机芯板有两个USB口，蓝色头应接第一个USB口 | 协议Seria、COM4、波特率115200、流控制不选 |


### 其他外设注意事项
##### 采集卡
确认蓝色指示灯不闪，持续稳亮
##### 测试精灵
小板电源线接到测试精灵的黄色插排（指示灯稳亮才是通电正常）上，才可以有断电上电功能，接上后，小板指示灯忽闪忽灭。日常调试，为了方便，可以直接将小板电源直接接到电源插排而非黄色插排上，方便通电调试
##### 遥控器与遥控头
遥控头指示灯是否亮
遥控器分为通用遥控器与东芝遥控器，一旦用通用遥控器遥控机芯板没有反应，就要想到是遥控器货不对板
##### AV线
用途一：接码流卡，直头接码流卡，曲头接机芯板
用途二：接墙上信号，直头接墙，曲头接机芯板

##### 22293
独享：一头接22293 USB，一头接电脑主机USB
共享：USB不用接，一个HDMI线从22293背部接到分配器，分配器out口连接一个HDMI线，该HDMI线连接至电脑
