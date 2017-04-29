package com.example.acer.hello.MyChange;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


/**
 * Created by slf on 2017/3/28.
 */

public class ControlTool {
    private static ControlTool instance = null;
    private Socket socket;
    static {
        instance = new ControlTool();
    }
    public static ControlTool getInstance() {
        if (instance==null){
            instance=new ControlTool();
        }
        return instance;
    }
    ControlTool() {
        // 创建一个Socket对象，并指定服务端的IP及端口号
        try {
            socket = new Socket("192.168.0.105",6688);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("test","Connected to server...sending echo string");
    }
    public  void connectServerWithTCPSocket(String number) {
        try {
            // 获取Socket的OutputStream对象用于发送数据。
            OutputStream outputStream = socket.getOutputStream();
            PrintWriter pw=new PrintWriter(outputStream);
            // 循环读取文件
            pw.write(number);
            // 发送读取的数据到服务端
            pw.flush();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
