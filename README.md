# 简介
Integration是个参考Spring Boot的轻量级java框架，不依赖任何现有框架，本身实现的spring和jpa等功能，可极快创建一个json数据传输的rest服务。

# 特点
1. 使用注解代替xml文件
2. 启动方便，不需要tomcat等服务器，在java环境下即可运行
3. 框架封装较少，可简便定位问题
4. 源代码可修改，使其更为契合项目开发

# 快速使用
1. [下载源码](https://github.com/zhukai-git/integration) 
2. 进入integration项目，执行mvn install
3. 新建maven项目，添加该框架jar包依赖，示例pom文件：
```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.zhukai.project</groupId>
    <artifactId>integration-demo</artifactId>
    <version>1.1</version>
    <dependencies>
        <dependency>
            <groupId>com.zhukai.framework</groupId>
            <artifactId>spring-integration</artifactId>
            <version>1.1</version>
        </dependency>
    </dependencies>
</project>
```
4. 最简单的hello,world。
文件结构：
![](https://homolo.top/demo_project.png)
Application.java
```$xslt
package com;

import com.zhukai.framework.spring.integration.SpringIntegration;

public class Application {
    public static void main(String[] args) {
        SpringIntegration.run(Application.class);
    }
}
```
HelloController.java
```$xslt
package com;

import com.zhukai.framework.spring.integration.annotation.web.RequestMapping;
import com.zhukai.framework.spring.integration.annotation.web.RestController;

@RestController
public class HelloController {
    
    @RequestMapping("hello")
    public String hello() {
        return "hello,world";
    }
}
```
5. 启动项目，访问[http://localhost:8080/hello](http://localhost:8080/hello)，出现hello,world。两种启动方式：
   + 开发工具启动com.Application
   + 使用assembly插件打成jar包，java -jar执行，见[开发文档](https://github.com/zhukai-git/integration/wiki/%E5%BC%80%E5%8F%91%E6%96%87%E6%A1%A3)
   
# 更多说明请阅读[开发文档](https://github.com/zhukai-git/integration/wiki/%E5%BC%80%E5%8F%91%E6%96%87%E6%A1%A3)