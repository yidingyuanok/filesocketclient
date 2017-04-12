package com.socket.client.util;


import com.socket.client.bean.SocketHeader;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kings on 17/2/13.
 */
public class JSON {

    public static SocketHeader parseObject(String text) {
        SocketHeader socketHeader = new SocketHeader();
        try {
            JSONObject obj = new JSONObject(text);
            socketHeader.setId(obj.optString("id"));
            socketHeader.setName(obj.optString("name"));
            socketHeader.setFilePath(obj.optString("filePath"));
            socketHeader.setLength(obj.optInt("length"));
            socketHeader.setPosition(obj.optLong("position"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return socketHeader;
    }

    public static String toJSONString(SocketHeader socketHeader) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("type", socketHeader.getType());
            obj.put("id",socketHeader.getId());
            obj.put("name",socketHeader.getName());
            obj.put("filePath",socketHeader.getFilePath());
            obj.put("length",socketHeader.getLength());
            obj.put("position",socketHeader.getPosition());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj.toString();
    }
}
