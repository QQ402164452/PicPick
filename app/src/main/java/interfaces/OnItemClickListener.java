package interfaces;

import android.view.View;

/**
 * Created by Jason on 2016/12/23.
 * RecyclerView itemView点击时回调的接口
 */

public interface OnItemClickListener {
    void onItemClick(View view, int position);
}
