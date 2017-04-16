package com.example.acer.hello;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.acer.hello.MyChange.ControlTool;


public class VideoActivity extends AppCompatActivity{

    public View VideoView = null;
    public Button leftBtn,rightBtn,topBtn,bottomBtn = null;
    public Button mPlayVideoButton = null;
    public Handler imageViewHandler = null;
    public ImageView imageView = null;
    public Boolean videoRunning = false;
    public Thread videoThread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        VideoView = getLayoutInflater().inflate(R.layout.video_main,null);
        setContentView(VideoView);
        init();
    }

    private void init() {
        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.ic_launcher);
        // 左右上下
        leftBtn = (Button) findViewById(R.id.left_btn);
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(VideoActivity.this,"左",Toast.LENGTH_SHORT).show();
//                ControlTool.connectServerWithTCPSocket("1");
            }
        });
        rightBtn = (Button) findViewById(R.id.right_btn);
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(VideoActivity.this,"右",Toast.LENGTH_SHORT).show();
//                ControlTool.connectServerWithTCPSocket("1");
            }
        });
        topBtn = (Button) findViewById(R.id.top_btn);
        topBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(VideoActivity.this,"上",Toast.LENGTH_SHORT).show();
//                ControlTool.connectServerWithTCPSocket("1");
            }
        });
        bottomBtn = (Button) findViewById(R.id.bottom_btn);
        bottomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(VideoActivity.this,"下",Toast.LENGTH_SHORT).show();
//                ControlTool.connectServerWithTCPSocket("1");
            }
        });

        imageViewHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                imageView.setImageBitmap((Bitmap)msg.obj);
            }
        };

        mPlayVideoButton = (Button) findViewById(R.id.playVideo);

        mPlayVideoButton.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v){
                boolean isRunning = false;
                synchronized (videoRunning){
                    isRunning = videoRunning;
                }
                if(!isRunning){
                    videoRunning = true;
                    GetImage getImage = new GetImage(null,imageViewHandler);
                    videoThread = new Thread(getImage);
                    videoThread.start();
                    mPlayVideoButton.setText("停止播放");
                }
                else{
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
        System.out.println(this.getLocalClassName()+"  paused");
    }


    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
    }

}
