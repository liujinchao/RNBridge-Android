package com.liujc.rnbridge;

import java.io.Serializable;

/**
 * @author liujc
 * @ClassName TestEvent
 * @date 2019/7/10
 * @Description (这里用一句话描述这个类的作用)
 */
public class TestEvent implements Serializable{
    private String name;
    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}

