package com.cqsd.bean;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author caseycheng
 * @date 2023/3/18-23:51
 **/
@SuppressWarnings({"unchecked", "rawtypes"})
public class ObjectMapping {
    /**
     * 用来存储对象的翻译方法 map[需要翻译的类型,map[目标翻译类型,目标翻译方法]]
     */
    private final Map<Class<?>, Map<Class<?>, Function>> covMapping = new HashMap<>();

    /**
     * 注册一个转换器
     *
     * @param in       需要转换的类型
     * @param out      转换后的类型
     * @param function 转换的方法
     * @param <T>      转换前的类型
     * @param <R>      转换后的类型
     */
    public <T, R> void register(Class<T> in, Class<R> out, Function<T, R> function) {
        //通过in来查询，如果没有查询到，那么就是第一次设置
        if (covMapping.containsKey(in)) {
            //如果获取到不为空，那么就直接设置即可
            covMapping.get(in).put(out, function);
        } else {
            //第一次设置,需要初始化map对象
            final Map<Class<?>, Function> objMap = new HashMap<>(10);
            objMap.put(out, function);
            covMapping.put(in, objMap);
        }
    }

    /**
     * 输入一个需要转换的对象还有一个输出对象的class，返回输出对象
     *
     * @param in  需要转换的对象
     * @param out 转换后的对象
     * @param <T> 需要转换的对象
     * @param <R> 转换后的对象
     * @return 转换后的对象
     */
    public <T, R> R map(T in, Class<R> out) {
        final var func = getFunc(in.getClass(), out);
        final var res = func.apply(in);
        return (R) res;
    }

    /**
     * 获取一个转换器方法
     *
     * @param in  需要转换的对象
     * @param out 转换后的对象方法
     * @param <T>
     * @param <R>
     * @return 转换的方法
     */
    public <T, R> Function getFunc(Class<T> in, Class<R> out) {
        final var functionMap = covMapping.get(in);
        if (functionMap == null) {
            throw new NullPointerException(String.format("%s没有转换器", in.getSimpleName()));
        }
        final var function = functionMap.get(out);
        if (function == null) {
            throw new NullPointerException(String.format("该%s下没有这个%s的转换器", in.getSimpleName(), out.getSimpleName()));
        }
        return function;
    }

    /**
     * 检查一个对象转换器是否存在
     *
     * @param in  输入对象
     * @param out 输出对象
     * @return 检查是否在这个输入对象下找到转换为输出对象的方法
     */
    public boolean containClass(Class<?> in, Class<?> out) {
        final var functionMap = covMapping.get(in);
        if (functionMap == null) {
            return false;
        }
        return functionMap.containsKey(out);
    }
}
