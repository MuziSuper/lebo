package cn.muzisheng.lebo.kafka;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kafka")
public class KafkaTestApi {
    private final Produce produce;
    public KafkaTestApi(Produce produce) {
        this.produce = produce;
    }
    @RequestMapping("/send")
    public void send(String msg) {
        produce.sendMsg();
    }
}
