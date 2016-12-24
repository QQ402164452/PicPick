package interfaces;

import android.view.View;

/**
 * Created by Jason on 2016/12/21.
 * 图片选择时 RecyclerView 中item点击时的接口回调
 */

public interface OnPhotoPicklItemClickListener {
    void onSelectItem(View view, int position, int selectCount);
    void onClickItem(View view,int position);
    void onTaskPhoto();
}
