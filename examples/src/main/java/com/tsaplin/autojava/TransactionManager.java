package com.tsaplin.autojava;

public interface TransactionManager {
    void beginTransaction();
    void commitTransaction();
    void rollbackTransaction();
}
