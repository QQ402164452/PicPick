package view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.example.jason.picpick.R;

/**
 * Created by Jason on 2016/12/21.
 * Activity基类
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState){//使用模板方法模式
        super.onCreate(savedInstanceState);
        initPre();//初始化前的准备 例如获取之前Activity传递过来的数据 getIntent()
        initView();//初始化视图
        initData();//初始化数据
        initListener();//初始化监听器
    }

    protected abstract void initPre();
    protected abstract void initView();
    protected abstract void initData();
    protected abstract void initListener();

    public void showToast(String string){
        Toast.makeText(this,string,Toast.LENGTH_SHORT).show();
    }

    public void initToolbar(Toolbar toolbar){//初始化Toolbar
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.tab_icon_back);//设置返回的图标
    }

    public void startAnimation(View view, int AnimationId){//开始动画
        if(view!=null&&AnimationId!=0){
            Animation animation= AnimationUtils.loadAnimation(this,AnimationId);
            view.startAnimation(animation);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==android.R.id.home){//当按下返回键时finish掉Activity
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
