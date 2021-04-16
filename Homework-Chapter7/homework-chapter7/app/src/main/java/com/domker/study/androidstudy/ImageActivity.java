package com.domker.study.androidstudy;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ImageActivity extends AppCompatActivity {
    ViewPager pager = null;
    LayoutInflater layoutInflater = null;
    List<View> pages = new ArrayList<View>();

    private void addImageFromURL(String URL){
        ImageView imageView = (ImageView)layoutInflater.inflate(R.layout.activity_image_item,null);
        Glide.with(this).load(URL).apply(new RequestOptions().circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL))
                .error(R.drawable.error)
                .into(imageView);
        pages.add(imageView);
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        layoutInflater = getLayoutInflater();
        pager = (ViewPager) findViewById(R.id.view_pager);
        addImageFromURL("https://th.bing.com/th/id/R47747671a551dddbea10f763561919e7?rik=cxA9G%2b74Fk3%2b4w&riu=http%3a%2f%2fbestanimations.com%2fAnimals%2fMammals%2fDolphins%2fdolphin%2fdolphin-animated-gif-13.gif&ehk=W95mcKc3uANIKhikTm6Ka6gOvmTX3WyclPhbOuw2VYc%3d&risl=&pid=ImgRaw");
        addImageFromURL("https://th.bing.com/th/id/R9652f289de894e1eb590ecd6dfeae11a?rik=PE%2fBXfV7tRpEmg&pid=ImgRaw");
        ViewAdapter adapter = new ViewAdapter();
        adapter.setDatas(pages);
        pager.setAdapter(adapter);
    }
}
