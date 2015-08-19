package ru.fmparty.apiaccess;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ru.fmparty.utils.AsyncResponse;
import ru.fmparty.utils.HttpObjectPair;
import ru.fmparty.utils.PostCallTask;

public class DbApi {
    private static String TAG = "Flashmob DbApi";

    public static void createUser(int socNetId, long socUserId, String name){

        List<HttpObjectPair> argsList = new ArrayList<>();
        argsList.add(new HttpObjectPair("do", "createUser"));
        argsList.add(new HttpObjectPair("socnetid", String.valueOf(socNetId)));
        argsList.add(new HttpObjectPair("socuserid", String.valueOf(socUserId)));
        argsList.add(new HttpObjectPair("name", name));

        new PostCallTask(asyncResponse).execute(argsList.toArray(new HttpObjectPair[argsList.size()]));
    }


    private static AsyncResponse asyncResponse = new AsyncResponse(){
        public void onSuccess(ResultObject resultObject) {
            try {
                JSONObject users = resultObject.getJsonObject();
                long id = users.getLong("id");
                Log.d(TAG, "onSuccess id =" + id);
                String name = users.getString("name");
                Log.d(TAG, "onSuccess name =" + name);

            }catch (JSONException e){
                Log.d(TAG, e.toString());
                Log.d(TAG, e.getMessage());
                e.printStackTrace();
            }
        }
    };
}
