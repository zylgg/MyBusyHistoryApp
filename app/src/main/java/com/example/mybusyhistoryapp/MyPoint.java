package com.example.mybusyhistoryapp;

/**
 * Created by TFHR02 on 2016/10/12.
 */
public class MyPoint {
    public String x;
    public  float y;

    public MyPoint(){}
    public MyPoint(String x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }
}
