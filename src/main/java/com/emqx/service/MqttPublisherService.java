package com.emqx.service;

import com.emqx.tool.Mqtts;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


//
@Service
public class MqttPublisherService {

    @Autowired
    private MqttClient mqttClient;

    private static final String TOPIC="test/topic";

    /**
     * 发送 100 条随机生成的 MQTT 消息。
     *
     * 每条消息包含当前生成时间戳和随机类型，格式为 JSON。
     *
     * @throws MqttException 如果在发送消息过程中出现错误
     */
    public void sendMessages() throws MqttException {

        for (int i = 0; i < 100; i++) {
            //System.out.println(new SimpleDateFormat("HH:mm:ss").format(new Date()));
            String timestamp = Mqtts.generateTimestamp();
            String type = Mqtts.generateType();
            String messageContent = String.format("{\"timestamp\":\"%s\", \"type\":\"%s\"}", timestamp, type);

            MqttMessage message = new MqttMessage(messageContent.getBytes());
            message.setQos(1);
            //message.setRetained(true);

            mqttClient.publish(TOPIC, message);
        }
    }
    /**
     * 向指定的 MQTT 主题添加测试消息。
     *
     * @param s 消息内容，以字符串形式传入
     * @throws MqttException 如果在发送消息过程中出现错误
     */
    public void addTestMessage(String s) throws MqttException {
        MqttMessage message = new MqttMessage(s.getBytes());
        message.setQos(1);
        //message.setRetained(true);

        mqttClient.publish(TOPIC, message);
    }
}