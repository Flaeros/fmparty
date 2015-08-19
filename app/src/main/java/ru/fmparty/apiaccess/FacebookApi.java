package ru.fmparty.apiaccess;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import ru.fmparty.utils.DownloadImageTask;

public class FacebookApi implements SocialNetworkApi {
    Activity activity;
    CallbackManager callbackManager;
    AccessToken token;
    final String TAG = "Flashmob: FacebookApi";
    final List<String> permissions = Arrays.asList("public_profile", "user_birthday", "user_hometown");

    private int result;

    private long userId;

    public FacebookApi(Activity act){
        this.activity = act;

        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, fbCallback);

        token = AccessToken.getCurrentAccessToken();
    }

    @Override
    public boolean isLoggedIn() {
        return token != null;
    }

    @Override
    public void login() {
        LoginManager.getInstance().logInWithReadPermissions(activity, permissions);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public int getResult() {
        return result;
    }
    @Override
    public void setResult(int result) {
        this.result = result;
    }

    FacebookCallback fbCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            FacebookApi.this.userId = Integer.valueOf(loginResult.getAccessToken().getUserId());
            createUser();
            setResult(ResultCode.SUCCESS.get());
        }
        @Override
        public void onCancel() {}
        @Override
        public void onError(FacebookException exception) {}
    };

    @Override
    public void createUser() {
        Log.v(TAG, "createUser");
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        Log.v(TAG, "createUser JSONObject" + object.toString());

                        try {

                            String name = object.getString("first_name");
                            long id = object.getLong("id");
                            Log.d(TAG, "id = " + id);
                            Log.d(TAG, "name = " + name);

                            DbApi.createUser(SocNetId.FACEBOOK.get(), id, name);
                        }
                        catch (Exception e){
                            Log.v(TAG, "JSON error");
                            e.printStackTrace();
                        }

                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    public int getSocialCodeId() {
        return SocNetId.FACEBOOK.get();
    }


    @Override
    public void populateUserInfo(final TextView userName, final TextView userDesc, final ImageView userAvatar) {
        Log.v(TAG, "populateUserInfoFB");
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        // Application code
                        Log.v(TAG, "JSONObject" + object.toString());

                        try {
                            userName.setText((String) object.get("name"));

                            String bdate = (String) object.get("birthday");
                            JSONObject hometown = (JSONObject) object.get("hometown");
                            String home_town = (String) hometown.get("name");
                            Log.v(TAG, "bdate = " + bdate);
                            Log.v(TAG, "home_town = " + home_town);
                            userDesc.setText(bdate + " " + home_town);

                            String url = "https://graph.facebook.com/" + object.get("id") + "/picture?type=normal";
                            Log.v(TAG, url);
                            Log.v(TAG, AccessToken.getCurrentAccessToken().getUserId());

                            new DownloadImageTask(userAvatar)
                                    .execute(url);
                        }
                        catch (JSONException e){
                            Log.v(TAG, "JSON error");
                            e.printStackTrace();
                        }

                    }
                });
        Log.v(TAG, "Bundle");
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,birthday,hometown,gender");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    public long getUserId() {
        return userId;
    }

    @Override
    public void setUserId() {
        String userId = AccessToken.getCurrentAccessToken().getUserId();
        Log.d(TAG, "[setUserId] = " + userId);
        this.userId = Integer.valueOf(userId);
    }

    @Override
    public void logout() {
        LoginManager.getInstance().logOut();
    }
}
