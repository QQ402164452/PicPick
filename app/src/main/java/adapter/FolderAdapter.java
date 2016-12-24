package adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.jason.picpick.R;

import java.util.ArrayList;

import bean.ImageFolder;
import interfaces.OnItemClickListener;

/**
 * Created by Jason on 2016/12/23.
 * 文件夹选择Adapter
 */

public class FolderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<ImageFolder> mList;//文件夹对象数组
    private OnItemClickListener mListener;
    private int mCurrentSelect=0;//当前选中的文件夹位置

    public FolderAdapter(Context context){
        this.mContext=context;
        this.mInflater=LayoutInflater.from(mContext);
        this.mList=new ArrayList<>();
    }

    public void setDataSource(ArrayList<ImageFolder> list){
        this.mList=list;
    }

    public void setListener(OnItemClickListener onItemClickListener){
        this.mListener=onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FolderHolder(mInflater.inflate(R.layout.photo_pick_folder_list_item,parent,false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        FolderHolder folder= (FolderHolder) holder;
        ImageFolder imageFolder=mList.get(position);
        folder.state.setVisibility(mCurrentSelect==position?View.VISIBLE:View.GONE);
        folder.name.setText(imageFolder.getName());
        folder.count.setText(String.valueOf(imageFolder.getCount())+"张");
        Glide.with(mContext).load(imageFolder.getFirstImagePath())
                .centerCrop()
                .into(folder.img);
        if(mListener!=null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(holder.itemView,position);
                }
            });
        }
    }

    public int getmCurrentSelect() {
        return mCurrentSelect;
    }

    public void setmCurrentSelect(int mCurrentSelect) {
        this.mCurrentSelect = mCurrentSelect;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private static class FolderHolder extends RecyclerView.ViewHolder{
        ImageView img;
        TextView name;
        TextView count;
        ImageView state;

        private FolderHolder(View itemView) {
            super(itemView);
            img= (ImageView) itemView.findViewById(R.id.PhotoPick_Folder_List_Item_img);
            name= (TextView) itemView.findViewById(R.id.PhotoPick_Folder_List_Item_name);
            count= (TextView) itemView.findViewById(R.id.PhotoPick_Folder_List_Item_count);
            state= (ImageView) itemView.findViewById(R.id.PhotoPick_Folder_List_Item_SelectedState);
        }
    }

}
