# integration
该框架参考spring-boot框架和tomcat，主要目的是为了学习框架的原理。目前只实现了基础的功能，还有很多不足和待完成的部分，
求帮忙提出意见。本人邮箱：15156851740@163.com

## 主要功能：
http服务器、spring-mvc的部分功能、spring的IOC和AOP基础、spring-data-jpa的部分功能。

## 框架使用(参见[demo项目](https://github.com/zhukai-git/integration-demo))：
1. 在项目下执行mvn clean install（导入maven库），然后新建maven项目，引入该框架jar包依赖
2. 或在com.zhukai.framework.spring.integration.demo/下直接开发

## 项目运行的两种方式：
1. 直接启动com.zhukai.framework.spring.integration.demo/TestApplication
2. 执行mvn assembly:assembly打包jar，然后java -jar运行target下spring-integration-{版本}-jar-with-dependencies.jar

访问[http://localhost:8080/hello](http://localhost:8080/hello)，出现"hello,world"说明启动正常

## 目前存在问题：
1. 暂时认为\r\n表示换行（适合LINUX，其他系统可能有问题）
2. 使用nio下载文件，可能会出现文件缺失问题
3. 上传文件，会缺失。。
4. 很多...

## 近期更新：
1. 增加文件上传下载支持
2. 添加@PathVariable注解
3. 添加log4j日志
4. 启动可配置使用nio或bio处理http请求
5. 添加spring schedule和session过时设置
6. 添加获取数据库连接等待机制
