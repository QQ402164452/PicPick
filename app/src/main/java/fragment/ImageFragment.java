package fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.jason.picpick.R;

import view.PreviewBaseActivity;

/**
 * Created by Jason on 2016/12/23.
 * 图片预览时ViewPager中的Fragment
 */

public class ImageFragment extends BaseFragment {
    private ImageView mImgView;
    private String mImgPath;//当前ImageView需要显示的图片绝对路径
    private PreviewBaseActivity mParent;//获取父类的Context

    public static String IMAGE_FRAGMENT_EXTRA="IMAGE_FRAGMENT_EXTRA";

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mParent= (PreviewBaseActivity) getActivity();//获取父类的Context
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mImgPath=getArguments().getString(IMAGE_FRAGMENT_EXTRA);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,Bundle saveInstanceState){
        View view=inflater.inflate(R.layout.fragment_image,parent,false);
        init(view);
        return view;
    }

    @Override
    protected void initView(View view) {
        mImgView= (ImageView) view.findViewById(R.id.ImageFragment_Img);
    }

    @Override
    protected void initData() {
        Glide.with(getActivity()).load(mImgPath).dontAnimate().into(mImgView);
    }

    @Override
    protected void initListener() {
        mImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mParent.fullControl();//调用父类 的控制全屏方法
            }
        });
    }

    public static ImageFragment newInstance(String path){//从Activity传递数据给Fragment
        ImageFragment fragment=new ImageFragment();
        Bundle bundle=new Bundle();
        bundle.putString(IMAGE_FRAGMENT_EXTRA,path);
        fragment.setArguments(bundle);
        return fragment;
    }
}
