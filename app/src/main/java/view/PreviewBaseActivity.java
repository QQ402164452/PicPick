package view;

import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.example.jason.picpick.R;

/**
 * Created by Jason on 2016/12/24.
 * 图片预览的基类
 */

public abstract class PreviewBaseActivity extends BaseActivity{
    protected RelativeLayout mBottom;
    protected Toolbar mToolbar;
    protected boolean isFull=false;

    public abstract void setResultBack();//数据返回必须在finish()前

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){//按下手机返回键时返回数据
        if(keyCode==KeyEvent.KEYCODE_BACK){
            setResultBack();
        }
        return super.onKeyDown(keyCode,event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){//按下Toolbar上的返回键时返回数据
        if(item.getItemId()==android.R.id.home){
            setResultBack();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void full(boolean enable) {//控制是否全屏显示
        if (enable) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(lp);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            WindowManager.LayoutParams attr = getWindow().getAttributes();
            attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(attr);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    public int getStatusBarHeight() {//获取系统状态栏的高度
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public void fullControl(){//控制是否全屏显示
        if(!isFull){
            isFull=true;
            hideTopBottom();
        }else{
            isFull=false;
            showTopBottom();
        }
    }

    public void hideTopBottom(){//隐藏动画
        startAnimation(mToolbar, R.anim.hide_toolbar_anim);
        startAnimation(mBottom,R.anim.popup_hidden_bottom_anim);
        mToolbar.postDelayed(new Runnable() {
            @Override
            public void run() {
                full(true);
            }
        },300);
    }

    public void showTopBottom(){//显示动画
        startAnimation(mToolbar,R.anim.show_toolbar_anim);
        startAnimation(mBottom,R.anim.popup_show_bottom_anim);
        mToolbar.postDelayed(new Runnable() {
            @Override
            public void run() {
                full(false);
            }
        },300);
    }



}
