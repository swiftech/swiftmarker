# SwiftMarker

SwiftMarker是一个轻量级的模板引擎

> 本项目基于 Apache license 发布


### 特性
* 轻量级的，依赖少
* 简单易用但是具有伸缩性。体积小运行快只支持基本的表达式。
* 使用句点符号 ```foo.bar``` 从数据模型中选取参数。
* 可用各种类型的组合成数据模型: array, ```List```, ```JsonArray```, ```Map```, ```JsonObject``` 甚至是普通的Java类。 可以自由的组合他们。
* 循环表达式
* 逻辑表达式

### 依赖
* JDK 8

### Maven

```xml
<dependency>
	<groupId>com.github.swiftech</groupId>
	<artifactId>swiftmarker</artifactId>
	<version>3.0</version>
</dependency>
```