package adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.jason.picpick.R;

import java.util.ArrayList;

import interfaces.OnPhotoPicklItemClickListener;

/**
 * Created by Jason on 2016/12/21.
 * 显示图片的RecyclerView的Adapter
 */

public class PhotoPickAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<String> mPath;//当前选中的文件夹中包含的所有图片的绝对路径
    private OnPhotoPicklItemClickListener mListener;
    private ArrayList<String> mSelect;//当前选中的图片数组

    public PhotoPickAdapter(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        mPath = new ArrayList<>();
        mSelect = new ArrayList<>();
    }

    public void setListener(OnPhotoPicklItemClickListener onPhotoPicklItemClickListener) {
        this.mListener = onPhotoPicklItemClickListener;
    }

    public void setDataSource(ArrayList<String> path) {
        this.mPath = path;
    }

    @Override
    public int getItemViewType(int position) {//RecyclerView 多Item布局返回的标志位
        return position == 0 ? 0 : 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        switch (viewType) {
            case 0://拍摄图片的首个Item
                holder = new TaskPhotoHolder(mInflater.inflate(R.layout.photo_pick_list_item_take_photo, parent, false));
                break;
            case 1://显示图片的Item
                holder = new PicViewHolder(mInflater.inflate(R.layout.photo_pick_list_item, parent, false));
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final int type = getItemViewType(position);
        switch (type) {
            case 0:
                if (mListener != null) {
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mListener.onTaskPhoto();//点击拍摄图片的回调
                        }
                    });
                }
                break;
            case 1:
                final PicViewHolder picHolder = (PicViewHolder) holder;
                final String path = mPath.get(position);
                picHolder.img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Glide.with(mContext).load(mPath.get(position))
                        .dontAnimate()
                        .centerCrop()
                        .into(picHolder.img);
                if(mSelect.contains(path)){
                    picHolder.state.setSelected(true);
                    picHolder.img.setColorFilter(Color.parseColor("#77000000"));//设置选中时的遮罩层
                }else{
                    picHolder.state.setSelected(false);
                    picHolder.img.setColorFilter(Color.parseColor("#00000000"));//设置未选中时的遮罩层
                }

                if (mListener != null) {
                    picHolder.state.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mSelect.contains(path)) {
                                mSelect.remove(path);
                                picHolder.state.setSelected(false);
                                picHolder.img.setColorFilter(Color.parseColor("#00000000"));
                            } else if (mSelect.size() < 9) {
                                mSelect.add(path);
                                picHolder.state.setSelected(true);
                                picHolder.img.setColorFilter(Color.parseColor("#77000000"));
                            }
                            mListener.onSelectItem(picHolder.state, position, mSelect.size());
                        }
                    });
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mListener.onClickItem(picHolder.state,position);
                        }
                    });
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mPath.size();
    }

    public ArrayList<String> getSelect() {
        return mSelect;
    }

    public void setSelect(ArrayList<String> mSelect) {
        this.mSelect = mSelect;
    }

    public ArrayList<String> getPath() {
        return mPath;
    }

    private static class PicViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        ImageView state;

        private PicViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.PhotoPick_list_item_img);
            state = (ImageView) itemView.findViewById(R.id.PhotoPick_list_item_isSelected_state);
        }
    }


    private static class TaskPhotoHolder extends RecyclerView.ViewHolder {

        private TaskPhotoHolder(View itemView) {
            super(itemView);
        }
    }
}
