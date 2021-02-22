# 基于 Netty 4.1.37.Final 版本的“源码”分析

## 涉及内容,包括但不限于：
+ 1、JAVA - BIO
+ 2、JAVA - NIO原理，以及JAVA-IO模型，reactor模型，select(),poll(),epoll()模型
+ 3、netty整个启动流程分析
+ 4、netty自定义的各种类，比如selector
+ 5、reactor模式在netty中的应用
+ 6、pipeline、handler执行逻辑
+ 7、netty线程模型
+ 8、服务端接受客户端连接、注册读写事件、等等等
## 研究netty源码前提
+ 1、尽量精通 Linux select(),poll(),epoll()模型，**必须精通** JAVA-NIO 网络编程
+ 2、深入理解《Scalable IO in Java》一书
+ 3、熟悉Netty的设计模式，熟练使用Netty的API
+ 4、本部作品就是一部netty**源码字典**，目的就是帮助想要精通netty源码的朋友尽快精通netty。