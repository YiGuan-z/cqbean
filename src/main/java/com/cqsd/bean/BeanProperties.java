package com.cqsd.bean;

import com.cqsd.result.Result;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author caseycheng
 * 只能对实例方法进行查询，不能查询静态方法
 * @date 2023/3/18-15:47
 **/
final public class BeanProperties {
    /**
     * 类型方法解析缓存
     */
    private static final Map<Class<?>, Map<PropertiesType, List<Method>>> CLASS_PROPERTIES_CACHE = new HashMap<>();
    private static final Map<Class<?>, List<Method>> CLASS_METHOD_CACHE = new HashMap<>();

    /**
     * 获取读方法
     *
     * @param cla
     * @param propName
     * @return
     */
    public Result<Method> getReadMethod(Class<?> cla, String propName) {
        final var readMethod = getReadMethod(cla);
        return getMethodResult(propName, readMethod);
    }

    /**
     * 获取写方法
     *
     * @param cla
     * @param propName
     * @return
     */
    public Result<Method> getWriteMethod(Class<?> cla, String propName) {
        final var readMethod = getWriteMethod(cla);
        return getMethodResult(propName, readMethod);
    }

    /**
     * 获取可读可写的属性名
     *
     * @param cla
     * @return
     */
    public List<String> getReadWriteMethodProp(Class<?> cla) {
        final var readSet = getByPropertiesType(cla, PropertiesType.read)
                .stream()
                .map(readMethod -> StringUtil.toLowerCase(StringUtil.remove_get_set(readMethod.getName())))
                .collect(Collectors.toSet());
        final var writeSet = getByPropertiesType(cla, PropertiesType.write)
                .stream()
                .map(readMethod -> StringUtil.toLowerCase(StringUtil.remove_get_set(readMethod.getName())))
                .collect(Collectors.toSet());
        //要将List转换为hash表来对比，我忘记了查找方法不一定是顺序来的。
        List<String> ret = new ArrayList<>();
        final var max = Math.max(readSet.size(), writeSet.size());
        if (max == readSet.size()) {
            for (String s : readSet) {
                if (writeSet.contains(s)) {
                    ret.add(s);
                }
            }
        } else {
            for (String s : writeSet) {
                if (readSet.contains(s)) {
                    ret.add(s);
                }
            }
        }

        return ret;
    }

    /**
     * 根据类型获取属性名
     *
     * @param cla
     * @return
     */
    public List<String> getProp(Class<?> cla, PropertiesType propertiesType) {
        final var methods = getByPropertiesType(cla, propertiesType);
        if (methods == null || methods.size() == 0) {
            return null;
        }
        switch (propertiesType){
            case read:
            case write:{
                return methods
                        .stream()
                        .map(Method::getName)
                        .map(StringUtil::remove_get_set)
                        .map(StringUtil::toLowerCase)
                        .collect(Collectors.toList());
            }
            default:{
                return methods
                        .stream()
                        .map(Method::getName)
                        .collect(Collectors.toList());
            }
        }

    }

    private Result<Method> getMethodResult(String propName, List<Method> readMethod) {
        for (Method method : readMethod) {
            var name = StringUtil.remove_get_set(method.getName());
            name = StringUtil.toLowerCase(name);
            if (propName.equals(name)) {
                return Result.ok(method);
            }
        }
        return Result.err("没有找到方法");
    }

    /**
     * 获取其它方法
     *
     * @param cla
     * @return
     */
    public List<Method> getOtherMethod(Class<?> cla) {
        return getByPropertiesType(cla, PropertiesType.other);
    }

    /**
     * 获取所有读方法
     *
     * @param cla
     * @return
     */
    public List<Method> getReadMethod(Class<?> cla) {
        return getByPropertiesType(cla, PropertiesType.read);
    }

    /**
     * 获取所有写方法
     *
     * @param cla
     * @return
     */
    public List<Method> getWriteMethod(Class<?> cla) {
        return getByPropertiesType(cla, PropertiesType.write);
    }

    /**
     * 获取toString方法
     *
     * @param cla
     * @return
     */
    public Method getToString(Class<?> cla) {
        return getByPropertiesType(cla, PropertiesType.toString).get(0);
    }

    private List<Method> getByPropertiesType(Class<?> cla, PropertiesType propertiesType) {
        final var prop = getBeanProperties(cla);
        return prop.get(propertiesType);
    }

    /**
     * 获取该bean的所有实例方法
     *
     * @param type
     * @return
     */
    public Map<PropertiesType, List<Method>> getBeanProperties(Class<?> type) {
        Map<PropertiesType, List<Method>> map = CLASS_PROPERTIES_CACHE.get(type);
        if (map == null) {
            //解析并存入map缓存
            final var methods = type.getDeclaredMethods();
            final Map<PropertiesType, List<Method>> typeListMap = Arrays.stream(methods)
                    //排除静态方法
                    .filter(method -> !Modifier.isStatic(method.getModifiers()))
                    .collect(Collectors.groupingBy(PropertiesType::chooseType));
            map = typeListMap;
            CLASS_PROPERTIES_CACHE.put(type, typeListMap);
        }
        return map;
    }

    /**
     * 获取所有方法包含父类，不查找Object.class
     *
     * @param type
     * @return
     */
    public List<Method> getAllMethods(Class<?> type) {
        final var map = getBeanProperties(type);
        var list = CLASS_METHOD_CACHE.get(type);
        if (list == null) {
            list = map.values()
                    .stream()
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            CLASS_METHOD_CACHE.put(type, list);
        }
        return list;
    }
}
