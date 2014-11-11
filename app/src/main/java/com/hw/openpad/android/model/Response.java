package com.hw.openpad.android.model;
import org.json.JSONObject;

/**
 * Created by ethan on 10/30/14.
 */
public class Response {

    public JSONObject data;
    public int code;
    public String msg;

    public Response(int code, String msg) {
        data = new JSONObject();
        this.code = code;
        this.msg = msg;
        try {
            JSONObject obj = new JSONObject();
            obj.put("code", code);
            obj.put("msg", msg);
            data.put("sts", obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Response(String json) throws Exception{
        data = new JSONObject(json);
        JSONObject sts = data.getJSONObject("sts");
        code = sts.getInt("code");
        msg = sts.getString("msg");
    }
}
