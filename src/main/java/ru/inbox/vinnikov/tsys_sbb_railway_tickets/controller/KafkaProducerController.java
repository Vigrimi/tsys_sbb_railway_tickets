package ru.inbox.vinnikov.tsys_sbb_railway_tickets.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.dto.ScheduleOnRwstationDto;

@RestController
@RequestMapping("sbb")
public class KafkaProducerController {

    /*@Autowired
    private KafkaTemplate<Long, ScheduleOnRwstationDto> kafkaTemplate;*/

    //-------------------------------------------------------------------------------------

    /*@PostMapping
    public void sendOrder(String msgId, String msg){
        kafkaTemplate.send("sbb", msgId, msg);
    }*/

    /*@PostMapping
    public void sendMsg(Long msgId, ScheduleOnRwstationDto msg){
        ListenableFuture<SendResult<Long, ScheduleOnRwstationDto>> future = kafkaTemplate.send("sbb", msgId, msg);
        future.addCallback(System.out::println, System.err::println);
        kafkaTemplate.flush();
    }*/

}
