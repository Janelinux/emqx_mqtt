# 项目目录结构

```plaintext
emqx
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── emqx
│   │   │           ├── EmqxApplication.java        # Spring Boot 启动类
│   │   │           ├── config                          # 配置类包
│   │   │           │   └── MqttConfig.java            # MQTT 配置类
│   │   │           ├── service                         # 服务层包
│   │   │           │   ├── MqttPublisherService.java   # MQTT 消息发送服务
│   │   │           │   ├── MqttSubscriberService.java  # MQTT 消息接收服务    
│   │   │           ├── controller                      # 控制器包
│   │   │           │   ├── MqttSendController.java         # 发送消息控制器
│   │   │           │   └── MqttQueryController.java         # 计数控制器
│   │   │           └── tool                           # 工具包
│   │   │               └── Mqtts                # 生成随机时间戳和类型
│   └── test
│       └── java
│           └── com
│               └── emqx
│                    └── EmqxApplicationTests   # 消息发送服务测试
│                   
└── README.md                                             # 项目说明文档
```
### 1. EmqxApplication.java
启动 Spring Boot 应用程序，初始化所有组件和服务。

### 2. MqttConfig.java
配置 MQTT 客户端的参数，包括服务器地址、端口和连接选项。

### 3. MqttPublisherService.java
负责将消息发送到指定的MQTT主题。提供方法用于构造和发送消息。

### 4. MqttSubscriberService.java
负责订阅 MQTT 主题并处理接收到的消息。解析消息内容并将其存储以供后续查询。

### 5. MqttSendController.java
提供 HTTP 接口以触发消息发送操作。接收来自客户端的请求，并调用消息发送服务。

### 6. MqttQueryController.java
提供 HTTP 接口以查询特定时间范围内的消息统计。调用订阅服务以获取消息计数。

### 7. Mqtts
工具类，用于生成随机时间戳和类型，以模拟消息的发送。

### 8. EmqxApplicationTests.java
包含单元测试，验证消息发送和接收功能的正确性。确保应用程序在不同条件下的稳定性。

## 为了有效地存储和查询接收到的消息，我们采用了以下数据结构：
1, **TreeMap**:
    用于存储消息，键为时间（以分钟为单位），值为一个包含各类型消息计数的 `HashMap`。
    这样做的好处是可以根据时间自动排序，方便进行时间范围的查询。

   ```java
   private TreeMap<Long, Map<String, Integer>> messageStore = new TreeMap<>();
   ```
首先通过@PostConstruct订阅指定的 MQTT 主题，收到消息后获取消息内容，解析其中的解析消息中的 JSON 数据
将时间戳字符串转换为毫秒级时间戳。并以分钟为key值,包含各类型消息计数的 `HashMap`为value值,如果之前treemap
里没有该以分钟为单位的时间戳，那么就新建一个hashmap，并把hashmap里对应类型的值设为1，有的话，就把hashmap里
对应类型的值加1
```java
messageStore.computeIfAbsent(timeInMinutes, k -> new HashMap<>()).merge(type, 1, Integer::sum);
```
在计数的时候直接在TreeMap里遍历指定时间范围内的消息，累加各类型的计数
```java
messageStore.subMap(startMinutes, true, endMinutes, true).forEach((time, counts) -> {
    counts.forEach((type, count) -> typeCount.put(type, typeCount.get(type) + count));
});
```
                        
