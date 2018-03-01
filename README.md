# P2PChat
P2P局域网聊天APP，目前实现了局域网设备的扫描连接，局域网聊天以及文件的互传

### 总体概要
移动设备通过广播的形式搜索同一局域网一定范围中其他的设备的存在，发送连接请求，得到同意后便建立P2P连接，记录下对方的IP地址，从而通过IP进行TCP的连接，实现聊天的信息发送。为实现网络聊天的功能，采用Java Socket编程，每台Android移动设备既是客户端又是服务端，同时具备发送和接受消息或文件的功能。

### 模块划分
由于每台设备同时作为服务端和客户端，因此，大体上功能都是一样的，即不用分别设计客户端与服务端的不同界面与功能模块。本系统的功能模块大致可分为以下几个：
（1）登陆模块（2）设备扫描连接模块（3）聊天模块（4）文件传输模块

### 程序截图
![](http://otpesi023.bkt.clouddn.com/P2P10.jpg)
![](http://otpesi023.bkt.clouddn.com/P2P3.jpg)
![](http://otpesi023.bkt.clouddn.com/P2P5.jpg)
![](http://otpesi023.bkt.clouddn.com/P2P6.jpg)
![](http://otpesi023.bkt.clouddn.com/P2P7.jpg)
