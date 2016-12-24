package view;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.jason.picpick.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import adapter.FolderAdapter;
import adapter.PhotoPickAdapter;
import bean.ImageFolder;
import customview.DividerGridItemDecoration;
import customview.DividerItemExceptLastDecoration;
import interfaces.IphotoPick;
import interfaces.OnItemClickListener;
import interfaces.OnPhotoPicklItemClickListener;
import presenter.PphotoPick;

/**
 * Created by Jason on 2016/12/21.
 * 图片选择
 */

public class PhotoPickActivity extends BaseActivity implements IphotoPick, View.OnClickListener {
    private RecyclerView mRecyclerView;
    private PphotoPick mPresenter;
    private PhotoPickAdapter mAdapter;
    private TextView mSelectCount;
    private TextView mImageFolder;
    private Button mConfirm;
    private PopupWindow mPopup;
    private RelativeLayout mBottom;
    private View mMask;

    private boolean isPopupShowing = false;
    private int mAnimationDuration = 300;

    private ArrayList<ImageFolder> mFolder;//文件夹对象数组
    private ArrayList<String> mImagePath;//当前Recycler显示的数据源数组 图片的绝对路径数组
    private ArrayList<String> mSelect;//当前已选择的图片数组 最多9张
    private String PHOTO_FILE_NAME;//当前拍摄的图片的文件名
    private int mCurFolPos = 0;//当前选中的文件夹位置

    private View mFolSelView;
    private FolderAdapter mFolAdapter;

    public final int PHOTO_SEE_REQUEST = 200;
    public final int PHOTO_TAKE_REQUEST = 201;
    public final static int PHOTO_TAKE_RESULT=301;
    public final static int PHOTO_PICK_RESULT=302;
    public final static String PHOTO_SELECT_IMG_ARRAYLIST="PHOTO_SELECT_IMG_ARRAYLIST";
    public final static String PHOTO_TAKE_IMG="PHOTO_TAKE_IMG";

    @Override
    protected void initPre() {
        Intent intent=getIntent();
        if(intent!=null){
            mSelect=intent.getStringArrayListExtra(PHOTO_SELECT_IMG_ARRAYLIST);//获取已选择的图片数组
            if(mSelect==null){
                mSelect=new ArrayList<>();
            }
        }
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_photo_pick);
        Toolbar toolbar = (Toolbar) findViewById(R.id.PhotoPickActivity_Toolbar);
        initToolbar(toolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.PhotoPick_RecyclerView);
        mSelectCount = (TextView) findViewById(R.id.PhotoPick_Bottom_Count);
        mImageFolder = (TextView) findViewById(R.id.PhotoPick_Bottom_ImageFolder);
        mBottom = (RelativeLayout) findViewById(R.id.PhotoPick_Bottom);
        mConfirm = (Button) findViewById(R.id.PhotoPick_Confirm_Btn);
        mMask = findViewById(R.id.PhotoPick_Mask);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.addItemDecoration(new DividerGridItemDecoration(this));

        initFolderPopup();//初始化文件夹选择View
    }

    @Override
    protected void initData() {
        mPresenter = new PphotoPick(this);
        mAdapter = new PhotoPickAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mPresenter.getAllPhoto(this);//调用Presenter 去获取数据 解耦
        mSelectCount.setText(String.format(Locale.CHINA, "已选%d/9", mSelect.size()));
    }

    @Override
    protected void initListener() {
        mAdapter.setListener(new OnPhotoPicklItemClickListener() {
            @Override
            public void onSelectItem(View view, int position, int selectCount) {//点击选择图标的回调
                mSelectCount.setText(String.format(Locale.CHINA, "已选%d/9", selectCount));
            }

            @Override
            public void onClickItem(View view, int position) {//点击图片的 回调
                Intent intent = new Intent(PhotoPickActivity.this, PhotoSeeActivity.class);
                intent.putStringArrayListExtra(PhotoSeeActivity.PHOTO_SEE_ARRAYLIST_SELECT, mAdapter.getSelect());
                intent.putStringArrayListExtra(PhotoSeeActivity.PHOTO_SEE_ARRAYLIST_PATH, mAdapter.getPath());
                intent.putExtra(PhotoSeeActivity.PHOTO_SEE_INT_CURRENT_ITEM, position);
                startActivityForResult(intent, PHOTO_SEE_REQUEST);//全屏预览图片
            }

            @Override
            public void onTaskPhoto() {//点击拍摄图片的 回调
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    SimpleDateFormat format=new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss",Locale.CHINA);
                    File dir=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/PhotoPickTemp");
                    if(!dir.exists()){
                        dir.mkdirs();
                    }
                    PHOTO_FILE_NAME=format.format(new Date())+".jpg";//指定拍摄图片时 返回的文件名
                    File photo=new File(dir,PHOTO_FILE_NAME);
                    Uri uri=Uri.fromFile(photo);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//拍摄图片
                    intent.putExtra(MediaStore.Images.Media.ORIENTATION,0);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
                    startActivityForResult(intent, PHOTO_TAKE_REQUEST);
                } else {
                    showToast("暂无外部存储");
                }

            }
        });
        mImageFolder.setOnClickListener(this);
        mMask.setOnClickListener(this);
        mConfirm.setOnClickListener(this);
    }

    @Override
    public void setData(final ArrayList<String> path, final ArrayList<ImageFolder> folders) {
        runOnUiThread(new Runnable() {//Presenter返回的数据源  从子线程切换到主线程
            @Override
            public void run() {
                mFolder = folders;
                mImagePath = path;
                mAdapter.setDataSource(path);
                mAdapter.setSelect(mSelect);
                mAdapter.notifyDataSetChanged();
                mFolAdapter.setDataSource(mFolder);
                mFolAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onDestroy() {
        Glide.get(this).clearMemory();//释放内存
        super.onDestroy();
    }

    public void initFolderPopup() {//初始化文件夹选择的PopupWindow
        mFolSelView = getLayoutInflater().inflate(R.layout.photo_pick_popup_select_folder, null);
        RecyclerView recyclerView = (RecyclerView) mFolSelView.findViewById(R.id.PhotoPick_Popup_Folder_RecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemExceptLastDecoration(this, DividerItemExceptLastDecoration.VERTICAL_LIST));
        mFolAdapter = new FolderAdapter(this);
        mFolAdapter.setListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                hidePopup();
                if (position != mCurFolPos) {
                    if (position == 0) {
                        mAdapter.setDataSource(mImagePath);//显示所有图片
                    } else {
                        mAdapter.setDataSource(mFolder.get(position).getImgPaths());//显示各个文件夹中的图片
                    }
                    mCurFolPos = position;
                    mAdapter.notifyDataSetChanged();
                    mImageFolder.setText(mFolder.get(position).getName());
                }
                mImageFolder.setSelected(false);
            }
        });
        recyclerView.setAdapter(mFolAdapter);

        mPopup = new PopupWindow(mFolSelView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, false);
        mPopup.setOutsideTouchable(false);//设置点击外部 Popup不消失
        mPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mMask.setVisibility(View.GONE);//RecyclerView上的 半透明遮罩层 隐藏
            }
        });
    }

    public void showFolderPopup(View view) {
        if (mPopup == null || !mPopup.isShowing()) {
            mFolAdapter.setmCurrentSelect(mCurFolPos);
            mFolAdapter.notifyDataSetChanged();

            view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);//测量View

            int[] location = new int[2];
            mBottom.getLocationOnScreen(location);//获取mBottom在屏幕上的左上角坐标

            mMask.setVisibility(View.VISIBLE);//RecyclerView上的 半透明遮罩层 显示
            startAnimation(mMask, R.anim.popup_show_alpha);//半透明遮罩层 透明度动画效果

            mPopup.showAtLocation(mBottom, Gravity.NO_GRAVITY, location[0], location[1] - view.getMeasuredHeight());//显示在Bottom上方
            startAnimation(mPopup.getContentView(), R.anim.popup_show_bottom_anim);//Popup从下往上的动画效果
            isPopupShowing = true;
        }
    }

    public void hidePopup() {//隐藏Popup
        if (mPopup != null && mPopup.isShowing() && isPopupShowing) {
            startAnimation(mPopup.getContentView(), R.anim.popup_hidden_bottom_anim);
            startAnimation(mMask, R.anim.popup_hidden_alpha);
            isPopupShowing = false;
            mPopup.getContentView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mPopup.dismiss();
                }
            }, mAnimationDuration);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mPopup != null && mPopup.isShowing()) {
                hidePopup();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PHOTO_SEE_REQUEST://从图片预览Activity返回
                    if (data != null) {
                        ArrayList<String> selects=data.getStringArrayListExtra(PhotoSeeActivity.PHOTO_SEE_ARRAYLIST_SELECT_RESULT);
                        mSelectCount.setText(String.format(Locale.CHINA, "已选%d/9", selects.size()));
                        mAdapter.setSelect(selects);
                        mAdapter.notifyDataSetChanged();
                    }
                    break;
                case PHOTO_TAKE_REQUEST://从拍摄图片Activity返回
                    File file=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/PhotoPickTemp/"+PHOTO_FILE_NAME);

                    Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);//刷新系统媒体库
                    scanIntent.setData(Uri.fromFile(new File(file.getAbsolutePath())));
                    sendBroadcast(scanIntent);//发送广播 刷新当前拍摄的图片 添加到系统媒体库

                    Intent intent=new Intent();
                    intent.putExtra(PHOTO_TAKE_IMG,file.getAbsolutePath());
                    setResult(PHOTO_TAKE_RESULT,intent);//返回到编辑朋友圈Activity
                    finish();
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.PhotoPick_Confirm_Btn:
                Intent intent=new Intent();
                intent.putStringArrayListExtra(PHOTO_SELECT_IMG_ARRAYLIST,mAdapter.getSelect());
                setResult(PHOTO_PICK_RESULT,intent);//返回到编辑朋友圈Activity
                finish();
                break;
            case R.id.PhotoPick_Bottom_ImageFolder:
                if (!mImageFolder.isSelected()) {
                    mImageFolder.setSelected(true);
                    showFolderPopup(mFolSelView);
                } else {
                    mImageFolder.setSelected(false);
                    hidePopup();
                }
                break;
            case R.id.PhotoPick_Mask://点击 RecyclerView上的 半透明遮罩层 关闭Popup
                hidePopup();
                break;
        }
    }

}
