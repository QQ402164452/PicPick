package view;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.jason.picpick.R;

import java.util.ArrayList;

import fragment.ImageFragment;

/**
 * Created by Jason on 2016/12/24.
 * 已选择的图片预览
 */

public class PreviewActivity extends PreviewBaseActivity implements View.OnClickListener{
    private ViewPager mViewPager;
    private TextView mDelBtn;
    private TextView mConfirm;
    private TextView mCurPos;
    private AlertDialog mDialog;
    private FragmentStatePagerAdapter mAdapter;

    private ArrayList<String> mSelect;//已选择的图片的绝对路径数组
    private int mCurrentItem;//当前选中跳转的图片位置

    public static final String PREVIEW_SELECT_IMG_ARRAYLIST="PREVIEW_SELECT_IMG_ARRAYLIST";
    public static final String PREVIEW_SELECT_CURRENT_POSITION="PREVIEW_SELECT_CURRENT_POSITION";
    public static final int PREVIEW_SELECT_RESULT=305;

    @Override
    protected void initPre() {
        Intent intent=getIntent();
        if(intent!=null){
            mSelect=intent.getStringArrayListExtra(PREVIEW_SELECT_IMG_ARRAYLIST);
            mCurrentItem=intent.getIntExtra(PREVIEW_SELECT_CURRENT_POSITION,1);
        }
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_preview);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        mToolbar= (Toolbar) findViewById(R.id.PreViewActivity_Toolbar);
        initToolbar(mToolbar);
        mToolbar.setPadding(0,getStatusBarHeight(),0,0);//设置状态栏高度的padding

        mViewPager= (ViewPager) findViewById(R.id.PreViewActivity_ViewPager);
        mConfirm= (TextView) findViewById(R.id.PreViewActivity_Confirm_Btn);
        mDelBtn= (TextView) findViewById(R.id.PreViewActivity_DeleteBtn);
        mCurPos= (TextView) findViewById(R.id.PreViewActivity_Current_Position);
        mBottom= (RelativeLayout) findViewById(R.id.PreViewActivity_Bottom);

        initAlertDialog();
    }

    @Override
    protected void initData() {
        FragmentManager mManager=getSupportFragmentManager();
        mCurPos.setText((mCurrentItem+1)+"/"+mSelect.size());
        mAdapter=new FragmentStatePagerAdapter(mManager) {
            @Override
            public Fragment getItem(int position) {
                return ImageFragment.newInstance(mSelect.get(position));
            }

            @Override
            public int getCount() {
                return mSelect.size();
            }

            @Override
            public int getItemPosition(Object object) {//当数据动态改变时 必须返回POSITION_NONE
                return POSITION_NONE;
            }
        };
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(mCurrentItem);
    }

    @Override
    protected void initListener() {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrentItem=position;
                mCurPos.setText((mCurrentItem+1)+"/"+mSelect.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }


        });
        mDelBtn.setOnClickListener(this);
        mConfirm.setOnClickListener(this);
    }

    @Override
    public void setResultBack() {
        Intent intent=new Intent();
        intent.putStringArrayListExtra(PREVIEW_SELECT_IMG_ARRAYLIST,mSelect);
        setResult(PREVIEW_SELECT_RESULT,intent);
    }

    public void initAlertDialog(){
        mDialog=new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("要删除这张照片吗?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mSelect.remove(mCurrentItem);
                        mCurrentItem--;
                        if(mSelect.size()==0){//当全部删除图片时关闭
                            Intent intent=new Intent();
                            intent.putExtra(PREVIEW_SELECT_IMG_ARRAYLIST,mSelect);
                            setResult(PREVIEW_SELECT_RESULT,intent);
                            finish();
                        }else{//删除图片后刷新ViewPager的Adapter
                            mAdapter.notifyDataSetChanged();
                            if(mCurrentItem<0){
                                mCurrentItem++;
                                mCurPos.setText((mCurrentItem+1)+"/"+mSelect.size());
                            }
                            mViewPager.setCurrentItem(mCurrentItem);
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.PreViewActivity_Confirm_Btn:
                setResultBack();
                finish();
                break;
            case R.id.PreViewActivity_DeleteBtn:
                mDialog.show();
                Button negative=mDialog.getButton(DialogInterface.BUTTON_NEGATIVE);//改变取消Button的样式 必须在show()后调用
                negative.setTextColor(getResources().getColor(R.color.grey));
                negative.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                Button positive=mDialog.getButton(DialogInterface.BUTTON_POSITIVE);//改变确认Button的样式 必须在show()后调用
                positive.setTextColor(getResources().getColor(R.color.orange));
                positive.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                break;
        }
    }
}
