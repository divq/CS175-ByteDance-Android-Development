package com.bytedance.practice5;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.bytedance.practice5.model.*;
import com.bytedance.practice5.socket.SocketActivity;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "chapter5";
    private FeedAdapter adapter = new FeedAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Fresco.initialize(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = findViewById(R.id.rv_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        findViewById(R.id.btn_upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,UploadActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.btn_mine).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData(Constants.STUDENT_ID);
            }
        });

        findViewById(R.id.btn_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData(null);
            }
        });
        findViewById(R.id.btn_socket).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SocketActivity.class);
                startActivity(intent);
            }
        });



    }

    //TODO 2
    // 用HttpUrlConnection实现获取留言列表数据，用Gson解析数据，更新UI（调用adapter.setData()方法）
    // 注意网络请求和UI更新分别应该放在哪个线程中
    private void getData(String studentId){
        new Thread(new Runnable() {
            @Override
            public void run() {
                MessageListResponse response = baseGetMessagesFromRemote(studentId);
                if(response == null || !response.success)
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "获取失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                else{
                    List<Message> messageList = response.feeds;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.setData(messageList);
                        }
                    });
                }
            }
        }).start();
    }

    private MessageListResponse baseGetMessagesFromRemote(String studentID)
    {
        Uri.Builder builder = Uri.parse(Constants.BASE_URL).buildUpon()
                .appendPath("messages")
                .appendQueryParameter("student_id", studentID == null ? "" : studentID);

        MessageListResponse messageListResponse = null;
        try {
            URL url = new URL(builder.build().toString());
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(6000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("token", Constants.token);
            if(conn.getResponseCode() == 200){
                InputStream in = conn.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(in, StandardCharsets.UTF_8));
                messageListResponse = new Gson().fromJson(bufferedReader, MessageListResponse.class);
                bufferedReader.close();
                in.close();
            }
            else{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "获取失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "获取失败", Toast.LENGTH_SHORT).show();
                }
            });
        }
        return messageListResponse;
    }
}