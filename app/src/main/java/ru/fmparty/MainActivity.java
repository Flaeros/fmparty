package ru.fmparty;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.FacebookSdk;
import com.vk.sdk.VKSdk;

import org.json.JSONException;

import ru.fmparty.apiaccess.Consts;
import ru.fmparty.apiaccess.FacebookApi;
import ru.fmparty.apiaccess.ResultCode;
import ru.fmparty.apiaccess.ResultObject;
import ru.fmparty.apiaccess.SocialNetworkApi;
import ru.fmparty.apiaccess.VkontakteApi;
import ru.fmparty.utils.DatabaseHelper;
import ru.fmparty.utils.InnerDB;


public class MainActivity extends Activity {
    private static final String TAG = "FlashMob MainActivity";

    private SocialNetworkApi socialNetworkApi;

    private InnerFragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        setContentView(R.layout.activity_main);

        VKSdk.initialize(this);
        FacebookSdk.sdkInitialize(this);

        manager = new InnerFragmentManager(this);

        if(isLogged()) {
            startMainFragment();
        }
        else{
            manager.startFragmentForAuth();
        }
    }

    private void startMainFragment() {
        Log.d(TAG, "startMainFragment");
        if(VKSdk.isLoggedIn())
            socialNetworkApi = new VkontakteApi(this);
        else if(AccessToken.getCurrentAccessToken() != null)
            socialNetworkApi = new FacebookApi(this);

        initializeMainFragment();
    }

    private void initializeMainFragment() {
        Log.d(TAG, "initializeMainFragment");
        socialNetworkApi.setUserId();
        manager.setSocialNetworkApi(socialNetworkApi);
        manager.initializeMainFragment();
    }

    public InnerFragmentManager getManager() { return manager; }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult");
        Log.d(TAG, "requestCode = " + requestCode + " resultCode = " + resultCode);

        socialNetworkApi = manager.getAuthorizedApi();
        socialNetworkApi.onActivityResult(requestCode, resultCode, data);
        int result = socialNetworkApi.getResult();

        if(ResultCode.SUCCESS.get() == result) {
            Log.v(TAG, "Success result code");
            getManager().endAuthFragment();
            initializeMainFragment();
            Log.v(TAG, "End onResult");
        }
        else if(ResultCode.ERROR.get() == result) {
            Log.v(TAG, "Error result code");
        }
        else
            Log.v(TAG, "Unknown result code");
    }

    private boolean isLogged() {
        return VKSdk.isLoggedIn() || AccessToken.getCurrentAccessToken() != null;
    }
}