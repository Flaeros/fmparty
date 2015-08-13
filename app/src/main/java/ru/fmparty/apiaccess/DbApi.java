package ru.fmparty.apiaccess;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.fmparty.utils.AsyncResponse;
import ru.fmparty.utils.HttpObjectPair;
import ru.fmparty.utils.PostCallTask;

public class DbApi {
    private static String TAG = "Flashmob DbApi";
    private SocialNetworkApi socialNetworkApi;

    public DbApi(SocialNetworkApi socialNetworkApi) {

        this.socialNetworkApi = socialNetworkApi;
    }

    public void createOrGetUser(int socNetId, int socUserId, String name){

        List<HttpObjectPair> argsList = new ArrayList<>();
        argsList.add(new HttpObjectPair("do", "createOrGetUser"));
        argsList.add(new HttpObjectPair("socnetid", String.valueOf(socNetId)));
        argsList.add(new HttpObjectPair("socuserid", String.valueOf(socUserId)));
        argsList.add(new HttpObjectPair("name", name));

        new PostCallTask(asyncResponse).execute(argsList.toArray(new HttpObjectPair[argsList.size()]));
    }


    AsyncResponse asyncResponse = new AsyncResponse(){
        @Override
        public void onTaskCompleted(JSONObject jsonObject) {
            try {
                Log.d(TAG, "onTaskCompleted jsonObject =" + jsonObject);

                int resultCode = jsonObject.getInt("resultCode");
                Log.d(TAG, "onTaskCompleted resultCode =" + resultCode);

                if(resultCode == ResultCode.SUCCESS.get()){
                    JSONObject resultObject = jsonObject.getJSONObject("resultObject");

                    int id = resultObject.getInt("id");
                    Log.d(TAG, "onTaskCompleted id =" + id);
                    String name = resultObject.getString("name");
                    Log.d(TAG, "onTaskCompleted name =" + name);

                }
                else {
                    Log.d(TAG, "dbApi error");
                }
            }catch (JSONException e){
                Log.d(TAG, e.toString());
                Log.d(TAG, e.getMessage());
                e.printStackTrace();
            }
        }
    };
}
