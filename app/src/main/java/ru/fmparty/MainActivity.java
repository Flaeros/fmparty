package ru.fmparty;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.vk.sdk.VKSdk;

import ru.fmparty.apiaccess.FacebookApi;
import ru.fmparty.apiaccess.ResultCode;
import ru.fmparty.apiaccess.SocialNetworkApi;
import ru.fmparty.apiaccess.VkontakteApi;


public class MainActivity extends Activity {
    private static final String TAG = "FlashMob";

    private SocialNetworkApi socialNetworkApi;

    private InnerFragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        VKSdk.initialize(this);
        FacebookSdk.sdkInitialize(this);

        Log.v(TAG, "manager = " + manager);

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
        manager.setSocialNetworkApi(socialNetworkApi);
        manager.initializeMainFragment();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        socialNetworkApi = manager.getAuthorizedApi();
        socialNetworkApi.onActivityResult(requestCode, resultCode, data);
        int result = socialNetworkApi.getResult();

        if(ResultCode.SUCCESS.get() == result) {
            initializeMainFragment();
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