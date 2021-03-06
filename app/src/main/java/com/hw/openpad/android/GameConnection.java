package com.hw.openpad.android;

import android.graphics.Point;
import android.util.Pair;

import com.hw.openpad.android.model.NetworkManager;
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
    Socket mSocket;
    InputStream mInputStream;
    OutputStream mOutputStream;
    public String mName, mDescription, mBanReason, mIcon;
    int mOpenSlots, mFilledSlots;
    boolean mIsBanned, mHasJoined;
    private Thread mThread;
    public JSONObject mPadConfig;
    private ControllerDelegate mDelegate;

    public GameConnection(InetAddress ip, Integer port) {
        try {
            this.mSocket = new Socket(ip, port);
            mInputStream = mSocket.getInputStream();
            mOutputStream = mSocket.getOutputStream();
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

            mOutputStream.write(request.toString().getBytes());
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
//        Log.d("OpenPad", "received msg: " + msg);
        try {
            Response response = new Response(msg);
            if (response.data.has("game")) {
                setInfo(response.data);
            } else if (response.data.has("accepted")) {
                boolean accepted = response.data.getBoolean("accepted");
                if (accepted) {
                    NetworkManager.didJoin(this);
                    mPadConfig = response.data.getJSONObject("padconfig");
                    if(mDelegate!=null)mDelegate.setPadConfig(mPadConfig);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setInfo(JSONObject jsonObject) {
        try {
            JSONObject gameData = jsonObject.getJSONObject("game");
            boolean firstTime = mName == null;
            mDescription = gameData.getString("desc");
            mName = gameData.getString("name");
            mOpenSlots = gameData.getInt("openslots");
            mFilledSlots = gameData.getInt("filledslots");
            mIcon = gameData.getString("icon");
            JSONObject banned = jsonObject.getJSONObject("banned");
            mIsBanned = banned.getBoolean("is");
            mBanReason = banned.getString("why");

            if (firstTime) {
                NetworkManager.addConnection(this);
            } else {
                NetworkManager.refreshConnections();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void listen() {
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] buffer = new byte[1024];
                String cur = "";
                try {
                    while (mSocket.isConnected()) {
                        int amt = mInputStream.read(buffer);

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
        mThread.start();
    }

    public void joinGame() {
        if (!mHasJoined) {
            NetworkManager.joinedGame = this;
            mHasJoined = true;
            Request request = new Request(2);
            try {
                mOutputStream.write(request.toString().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void disconnect() {
        Request request = new Request(3);
        try {
            mOutputStream.write(request.toString().getBytes());
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setDelegate(ControllerDelegate d){
        mDelegate = d;
    }

    public interface ControllerDelegate{
        public void setPadConfig(JSONObject padConfig);
    }

    public void sendPadUpdate(int id, Pair<Double, Double> coords, int action) {
        Request request = new Request(5);
        try {
            request.data.put("action", action);
            JSONObject coordinates = new JSONObject();
            coordinates.put("x", coords.first);
            coordinates.put("y", coords.second);
            request.data.put("position", coordinates);
            request.data.put("controlid", id);

            mOutputStream.write(request.toString().getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendPadUpdate(int id, Point coords, int action) {
        sendPadUpdate(id, new Pair<Double, Double>(coords.x + 0.0, coords.y + 0.0), action);
    }
}