package ru.inbox.vinnikov.tsys_sbb_railway_tickets.unit;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;
import java.util.Random;

//@SpringBootTest
class TsysSbbRailwayTicketsApplicationTests {

	@Test//(expected = ArithmeticException.class)
	public void getFourDigits1Code() {
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
