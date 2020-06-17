package com.example.mybusyhistoryapp;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by TFHR02 on 2016/10/12.
 */
public class chartdata {
    private float Ymaxvalues;
    private float Yminvalues;
    //代表某种折线的点集合
    private List<MyPoint> points;
    private List<MyPoint> points2;
    private List<MyPoint> points3;

    //x轴数据单位
    private String Xunit=null;
    //yz轴数据单位
    private String Yunit=null;



    //--------------------------------封装的测试数据---------------------------------
    public static Random random = new Random();
    //目前对外的测试数据
    public static chartdata datas=null;
    public static chartdata datas3=null;

    public static List<MyPoint> lists = new ArrayList<MyPoint>();
    public static List<MyPoint> lists2 = new ArrayList<MyPoint>();
    public static List<MyPoint> lists3 = new ArrayList<MyPoint>();
    public static int xarray[] = new int[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17, 18, 19, 20, 21, 22, 23, 24};
//    public static int xarray[] = new int[]{9,10,11};

    static {
        for (int i = 0; i < xarray.length; i++) {
            float yy = random.nextFloat() * 640 ;
            float yy2 = random.nextFloat() * 640 ;
            float yy3 = random.nextFloat() * 640 ;

            //若营业时间在03：00-22：00 则在1点、2点 、23点、24点无数据
            if (i==0||i==1||i==22||i==23){
                yy=1;
                yy2=1;
                yy3=1;
            }

            //构造一条数据
            MyPoint point = new MyPoint();
            point.x = xarray[i]+"";
            point.y = (int) yy;
            //构造两条数据
            MyPoint point2 = new MyPoint();
            point2.x = xarray[i]+"";
            point2.y = (int) yy2;
            //构造三条数据
            MyPoint point3 = new MyPoint();
            point3.x = xarray[i]+"";
            point3.y = (int) yy3;

            lists.add(point);
            lists2.add(point2);
            lists3.add(point3);
        }
        datas=new chartdata(648,5,lists,null,null);
        datas3=new chartdata(648,5,lists,lists2,lists3);
    }
//----------------------------------------------------------------------
    public chartdata(){};

    public chartdata(float ymaxvalues, float yminvalues, List<MyPoint> points, List<MyPoint> points2, List<MyPoint> points3) {
        Ymaxvalues = ymaxvalues;
        Yminvalues = yminvalues;
        this.points = points;
        this.points2 = points2;
        this.points3 = points3;
    }

    public static chartdata getDatas3() {
        return datas3;
    }

    public static void setDatas3(chartdata datas3) {
        chartdata.datas3 = datas3;
    }

    public static chartdata getDatas() {
        return datas;
    }

    public static void setDatas(chartdata datas) {
        chartdata.datas = datas;
    }

    public List<MyPoint> getPoints2() {
        return points2;
    }

    public void setPoints2(List<MyPoint> points2) {
        this.points2 = points2;
    }

    public List<MyPoint> getPoints3() {
        return points3;
    }

    public void setPoints3(List<MyPoint> points3) {
        this.points3 = points3;
    }

    public List<MyPoint> getPoints() {
        return points;
    }

    public void setPoints(List<MyPoint> points) {
        this.points = points;
    }

    public static Random getRandom() {
        return random;
    }

    public static void setRandom(Random random) {
        chartdata.random = random;
    }

    public float getYmaxvalues() {
        return Ymaxvalues;
    }

    public void setYmaxvalues(float ymaxvalues) {
        Ymaxvalues = ymaxvalues;
    }

    public float getYminvalues() {
        return Yminvalues;
    }

    public void setYminvalues(float yminvalues) {
        Yminvalues = yminvalues;
    }

    public String getXunit() {
        return Xunit;
    }

    public void setXunit(String xunit) {
        Xunit = xunit;
    }

    public String getYunit() {
        return Yunit;
    }

    public void setYunit(String yunit) {
        Yunit = yunit;
    }
}
