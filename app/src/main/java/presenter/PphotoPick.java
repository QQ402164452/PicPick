package presenter;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

import bean.ImageFolder;
import interfaces.IphotoPick;

/**
 * Created by Jason on 2016/12/21.
 * MVP模式中的Presenter 习惯将Model业务逻辑写在Presenter 容易理解
 */

public class PphotoPick  {
    private IphotoPick mView;
    private HashMap<String,ImageFolder> mHashMap;//标志Map 主要来确保文件夹对象的唯一性 避免创建多个相同的对象
    private ArrayList<ImageFolder> mFolder;//保存文件夹对象的数组
    private ArrayList<String> mTotalPath;//保存系统中所有图片的绝对路径的数组

    public PphotoPick(IphotoPick iphotoPick){
        mView=iphotoPick;
    }

    public void getAllPhoto(final Context context){
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){//判断是否有外部存储空间
            mView.showToast("暂无外部存储");
            return;
        }
        new Thread(new Runnable() {//启动一个子线程来做耗时的获取系统图片的线程 防止ANR
            @Override
            public void run() {
                Uri imageUri= MediaStore.Images.Media.EXTERNAL_CONTENT_URI;//系统中图片的URI
                ContentResolver contentResolver=context.getContentResolver();//用contentResolver来访问ContentProvider
                Cursor cursor=contentResolver.query(imageUri,null,
                        MediaStore.Images.Media.MIME_TYPE+"=? or "
                +MediaStore.Images.Media.MIME_TYPE+"=?",
                        new String[]{"image/jpeg","image/png"},//获取jpeg,png,jpg格式的图片
                        MediaStore.Images.Media.DATE_MODIFIED+" DESC");//按照时间降序排列
                final int total=cursor.getCount();
                if(total>0){
                    mTotalPath=new ArrayList<>(total+1);//避免内部多次扩容
                    mTotalPath.add("_TAKE_PHOTO");//拍摄图片的Item的占位符
                    mFolder=new ArrayList<>();
                    mHashMap=new HashMap<>();

                    cursor.moveToFirst();
                    String firstImage=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));//获取系统中第一张图片的绝对路径

//                    FilenameFilter filenameFilter=new FilenameFilter() {//文件名过滤器
//                        @Override
//                        public boolean accept(File dir, String name) {
//                            if(name.endsWith(".jpg")||
//                                    name.endsWith(".png")||
//                                    name.endsWith(".jpeg")){
//                                return true;
//                            }else{
//                                return false;
//                            }
//                        }
//                    };

                    cursor.moveToPrevious();
                    while(cursor.moveToNext()){
                        String path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));//获取图片的路径
                        mTotalPath.add(path);

                        File parentFile=new File(path).getParentFile();//获取文件的文件夹对象
                        if(parentFile==null){
                            continue;
                        }

                        String dirPath=parentFile.getAbsolutePath();//获取文件的文件夹绝对路径
                        if(!mHashMap.containsKey(dirPath)){
//                            int picCount=parentFile.list(filenameFilter).length;//获取当前目录中所有图片的数量

                            ImageFolder folder=new ImageFolder(dirPath,path);
                            folder.addPath("_TAKE_PHOTO");//拍摄图片的Item的占位符
                            folder.addPath(path);

                            mHashMap.put(dirPath,folder);
                            mFolder.add(folder);
                        }else{
                            mHashMap.get(dirPath).addPath(path);
                        }
                    }

                    ImageFolder totalImage=new ImageFolder("所有图片",total,firstImage,mTotalPath);
                    mFolder.add(0,totalImage);//将所有图片的Item插入到文件夹选择的第一位置
                    cursor.close();
                    mHashMap=null;//释放内存
                    mView.setData(mTotalPath,mFolder);//接口回调 显示数据
                }
            }
        }).start();
    }
}
