# integration
该框架参考springboot框架和tomcat，主要目的是为了学习框架的原理。目前只实现了基础的功能，还有很多不足和待完成的部分，
求帮忙提出意见。本人邮箱：15156851740@163.com

## 主要功能：
http服务器、springmvc的部分功能、spring的IOC和AOP基础、spring-data-jpa的部分功能。

## 项目描述：
1. 框架的测试项目为/src/main/java/demo，项目启动入口是Application的main函数
2. 数据库配置和服务器配置在resources下的application.yml文件
3. 文件资源需要放在resources下public目录下，访问例localhost:9001/public/login.html

## 目前存在问题：
1. springmvc的@PathVariable注解不知道如何实现
2. [Issue #1](https://github.com/zhukai-git/integration/issues/1)  
3. mac系统暂时无法运行该项目（StringUtil的readLine方法存在问题）
3. 很多...

## 近期更新：
1. 使用nio替换bio处理http请求协议
2. 添加spring schedule和session过时设置
3. 添加获取数据库连接等待机制
4. 添加@Transactional（简单的事务支持）
5. 添加@Index（数据库表索引）
