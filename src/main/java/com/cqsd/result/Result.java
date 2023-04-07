package com.cqsd.result;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author caseycheng
 * @date 2023/3/16-09:19
 **/
public final class Result<T> {
    /**
     * 保存的实例
     */
    private final T instance;
    /**
     * 保存的错误
     */
    private final String error;

    private Result(T instance, String error) {
        this.instance = instance;
        this.error = error;
    }

    public static <T> Result<T> ok(T instance) {
        return new Result<>(instance, null);
    }

    public static <T> Result<T> err(String errMsg) {
        return new Result<>(null, errMsg);
    }

    public T unwarp() {
        if (isErr()) {
            throw new RuntimeException(error);
        }
        return instance;
    }

    @Contract(pure = true)
    public boolean is_ok_instance(@NotNull Class<?> type) {
        return type.isInstance(instance);
    }

    public boolean isOk() {
        return instance != null;
    }

    public boolean isOkAnd(Predicate<T> action) {
        if (isOk()) {
            return action.test(instance);
        }
        return false;
    }

    public boolean isErr() {
        return error != null;
    }

    public boolean is_err_and(Predicate<String> action) {
        if (isErr()) {
            return action.test(error);
        }
        return false;
    }

    public Optional<T> ok() {
        if (isOk()) {
            return Optional.of(instance);
        }
        return Optional.empty();
    }

    @Contract(pure = true)
    public String err() {
        if (isErr()) {
            return error;
        }
        return null;
    }

    public <R> Result<R> map(Function<T, R> action) {
        if (isOk()) {
            return Result.ok(action.apply(instance));
        }
        return Result.err(error);
    }

    @NotNull
    public <R> Result<R> map_or(Function<T, R> action, Supplier<R> defaul) {
        if (isOk()) {
            return Result.ok(action.apply(instance));
        }
        return Result.ok(defaul.get());
    }
}
