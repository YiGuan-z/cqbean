package com.cqsd.bean;

import com.cqsd.asserts.Assert;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author caseycheng
 * @date 2023/3/18-21:10
 **/
public final class BeanField {
    /**
     * 对象的字段解析缓存
     */
    private final Map<Class<?>, List<Field>> cache = new HashMap<>();
    /**
     * 八大基本类型白名单，这几个类型不用当成对象拆分
     */
    private final List<Class<?>> whitelist = List.of(
            String.class, Integer.class,
            Byte.class, Short.class,
            Long.class, Boolean.class,
            Float.class, Double.class,
            Character.class, int.class,
            byte.class, short.class,
            long.class, boolean.class,
            float.class, double.class,
            char.class
    );

    /**
     * 往目标对象设置单个值
     */

    public void setProp(@NotNull final Object bean, @NotNull final String key, final Object value) {
        try {
            final var field = bean.getClass().getDeclaredField(key);
            field.setAccessible(true);
            field.set(bean, value);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("未找到目标key", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("无法为bean设置值" + e);
        }
    }

    /**
     * 通过map中的值对bean进行写入
     *
     * @param bean
     * @param props
     */
    @Contract("null,null->fail;null,_->fail;_,null->fail")
    public void setProps(final Object bean, final Map<String, Object> props) {
        //从bean对象中获取字段集合并转化为字段名和字段对象的map集合
        final var fieldMap = getBeanField(bean.getClass(), Object.class)
                .stream()
                .map(Field::getName)
                .filter(props::containsKey)
                .collect(Collectors.toList());
        //获取key和value并往对象中写入，以map中的对象为准
        for (var key : fieldMap) {
            //获取Key参数
            final var value = props.get(key);
            setProp(bean, key, value);
        }
    }

    /**
     * 将Map对象解析为对应Class
     *
     * @param clazz
     * @param prop
     */
    public <T> T parseMap(final Class<T> clazz, final Map<String, Object> prop) throws InstantiationException {
        T instance;
        try {
            instance = clazz.newInstance();
        } catch (IllegalAccessException e) {
            throw new RuntimeException("需要一个无参构造器", e);
        }
        setProps(instance, prop);
        return instance;
    }

    /**
     * 将一个对象转化为Map
     * 注意，这里只是拷贝了指针，如果一个地方发生变动，另一个地方也会发生变动，基元除外
     *
     * @param bean    需要解析的bean对象
     * @param putNull 传入false就会跳过null值
     * @return Map集合
     */
    public Map<String, Object> toHashMap(final Object bean, final boolean putNull) {
        final List<Field> fields = getBeanField(bean.getClass(), Object.class);
        //设置返回的结构
        final Map<String, Object> ret = new HashMap<>(fields.size());
        final var iterator = fields.iterator();
        while (iterator.hasNext()) {
            try {
                final var field = iterator.next();
                final var name = field.getName();
                Object value = field.get(bean);
                //如果putNull为false并且value测试为true，就跳过这里
                if (!putNull && Objects.isNull(value)) {
                    continue;
                }
                if (!whitelist.contains(field.getType())) {
                    //需要对对象进行处理
                    value = toHashMap(value, putNull);
                }
                ret.put(name, value);
            } catch (IllegalAccessException ignore) {

            }
        }
        return ret;
    }

    /**
     * 获取子类到父类的所有字段,从这里面出来的字段都是关闭了安全检查的，不用重新关闭一次。
     *
     * @param beanClass 需要获取的类
     * @return 子类到父类的字段列表
     */
    public List<Field> getBeanField(final Class<?> beanClass, Class<?> stopClass) {
        //优先在缓存中获取
        if (cache.containsKey(beanClass)) {
            return cache.get(beanClass);
        }
        //如果没有 开始解析
        List<Field> ret = new ArrayList<>();
        Class<?> iterClass = beanClass;
        do {
            if (iterClass.equals(stopClass)) break;
            ret.addAll(List.of(iterClass.getDeclaredFields()));
            iterClass = iterClass.getSuperclass();
        } while (iterClass != null);
        //排除静态字段
        ret.removeIf(this::isStatic);
        //设置为关闭安全检查
        uncheckedFields(ret);
        //往缓存中设置
        cache.put(beanClass, ret);
        return ret;
    }

    /**
     * 判断字段是不是静态
     *
     * @param field 一个字段
     * @return 是否为静态字段
     */
    public boolean isStatic(@NotNull Field field) {
        return Modifier.isStatic(field.getModifiers());
    }

    /**
     * 解锁字段
     *
     * @param field 字段
     */
    public void uncheckedField(@NotNull Field field) {
        field.setAccessible(true);
    }

    /**
     * 对字段集合进行解锁
     *
     * @param fields 一个需要解锁的字段集合
     */
    public void uncheckedFields(@NotNull List<Field> fields) {
        for (Field field : fields) {
            uncheckedField(field);
        }
    }

    /**
     * 提供一个class对象，该方法会从子类一直扫描到父类
     *
     * @param clazz  需要扫描的对象
     * @param action 提供出去的扫描到的class
     */
    @Contract("null,_->fail")
    public void scanClass(Class<?> clazz, Consumer<Class<?>> action) {
        Assert.requireNotNull(action, "action is null");
        scanClass(clazz, Object.class, action);
    }

    /**
     * 提供一个class对象，该方法会从子类一直扫描到父类
     *
     * @param startClass 需要扫描的对象
     * @param stopClass  停止扫描的对象
     * @param action     提供出去的扫描到的class
     */
    @Contract("null,_,_->fail; _,_,null->fail")
    public void scanClass(Class<?> startClass, Class<?> stopClass, Consumer<Class<?>> action) {
        Objects.requireNonNull(startClass);
        Objects.requireNonNull(stopClass);
        Objects.requireNonNull(action);
        Class<?> cla = startClass;
        do {
            action.accept(cla);
            cla = cla.getSuperclass();
            //当前class不为空，并且不是stopClass的时候就循环
        } while (cla != null && !cla.equals(stopClass));
    }

    /**
     * 将方法数组解析为 方法名和方法的map
     *
     * @param methods
     * @return
     */
    @Contract("_->!null")
    public Map<String, Method> parseMethodArray(@NotNull Method[] methods) {
        return Arrays.stream(methods).collect(Collectors.toUnmodifiableMap(Method::getName, r -> r));
    }

    /**
     * 将字段数组解析为字段名和字段的Map
     *
     * @param fields
     * @return
     */
    @Contract("_->!null")
    public Map<String, Field> parseFieldArray(@NotNull Field[] fields) {
        return Arrays.stream(fields).collect(Collectors.toUnmodifiableMap(Field::getName, r -> r));
    }

    /**
     * 查找最多参数的构造器
     *
     * @param constructors
     * @return
     */
    @Contract("_->!null;")
    public Constructor<?> findAllArgsConstructor(@NotNull Constructor<?>[] constructors) {
        return Arrays.stream(constructors).max(Comparator.comparingInt(Constructor::getParameterCount)).get();
    }
}
