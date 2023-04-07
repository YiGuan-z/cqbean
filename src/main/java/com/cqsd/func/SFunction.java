package com.cqsd.func;

/**
 * @author caseycheng
 * @date 2023/3/23-22:49
 **/
public class SFunction {
    @FunctionalInterface
    public interface Function1<T, R> {
        R apply(T t);
    }

    @FunctionalInterface
    public interface Function2<T, T1, R> {
        R apply(T t, T1 t1);
    }

    @FunctionalInterface
    public interface Function3<T, T1, T2, R> {
        R apply(T t, T1 t1, T2 t2);
    }
}
