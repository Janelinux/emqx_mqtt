package com.emqx.config;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


//与mqtt进行连接
@Configuration
public class MqttConfig {

    public static final String BROKER="tcp://localhost:1883";

    public static final String CLIENT_ID = "mqttx_bd59639c";

    public static  final String USERNAME ="admin";

    public static final String PASSWORD ="159357456258qwE";
    /**
     * 创建并配置 MQTT 客户端 Bean。
     *
     * @return 配置好的 MqttClient 实例
     * @throws MqttException 如果在创建或连接客户端时出现错误
     */
    @Bean
    public MqttClient mqttClient() throws MqttException {
        MqttClient client = new MqttClient(BROKER, CLIENT_ID, new MemoryPersistence());
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(USERNAME);
        options.setKeepAliveInterval(60);
        options.setCleanSession(false);
        //client.setTimeToWait(30 * 1000);
        options.setPassword(PASSWORD.toCharArray());
        client.connect(options);
        return client;
    }
}
