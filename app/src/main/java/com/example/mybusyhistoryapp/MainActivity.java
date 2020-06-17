package com.example.mybusyhistoryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CurveLineChartView cur_tem_hum_temper=findViewById(R.id.cur_tem_hum_temper);
        BusyFlowChartView cur_busy_flow=findViewById(R.id.cur_busy_flow);

        //温度表
        cur_tem_hum_temper.setData(chartdata.getDatas());
        //繁忙流量数据
        cur_busy_flow.setData(chartdata.getDatas());
    }
}
