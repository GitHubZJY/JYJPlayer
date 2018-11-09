package com.zjyang.base.base;

/**
 * Created by 74215 on 2018/7/21.
 */

public abstract class BasePresenter<V, M> {

    protected M mModel;
    protected V mView;

    public void attachV(V v){
        this.mView = v;
        mModel = createModel();
    }

    public abstract M createModel();

}
