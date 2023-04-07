package com.cqsd.bean;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author caseycheng
 * @date 2023/3/18-18:14
 **/
public enum PropertiesType {
    /**
     * 读
     */
    read,
    /**
     * 写
     */
    write,
    /**
     * toString
     */
    toString,
    /**
     * 其它方法
     */
    other;
    public static final String GET = "get";
    public static final String SET = "set";
    public static final String IS = "is";
    public static final String TO_STRING = "toString";

    /**
     * 通过方法对象判断是个什么方法
     *
     * @param method
     * @return
     */
    public static PropertiesType chooseType(Method method) {
        Objects.requireNonNull(method);
        final var name = method.getName();
        if (name.startsWith(GET) || name.startsWith(IS)) {
            return read;
        }
        if (name.startsWith(SET)) {
            return write;
        }
        if (name.equals(TO_STRING)) {
            return toString;
        }
        return other;
    }
}
