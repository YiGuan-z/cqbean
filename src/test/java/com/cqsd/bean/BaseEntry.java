package com.cqsd.bean;

import java.io.Serializable;

/**
 * @author caseycheng
 * @date 2022/12/13-17:36
 **/
public abstract class BaseEntry implements Serializable {
    private Long id;

    public BaseEntry setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getId() {
        return id;
    }
}
