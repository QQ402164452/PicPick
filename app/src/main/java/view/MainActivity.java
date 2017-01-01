package view;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.jason.picpick.R;

import java.util.ArrayList;
import java.util.jar.Manifest;

import adapter.SelectAdapter;
import interfaces.OnSelectItemClickListener;
import util.PermissionCodes;

public class MainActivity extends BaseActivity {
    private ArrayList<String> mSelect;//当前选择的图片数组
    private RecyclerView mRecyclerView;
    private SelectAdapter mAdapter;

    public final static int PHOTO_PICK_REQUEST = 300;
    public final static int PREVIEW_PHOTO_REQUEST = 301;

    private int mClickPosition;
    private boolean mIsAddBtn;

    @Override
    protected void initPre() {

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.MainActivity_RecyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
    }

    @Override
    protected void initData() {
        mSelect = new ArrayList<>();
        mSelect.add("_Add_Btn");//添加 加载更多的图片的 标志位
        mAdapter = new SelectAdapter(this, mSelect);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void initListener() {
        mAdapter.setOnItemClickListener(new OnSelectItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position, boolean isAddBtn) {
                mClickPosition=position;
                mIsAddBtn=isAddBtn;
                if(checkPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE,PermissionCodes.PERMISSIONS_REQUEST_STORAGE)){
                    toActivity();
                }
            }
        });
    }

    @Override
    public void onPermissionSuccess(int requestCode){
        if(requestCode==PermissionCodes.PERMISSIONS_REQUEST_STORAGE){
            toActivity();
        }
    }

    public void toActivity(){
        if (mIsAddBtn) {
            Intent intent = new Intent(MainActivity.this, PhotoPickActivity.class);
            ArrayList<String> temp = new ArrayList<>();
            temp.addAll(mSelect.subList(0, mSelect.size() - 1));//去掉 加载更多的图片的 标志位
            intent.putStringArrayListExtra(PhotoPickActivity.PHOTO_SELECT_IMG_ARRAYLIST, temp);
            startActivityForResult(intent, PHOTO_PICK_REQUEST);
        } else {
            Intent intent = new Intent(MainActivity.this, PreviewActivity.class);
            if (mSelect.size() < 9) {//如果图片不满9张 显示 加载更多图片的 标志
                ArrayList<String> temp = new ArrayList<>();
                temp.addAll(mSelect.subList(0, mSelect.size() - 1));//去掉 加载更多的图片的 标志位
                intent.putStringArrayListExtra(PreviewActivity.PREVIEW_SELECT_IMG_ARRAYLIST, temp);
            } else {//如果图片满9张 数组中没有 显示 加载更多图片的 标志位
                intent.putStringArrayListExtra(PreviewActivity.PREVIEW_SELECT_IMG_ARRAYLIST, mSelect);
            }
            intent.putExtra(PreviewActivity.PREVIEW_SELECT_CURRENT_POSITION, mClickPosition);
            startActivityForResult(intent, PREVIEW_PHOTO_REQUEST);
        }
    }

    @Override
    public void onNewIntent(Intent intent) {//当前Activity的启动模式为SingleTask 从ACtivity栈上该Activity上的Activity启动该Activity会回调onNewIntent
        super.onNewIntent(intent);
        if (intent != null) {
            ArrayList<String> selects = intent.getStringArrayListExtra(PhotoSeeActivity.PHOTO_SEE_ARRAYLIST_SELECT_RESULT);
            mSelect.clear();
            mSelect.addAll(selects);
            if (selects.size() < 9) {//不满9张 显示加载更多的图标
                mSelect.add("_Add_Btn");
            }
            mAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onActivityResult(int requestId, int resultId, Intent data) {
        super.onActivityResult(requestId, resultId, data);
        switch (requestId) {
            case PHOTO_PICK_REQUEST:
                switch (resultId) {
                    case PhotoPickActivity.PHOTO_TAKE_RESULT:
                        if (data != null) {
                            String imgPath = data.getStringExtra(PhotoPickActivity.PHOTO_TAKE_IMG);
                            mSelect.add(0, imgPath);
                            if (mSelect.size() > 9) {//满9张 不显示加载更多的图标
                                mSelect.remove(mSelect.size() - 1);
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                        break;
                    case PhotoPickActivity.PHOTO_PICK_RESULT:
                        if (data != null) {
                            ArrayList<String> selects = data.getStringArrayListExtra(PhotoPickActivity.PHOTO_SELECT_IMG_ARRAYLIST);
                            mSelect.clear();
                            mSelect.addAll(selects);
                            if (mSelect.size() < 9) {//不满9张 显示加载更多的图标
                                mSelect.add("_Add_Btn");
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                        break;
                }
                break;
            case PREVIEW_PHOTO_REQUEST:
                switch (resultId) {
                    case PreviewActivity.PREVIEW_SELECT_RESULT:
                        if (data != null) {
                            ArrayList<String> selects = data.getStringArrayListExtra(PreviewActivity.PREVIEW_SELECT_IMG_ARRAYLIST);
                            mSelect.clear();
                            mSelect.addAll(selects);
                            if (mSelect.size() < 9) {//不满9张 显示加载更多的图标
                                mSelect.add("_Add_Btn");
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                        break;
                }
                break;
        }
    }
}
