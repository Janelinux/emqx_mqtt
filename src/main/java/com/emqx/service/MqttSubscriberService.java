package com.emqx.service;

import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class MqttSubscriberService {
    private  static final String TOPIC="test/topic";

    // 用TreeMap按时间戳存储消息，Map中的key是消息类型，value是消息数量
    private TreeMap<Long, Map<String, Integer>> messageStore = new TreeMap<>();
    private ObjectMapper objectMapper = new ObjectMapper(); // 用于解析 JSON

    @Autowired
    private MqttClient mqttClient;

    /**
     * 初始化订阅逻辑，使用 @PostConstruct 注解在类实例化后自动调用。
     *
     * @throws MqttException 如果在订阅过程中出现错误
     */
    @PostConstruct
    public void init() throws MqttException {
        // 初始化订阅逻辑
        mqttClient.subscribe(TOPIC, (topic, message) -> {
            try {
                // 获取消息内容
                String payload = new String(message.getPayload());
                System.out.println("Received message: " + payload);
                // 解析消息中的 JSON 数据
                JsonNode jsonNode = objectMapper.readTree(payload);
                String type = jsonNode.get("type").asText(); // 获取 type
                String timestampStr = jsonNode.get("timestamp").asText(); // 获取 timestamp

                // 将 timestamp 字符串转为 long 类型时间戳
                long timestamp = convertToTimestamp(timestampStr);
                long timeInMinutes = timestamp / 1000 / 60;

                // 存储消息到 TreeMap
                messageStore.computeIfAbsent(timeInMinutes, k -> new HashMap<>())
                        .merge(type, 1, Integer::sum);

            } catch (Exception e) {
                // 处理解析异常
                e.printStackTrace();
            }
        });
    }
    /**
     * 将给定的时间戳字符串转换为毫秒级时间戳。
     *
     * @param timestampStr 时间戳字符串，格式为 "yyyy-MM-dd HH:mm:ss"
     * @return 转换后的时间戳（毫秒），如果解析失败则返回 0
     */
    private long convertToTimestamp(String timestampStr) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return formatter.parse(timestampStr).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0; // 处理解析异常的返回值
        }
    }

    /**
     * 提供访问 messageStore 的接口
     * @return 返回一个储存消息的数据结构
     */
    public TreeMap<Long, Map<String, Integer>> getMessageStore() {
        return messageStore;
    }
    /**
     * 获取指定时间范围内各类型消息的数量。
     *
     * @param startTime 起始时间，格式为 yyyy-MM-dd HH:mm
     * @param endTime   结束时间，格式为 yyyy-MM-dd HH:mm
     * @return 返回一个包含各类型消息数量的对象，按类型分类统计
     */
    public Map<String, Integer> getMessageCount(String startTime,String endTime) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        // 将输入的字符串时间解析为 Date 对象
        Date startDate = formatter.parse(startTime);
        Date endDate = formatter.parse(endTime);

        // 将 Date 对象转换为分钟单位的时间戳（以便与 TreeMap 中的数据匹配）
        long startMinutes = startDate.getTime() / 1000 / 60;
        long endMinutes = endDate.getTime() / 1000 / 60;

        // 初始化结果存储，记录 A, B, C, D 类型的计数
        Map<String, Integer> typeCount = new HashMap<>();
        typeCount.put("A", 0);
        typeCount.put("B", 0);
        typeCount.put("C", 0);
        typeCount.put("D", 0);
        messageStore.subMap(startMinutes, true, endMinutes, true).forEach((time, counts) -> {
            // 将每种类型的消息计数累加
            counts.forEach((type, count) -> typeCount.put(type, typeCount.get(type) + count));
        });
        return typeCount;
    }
    public void clearMessages() {
        messageStore.clear();  // 清空消息存储
    }


}
