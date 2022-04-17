package ru.inbox.vinnikov.tsys_sbb_railway_tickets.unit;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity.TrainZug;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.repository.TrainRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import static org.mockito.Mockito.when;

class TsysSbbRailwayTicketsApplicationTests {

	@Mock
	private TrainRepository trainRepository;
	@Mock
	private TrainZug trainZug9;
	@Mock
	private TrainZug trainZug8;
	@Mock
	private TrainZug trainZug7;
	@Mock
	private ArrayList<TrainZug> allTrainsListGiven;

	public TsysSbbRailwayTicketsApplicationTests() {
		MockitoAnnotations.initMocks(this);
		this.trainZug9 = new TrainZug();
		trainZug9.setId(9L);
		trainZug9.setVersion(0);
		trainZug9.setNumberTrainNummerZug("999");
		trainZug9.setPassengersCapacityPassagierkapazitat(999);
		this.trainZug8 = new TrainZug();
		trainZug8.setId(8L);
		trainZug8.setVersion(0);
		trainZug8.setNumberTrainNummerZug("888");
		trainZug8.setPassengersCapacityPassagierkapazitat(888);
		this.trainZug7 = new TrainZug();
		trainZug7.setId(7L);
		trainZug7.setVersion(0);
		trainZug7.setNumberTrainNummerZug("777");
		trainZug7.setPassengersCapacityPassagierkapazitat(777);
		this.allTrainsListGiven = new ArrayList(Arrays.asList(trainZug7,trainZug8,trainZug9));
	}

	@Test
	void findAllTrainsTest() {
		// given
		when(trainRepository.findAll()).thenReturn(allTrainsListGiven);
		// when do
		ArrayList<TrainZug> allTrainsList = trainRepository.findAll();
		// result
		Assertions.assertEquals(allTrainsListGiven,allTrainsList);
	}

	@Test
	void getAllTrainsNumbersStrTest() {
		// given
		String allTrainsNamesGiven = "Список номеров поездов уже имеющихся в базе: 777; 888; 999; ";
		when(trainRepository.findAll()).thenReturn(allTrainsListGiven);
		// when do
		ArrayList<TrainZug> allTrainsList = trainRepository.findAll();
		String allTrainsNames = "Список номеров поездов уже имеющихся в базе: ";
		for (TrainZug trainZug : allTrainsList) {
			allTrainsNames = allTrainsNames + trainZug.getNumberTrainNummerZug() + "; ";
		}
		// result
		Assertions.assertEquals(allTrainsNamesGiven,allTrainsNames);
	}

	@Test
	void findByNumberTrain999Test() {
		// given
		when(trainRepository.findByNumberTrain("999")).thenReturn(trainZug9);
		// when do
		TrainZug byNumberTrain = trainRepository.findByNumberTrain("999");
		// result
		Assertions.assertEquals(trainZug9,byNumberTrain);
	}

	@Test
	void findByNumberTrain998Test() {
		// given
		when(trainRepository.findByNumberTrain("998")).thenReturn(null);
		// when do
		TrainZug byNumberTrain = trainRepository.findByNumberTrain("998");
		// result
		Assertions.assertNull(byNumberTrain);
	}

	@Test
	void findByNumberTrainEmptyTest() {
		// given
		String trainNumber = "";
		TrainZug trainFmDB = new TrainZug();
		// when do
		if (trainNumber.isBlank() || trainNumber.isEmpty()){
			trainFmDB = null;
		}
		// result
		Assertions.assertNull(trainFmDB);
	}

	//-------------------------------------
	// проверка, что секретный код в диапазоне 1000-9999
	@Test//(expected = ArithmeticException.class)
	public void getFourDigits1Code() {
		// given:
		Random random = new Random();
		// when:
		// формируем код для оплаты билета
		int code = 0;
		int module = 1000;
		for (int i = 0; i < 4; i++) {
			int codeElement = random.nextInt(10);
			if (codeElement == 0 && module == 1000)
				codeElement = 3;
			codeElement = codeElement * module;
			module /= 10;
			code += codeElement;
		}
		System.out.println("-----1-code:" + code);
		// result:
		Assertions.assertTrue(code > 999 && code < 10_000);
	}

	@Test
	public void getFourDigits2Code() {
		// given:
		Random random = new Random();
		// when:
		// типа код для оплаты билета
		int code = 0;
		int module = 1000;
		for (int i = 0; i < 4; i++) {
			int codeElement = random.nextInt(10);
			if (codeElement == 0 && module == 1000)
				codeElement = 3;
			codeElement = codeElement * module;
			module /= 10;
			code += codeElement;
		}
		System.out.println("-----2-code:" + code);
		// result:
		Assertions.assertTrue(code > 999 && code < 10_000);
	}

	@Test
	public void getFourDigits3Code() {
		// given:
		Random random = new Random();
		// when:
		// типа код для оплаты билета
		int code = 0;
		int module = 1000;
		for (int i = 0; i < 4; i++) {
			int codeElement = random.nextInt(10);
			if (codeElement == 0 && module == 1000)
				codeElement = 3;
			codeElement = codeElement * module;
			module /= 10;
			code += codeElement;
		}
		System.out.println("-----3-code:" + code);
		// result:
		Assertions.assertTrue(code > 999 && code < 10_000);
	}

	@Test
	public void getFourDigits4Code() {
		// given:
		Random random = new Random();
		// when:
		// типа код для оплаты билета
		int code = 0;
		int module = 1000;
		for (int i = 0; i < 4; i++) {
			int codeElement = random.nextInt(10);
			if (codeElement == 0 && module == 1000)
				codeElement = 3;
			codeElement = codeElement * module;
			module /= 10;
			code += codeElement;
		}
		System.out.println("-----4-code:" + code);
		// result:
		Assertions.assertTrue(code > 999 && code < 10_000);
	}

}
