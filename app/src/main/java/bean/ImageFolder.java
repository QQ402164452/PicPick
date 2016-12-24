package bean;

import java.util.ArrayList;

/**
 * Created by Jason on 2016/12/21.
 * 文件夹对象数组
 */

public class ImageFolder {
    private String dir;//文件夹路径
    private String firstImagePath;//第一张图片的路径
    private String name;//文件夹的名称
    private int count;//图片的数量
    private ArrayList<String> imgPaths;//当前文件夹中包含的所有图片的绝对路径

    public ImageFolder(String dir,String firstImagePath,int count){
        this.dir=dir;
        this.firstImagePath=firstImagePath;
        this.count=count;
        int lastIndexOf=this.dir.lastIndexOf("/");//从文件路径中提取文件名
        this.name=dir.substring(lastIndexOf+1);
        imgPaths=new ArrayList<>();
    }

    public ImageFolder(String name,int count,String firstImagePath,ArrayList<String> imgPaths){
        this.name=name;
        this.count=count;
        this.dir="";
        this.firstImagePath=firstImagePath;
        this.imgPaths=imgPaths;
    }

    public void addPath(String str){
        imgPaths.add(str);
    }

    public ArrayList<String> getImgPaths() {
        return imgPaths;
    }

    public void setImgPaths(ArrayList<String> imgPaths) {
        this.imgPaths = imgPaths;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstImagePath() {
        return firstImagePath;
    }

    public void setFirstImagePath(String firstImagePath) {
        this.firstImagePath = firstImagePath;
    }
}
