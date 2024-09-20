package com.emqx.controller;

import com.emqx.service.MqttSubscriberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
public class MqttQueryController {

    @Autowired
    private MqttSubscriberService mqttSubscriberService;
    /**
     * 根据给定的时间范围查询消息计数。
     *
     * @param startTime 起始时间，格式为 "yyyy-MM-dd HH:mm"
     * @param endTime   结束时间，格式为 "yyyy-MM-dd HH:mm"
     * @return 返回一个包含各类型消息计数的映射，键为类型，值为对应的数量
     * @throws ParseException 如果时间格式解析失败
     */
    @GetMapping("/get_message_count")
    public Map<String, Integer> getMessageCount(@RequestParam String startTime, @RequestParam String endTime) throws ParseException {
       return mqttSubscriberService.getMessageCount(startTime, endTime);
    }
}