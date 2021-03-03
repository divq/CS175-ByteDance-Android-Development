package com.example.homework1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.widget.TextView;

//测试用
//import java.text.SimpleDateFormat;
//import java.util.Date;

public class DisplayActivity extends AppCompatActivity {
    Handler handler;
    Runnable runnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        Intent intent = getIntent();
        String text = intent.getStringExtra("extra");
        TextView textView = findViewById(R.id.textview);
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                String htmlText = "";
                int step = 0xFFFFFF / text.length();
                int start = (int)(Math.random()*(0xFFFFFF));
                for(int i =0 ;i<text.length();i++)
                {
                    int rgb = (start + step*i)%0xFFFFFF;
                    String st = Integer.toHexString(rgb).toUpperCase();
                    st = String.format("%6s",st);
                    st= st.replaceAll(" ","0");

                    htmlText+="<span style=\"color:#"+st+"\">"+text.charAt(i)+"</span>";
                }

                textView.setText(Html.fromHtml(htmlText));

//              测试handler在按返回键后是否会结束
//                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_hhmmss");
//                Date date = new Date();
//                String curDatetimeStr =simpleDateFormat.format(date);
//                System.out.println("在输出字符"+curDatetimeStr);

                handler.postDelayed(this,200);
            }
        };
        handler.postDelayed(runnable,100);
    }
    public void onBackPressed() {
        NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
        handler.removeCallbacks(runnable);
        finish();
    }
}