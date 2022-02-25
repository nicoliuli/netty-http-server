package com.nico.dispatch;

@FunctionalInterface
public interface FunctionWith3Args<B,M,A,R> {
    R apply(B b,M m,A a);
}
