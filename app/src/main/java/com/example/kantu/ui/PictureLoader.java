package com.example.kantu.ui;


import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class PictureLoader  {

    private ImageView loadImg;
    private String  imgUrl;
    private byte[] picByte;

    @SuppressLint("HandlerLeak")
    Handler handler=new Handler() {

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            if (msg.what==0x123){
                if (picByte!=null){
                    Bitmap bitmap= BitmapFactory.decodeByteArray(picByte,0,picByte.length);
                   loadImg.setImageBitmap(bitmap);

                }
            }

        }
    };


    public void  load(ImageView loadImg,String imgUrl){
        this.loadImg=loadImg;
        this.imgUrl=imgUrl;

        //ImageView 转为 drawable
        Drawable drawable=loadImg.getDrawable();

        if (drawable!=null&&drawable instanceof BitmapDrawable){//转换.9图

            //drawable 转为 bitmap
            Bitmap bitmap=((BitmapDrawable) drawable).getBitmap();

            if (bitmap!=null && !bitmap.isRecycled()){//判断位图内存是否已释放
                bitmap.recycle();//回收位图
            }
        }

        //启动线程
        new Thread(runnable ).start();
    }

    Runnable runnable =new Runnable() {
        @Override
        public void run() {

            try {
                URL url=new URL(imgUrl);
                HttpURLConnection conn= (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(10000);

                if (conn.getResponseCode() == 200){//成功

                    InputStream in=conn.getInputStream();
                    ByteArrayOutputStream out=new ByteArrayOutputStream();

                    byte[] bytes= new byte[1024];
                    int length= -1;
                    while ((length=in.read(bytes))!=-1){
                        out.write(bytes,0,length);
                    }

                    picByte=out.toByteArray();
                    in.close();
                    out.close();
                    handler.sendEmptyMessage(0x123);
                }


            } catch (IOException e) {
                e.printStackTrace();
                Log.e("zjq2",e.getMessage());

            }
        }
    };





}
