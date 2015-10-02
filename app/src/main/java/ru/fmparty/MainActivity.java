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
import ru.fmparty.utils.ImageHelper;


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
    public SocialNetworkApi getSocialNetworkApi() { return socialNetworkApi; }

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