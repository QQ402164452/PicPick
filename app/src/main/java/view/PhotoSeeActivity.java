package view;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.jason.picpick.R;

import java.util.ArrayList;
import java.util.Locale;

import fragment.ImageFragment;

/**
 * Created by Jason on 2016/12/23.
 * 图片预览
 */

public class PhotoSeeActivity extends PreviewBaseActivity implements View.OnClickListener {
    private ArrayList<String> mPath;
    private ArrayList<String> mSelect;
    private int mCurrentItem;
    private int mImgCount;

    private ViewPager mViewPager;
    private TextView mCurrentPos;
    private TextView mSelectBtn;
    private TextView mSelectCount;
    private Button mConfirm;

    public static String PHOTO_SEE_ARRAYLIST_PATH="PHOTO_SEE_ARRAYLIST_PATH";
    public static String PHOTO_SEE_ARRAYLIST_SELECT="PHOTO_SEE_HASHSET_SELECT";
    public static String PHOTO_SEE_INT_CURRENT_ITEM="PHOTO_SEE_INT_CURRENT_ITEM";

    public static String PHOTO_SEE_ARRAYLIST_SELECT_RESULT="PHOTO_SEE_HASHSET_SELECT_RESULT";

    @Override
    protected void initPre() {
        Intent intent=getIntent();
        if(intent!=null){
            mPath=intent.getStringArrayListExtra(PHOTO_SEE_ARRAYLIST_PATH);
            mSelect=intent.getStringArrayListExtra(PHOTO_SEE_ARRAYLIST_SELECT);
            mCurrentItem=intent.getIntExtra(PHOTO_SEE_INT_CURRENT_ITEM,1);
            mImgCount=mPath.size()-1;
        }
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_photo_see);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        mToolbar= (Toolbar) findViewById(R.id.PhotoSeeActivity_Toolbar);
        initToolbar(mToolbar);
        mToolbar.setPadding(0,getStatusBarHeight(),0,0);//设置状态栏高度的padding

        mCurrentPos= (TextView) findViewById(R.id.PhotoSeeActivity_Current_Position);
        mViewPager= (ViewPager) findViewById(R.id.PhotoSeeActivity_ViewPager);
        mBottom= (RelativeLayout) findViewById(R.id.PhotoSeeActivity_Bottom);
        mSelectBtn= (TextView) findViewById(R.id.PhotoSeeActivity_SelectBtn);
        mSelectCount= (TextView) findViewById(R.id.PhotoSeeActivity_Select_Count);
        mConfirm= (Button) findViewById(R.id.PhotoSeeActivity_Confirm_Btn);
    }

    @Override
    protected void initData() {
        FragmentManager mManager=getSupportFragmentManager();
        mCurrentPos.setText(mCurrentItem+"/"+mImgCount);
        mSelectBtn.setSelected(mSelect.contains(mPath.get(mCurrentItem)));
        mSelectCount.setText(String.format(Locale.CHINA,"已选%d/9",mSelect.size()));
        mViewPager.setAdapter(new FragmentStatePagerAdapter(mManager) {
            @Override
            public Fragment getItem(int position) {
                return ImageFragment.newInstance(mPath.get(position+1));//从Activity传递图片的绝对路径到Fragment
            }

            @Override
            public int getCount() {
                return mPath.size()-1;//去掉 拍摄图片的 标志位
            }
        });
        mViewPager.setCurrentItem(mCurrentItem-1);//显示点击时跳转过来的 图片位置
    }

    @Override
    protected void initListener() {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrentItem=position+1;
                mCurrentPos.setText(mCurrentItem+"/"+mImgCount);
                mSelectBtn.setSelected(mSelect.contains(mPath.get(mCurrentItem)));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mSelectBtn.setOnClickListener(this);
        mConfirm.setOnClickListener(this);
    }

    public void setResultBack(){
        Intent intent=new Intent();
        intent.putStringArrayListExtra(PHOTO_SEE_ARRAYLIST_SELECT_RESULT,mSelect);
        setResult(RESULT_OK,intent);//setResult() 需要在finish()方法调用前,才可以set成功
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.PhotoSeeActivity_SelectBtn:
                if(mSelectBtn.isSelected()){
                    mSelectBtn.setSelected(false);
                    mSelect.remove(mPath.get(mCurrentItem));
                }else{
                    if(mSelect.size()<9){
                        mSelectBtn.setSelected(true);
                        mSelect.add(mPath.get(mCurrentItem));
                    }
                }
                mSelectCount.setText(String.format(Locale.CHINA,"已选%d/9",mSelect.size()));
                break;
            case R.id.PhotoSeeActivity_Confirm_Btn:
                Intent intent=new Intent(PhotoSeeActivity.this,MainActivity.class);
                intent.putStringArrayListExtra(PHOTO_SEE_ARRAYLIST_SELECT_RESULT,mSelect);
                startActivity(intent);
                finish();
                break;
        }
    }
}
