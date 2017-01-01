package view;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.example.jason.picpick.R;

import java.util.ArrayList;
import java.util.List;

import util.PermissionCodes;

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

    protected boolean checkPermission(String permission, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                    showAlertDialog("部分权限被禁止，将导致程序无法正常运行。是否开启部分权限？(步骤：应用信息->权限->'勾选')");
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
                }
                return false;
            }else{
                return true;
            }
        }
        return true;
    }

    protected void checkAllPermission(String[] perArray, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> deniedPers = new ArrayList<>();
            for (int i = 0; i < perArray.length; i++) {//获取批量请求中 被拒绝的权限列表
                if (ContextCompat.checkSelfPermission(this, perArray[i]) != PackageManager.PERMISSION_GRANTED) {
                    deniedPers.add(perArray[i]);
                }
            }
            int deniedSize = deniedPers.size();
            if (deniedSize != 0) {//进行批量请求
                ActivityCompat.requestPermissions(this, deniedPers.toArray(new String[deniedSize]), requestCode);
            }
        }
    }

    protected void onRequestAllResult(String[] permission, int[] grantResults) {
        int grantedNum = 0;
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                grantedNum++;
            }
        }
        if (grantedNum != permission.length) {
            showAlertDialog("部分权限被禁止，将导致程序无法正常运行。是否开启部分权限？(步骤：应用信息->权限->'勾选')");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permission, int[] grantResults) {
        switch (requestCode) {
            case PermissionCodes.PERMISSIONS_REQUEST_ALL:
                onRequestAllResult(permission, grantResults);
                break;
            case PermissionCodes.PERMISSIONS_REQUEST_LOCATION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onPermissionSuccess(PermissionCodes.PERMISSIONS_REQUEST_LOCATION);
                }else{
                    showAlertDialog("位置信息权限被禁止，将导致定位失败。是否开启该权限？(步骤：应用信息->权限->'勾选'位置信息)");
                }
                break;
            case PermissionCodes.PERMISSIONS_REQUEST_PHONE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onPermissionSuccess(PermissionCodes.PERMISSIONS_REQUEST_PHONE);
                }else {
                    showAlertDialog("拨打电话权限被禁止，无法使用拨打电话功能。是否开启该权限？(步骤：应用信息->权限->'勾选'电话)");
                }
                break;
            case PermissionCodes.PERMISSIONS_REQUEST_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onPermissionSuccess(PermissionCodes.PERMISSIONS_REQUEST_STORAGE);
                }else {
                    showAlertDialog("存储空间权限被禁止，无法使用读取存储空间。是否开启该权限？(步骤：应用信息->权限->'勾选'存储空间)");
                }
                break;
        }
    }

    protected void showAlertDialog(String content) {
        new AlertDialog.Builder(this)
                .setTitle("警告")
                .setMessage(content)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Uri packageUri = Uri.parse("package:" + getPackageName());
                        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageUri);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                }).create().show();
    }

    public void onPermissionSuccess(int requestCode){

    }
}
