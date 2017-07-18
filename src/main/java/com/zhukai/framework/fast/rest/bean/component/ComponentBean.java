package com.zhukai.framework.fast.rest.bean.component;

import com.zhukai.framework.fast.rest.bean.BaseBean;
import com.zhukai.framework.fast.rest.bean.ChildBean;

import java.util.List;

public class ComponentBean extends BaseBean {

    private List<ChildBean> children;

    private boolean singleton;

    public List<ChildBean> getChildren() {
        return children;
    }

    public void setChildren(List<ChildBean> children) {
        this.children = children;
    }

    public boolean isSingleton() {
        return singleton;
    }

    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }
}
