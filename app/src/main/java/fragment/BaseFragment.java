package fragment;

import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by Jason on 2016/12/23.
 * Fragment 基类
 */

public abstract class BaseFragment extends Fragment {

    public void init(View view){//使用模板设计模式
        initView(view);
        initData();
        initListener();
    }

    protected abstract void initView(View view);
    protected abstract void initData();
    protected abstract void initListener();


}
