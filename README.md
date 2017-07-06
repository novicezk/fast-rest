# integration
该框架参考spring-boot框架，主要目的是为了学习框架的原理。目前只实现了基础的功能，还有很多不足和待完成的部分，
有问题和建议可以在issues中提出。

## 主要功能：
http服务器、spring-mvc的部分功能、spring的IOC和AOP基础、spring-data-jpa的部分功能。

## 框架使用(参见[demo项目](https://github.com/zhukai-git/integration-demo))：
1. 在项目下执行mvn clean install（导入maven库），然后新建maven项目，引入该框架jar包依赖
2. 或在com.zhukai.framework.spring.integration.demo/下直接开发

## 项目运行的两种方式：
1. 直接启动com.zhukai.framework.spring.integration.demo/ApplicationDemo
2. 执行mvn assembly:single打包jar，然后java -jar运行target下spring-integration-{版本}-jar-with-dependencies.jar

访问[http://localhost:8080/hello](http://localhost:8080/hello)，出现"hello,world"说明启动正常
