package interfaces;

import android.view.View;

/**
 * Created by Jason on 2016/12/24.
 * 编辑朋友圈时RecyclerView中item点击时的接口回调
 */

public interface OnSelectItemClickListener {
    void onItemClickListener(View view, int position,boolean isAddBtn);
}
