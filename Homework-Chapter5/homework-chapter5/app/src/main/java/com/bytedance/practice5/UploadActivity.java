package com.bytedance.practice5;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bytedance.practice5.model.UploadResponse;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UploadActivity extends AppCompatActivity {
    private static final String TAG = "chapter5";
    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024;
    private static final int REQUEST_CODE_COVER_IMAGE = 101;
    private static final String COVER_IMAGE_TYPE = "image/*";
    private IApi api;
    private Uri coverImageUri;
    private SimpleDraweeView coverSD;
    private EditText toEditText;
    private EditText contentEditText ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initNetwork();
        setContentView(R.layout.activity_upload);
        coverSD = findViewById(R.id.sd_cover);
        toEditText = findViewById(R.id.et_to);
        contentEditText = findViewById(R.id.et_content);
        findViewById(R.id.btn_cover).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFile(REQUEST_CODE_COVER_IMAGE, COVER_IMAGE_TYPE, "选择图片");
            }
        });


        findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitMessageWithURLConnection();
                //submit();
                //submit, submitMessageWithURLConnection都能成功上传
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE_COVER_IMAGE == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                coverImageUri = data.getData();
                coverSD.setImageURI(coverImageUri);

                if (coverImageUri != null) {
                    Log.d(TAG, "pick cover image " + coverImageUri.toString());
                } else {
                    Log.d(TAG, "uri2File fail " + data.getData());
                }

            } else {
                Log.d(TAG, "file pick fail");
            }
        }
    }

    private void initNetwork() {
        //TODO 3
        // 创建Retrofit实例
        // 生成api对象
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        api = retrofit.create(IApi.class);
    }

    private void getFile(int requestCode, String type, String title) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(type);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        intent.putExtra(Intent.EXTRA_TITLE, title);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, requestCode);
    }

    private void submit() {
        byte[] coverImageData = readDataFromUri(coverImageUri);
        if (coverImageData == null || coverImageData.length == 0) {
            Toast.makeText(this, "封面不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        String to = toEditText.getText().toString();
        if (TextUtils.isEmpty(to)) {
            Toast.makeText(this, "请输入TA的名字", Toast.LENGTH_SHORT).show();
            return;
        }
        String content = contentEditText.getText().toString();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "请输入想要对TA说的话", Toast.LENGTH_SHORT).show();
            return;
        }

        if ( coverImageData.length >= MAX_FILE_SIZE) {
            Toast.makeText(this, "文件过大", Toast.LENGTH_SHORT).show();
            return;
        }
        //TODO 5
        // 使用api.submitMessage()方法提交留言
        // 如果提交成功则关闭activity，否则弹出toast
        MultipartBody.Part multipart_from =
                MultipartBody.Part.createFormData("from",Constants.USER_NAME);
        MultipartBody.Part multipart_to =
                MultipartBody.Part.createFormData("to", to);
        MultipartBody.Part multipart_content =
                MultipartBody.Part.createFormData("content",content);
        MultipartBody.Part multipart_image =
                MultipartBody.Part.createFormData("image",
                "cover.png",
                RequestBody.create(MediaType.parse("multipart/form-data"), coverImageData));
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<UploadResponse> call = api.submitMessage(
                        Constants.STUDENT_ID,
                        "",
                        multipart_from,
                        multipart_to,
                        multipart_content,
                        multipart_image,
                        Constants.token);
                try {
                    Response<UploadResponse> response = call.execute();
                    if (response.isSuccessful() && response.body().success) {
                        Log.d("upload","success");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(UploadActivity.this,
                                        "上传成功!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        UploadActivity.this.finish();
                    }
                    else {
                        Log.d("upload","failure");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(UploadActivity.this,
                                        "上传失败!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    // TODO 7 选做 用URLConnection的方式实现提交
    private void submitMessageWithURLConnection(){
        byte[] coverImageData = readDataFromUri(coverImageUri);
        if (coverImageData == null || coverImageData.length == 0) {
            Toast.makeText(this, "封面不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        String to = toEditText.getText().toString();
        if (TextUtils.isEmpty(to)) {
            Toast.makeText(this, "请输入TA的名字", Toast.LENGTH_SHORT).show();
            return;
        }
        String content = contentEditText.getText().toString();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "请输入想要对TA说的话", Toast.LENGTH_SHORT).show();
            return;
        }

        if ( coverImageData.length >= MAX_FILE_SIZE) {
            Toast.makeText(this, "文件过大", Toast.LENGTH_SHORT).show();
            return;
        }



        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Uri.Builder builder = Uri.parse(Constants.BASE_URL).buildUpon()
                            .appendPath("messages")
                            .appendQueryParameter("student_id", Constants.STUDENT_ID)
                            .appendQueryParameter("extra_value", "");
                    URL url = new URL(builder.build().toString());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.setConnectTimeout(6000);
                    conn.setRequestMethod("POST");
                    conn.addRequestProperty("token", Constants.token);

                    String boundary = "2cbbd91c-8783-4b6d-912f-6366cfc1c429";

                    conn.addRequestProperty("Content-Type",
                            "multipart/form-data;boundary=" + boundary);

                    String newLine = "\r\n";
                    String postBody = "";

                    String[] keys = {"from", "to", "content"};
                    String[] values = {Constants.USER_NAME, to, content};

                    for(int i = 0; i < keys.length; i++)
                    {
                        postBody += ("--" + boundary);
                        postBody += newLine;
                        postBody += String.format("Content-Disposition: form-data; name=\"%s\"",
                                keys[i]);
                        postBody += newLine;
                        postBody += String.format("Content-Length: %d", values[i].length());
                        postBody += newLine;
                        postBody += newLine;
                        postBody += values[i];
                        postBody += newLine;
                    }

                    // constructing the image
                    postBody += ("--" + boundary);
                    postBody += newLine;
                    postBody +=
                            "Content-Disposition: form-data; name=\"image\"; filename=\"cover.png\"";
                    postBody += newLine;
                    postBody += "Content-Type: multipart/form-data";
                    postBody += newLine;
                    postBody += String.format("Content-Length: %d", coverImageData.length);
                    postBody += newLine;
                    postBody += newLine;


                    conn.getOutputStream().write(postBody.getBytes());
                    conn.getOutputStream().write(coverImageData);
                    conn.getOutputStream().write(("\r\n--" + boundary + "--\r\n").getBytes());
                    conn.getOutputStream().flush();

                    if(conn.getResponseCode() == 200) {
                        Log.d("upload","success");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(UploadActivity.this,
                                        "上传成功!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        UploadActivity.this.finish();
                    }
                    else {
                        Log.d("upload","failure");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(UploadActivity.this,
                                        "上传失败!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private byte[] readDataFromUri(Uri uri) {
        byte[] data = null;
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            data = Util.inputStream2bytes(is);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }


}
