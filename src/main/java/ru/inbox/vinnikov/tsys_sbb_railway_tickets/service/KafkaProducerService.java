package ru.inbox.vinnikov.tsys_sbb_railway_tickets.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.PostMapping;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.dto.ScheduleOnRwstationDto;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity.TrainZug;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.repository.TrainRepository;

@Service
public class KafkaProducerService {

    @Autowired
    private KafkaTemplate<Long, String> kafkaTemplate;

    //-------------------------------------------------------------------------------------

    public void sendStringMsg(Long msgId, String msg){
        ListenableFuture<SendResult<Long, String>> future = kafkaTemplate.send("sbb", msgId, msg);
        future.addCallback(System.out::println, System.err::println);
        kafkaTemplate.flush();
    }
}
