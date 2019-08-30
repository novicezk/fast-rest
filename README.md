# 简介
Fast Rest是个使用javase实现的后台开发框架demo，包括了tomcat、ioc、aop、jpa、transactional、spring mvc、计划任务、事件、数据库连接池等基础功能，适合java初学者接触，以了解一些常用技术的实现。当然，这个项目是刚毕业那段时间做的，有些技术是想当然实现的，跟官方实现还是有很多不同。有兴趣的欢迎一起学习交流！～

# demo体验
1. 下载该项目，新建maven项目，把本项目作为模块引入
2. 添加该框架jar包依赖，示例pom文件：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.zhukai.test</groupId>
    <artifactId>demo</artifactId>
    <version>1.3</version>
    <dependencies>
        <dependency>
            <groupId>com.zhukai.framework</groupId>
            <artifactId>fast-rest</artifactId>
            <version>1.3</version>
        </dependency>
    </dependencies>
</project>
```
3. 编码

文件结构：
```$xslt
src
--main
----java
------com.zhukai.test.demo
--------Application.java
--------HelloController.java
```
Application.java(项目启动入口，必须放在一个包下，其他类需要放在它的同级或子级包下)
```java
package com.zhukai.test.demo;

import com.zhukai.framework.fast.rest.FastRestApplication;

public class Application {
    public static void main(String[] args) {
        FastRestApplication.run(Application.class);
    }
}
```
HelloController.java
```java
package com.zhukai.test.demo;

import com.zhukai.framework.fast.rest.annotation.web.RequestMapping;
import com.zhukai.framework.fast.rest.annotation.web.RestController;

@RestController
public class HelloController {
    
    @RequestMapping("/hello")
    public String hello() {
        return "hello,world";
    }
}
```
4. 启动项目，访问[http://localhost:8080/hello](http://localhost:8080/hello)，出现hello,world。两种启动方式：
+ 开发工具启动`com.zhukai.test.demo.Application` 
+ 使用assembly插件打成jar包，`java -jar`执行，见[使用文档](https://github.com/novicezk/fast-rest/wiki#user-content-1-java--jar命令启动项目)
   
# 更多说明请阅读[使用文档](https://github.com/novicezk/fast-rest/wiki)
