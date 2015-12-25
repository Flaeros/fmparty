package ru.fmparty.apiaccess;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import ru.fmparty.MainActivity;

public class FacebookApi implements SocialNetworkApi {
    MainActivity activity;
    CallbackManager callbackManager;
    AccessToken token;
    final String TAG = "FlashMob: FacebookApi";
    final List<String> permissions = Arrays.asList("public_profile", "user_birthday", "user_hometown");

    private int result;
    private long userId;

    public FacebookApi(MainActivity act){
        this.activity = act;

        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, fbCallback);

        token = AccessToken.getCurrentAccessToken();
    }

    @Override
    public boolean isLoggedIn() { return token != null; }

    @Override
    public void login() { LoginManager.getInstance().logInWithReadPermissions(activity, permissions); }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public int getResult() { return result; }
    @Override
    public void setResult(int result) {
        this.result = result;
    }

    FacebookCallback fbCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            FacebookApi.this.userId = Long.valueOf(loginResult.getAccessToken().getUserId());
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
                            DbApi.getInstance().createUser(name);
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
    public int getSocialCodeId() { return SocNetId.FACEBOOK.get(); }

    @Override
    public String getToken() {
        return AccessToken.getCurrentAccessToken().getToken();
    }

    @Override
    public long getUserId() { return userId; }

    @Override
    public void setUserId() {
        String userId = AccessToken.getCurrentAccessToken().getUserId();
        Log.d(TAG, "[setUserId] = " + userId);
        this.userId = Long.valueOf(userId);
    }

    @Override
    public void logout() { LoginManager.getInstance().logOut(); }
}
