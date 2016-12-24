package interfaces;

import java.util.ArrayList;

import bean.ImageFolder;

/**
 * Created by Jason on 2016/12/21.
 * 图片选择的Interface回调
 */

public interface IphotoPick {
    void showToast(String str);
    void setData(ArrayList<String> path, ArrayList<ImageFolder> folders);
}
