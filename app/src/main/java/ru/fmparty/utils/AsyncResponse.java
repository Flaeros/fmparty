package ru.fmparty.utils;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import ru.fmparty.apiaccess.ResultCode;

public abstract class AsyncResponse {
    private static String TAG = "AsyncResponse";

    void onCompleted(JSONObject jsonObject) {
        Log.d(TAG, "onTaskCompleted jsonObject =" + jsonObject);

        try {
            int resultCode = jsonObject.getInt("resultCode");
            Log.d(TAG, "onTaskCompleted resultCode =" + resultCode);
            if(resultCode == ResultCode.SUCCESS.get()){
                JSONObject resultObject = jsonObject.getJSONObject("resultObject");
                onSuccess(resultObject);
            }
            else {
                onError();
            }
        } catch (JSONException e) {
            Log.d(TAG, e.toString());
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    void onSuccess(JSONObject jsonObject) {

    }

    void onError() {
        Log.d(TAG, "error");
    }

}
