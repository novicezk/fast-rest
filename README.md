# 简介
Fast Rest是个参考Spring Boot的轻量级Java框架，不依赖任何现有框架，本身实现的Spring和JPA等功能，可极快创建一个json数据传输的REST HTTP或HTTPS服务。

# 特点
1. 使用注解代替xml文件
2. 启动方便，不需要tomcat等服务器，在java环境下即可运行
3. 框架封装较少，可简便定位问题
4. 源代码可修改，使其更为契合项目开发

# 框架使用
1. 导入本地maven仓库，2种方式：
+ 下载该项目，git clone或下载zip；进入fast-rest项目，执行`mvn install`
+ 下载[本项目jar包](https://homolo.top/file/download?fileName=jar/fast-rest-1.3-jar-with-dependencies.jar)，进入下载目录，执行`mvn install:install-file -Dfile=fast-rest-1.3-jar-with-dependencies.jar -DgroupId=com.zhukai.framework -DartifactId=fast-rest -Dversion=1.3 -Dpackaging=jar`
2. 新建maven项目，添加该框架jar包依赖，示例pom文件：
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
------com
--------Application.java
--------HelloController.java
```
Application.java(项目启动入口，必须放在一个包下，其他类需要放在它的同级或子级包下)
```java
package com;

import com.zhukai.framework.fast.rest.FastRestApplication;

public class Application {
    public static void main(String[] args) {
        FastRestApplication.run(Application.class);
    }
}
```
HelloController.java
```java
package com;

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
+ 开发工具启动`com.Application` 
+ 使用assembly插件打成jar包，`java -jar`执行，见[使用文档](https://github.com/zhukai-git/fast-rest/wiki)
   
# 更多说明请阅读[使用文档](https://github.com/zhukai-git/fast-rest/wiki)
