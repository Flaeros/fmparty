package ru.fmparty.apiaccess;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ru.fmparty.ChatActivity;
import ru.fmparty.utils.AsyncResponse;
import ru.fmparty.utils.HttpObjectPair;
import ru.fmparty.utils.PostCallTask;

public class DbApi {
    private final static String TAG = "FlashMob DbApi";

    public static void createUser(int socNetId, long socUserId, String name){

        List<HttpObjectPair> argsList = new ArrayList<>();
        argsList.add(new HttpObjectPair("do", "createUser"));
        argsList.add(new HttpObjectPair("socnetid", String.valueOf(socNetId)));
        argsList.add(new HttpObjectPair("socuserid", String.valueOf(socUserId)));
        argsList.add(new HttpObjectPair("name", name));

        new PostCallTask().execute(argsList.toArray(new HttpObjectPair[argsList.size()]));
    }

    public static void sendMsg(String message, int chatId, int socNetId, int socUserId) {
        List<HttpObjectPair> argsList = new ArrayList<>();
        argsList.add(new HttpObjectPair("do", "sendMsg"));
        argsList.add(new HttpObjectPair("textMsg", message));
        argsList.add(new HttpObjectPair("chatid", String.valueOf(chatId)));
        argsList.add(new HttpObjectPair("socnetid", String.valueOf(socNetId)));
        argsList.add(new HttpObjectPair("socuserid", String.valueOf(socUserId)));
        new PostCallTask().execute(argsList.toArray(new HttpObjectPair[argsList.size()]));
    }

    public static void getMessages(Activity context, int chatId, int socUserId, int socNetId) {
        List<HttpObjectPair> argsList = new ArrayList<>();
        argsList.add(new HttpObjectPair("do", "getMessages"));
        argsList.add(new HttpObjectPair("chatid", String.valueOf(chatId)));
        argsList.add(new HttpObjectPair("socuserid", String.valueOf(socUserId)));
        argsList.add(new HttpObjectPair("socnetid", String.valueOf(socNetId)));

        new PostCallTask(new AsyncResponse() {
            void onSuccess(ResultObject resultObject) {
                Log.d(TAG, "resultObject = " + resultObject);
            }
        }).execute(argsList.toArray(new HttpObjectPair[argsList.size()]));
    }
}
