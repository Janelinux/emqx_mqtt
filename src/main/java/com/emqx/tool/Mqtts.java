package com.emqx.tool;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Mqtts {


    /**
     * 生成一个随机时间戳，范围为当前时间前后 30 秒。
     *
     * @return 返回格式为 "yyyy-MM-dd HH:mm:ss" 的随机时间戳字符串
     */
    public static String generateTimestamp() {
        Random random = new Random();
        int randomSeconds = random.nextInt(61) - 30;
        Date now = new Date();
        now.setTime(now.getTime() + randomSeconds * 1000L);
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now);
    }

    /**
     * 随机生成一个消息类型，类型范围为 "A", "B", "C", "D"。
     *
     * @return 返回一个随机选择的消息类型字符串
     */
    public static String generateType() {
        String[] types = {"A", "B", "C", "D"};
        Random random = new Random();
        return types[random.nextInt(types.length)];
    }
}
