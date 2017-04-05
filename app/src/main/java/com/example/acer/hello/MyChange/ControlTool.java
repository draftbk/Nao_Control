package com.example.acer.hello.MyChange;

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
    public static void connectServerWithTCPSocket(String number) {

        Socket socket;
        try {// 创建一个Socket对象，并指定服务端的IP及端口号
            socket = new Socket("192.168.0.100",6688);
            Log.e("test","Connected to server...sending echo string");
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
