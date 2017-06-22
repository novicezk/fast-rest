package com.zhukai.framework.spring.integration.bean.component;

import com.zhukai.framework.spring.integration.bean.BaseBean;
import com.zhukai.framework.spring.integration.bean.ChildBean;

import java.util.List;

/**
 * Created by zhukai on 17-1-17.
 */
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
