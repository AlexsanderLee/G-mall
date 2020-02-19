# G-mall
#### 商城实战项目

gmall-user-service用户服务service层端口号：8070

gmall-user-web用户服务web层端口号：8080

gmall-manage-service用户服务service层端口号：8071

gmall-manage-web用户服务web层端口号：8081

gmall-item-web用户服务web层端口号：8082

gmall-search-service  8073

gmall-search-web    8083

1、创建parent和api包；

parent中的pom.xml说明：dependencyManagement里只是声明依赖，并不实现引入，因此子项目需要显示的声明需要用的依赖。如果不在子项目中声明依赖，是不会从父项目中继承下来的；只有在子项目中写了该依赖项，并且没有指定具体版本，才会从父项目中继承该项，并且version和scope都读取自父pom;另外如果子项目中指定了版本号，那么会使用子项目中指定的jar版本。

2、抽取utils工程；

```
1 项目中的通用框架，是所有应用工程需要引入的包
例如：springboot、common-langs、common-beanutils

2 基于soa的架构理念，项目分为web前端controller(webUtil)
Jsp、thymeleaf、cookie工具类
加入commonUtil

3 基于soa的架构理念，项目分为web后端service(serviceUtil)
Mybatis、mysql、redis
加入commonUtil
```

3、将user项目拆分成user-service和user-web，后续开发将每个部分的代码拆分为service和web部分。service层为service实现类和mapper接口。web层为controller。

4、在common-util中引入dubbo框架(web和service层将来都需要使用dubbo进行通讯)

5、前后端分离开发，通过运行前端网页，查找需要实现的控制类（F12，查找需要实现的方法和参数，在后端web层实现功能）。

6、分布式文件系统（nginx+fastdfs,配置Linux后，需要下载fastdfs-client-java源码，导入项目）。  
  
7、开发item模块，开发search模块，使用elasticsearch实现搜索功能。