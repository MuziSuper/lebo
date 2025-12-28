package cn.muzisheng.lebo.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class Listen {
    @KafkaListener(topics = "someTopic", groupId = "test")
    public void listen(String message) {
        System.out.println("Received: " + message);
    }
}
