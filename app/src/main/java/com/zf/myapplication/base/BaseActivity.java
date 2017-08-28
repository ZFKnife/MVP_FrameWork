package com.zf.myapplication.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.lang.reflect.Field;

/**
 * 2033152950
 * Created by zf on 2017/8/12 0025.
 */
public abstract class BaseActivity<V, T extends BasePresenter<V>> extends AppCompatActivity {

    protected T presenter;
    private int mLayoutId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        displayInjectLayout();
        displayInjectView();
        presenter = initPrasenter();
    }

    private void displayInjectView() {
        if (mLayoutId < 0) {
            return;
        }
        Class<?> clazz = this.getClass();
        Field[] fields = clazz.getDeclaredFields();//获得声明的成员变量
        for (Field field : fields) {
            //判断是否有注解
            try {
                if (field.getAnnotations() != null) {
                    if (field.isAnnotationPresent(ViewInject.class)) {//如果属于这个注解
                        //为这个控件设置属性
                        field.setAccessible(true);//允许修改反射属性
                        ViewInject inject = field.getAnnotation(ViewInject.class);
                        field.set(this, this.findViewById(inject.value()));
                    }
                }
            } catch (Exception e) {
                Log.e("wusy", "not found view id!");
            }
        }
    }

    private void displayInjectLayout() {
        Class<?> clazz = this.getClass();
        if (clazz.getAnnotations() != null) {
            if (clazz.isAnnotationPresent(LayoutInject.class)) {
                LayoutInject inject = clazz.getAnnotation(LayoutInject.class);
                mLayoutId = inject.value();
                setContentView(mLayoutId);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.setIView((V) this);
    }

    @Override
    protected void onDestroy() {
        presenter.cancelIView();
        super.onDestroy();
    }

    protected abstract T initPrasenter();

}
