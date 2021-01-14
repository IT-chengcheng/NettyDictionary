# 基于 Netty 4.1.37.Final 的源码分析

## 涉及内容
+ 1、JAVA - BIO
+ 2、JAVA - NIO原理，以及JAVA-IO模型，reactor模型，select(),poll(),epoll()模型
+ 3、netty启动流程分析，pipeline、handler等
+ 4、netty自定义的各种类，比如selector
## 使用方式
+ 1、尽量精通 Linux select(),poll(),epoll()模型，**必须精通** JAVA-NIO 网络编程
+ 2、深入理解《Scalable IO in Java》一书
+ 3、编译运行代码，保证可以断点调试
+ 4、熟悉Netty的设计模式，熟悉Netty的API，然后就可以着手看源码了

## Netty Project
+ Netty is an asynchronous event-driven network application framework for rapid development of maintainable high performance protocol servers & clients.
## Links
* [Web Site](http://netty.io/)
* [Downloads](http://netty.io/downloads.html)
* [Documentation](http://netty.io/wiki/)
* [@netty_project](https://twitter.com/netty_project)
## How to build
+ For the detailed information about building and developing Netty, 
+ please visit [the developer guide](http://netty.io/wiki/developer-guide.html).  This page only gives very basic information.
+ You require the following to build Netty:
  * Latest stable [Oracle JDK 7](http://www.oracle.com/technetwork/java/)
  * Latest stable [Apache Maven](http://maven.apache.org/)
  * If you are on Linux, you need [additional development packages](http://netty.io/wiki/native-transports.html) installed on your system, because you'll build the native transport.
+ Note that this is build-time requirement.  JDK 5 (for 3.x) or 6 (for 4.0+) is enough to run your Netty-based application.
## Branches to look
+ Development of all versions takes place in each branch whose name is identical to `<majorVersion>.<minorVersion>`.  
+ For example, the development of 3.9 and 4.0 resides in [the branch '3.9'](https://github.com/netty/netty/tree/3.9) 
+ and [the branch '4.0'](https://github.com/netty/netty/tree/4.0) respectively.
## Usage with JDK 9
+ Netty can be used in modular JDK9 applications as a collection of automatic modules. The module names follow the
+ reverse-DNS style, and are derived from subproject names rather than root packages due to historical reasons. They
+ are listed below:
 * `io.netty.all`
 * `io.netty.buffer`
 * `io.netty.codec`
 * `io.netty.codec.dns`
 * `io.netty.codec.haproxy`
 * `io.netty.codec.http`
 * `io.netty.codec.http2`
 * `io.netty.codec.memcache`
 * `io.netty.codec.mqtt`
 * `io.netty.codec.redis`
 * `io.netty.codec.smtp`
 * `io.netty.codec.socks`
 * `io.netty.codec.stomp`
 * `io.netty.codec.xml`
 * `io.netty.common`
 * `io.netty.handler`
 * `io.netty.handler.proxy`
 * `io.netty.resolver`
 * `io.netty.resolver.dns`
 * `io.netty.transport`
 * `io.netty.transport.epoll` (`native` omitted - reserved keyword in Java)
 * `io.netty.transport.kqueue` (`native` omitted - reserved keyword in Java)
 * `io.netty.transport.unix.common` (`native` omitted - reserved keyword in Java)
 * `io.netty.transport.rxtx`
 * `io.netty.transport.sctp`
 * `io.netty.transport.udt`
+ Automatic modules do not provide any means to declare dependencies, so you need to list each used module separately in your `module-info` file.
