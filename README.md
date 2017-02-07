# integration
该框架参考springboot框架，主要目的是为了学习框架的原理。目前只实现了基础的功能，还有很多不足和待完成的部分，
求帮忙提出意见。本人邮箱：15156851740@163.com

## 主要功能：
webserver、springmvc的部分功能、spring的IOC和AOP基础、spring-data-jpa的部分功能。

## 项目描述：
1. 项目启动入口是Application的main函数
2. 数据库配置（不需要数据库的注掉datasource）和启动端口配置在resources下的application.yml文件
3. 文件资源需要放在resources下public目录下，访问例localhost:9001/public/login.html

## 目前存在问题：
1. springmvc的@PathVariable注解不知道如何实现
2. 并发未仔细测试
3. [Issue #1](https://github.com/zhukai-git/integration/issues/1)  
4. 很多...
