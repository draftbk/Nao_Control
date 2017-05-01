package com.example.acer.hello.MyChange;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.pdf.PdfRenderer;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.a520wcf.yllistview.YLListView;
import com.example.acer.hello.R;

public class PostureActivity extends AppCompatActivity {
    private YLListView listView;
    private ControlTool controlTool;
    private String posture;
    private String[] postures;
    private String posturetag;
    private String[] posturetags;
    private DemoAdapter myAdapter;
//    private String[] postures=new String[]{"Dance","restAndUp","LHForOpen","RHForOpen","BHCrossDown","BHCrossUp",
//            "ChestForLeft","LHhorUp","BHForOpen","PointToRight","RUpLeftToFor","PointToLeft","UpPointToLeft","ChestForRight","RHhorUp",
//            "RHhorizon","LUpRightToFor","Nao_rest"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posture);
        initControlTool();
        initList();
    }
    private void initControlTool() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                controlTool=ControlTool.getInstance(PostureActivity.this);
            }
        }).start();
    }
    private void initList() {
        SharedPreferences sp = getSharedPreferences("PostureAndTags", Context.MODE_PRIVATE);
        posture = sp.getString("posture", "Dance,restAndUp,LHForOpen,RHForOpen,BHCrossDown,BHCrossUp," +
                "ChestForLeft,LHhorUp,BHForOpen,PointToRight,RUpLeftToFor," +
                "PointToLeft,UpPointToLeft,ChestForRight,RHhorUp, RHhorizon," +
                "LUpRightToFor,Nao_rest");
        posturetag = sp.getString("postureTag", "53,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17");
        postures=posture.split(",");
        posturetag="53,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18";
        posturetags=posturetag.split(",");
        listView = (YLListView) findViewById(R.id.listView);
        // 不添加也有默认的头和底
        View topView=View.inflate(this,R.layout.top,null);
        listView.addHeaderView(topView);
        View bottomView=new View(getApplicationContext());
        listView.addFooterView(bottomView);
        // 顶部和底部也可以固定最终的高度 不固定就使用布局本身的高度
        listView.setFinalBottomHeight(100);
        listView.setFinalTopHeight(200);
        myAdapter=new DemoAdapter();
        listView.setAdapter(myAdapter);
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
                        try {
                            controlTool.connectServerWithTCPSocket(posturetags[finalPosition]);
                        }catch (Exception e){
                            Toast.makeText(PostureActivity.this,"异常",Toast.LENGTH_LONG).show();
                        }
                    }
                }).start();
                Toast.makeText(PostureActivity.this,"发送："+posturetags[finalPosition],Toast.LENGTH_LONG).show();

            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                position=position-listView.getHeaderViewsCount();
                Log.e("test","position"+position);
                final int finalPosition = position;
                dialog_choose(finalPosition);
                return false;
            }
        });
    }
    private void dialog_choose(final int position) {
        final String items[]={"修改","添加","删除"};
        //dialog参数设置
        AlertDialog.Builder builder=new AlertDialog.Builder(this);  //先得到构造器
        builder.setTitle("修改"); //设置标题
        //builder.setMessage("是否确认退出?"); //设置内容
        builder.setIcon(R.mipmap.robot);//设置图标，图片id即可
        //设置列表显示，注意设置了列表显示就不要设置builder.setMessage()了，否则列表不起作用。
        builder.setItems(items,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(PostureActivity.this, items[which], Toast.LENGTH_SHORT).show();
                switch (which){
                    case 0:
                        changePostureTag(position);
                        myAdapter.notifyDataSetChanged();
                        break;
                    case 1:
                        addPostureTag(position);
                        myAdapter.notifyDataSetChanged();
                        break;
                    case 2:
                        movePostureByPosition(position);
                        myAdapter.notifyDataSetChanged();
                        break;
                }
            }

            private void upDateSP() {
                SharedPreferences sp1 = getSharedPreferences("PostureAndTags", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor1 = sp1.edit();
                String tempPosture="";
                String tempPosturetag="";
                if (postures.length>0){
                    tempPosture=postures[0];
                    tempPosturetag=posturetags[0];
                }
                for (int i=1;i<postures.length;i++){
                    tempPosture=tempPosture+","+postures[i];
                    tempPosturetag=tempPosturetag+","+posturetags[i];
                }
                editor1.putString("posture", tempPosture);
                editor1.putString("postureTag", tempPosturetag);
                editor1.commit();
            }

            private void addPostureTag(final int position) {
                //弹出框
                AlertDialog.Builder builder = new AlertDialog.Builder(PostureActivity.this);
                //    通过LayoutInflater来加载一个xml的布局文件作为一个View对象
                View view = LayoutInflater.from(PostureActivity.this).inflate(R.layout.posture_dialog, null);
                //    设置我们自己定义的布局文件作为弹出框的Content
                builder.setView(view);
                final EditText editName = (EditText)view.findViewById(R.id.name);
                final EditText editTag = (EditText)view.findViewById(R.id.tag);
                editName.setText(postures[position]);
                editTag.setText(posturetags[position]);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        String []temp_postures=new String[postures.length+1];
                        String []temp_posturetags=new String[postures.length+1];
                        for(int i=0;i<(postures.length+1);i++){
                            Log.e("test","i...."+i);
                            if (i<=position){
                                temp_postures[i]=postures[i];
                                temp_posturetags[i]=posturetags[i];
                            }else if (i==(position+1)){
                                temp_postures[i]= editName.getText().toString();
                                temp_posturetags[i]=editTag.getText().toString();
                            }else {
                                temp_postures[i]=postures[i-1];
                                temp_posturetags[i]=posturetags[i-1];
                            }
                        }
                        postures=temp_postures;
                        posturetags=temp_posturetags;
                        upDateSP();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                builder.show();
            }

            private void changePostureTag(final int position) {
                //弹出框
                AlertDialog.Builder builder = new AlertDialog.Builder(PostureActivity.this);
                //    通过LayoutInflater来加载一个xml的布局文件作为一个View对象
                View view = LayoutInflater.from(PostureActivity.this).inflate(R.layout.posture_dialog, null);
                //    设置我们自己定义的布局文件作为弹出框的Content
                builder.setView(view);
                final EditText editName = (EditText)view.findViewById(R.id.name);
                final EditText editTag = (EditText)view.findViewById(R.id.tag);
                editName.setText(postures[position]);
                editTag.setText(posturetags[position]);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        postures[position]= editName.getText().toString();
                        posturetags[position]=editTag.getText().toString();
                        upDateSP();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                builder.show();
            }

            private void movePostureByPosition(int position) {
                String []temp_postures=new String[postures.length-1];
                String []temp_posturetags=new String[postures.length-1];
                Log.e("test","postures.length-1"+(postures.length-1));
                for(int i=0;i<(postures.length-1);i++){
                    Log.e("test","i...."+i);
                    if (i<position){
                        temp_postures[i]=postures[i];
                        temp_posturetags[i]=posturetags[i];
                    }else {
                        temp_postures[i]=postures[i+1];
                        temp_posturetags[i]=posturetags[i+1];
                    }
                }
                postures=temp_postures;
                posturetags=temp_posturetags;
                upDateSP();
            }
        });
        builder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(PostureActivity.this, "确定", Toast.LENGTH_SHORT).show();
            }
        });
        builder.create().show();
    }



    class DemoAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return postures.length;
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
