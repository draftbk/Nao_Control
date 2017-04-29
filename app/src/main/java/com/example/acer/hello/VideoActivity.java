package com.example.acer.hello;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.acer.hello.MyChange.ControlTool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class VideoActivity extends AppCompatActivity{

    public View VideoView = null;
    public Button leftBtn,rightBtn,topBtn,bottomBtn,photoBtn= null;
    public Button mPlayVideoButton = null;
    public Handler imageViewHandler = null;
    public ImageView imageView = null;
    public Boolean videoRunning = false;
    public Thread videoThread = null;
    private Bitmap bitmap=null;
    private ControlTool controlTool;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        VideoView = getLayoutInflater().inflate(R.layout.video_main,null);
        setContentView(VideoView);
        init();
        initControlTool();
    }
    private void initControlTool() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                controlTool=ControlTool.getInstance();
            }
        }).start();

    }

    private void init() {
        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.screen);
        // 左右上下
        leftBtn = (Button) findViewById(R.id.left_btn);
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(VideoActivity.this,"左",Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        controlTool.connectServerWithTCPSocket("22");
                    }
                }).start();
            }
        });
        rightBtn = (Button) findViewById(R.id.right_btn);
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(VideoActivity.this,"右",Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        controlTool.connectServerWithTCPSocket("23");
                    }
                }).start();
            }
        });
        topBtn = (Button) findViewById(R.id.top_btn);
        topBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(VideoActivity.this,"上",Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        controlTool.connectServerWithTCPSocket("20");
                    }
                }).start();
            }
        });
        bottomBtn = (Button) findViewById(R.id.bottom_btn);
        bottomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(VideoActivity.this,"下",Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        controlTool.connectServerWithTCPSocket("21");
                    }
                }).start();
            }
        });

        photoBtn = (Button) findViewById(R.id.photo_btn);
        photoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(VideoActivity.this,"照片存储到相册...",Toast.LENGTH_SHORT).show();
//                ControlTool.connectServerWithTCPSocket("1");
//                saveMyBitmap(bitmap,"nao");
                saveImageToGallery(VideoActivity.this,bitmap);
            }
        });

        imageViewHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                imageView.setImageBitmap((Bitmap)msg.obj);
                bitmap=(Bitmap)msg.obj;
            }
        };

        mPlayVideoButton = (Button) findViewById(R.id.playVideo);

        mPlayVideoButton.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v){
                boolean isRunning = false;
                synchronized (videoRunning){
                    Log.e("test","videoRunning.............."+videoRunning);
                    isRunning = videoRunning;
                }
//                if(mPlayVideoButton.getText().equals("播放")){
                if(!isRunning){
                    Log.e("test","videoState.............."+"play");
                    videoRunning = true;
                    GetImage getImage = new GetImage(null,imageViewHandler);
                    videoThread = new Thread(getImage);
                    videoThread.start();
                    mPlayVideoButton.setText("停止播放");
                }
                else{
                    Log.e("test","videoState............................"+"stop");
                    videoRunning = false;
                    videoThread.interrupt();
                    mPlayVideoButton.setText("播放");
                }

            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        System.out.println(this.getLocalClassName()+"  resumed");
    }

    @Override
    protected void onPause(){
        super.onPause();
        if (videoRunning){
            videoRunning = false;
            videoThread.interrupt();
        }
        System.out.println(this.getLocalClassName()+"  paused");
    }


    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
    }
    public static void saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "Nao_pic");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            Log.e("test",9+"");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
            Log.e("test",10+"");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
    }

}
