## spring 项目自动生成模型层代码

参考地址https://www.cnblogs.com/hjwublog/p/9957312.html

## 模型层

domain,mapper,dao

## 创建项目

File->new->project->Spring initializr->Next 依次输入maven坐标信息，Dependencies选择Mysql、Mybatis。在File->Setting->Maven中配置本地Maven安装目录和setting.xml配置文件路径，完成。

## 创建数据库与关系表

设计好数据库后，在mysql中创建数据库***springtest***

## 配置application.properties

配置application.properties连接上数据库***springtest***，为后续自动生成代码做准备，

里面还有各种配置，根据项目实际进行配置

```properties
# 连接数据库
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/springtest?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=xxx

# 服务端口配置
server.port=8080
banner.charset=UTF-8
server.tomcat.uri-encoding=UTF-8
spring.http.encoding.charset=UTF-8

# 2020/3/6 gxh add 邮件发送
#邮箱服务器地址
spring.mail.host=smtp.163.com
#发件人地址
spring.mail.username=xxx@163.com
#邮箱的授权码
spring.mail.password=xxxx
spring.mail.default-encoding=utf-8
#这个是非必须，我自己自定义的，可以便于程序中取值
mail.fromMail.addr=xxx@163.com
```

## 自动生成代码插件与文件配置

### pom.xml添加相关依赖包

```xml
<!-- MyBatis 生成器 -->
<dependency>    
    <groupId>org.mybatis.generator</groupId>    
    <artifactId>mybatis-generator-core</artifactId>    
    <version>1.3.3</version>
</dependency>
<!-- MyBatis-->
<dependency>    
    <groupId>org.mybatis</groupId>    
    <artifactId>mybatis</artifactId>    
    <version>3.4.2</version>
</dependency>
<!--Mysql数据库驱动-->
<dependency>    
    <groupId>mysql</groupId>    
    <artifactId>mysql-connector-java</artifactId>    
    <version>5.1.46</version>
</dependency>
<!--MyBatis分页插件-->
<dependency>    
    <groupId>com.github.pagehelper</groupId>    
    <artifactId>pagehelper-spring-boot-starter</artifactId>    
    <version>1.2.3</version>
</dependency>
<!--Swagger-UI API文档生产工具-->
<dependency>    
    <groupId>io.springfox</groupId>    
    <artifactId>springfox-swagger2</artifactId>    
    <version>2.6.1</version>
</dependency>
<dependency>    
    <groupId>io.springfox</groupId>    
    <artifactId>springfox-swagger-ui</artifactId>    
    <version>2.6.1</version>
</dependency>
<!--JWT(Json Web Token)登录支持-->
<dependency>    
    <groupId>io.jsonwebtoken</groupId>    
    <artifactId>jjwt</artifactId>    
    <version>0.9.0</version>
</dependency>
```

### generatorConfig文件配置

新增/src/main/resources/generatorConfig.xml配置文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE generatorConfiguration
        PUBLIC "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"
        "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd">
<generatorConfiguration>
    <properties resource="generator.properties"/>
    <context id="MySqlContext" targetRuntime="MyBatis3" defaultModelType="flat">
        <property name="beginningDelimiter" value="`"/>
        <property name="endingDelimiter" value="`"/>
        <property name="javaFileEncoding" value="UTF-8"/>

        <!-- 为模型生成序列化方法-->
        <plugin type="org.mybatis.generator.plugins.SerializablePlugin"/>
        <!-- 为生成的Java模型创建一个toString方法 -->
        <plugin type="org.mybatis.generator.plugins.ToStringPlugin"/>
        <commentGenerator type="com.example.myspring2.generator.CommentGenerator">
            <!-- 是否去除自动生成的注释 true：是 ： false:否 -->
            <property name="suppressAllComments" value="true"/>
            <property name="suppressDate" value="true"/>
            <property name="addRemarkComments" value="true"/>
        </commentGenerator>
<!--jdbc.driverClass等4个变量从/src/main/resources/generator.properties文件中读取-->
        <jdbcConnection driverClass="${jdbc.driverClass}"
                        connectionURL="${jdbc.connectionURL}"
                        userId="${jdbc.userId}"
                        password="${jdbc.password}">
        </jdbcConnection>
        <!--生成模型的包名和位置-->
        <javaModelGenerator targetPackage="com.example.myspring2.domain" targetProject="D:\javaSpace\myspring2\src\main\java" />
        <!-- 生成XML映射文件的包名和位置-->
        <sqlMapGenerator targetPackage="mapper" targetProject="D:\javaSpace\myspring2\src\main\resources"/>
        <!--生成DAO的包名和位置-->
        <javaClientGenerator type="XMLMAPPER" targetPackage="com.example.myspring2.dao"
                             targetProject="D:\javaSpace\myspring2\src\main\java"/>
        
<!--生成全部表tableName设为%;自动生成mybatis example接口,将enableCountByExample等参数置为true-->
        <table tableName="%"  enableCountByExample="true" enableUpdateByExample="true" enableDeleteByExample="true"
               enableSelectByExample="true" selectByExampleQueryId="true" >
            <generatedKey column="id" sqlStatement="MySql" identity="true"/>
        </table>
    </context>
</generatorConfiguration>
```

/src/main/resources/generator.properties文件的内容：

```properties
jdbc.driverClass=com.mysql.jdbc.Driver
jdbc.connectionURL= jdbc:mysql://127.0.0.1:3306/autotest?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC
jdbc.userId=root
jdbc.password=xxx
```

## Generator执行

/src/java/generator/CommentGenerator.java

运行/src/java/generator/Generator.java自动生成模型层代码

```java
// /src/java/generator/CommentGenerator.java
package com.example.myspring2.generator;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.internal.DefaultCommentGenerator;
import org.mybatis.generator.internal.util.StringUtility;

import java.util.Properties;

public class CommentGenerator extends DefaultCommentGenerator{

    private boolean addRemarkComments = false;

    /**
     * 设置用户配置的参数
     */
    @Override
    public void addConfigurationProperties(Properties properties) {
        super.addConfigurationProperties(properties);
        this.addRemarkComments = StringUtility.isTrue(properties.getProperty("addRemarkComments"));
    }

    /**
     * 给字段添加注释
     */
    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable,
                                IntrospectedColumn introspectedColumn) {
        String remarks = introspectedColumn.getRemarks();
        //根据参数和备注信息判断是否添加备注信息
        if(addRemarkComments&&StringUtility.stringHasValue(remarks)){
            //文档注释开始
            field.addJavaDocLine("/**");
            //获取数据库字段的备注信息
            String[] remarkLines = remarks.split(System.getProperty("line.separator"));
            for(String remarkLine:remarkLines){
                field.addJavaDocLine(" * "+remarkLine);
            }
            addJavadocTag(field, false);
            field.addJavaDocLine(" */");
        }
    }
}
```

```java
// /src/java/generator/Generator.java
package com.example.myspring2.generator;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 用于生产MBG的代码
 */
public class Generator {

    public static void main(String[] args) throws Exception {
        List<String> warnings=new ArrayList<String>();

        boolean overwrite=true;

        InputStream is=Generator.class.getResourceAsStream("/generatorConfig.xml");8
        ConfigurationParser cp=new ConfigurationParser(warnings);
        Configuration config=cp.parseConfiguration(is);
        is.close();

        DefaultShellCallback callback=new DefaultShellCallback(overwrite);
        MyBatisGenerator myBatisGenerator=new MyBatisGenerator(config,callback,warnings);
        //执行生产代码
        myBatisGenerator.generate(null);

        for (String warning: warnings){
            System.out.println(warning);
        }
    }
}

```

