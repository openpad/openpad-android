package com.hw.openpad.android;

import android.util.Log;

import com.hw.openpad.android.model.Request;
import com.hw.openpad.android.model.Response;
import com.hw.openpad.android.model.UserData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
* Created by ethan on 10/29/14.
*/
public class GameConnection {
    Socket socket;
    InputStream inputStream;
    OutputStream outputStream;
    InetAddress ip;
    public String name, description, banReason;
    int port, openSlots, filledSlots;
    boolean isBanned;
    private Thread thread;

    public GameConnection(InetAddress ip, Integer port) {
        try {
            this.socket = new Socket(ip, port);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendDiscovery() {
        try {
            Request request = new Request(0);
            JSONObject id = new JSONObject();
            id.put("phoneID", UserData.getValue("phoneID"));
            id.put("firstname", UserData.getValue("firstname"));
            id.put("lastname", UserData.getValue("lastname"));
            id.put("username", UserData.getValue("username"));
            id.put("fbuid", "");
            request.data.put("id", id);

            request.data.put("APIVersion", 1);

            outputStream.write(request.toString().getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int stringLength(byte[] bytes, int off){
        for(int i = 0; i < bytes.length-off; i++){
            if(bytes[i+off]==0)return i;
        }
        return bytes.length-off;
    }

    private void handleMsg(String msg) {
        Log.d("OpenPad", "received msg: " + msg);
        try {
            Response response = new Response(msg);
            if (response.data.has("game")) {
                setInfo(response.data);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setInfo(JSONObject jsonObject) {
        try {
            description = jsonObject.getString("description");
            name = jsonObject.getString("name");
            openSlots = jsonObject.getInt("openslots");
            filledSlots = jsonObject.getInt("filledslots");
            isBanned = jsonObject.getBoolean("is");
            banReason = jsonObject.getString("why");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void listen() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] buffer = new byte[1024];
                String cur = "";
                try {
                    while (socket.isConnected()) {
                        int amt = inputStream.read(buffer);

                        int bytesProcessed = 0;
                        do {
                            int length = (int) stringLength(buffer, bytesProcessed);
                            cur += new String(buffer, bytesProcessed, length);
                            if(bytesProcessed+length < amt) {
                                handleMsg(cur);
                                cur = "";
                            }
                            bytesProcessed += length + 1;
                        } while (bytesProcessed < amt);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public void joinGame() {
        Request request = new Request(2);
        try {
            outputStream.write(request.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}