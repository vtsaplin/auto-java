package com.tsaplin.autojava;

import com.tsaplin.autojava.trait.AroundAspect;
import com.tsaplin.autojava.trait.CallableMethod;
import com.tsaplin.autojava.trait.Trait;

import java.lang.annotation.Annotation;

import static com.google.common.base.Preconditions.checkNotNull;

@Trait
public class TransactionalService implements AroundAspect {

    private TransactionManager transactionManager;

    public TransactionManager getTransactionManager() {
        return transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public Object around(CallableMethod callableMethod, Object o, Object[] args) throws Throwable {
        if (isTransactional(callableMethod)) {
            checkNotNull(transactionManager).beginTransaction();
            try {
                Object result = callableMethod.call(o, args);
                transactionManager.commitTransaction();
                return result;
            } catch(Throwable e) {
                transactionManager.rollbackTransaction();
                throw e;
            }
        }
        return callableMethod.call(o, args);
    }

    private boolean isTransactional(CallableMethod callableMethod) {
        for (Annotation ann : callableMethod.getAnnotations()) {
            if (ann instanceof Transactional) {
                return true;
            }
        }
        return false;
    }
}
