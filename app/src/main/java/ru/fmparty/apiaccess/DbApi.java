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

    public void createOrGetUser(int socNetId, long socUserId, String name){

        List<HttpObjectPair> argsList = new ArrayList<>();
        argsList.add(new HttpObjectPair("do", "createOrGetUser"));
        argsList.add(new HttpObjectPair("socnetid", String.valueOf(socNetId)));
        argsList.add(new HttpObjectPair("socuserid", String.valueOf(socUserId)));
        argsList.add(new HttpObjectPair("name", name));

        new PostCallTask(asyncResponse).execute(argsList.toArray(new HttpObjectPair[argsList.size()]));
    }


    AsyncResponse asyncResponse = new AsyncResponse(){
        public void onSuccess(JSONObject jsonObject) {
            try {
                long id = jsonObject.getLong("id");
                Log.d(TAG, "onSuccess id =" + id);
                String name = jsonObject.getString("name");
                Log.d(TAG, "onSuccess name =" + name);
                socialNetworkApi.setUserId(id);

            }catch (JSONException e){
                Log.d(TAG, e.toString());
                Log.d(TAG, e.getMessage());
                e.printStackTrace();
            }
        }
    };
}
