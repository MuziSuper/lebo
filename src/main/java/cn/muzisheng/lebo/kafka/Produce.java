package cn.muzisheng.lebo.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class Produce {
    private final KafkaTemplate<String, String> kafkaTemplate;
    public Produce(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMsg() {
        kafkaTemplate.send("someTopic", "Hello Kafka");
    }
}