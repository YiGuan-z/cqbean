package com.cqsd.bean;

import com.cqsd.result.Result;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

class BeanUtilsTest {
    BeanField beanField=new BeanField();
    BeanProperties beanProperties=new BeanProperties();
    @Test
    void testGetSuperProp() {
        final var info = new UserInfo();
        info.setInfo("卧槽").setCity("成都").setEmail("12345678@qq.com").setGender(1).setPassword("dafdfaf");
        final var userInfo = new UserInfo().setNickname("niad").setPassword("11111").setLevel(2).setCity("四川");
        info.setUserInfo(userInfo);
        for (Field field : beanField.getBeanField(info.getClass(), Object.class)) {
            System.out.println(field.getName());
        }
        final var map = beanField.toHashMap(info, false);
        System.out.println(map);
    }

    @Test
    void testGetOrWriteProp() {
        final var prop = beanProperties.getReadWriteMethodProp(UserInfo.class);
        System.out.println(prop);
    }

    @Test
    void getWritleMethod() {
        final var password = beanProperties.getWriteMethod(UserInfo.class, "password");
        final var name = password.unwarp().getName();
        Assertions.assertEquals("setPassword", name);
    }

    @Test
    void getReadMethod() {
        final var password = beanProperties.getReadMethod(UserInfo.class, "password");
        final var name = password.unwarp().getName();
        Assertions.assertEquals("getPassword", name);
    }

    @Test
    void getPropName() {
        var propName = beanProperties.getProp(UserInfo.class, PropertiesType.read);
        System.out.println(propName);
        propName = beanProperties.getProp(UserInfo.class, PropertiesType.write);
        System.out.println(propName);
        propName = beanProperties.getReadWriteMethodProp(UserInfo.class);
        System.out.println(propName);
    }
    @Test
    void setPropTest(){
        final var info = new UserInfo();
        System.out.println(info);
        beanField.setProp(info,"city","四川");
        System.out.println(info);
    }
    @Test
    void getOtherMethod(){
        final var method = beanProperties.getOtherMethod(Result.class);
        System.out.println(method);
    }
}