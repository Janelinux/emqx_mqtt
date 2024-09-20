package com.emqx.controller;

import com.emqx.service.MqttPublisherService;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

//发送信息的send api
@RestController
public class MqttSendController {

    @Autowired
    private MqttPublisherService mqttPublisherService;
    /**
     * 发送 MQTT 消息的 HTTP 接口。
     *
     * 当收到请求时，将调用消息发送服务发送一批 MQTT 消息。
     *
     * @return 返回成功消息，指示消息已成功发送
     * @throws MqttException 如果在发送消息过程中出现错误
     */
    @GetMapping("/send")
    public String sendMqttMessages() throws MqttException {
        mqttPublisherService.sendMessages();
        return "Messages sent successfully!";
    }
}