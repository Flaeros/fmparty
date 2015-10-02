package ru.fmparty.utils;

import android.util.Log;

import ru.fmparty.apiaccess.ResultCode;
import ru.fmparty.apiaccess.ResultObject;

public abstract class AsyncResponse {
    private static String TAG = "FlashMob AsyncResponse";

    void onCompleted(ResultObject resultObject) {
        Log.d(TAG, "onTaskCompleted jsonObject =" + resultObject);

        int resultCode = resultObject.getResultCode();
        Log.d(TAG, "onTaskCompleted resultCode =" + resultCode);

        if(resultCode == ResultCode.SUCCESS.get()){
            onSuccess(resultObject);
        }
        else {
            onError();
        }
    }

    abstract protected void onSuccess(ResultObject resultObject);

    void onError() {
        //or for no result
    }

}
