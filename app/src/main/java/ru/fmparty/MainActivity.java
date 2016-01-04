package ru.fmparty;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.vk.sdk.VKSdk;

import ru.fmparty.apiaccess.DbApi;
import ru.fmparty.apiaccess.FacebookApi;
import ru.fmparty.apiaccess.ResultCode;
import ru.fmparty.apiaccess.SocialAccess;
import ru.fmparty.apiaccess.SocialNetworkApi;
import ru.fmparty.apiaccess.VkontakteApi;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "FlashMob MainActivity";

    private SocialNetworkApi socialNetworkApi;

    private InnerFragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        setContentView(R.layout.activity_main);
        getSupportActionBar().setElevation(0);

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

        Log.d(TAG, "[startMainFragment] socialNetworkApi = " + socialNetworkApi);
        initializeMainFragment();
    }

    private void initializeMainFragment() {
        Log.d(TAG, "initializeMainFragment");
        socialNetworkApi.setUserId();
        Log.d(TAG, "set socialNetworkApi = " + socialNetworkApi);
        manager.setSocialNetworkApi(socialNetworkApi);
        SocialAccess.getInstance(socialNetworkApi).getApi(); // set api

        DbApi.getInstance().setSocialNetworkApi(socialNetworkApi);
        manager.initializeMainFragment();
    }

    public InnerFragmentManager getManager() { return manager; }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult");
        Log.d(TAG, "requestCode = " + requestCode + " resultCode = " + resultCode);

        //for image select when create chat
        if (requestCode == 1)
            if (resultCode == Activity.RESULT_OK) {
                manager.getCreateMobFragment().onActivityResultHelp(data, this);
                return;
            }

        boolean fromChat = false;

        String chatClosed = data.getStringExtra("ChatClosed");
        if(chatClosed != null) {
            Log.d(TAG, "chatClosed = " + chatClosed);
            if("Yes".equals(chatClosed))
                fromChat = true;

        }

        Log.d(TAG, "requestCode = " + requestCode);
        Log.d(TAG, "resultCode = " + resultCode);
        //success auth code for vk & fb
        if(requestCode == 10485 || requestCode == 64206)
            socialNetworkApi = manager.getAuthorizedApi();

        if(socialNetworkApi == null) {
            Log.v(TAG, "Error");
            return;
        }
        socialNetworkApi.onActivityResult(requestCode, resultCode, data);
        int result = socialNetworkApi.getResult();

        Log.d(TAG, "result = " + result);

        if(ResultCode.SUCCESS.get() == result && !fromChat) {
            Log.v(TAG, "Success result code");
            getManager().endAuthFragment();
            initializeMainFragment();
            Log.v(TAG, "End onResult");
        }
        else if(ResultCode.ERROR.get() == result || fromChat) {
            Log.v(TAG, "Error result code");
        }
        else
            Log.v(TAG, "Unknown result code");
    }

    private boolean isLogged() {
        return VKSdk.isLoggedIn() || AccessToken.getCurrentAccessToken() != null;
    }
}