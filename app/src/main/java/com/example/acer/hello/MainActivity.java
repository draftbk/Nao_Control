package com.example.acer.hello;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.method.KeyListener;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.aldebaran.qi.Future;
import com.example.acer.hello.MyChange.ControlTool;
import com.example.acer.hello.MyChange.FirstActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    public Button stopBehaviorButton = null;
    public Switch connectSwitch = null;
    public Spinner deviceSpinner = null;
    public ArrayAdapter<String> deviceSpinnerAdapter = null;
    public Handler deviceSpinnerHandler = null;
    public Handler BehaviorTreeHandler = null;
    public EditText ipTextfield,serve_ipEdit,serve_portEdit = null;
    public boolean imgRunning = false;
    public Object objLock = new Object();
    public Set deviceSet = new HashSet();
    public ArrayList<String> deviceNames = null;
    public Toolbar toolbar = null;
    public Handler runningBehaviorTextViewHandler = null;
    public TextView runningBehaviorTextView = null;
    public KeyListener keyListener = null;
    public PostureManager postureManager = null;
    public ArrayList<String> postureNames = null;
    public Handler postureManagerHandler = null;
    public TextView currentPostureView = null;
    public StiffnessManager stiffnessManager = null;
    public Handler stiffnessHandler = null;
    public ImageButton stiffnessButton = null;
    public ControlTool controlTool;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }
    private void initControlTool() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                controlTool= ControlTool.getInstance(MainActivity.this);
            }
        }).start();

    }
    private void stopMySocket() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                controlTool.connectServerWithTCPSocket("exit");
                controlTool.stopScoket();
            }
        }).start();

    }
    private void init(){
        serve_ipEdit= (EditText) findViewById(R.id.ipInputAddress);
        serve_portEdit= (EditText) findViewById(R.id.ipInputPort);
        checkIfFirstRunning();
        initToolbar();
        initRunningBehavior();
        initCrashHandler();
        initDeviceSpinner();
        initStopBehaviorButton();
        initConnectionButton();
        initSwtichButton();
        initBonjour();
        initPostureManager();
        initStiffnessManager();
        trybedown();
    }
//    后来进来的时候的操作
    private void trybedown() {
        if (Naoqi.getInstance().isRunning()){
            connectSwitch.setChecked(true);
        }
    }

    private void checkIfFirstRunning(){
        SharedPreferences sharedPreferences =
                this.getSharedPreferences("share", MODE_PRIVATE);
        String _version = sharedPreferences.getString(Version.VersionKeyForSharedPreferences,"none");
        System.out.println("Got Version in Preferences: "+_version);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

    }

    private void initToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //Toolbar的setTitle方法要在setSupportActionBar(toolbar)之前调用，否则不起作用
        toolbar.setTitle("连接");
        setSupportActionBar(toolbar);
    }

    private void initRunningBehavior(){
        runningBehaviorTextView = (TextView) findViewById(R.id.runningTextView);

        runningBehaviorTextViewHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg){
                runningBehaviorTextView.setText("正在运行的行为："+(String) msg.obj);
            }
        };

        BehaviorManager.getInstance().setRunningBehaviorTextViewHandler(runningBehaviorTextViewHandler);

        BehaviorManager.getInstance().setBehaviorTreeHandler(runningBehaviorTextViewHandler);
    }

    private void initCrashHandler(){
        CrashHandler handler = CrashHandler.getInstance();
        handler.init(getApplicationContext());
    }

    private void initDeviceSpinner(){
        ipTextfield = (EditText) findViewById(R.id.ipInputBox);
        deviceSpinner = (Spinner) findViewById(R.id.deviceSpinner);
        deviceNames = new ArrayList();
        deviceNames.add("none");
        deviceSpinnerAdapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item, deviceNames);
        deviceSpinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        deviceSpinner.setAdapter(deviceSpinnerAdapter);
        deviceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("onItemSelected");
                int backSplashPos = 0;
                int endPosOfIP = 0;
                String selected = " ";
                try {
                    selected = deviceNames.get(position);
                    backSplashPos = selected.indexOf("/");
                    endPosOfIP = selected.indexOf(":");

                    if(backSplashPos > 0 && endPosOfIP > backSplashPos){
                        String _IP = selected.substring(backSplashPos + 1, endPosOfIP);
                        ipTextfield.setText(_IP);
                        System.out.println("IP: " + _IP);
                    }
                }
                catch (NullPointerException e){
                    e.printStackTrace();
                }

                System.out.println(selected);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                System.out.println("onNothingSelected");
            }
        });

        deviceSpinnerHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg){
                deviceNames.add((String) msg.obj);
            }
        };

    }

    private void initStopBehaviorButton(){

        stopBehaviorButton = (Button) findViewById(R.id.StopBehavior);
        stopBehaviorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    BehaviorManager.getInstance().getBehaviorManagerObject()
                            .call("stopAllBehaviors");
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initConnectionButton(){

        connectSwitch = (Switch) findViewById(R.id.connectButton);
        connectSwitch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                boolean running = false;
                synchronized (objLock){
                    running = imgRunning;
                }
                if (serve_ipEdit.getText().toString().equals("")||serve_portEdit.getText().toString().equals("")||ipTextfield.getText().toString().equals("")){
                    Toast.makeText(MainActivity.this,"请填写完整的信息",Toast.LENGTH_SHORT).show();
                    connectSwitch.setChecked(false);
                }
                else {
                    if(!running){
                        imgRunning = true;
                        SharedPreferences sp = getSharedPreferences("serve_ip", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("ip",serve_ipEdit.getText().toString());
                        editor.putInt("port", Integer.parseInt(serve_portEdit.getText().toString()));
                        editor.putInt("tag", 1);
                        editor.commit();
                        Editable editableText;
                        try {
                            editableText = ipTextfield.getText();
                            keyListener = ipTextfield.getKeyListener();
                            ipTextfield.setKeyListener(null);
                        }
                        catch (NullPointerException e){
                            return;
                        }
                        String ip = editableText.toString();
                        if(Utillity.isIPValid(ip)){
                            Naoqi.getInstance().init("tcp://" + ip + ":9559");
                            getToolbar().setTitle("Connected to "+ip);
                            try {
                                BehaviorManager.getInstance().Init(Naoqi.getInstance());
                                postureManager.Init(postureManagerHandler);
                                stiffnessManager.Init(stiffnessHandler);
                                initControlTool();
                            }
                            catch (Exception e){
                                e.printStackTrace();
                                System.out.println(e.getMessage());
                            }
                        }
                        else{
                            Message msg = new Message();
                            msg.obj = ip + "is invalid\n";
                        }
                    }
                    else{
                        ipTextfield.setKeyListener(keyListener);
                        Naoqi.getInstance().stop();
                        postureManager.interrupt();
                        stiffnessManager.interrupt();
                        BehaviorManager.getInstance().interrupt();
                        System.out.println("tried to disconnect");
                        stopMySocket();
                        imgRunning = false;
                    }

                }

            }
        });
    }

    private void initSwtichButton(){

        Button switchButton = (Button)findViewById(R.id.SwitchToVideo);
        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("fuck","shit");
                intent.setClass(MainActivity.this,VideoActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                MainActivity.this.startActivity(intent);
                //MainActivity.this.finish();
            }
        });
    }

    private void initBonjour(){

        Bonjour.getInstance().Init(getApplicationContext(),
                deviceSpinnerHandler,deviceSet);

        BehaviorTreeHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                Pair<List<Node>,List<Node>> pair = (Pair<List<Node>,List<Node>>)  msg.obj;
                ListView mTree = (ListView) findViewById(R.id.TreeListView);
                if(mTree == null){
                    System.out.println("mTree is null");
                }
                TreeListViewAdapter mAdapter = null;
                try
                {
                    mAdapter = new TreeAdapter<BehaviorBean>(mTree,
                            getApplicationContext(), pair.first , pair.second, 0);
                    mAdapter.setOnTreeNodeClickListener(new TreeListViewAdapter.OnTreeNodeClickListener()
                    {
                        @Override
                        public void onClick(Node node, int position)
                        {
                            if (node.isLeaf())
                            {
                                Toast.makeText(getApplicationContext(), node.getName(),
                                        Toast.LENGTH_SHORT).show();
                                System.out.println("onClick : " + node.getName());
                                final String FullName = BehaviorManager.getInstance().
                                        getRoot().traverseAndGetFullName(node.getName());
                                System.out.println("Behavior : " + FullName + "  selected");

                                new AlertDialog.Builder(MainActivity.this).setTitle("确认运行行为？")
                                        .setMessage("确认要运行 "+FullName + " 吗？")
                                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                try{
                                                    final Future<Object> startBehavior = BehaviorManager.getInstance().getBehaviorManagerObject()
                                                            .call("startBehavior", FullName);
                                                    System.out.println("startBehavior: "+FullName);
                                                }
                                                catch (Exception e){
                                                    e.printStackTrace();
                                                }
                                            }
                                        }).setNegativeButton("no", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).show();

                            }
                        }

                    });

                } catch (Exception e)
                {
                    e.printStackTrace();
                    return;
                }
                mTree.setAdapter(mAdapter);
            }
        };

        BehaviorManager.getInstance().setBehaviorTreeHandler(BehaviorTreeHandler);

    }

    private void initPostureManager(){
        Spinner postureSpinner = (Spinner) findViewById(R.id.postureSpinner);
        postureNames = new ArrayList<String>();
        for(Map.Entry<PostureManager.Postures,String> entry :
                PostureManager.enumToString.entrySet()){
            String str = entry.getValue();
            postureNames.add(str);
        }
        ArrayAdapter<String> postureSpinnerAdapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item, postureNames);
        postureSpinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        postureSpinner.setAdapter(postureSpinnerAdapter);

        postureManager = new PostureManager(Naoqi.getInstance());

        currentPostureView = (TextView) findViewById(R.id.currentPosture);

        postureManagerHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg){
                currentPostureView.setText("机器人姿态："+(String) msg.obj);
            }
        };

        postureSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private boolean fistTime = true;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(fistTime){
                    fistTime = false;
                    return;
                }
                String selected = null;
                try {
                    final String _selected = postureNames.get(position);
                    selected = _selected;
                    new AlertDialog.Builder(MainActivity.this).setTitle("确认更改姿态？")
                            .setMessage("确认要改变姿态到 "+ selected + " 吗？")
                            .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try{
                                        postureManager.gotoPosture(_selected);
                                        System.out.println("startBehavior: " + _selected);
                                    }
                                    catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            }).setNegativeButton("no", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();

                }
                catch (NullPointerException e){
                    e.printStackTrace();
                }

                System.out.println("postureSpinner: " + selected);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initStiffnessManager(){
        stiffnessButton = (ImageButton) findViewById(R.id.stiffnessButton);
        stiffnessManager = new StiffnessManager(Naoqi.getInstance());

        stiffnessHandler = new Handler(Looper.getMainLooper()){
            private Float prevStiffness = -1.0f;

            @Override
            public void handleMessage(Message msg){
                Float stiffness = (Float)msg.obj;
                if(stiffness != prevStiffness){
                    if (stiffness < 0.1f){
                        stiffnessButton.setBackgroundResource(R.drawable.stiffness_button_green);
                    }
                    else if(stiffness > 0.9f){
                        stiffnessButton.setBackgroundResource(R.drawable.stiffness_button_red);
                    }
                    else{
                        stiffnessButton.setBackgroundResource(R.drawable.stiffness_button_orange);
                    }
                    prevStiffness = stiffness;
                }

            }
        };

        stiffnessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!stiffnessManager.isRunning())
                    return;
                Float stiffness = stiffnessManager.getStiffness();
                if(stiffness > 0.1){
                    stiffnessManager.rest();
                }
                else{
                    //stiffnessManager.wakeUp();
                    stiffnessManager.stiffnessUp();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_author) {
            Intent intent=new Intent(MainActivity.this, FirstActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        //System.out.println("saving state");
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

    public Toolbar getToolbar(){
        return toolbar;
    }
}

class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static CrashHandler instance;  //单例引用，这里我们做成单例的，因为我们一个应用程序里面只需要一个UncaughtExceptionHandler实例

    private CrashHandler(){}

    public synchronized static CrashHandler getInstance(){  //同步方法，以免单例多线程环境下出现异常
        if (instance == null){
            instance = new CrashHandler();
        }
        return instance;
    }

    public void init(Context ctx){  //初始化，把当前对象设置成UncaughtExceptionHandler处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {  //当有未处理的异常发生时，就会来到这里。。
        System.out.println("uncaughtException, thread: " + thread
                + " name: " + thread.getName() + " id: " + thread.getId() + "exception: "
                + ex);
        ex.printStackTrace();
        String threadName = thread.getName();
    }


}