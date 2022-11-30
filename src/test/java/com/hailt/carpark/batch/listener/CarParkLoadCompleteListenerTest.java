package com.hailt.carpark.batch.listener;

import com.hailt.carpark.batch.component.CarParkAvailabilityLoader;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CarParkLoadCompleteListenerTest {

    @Mock
    private JobExecution jobExecution;

    @Mock
    private CarParkAvailabilityLoader carParkAvailabilityLoader;

    @InjectMocks
    private CarParkLoadCompleteListener carParkLoadCompleteListener;

    @After
    public void teardown() {
        reset(carParkAvailabilityLoader);
    }

    @Test
    public void afterJob_ifExecutionStatusIsCompleted_ThenInvokeCarParkAvailabilityLoader() {
        when(jobExecution.getExitStatus()).thenReturn(ExitStatus.COMPLETED);

        carParkLoadCompleteListener.afterJob(jobExecution);

        verify(carParkAvailabilityLoader, times(1)).loadData();
    }

    @Test
    public void afterJob_ifExecutionStatusIsFailed_ThenDoNothing() {
        when(jobExecution.getExitStatus()).thenReturn(ExitStatus.FAILED);

        carParkLoadCompleteListener.afterJob(jobExecution);

        verify(carParkAvailabilityLoader, times(0)).loadData();
    }
}
