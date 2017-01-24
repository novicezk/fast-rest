# integration
使用javaSE实现springboot框架，为了深入学习框架的原理。目前只实现了基础的功能，有很多不足，请指正。

一、主要功能：webserver、springmvc的部分功能、spring的IOC和AOP、spring-data-jpa的部分功能。
二、目前存在问题：
 （1）springmvc的@PathVariable注解不知道如何实现；
 （2）Issue #1
三、项目描述：项目启动入口是Application的main函数，数据库配置和启动端口配置在resources下的application.yml文件，
文件资源需要放在resources下public目录下，访问例localhost:9001/public/login.html
