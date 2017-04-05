package com.example.acer.hello.MyChange;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.acer.hello.R;
import com.example.acer.hello.VideoActivity;
import com.hitomi.cmlibrary.CircleMenu;
import com.hitomi.cmlibrary.OnMenuSelectedListener;
import com.hitomi.cmlibrary.OnMenuStatusChangeListener;

public class FirstActivity extends AppCompatActivity {
    private CircleMenu circleMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        init();
    }
    private void init() {
        circleMenu = (CircleMenu) findViewById(R.id.circle_menu);

        circleMenu.setMainMenu(Color.parseColor("#4169E1"), R.mipmap.button, R.mipmap.wall)
                .addSubMenu(Color.parseColor("#FF4B32"), R.mipmap.music)
                .addSubMenu(Color.parseColor("#258CFF"), R.mipmap.cloud)
                .addSubMenu(Color.parseColor("#30A400"), R.mipmap.email)
                .addSubMenu(Color.parseColor("#7B68EE"), R.mipmap.dance)
                .addSubMenu(Color.parseColor("#FF6A00"), R.mipmap.laugh)
                .addSubMenu(Color.parseColor("#778899"), R.mipmap.camera)
                .setOnMenuSelectedListener(new OnMenuSelectedListener() {

                    @Override
                    public void onMenuSelected(int index) {
                        switch (index){
                            case 0:
                                Toast.makeText(getApplicationContext(), "播放音乐",
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                Toast.makeText(getApplicationContext(), "天气预报",
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case 2:
                                Toast.makeText(getApplicationContext(), "发送邮件",
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case 3:
                                Toast.makeText(getApplicationContext(), "跳舞",
                                        Toast.LENGTH_SHORT).show();
                                Intent intent3=new Intent(FirstActivity.this,PostureActivity.class);
                                startActivity(intent3);
                                break;
                            case 4:
                                Toast.makeText(getApplicationContext(), "讲笑话",
                                        Toast.LENGTH_SHORT).show();
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
