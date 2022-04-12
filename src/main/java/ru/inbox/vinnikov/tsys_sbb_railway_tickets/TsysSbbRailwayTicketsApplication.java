package ru.inbox.vinnikov.tsys_sbb_railway_tickets;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.service.WhatsAppService;

import java.time.LocalDateTime;

/**
 *  some javadoc
 *  branch step1 try to do it
 */

@SpringBootApplication
public class TsysSbbRailwayTicketsApplication {

	public static org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(TsysSbbRailwayTicketsApplication.class);

	public static void main(String[] args) {

		LOGGER.info("\n ===============================> sbb tickets started -> " + LocalDateTime.now());
		SpringApplication.run(TsysSbbRailwayTicketsApplication.class, args);
		LOGGER.info("\n ===============================> sbb tickets finished -> " + LocalDateTime.now());

		// TODO включить ватсап при демонстрации + тикет сервис
//		LOGGER.info("\n ==whatsAppService=============================> sbb tickets started -> " + LocalDateTime.now());
//		WhatsAppService whatsAppService = new WhatsAppService();
//		whatsAppService.runWhatsapWeb();
//		LOGGER.info("\n ==whatsAppService=============================> sbb tickets finished -> " + LocalDateTime.now());
	}

}
