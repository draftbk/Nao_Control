package com.example.acer.hello.MyChange;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.a520wcf.yllistview.YLListView;
import com.example.acer.hello.R;

public class PostureActivity extends AppCompatActivity {
    private YLListView listView;
    private String[] postures=new String[]{"姿势一","姿势一","姿势一","姿势一","姿势一",
            "姿势一","姿势一","姿势一","姿势一","姿势一","姿势一","姿势一","姿势一","姿势一",
            "姿势一","姿势一","姿势一","姿势一"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posture);
        initList();
    }

    private void initList() {
        listView = (YLListView) findViewById(R.id.listView);
        // 不添加也有默认的头和底
        View topView=View.inflate(this,R.layout.top,null);
        listView.addHeaderView(topView);
        View bottomView=new View(getApplicationContext());
        listView.addFooterView(bottomView);

        // 顶部和底部也可以固定最终的高度 不固定就使用布局本身的高度
        listView.setFinalBottomHeight(100);
        listView.setFinalTopHeight(200);

        listView.setAdapter(new DemoAdapter());

        //YLListView默认有头和底  处理点击事件位置注意减去
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position=position-listView.getHeaderViewsCount();
                Log.e("test","position"+position);
                final int finalPosition = position;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ControlTool.connectServerWithTCPSocket((finalPosition +2)+"");//2-17
                    }
                }).start();

            }
        });
    }
    class DemoAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return 18;
        }
        @Override
        public Object getItem(int position) {
            return position;
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv;
            if(convertView!=null&&convertView instanceof TextView){
                tv= (TextView) convertView;
            }else{
                tv=new TextView(getApplicationContext());
                tv.setTextColor(Color.BLACK);
                tv.setBackgroundResource(R.drawable.bg_item);
                tv.setGravity(1);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,30);
            }
            tv.setText(postures[position]);
            return tv;
        }

    }
}
