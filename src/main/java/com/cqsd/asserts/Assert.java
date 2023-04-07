package com.cqsd.asserts;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nls;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author caseycheng
 * @date 2023/3/13-03:37
 **/
public class Assert {
    @Contract(value = "null,_->fail", pure = true)
    public static void requireNotNull(Object obj, Supplier<RuntimeException> throwable) {
        if (obj == null) {
            throw throwable.get();
        }
    }

    @Contract(value = "null,_->fail", pure = true)
    public static void requireNotNull(Object obj, @Nls String message) {
        if (obj == null) {
            throw new RuntimeException(message);
        }
    }
    @Contract(value = "null->fail", pure = true)
    public static void requireNotNull(Object obj) {
        if (obj == null) throw new RuntimeException();
    }

    /**
     * 这里的方法运算为结果为true就报错
     *
     * @param t
     * @param express
     * @param message
     * @param <T>
     */
    public static <T> void assertFalse(T t, Predicate<T> express, String message) {
        if (express.test(t)) {
            throw new RuntimeException(message);
        }
    }

    /**
     * 这里的方法运算为true就不会报错
     *
     * @param t
     * @param express
     * @param message
     * @param <T>
     */
    public static <T> void assertTrue(T t, Predicate<T> express, String message) {
        if (!express.test(t)) {
            throw new RuntimeException(message);
        }
    }

    public static void requireNotNullMap(Map<?, ?> params) {
        Assert.requireNotNull(params);
        Assert.assertFalse(params, (param) -> param.size() == 0, "该map为空");
    }

    public static void requireNotNullList(List<?> list) {
        Assert.requireNotNull(list);
        Assert.assertFalse(list, (t) -> t.size() == 0, "该list为空");
    }
}
