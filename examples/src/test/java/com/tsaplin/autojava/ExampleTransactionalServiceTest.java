package com.tsaplin.autojava;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class ExampleTransactionalServiceTest {

    TransactionManager transactionManager = Mockito.mock(TransactionManager.class);

    ExampleTransactionalService transactionalService = new ExampleTransactionalService();

    @Before
    public void setUp() {
        transactionalService.setTransactionManager(transactionManager);
    }

    @Test
    public void testTransactionalMethod() throws Exception {
        assertEquals(transactionalService.transactionalMethod(), true);
        Mockito.verify(transactionManager).beginTransaction();
        Mockito.verify(transactionManager).commitTransaction();
    }

    @Test(expected = RuntimeException.class)
    public void testTransactionalMethodThrowing() throws Exception {
        transactionalService.transactionalMethodThrowing();
        Mockito.verify(transactionManager).beginTransaction();
        Mockito.verify(transactionManager).rollbackTransaction();
    }

    @Test
    public void testRegularMethod() throws Exception {
        transactionalService.regularMethod();
        Mockito.verifyZeroInteractions(transactionManager);
    }
}
