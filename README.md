# 说明

这是一个使用企业微信作为发送通知的 starter

注意, 这个只能在 Spring Boot 版本中使用 且 > `2.0.3-release` 版本

# 使用

```xml
        <dependency>
            <groupId>com.uniweibo</groupId>
            <artifactId>wx-notice-spring-boot-starter</artifactId>
            <version>1.0.1</version>
        </dependency>
```


`application.property` 中加入

```bash
app.wxnotice.accounts[0]=应用的secret,应用的agetnid
app.wxnotice.accounts[1]=第二个应用的secret,第二个应用的agetnid
...
app.wxnotice.corpid=你的企业微信 corpid
app.wxnotice.default-agent=默认使用哪个agentid
```

# Java 使用服务


```java
    @Autowired
    private WXService wxService;

    ...
    调用发送文本:

    wxService.sendText("hello world")
```