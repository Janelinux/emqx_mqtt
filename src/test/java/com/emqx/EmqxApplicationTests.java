package com.emqx;

import com.emqx.service.MqttPublisherService;
import com.emqx.service.MqttSubscriberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class EmqxApplicationTests {

    @Autowired
    private MqttPublisherService publisherService;

    @Autowired
    private MqttSubscriberService subscriber;


    @BeforeEach
    void setUp() {
        // 清空 subscriber 的消息存储
        subscriber.clearMessages();
    }

    @Test
    void testMessageSendingAndReceiving() throws Exception {
        // 发送 100 条消息
        publisherService.sendMessages();

        // 模拟时间间隔后接收消息
        Thread.sleep(2000);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now.minusMinutes(1);
        LocalDateTime endTime = now.plusMinutes(1);

        String startTimeStr = startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        String endTimeStr = endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        // 验证接收的消息数量
        var counts = subscriber.getMessageCount(startTimeStr,endTimeStr); // 获取最近 1 分钟内的消息
        assertEquals(100, counts.get("A") + counts.get("B") + counts.get("C") + counts.get("D"));
    }

    @Test
    void testQueryMessageCount() throws Exception {
        // 发送一些测试消息
        publisherService.addTestMessage("{\"timestamp\":\"2024-09-20 14:00:00\", \"type\":\"A\"}");
        publisherService.addTestMessage("{\"timestamp\":\"2024-09-20 14:00:59\", \"type\":\"B\"}");

        Map<String, Integer> counts = subscriber.getMessageCount("2024-09-20 14:00", "2024-09-20 14:01");
        //验证结果
        assertEquals(1, counts.get("A"));
        assertEquals(1, counts.get("B"));
        assertEquals(0, counts.get("C"));
        assertEquals(0, counts.get("D"));
    }

}
