package ru.inbox.vinnikov.tsys_sbb_railway_tickets.mockito.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity.TrainZug;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.repository.TrainRepository;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class TrainRepositoryTest {
//    TrainRepository trainRepositoryMock = Mockito.mock(TrainRepository.class);
    @Mock
    private TrainRepository trainRepository;
    @Mock
    private TrainZug trainZug;

    public TrainRepositoryTest() {
        MockitoAnnotations.initMocks(this);
        this.trainZug = new TrainZug();
        trainZug.setId(9L);
        trainZug.setVersion(0);
        trainZug.setNumberTrainNummerZug("999");
        trainZug.setPassengersCapacityPassagierkapazitat(999);
    }

    @Test
    void findByNumberTrainTest() {
        // given
        when(trainRepository.findByNumberTrain("999")).thenReturn(trainZug);
        // when do
        TrainZug byNumberTrain = trainRepository.findByNumberTrain("999");
        // result
        Assertions.assertEquals(trainZug,byNumberTrain);
    }

    @Test
    void findByNumberTrainTest_NotNull() {
        // given
        when(trainRepository.findByNumberTrain("999")).thenReturn(trainZug);
        // when do
        TrainZug byNumberTrain = trainRepository.findByNumberTrain("999");
        // result
        Assertions.assertNotNull(byNumberTrain);
    }

    @Test
    void findByNumberTrain_Throw_Exception() {
        when(trainRepository.findByNumberTrain("invalidNumberTrain"))
                .thenThrow(new IllegalArgumentException());
    }

     /*@BeforeEach
    void setUp() {
    }*/

    /*@AfterEach
    void tearDown() {
    }*/

    /*@Test
    void findAll() {
    }*/
}