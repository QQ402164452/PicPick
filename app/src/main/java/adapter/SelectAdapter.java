package adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.jason.picpick.R;

import java.util.ArrayList;

import interfaces.OnSelectItemClickListener;

/**
 * Created by Jason on 2016/12/24.
 * 编辑朋友圈的RecyclerView
 */

public class SelectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<String> mList;//当前选中的图片的绝对路径数组
    private OnSelectItemClickListener mListener;

    public SelectAdapter(Context context,ArrayList<String> list){
        this.mContext=context;
        this.mInflater=LayoutInflater.from(mContext);
        this.mList=list;
    }

    public void setDataSource(ArrayList<String> list){
        this.mList=list;
    }

    public void setOnItemClickListener(OnSelectItemClickListener onItemClickListener){
        this.mListener=onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SelectHolder(mInflater.inflate(R.layout.select_img_list_item,parent,false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        SelectHolder select= (SelectHolder) holder;
        final boolean isAddBtn;
        if(mList.get(position).equals("_Add_Btn")){
            Glide.with(mContext).load(R.drawable.content_img_cam_def).dontAnimate().into(select.img);
            isAddBtn=true;
        }else{
            Glide.with(mContext).load(mList.get(position)).dontAnimate().into(select.img);
            isAddBtn=false;
        }
        if(mListener!=null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClickListener(holder.itemView,position,isAddBtn);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private static class SelectHolder extends RecyclerView.ViewHolder{
        ImageView img;

        private SelectHolder(View itemView) {
            super(itemView);
            img= (ImageView) itemView.findViewById(R.id.Select_List_Item_Img);
        }
    }



}
