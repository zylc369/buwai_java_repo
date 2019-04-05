# Buwai's Java Repo
## 前言

不歪的Java库，沉淀了代码编写过程中封装的类和方法。



## 项目：buwai-jar-project

### commons模块

**描述**

存放公共功能。



**坐标**

``` xml
<groupId>buwai.commons</groupId>
<artifactId>commons</artifactId>
```



**功能详情**

- 日志相关：
  - LogTLS：线程局部存储封装
  - TraceUtils：存放和取出日志跟踪信息。TraceId, TreadId
- 线程相关：
  - MDCThread：MDC线程，创建的线程继承父线程的上下文，如果父线程设置了TraceId，那么子线程打印时也会打印出来。
  - MDCThreadPoolTaskExecutor：MDC线程池，继承父线程的上下文



### db模块

**描述**

数据库操作代码。



**坐标**

```xml
<groupId>buwai.commons</groupId>
<artifactId>db</artifactId>
```



**功能详情**

- MySQLConnect：JDBC方式的MySQL数据库连接代码
- DBHelper：数据库操作帮助类



### network-proxy模块

**描述**

获取网络代理。通常，大量爬取数据时如果不更换IP地址，那么当前IP访问频率较高会被认为属于异常访问而被封禁一段时间，那么此时如果有网络代理即可继续爬取数据绕过这个限制。这个库就是为了爬取代理IP而编写的。



**坐标**

```xml
<groupId>buwai-commons</groupId>
<artifactId>network-proxy</artifactId>
```



**功能详情**

- NetworkProxyService：获取代理IP的服务