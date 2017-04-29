package com.example.acer.hello.MyChange;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.acer.hello.MainActivity;
import com.example.acer.hello.R;
import com.example.acer.hello.VideoActivity;
import com.hitomi.cmlibrary.CircleMenu;
import com.hitomi.cmlibrary.OnMenuSelectedListener;
import com.hitomi.cmlibrary.OnMenuStatusChangeListener;

import java.net.Socket;

public class FirstActivity extends AppCompatActivity {
    private CircleMenu circleMenu;
    private  ControlTool controlTool;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        initControlTool();
        init();
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
        circleMenu = (CircleMenu) findViewById(R.id.circle_menu);

        circleMenu.setMainMenu(Color.parseColor("#4169E1"), R.mipmap.button, R.mipmap.wall)
                .addSubMenu(Color.parseColor("#FF4B32"), R.mipmap.music)
                .addSubMenu(Color.parseColor("#258CFF"), R.mipmap.cloud)
                .addSubMenu(Color.parseColor("#7B68EE"), R.mipmap.dance)
                .addSubMenu(Color.parseColor("#30A400"), R.mipmap.settings)
                .addSubMenu(Color.parseColor("#FF6A00"), R.mipmap.laugh)
                .addSubMenu(Color.parseColor("#778899"), R.mipmap.camera)
                .setOnMenuSelectedListener(new OnMenuSelectedListener() {
                    @Override
                    public void onMenuSelected(int index) {
                        switch (index){
                            case 0:
                                Toast.makeText(getApplicationContext(), "播放音乐",
                                        Toast.LENGTH_SHORT).show();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                            controlTool.connectServerWithTCPSocket("31");
                                    }
                                }).start();
                                break;
                            case 1:
                                Toast.makeText(getApplicationContext(), "天气预报",
                                        Toast.LENGTH_SHORT).show();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        controlTool.connectServerWithTCPSocket("25");
                                    }
                                }).start();
                                break;
                            case 2:
                                Toast.makeText(getApplicationContext(), "列表",
                                        Toast.LENGTH_SHORT).show();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        controlTool.connectServerWithTCPSocket("24");
                                    }
                                }).start();
                                Intent intent2=new Intent(FirstActivity.this,PostureActivity.class);
                                startActivity(intent2);
                                break;
                            case 3:
                                Toast.makeText(getApplicationContext(), "设置",
                                        Toast.LENGTH_SHORT).show();
                                Intent intent3=new Intent(FirstActivity.this,MainActivity.class);
                                startActivity(intent3);
                                break;
                            case 4:
                                Toast.makeText(getApplicationContext(), "讲笑话",
                                        Toast.LENGTH_SHORT).show();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        controlTool.connectServerWithTCPSocket("32");
                                    }
                                }).start();
                                break;
                            case 5:
                                Toast.makeText(getApplicationContext(), "视觉",
                                        Toast.LENGTH_SHORT).show();
                                Intent intent5= new Intent(FirstActivity.this, VideoActivity.class);
                                startActivity(intent5);
                                break;
                        }
                    }

                }).setOnMenuStatusChangeListener(new OnMenuStatusChangeListener() {

            @Override
            public void onMenuOpened() {}

            @Override
            public void onMenuClosed() {}

        });

    }
}
