package com.cqsd.result;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author caseycheng
 * 该包装可让lambda在使用外部变量时不用转变为final或者atomic
 * @date 2023/3/27-11:21
 **/
public class Box<T> {
    private T instance;

    private Box(T instance) {
        this.instance = instance;
    }

    /**
     * 创建一个非空盒子
     * @param instance
     * @return
     * @param <T>
     */
    @NotNull
    @Contract("_ -> new")
    public static <T> Box<T> newBox(T instance) {
        Objects.requireNonNull(instance);
        return new Box<>(instance);
    }

    /**
     * 创建一个空盒子
     * @return
     * @param <T>
     */
    @NotNull
    @Contract(value = " -> new", pure = true)
    public static <T> Box<T> newEmptyBox() {
        return new Box<>(null);
    }

    public T instance() {
        return instance;
    }

    public Box<T> setInstance(T instance) {
        this.instance = instance;
        return this;
    }

    public Class<?> getType() {
        return instance.getClass();
    }
}
