package ru.fmparty.apiaccess;

import android.util.Log;

public class SocialAccess {
    final static String TAG = "FlashMob SocialAccess";

    private SocialNetworkApi socialNetworkApi;

    private static volatile SocialAccess instance;

    private SocialAccess(){}

    private SocialAccess(SocialNetworkApi api){
        Log.d(TAG, "[SocialAccess] create");
        this.socialNetworkApi = api;
    }

    public static SocialAccess getInstance() {
        Log.d(TAG, "[getInstance]");
        SocialAccess localInstance = instance;
        if (localInstance == null) {
            synchronized (SocialAccess.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new SocialAccess();
                }
            }
        }
        return localInstance;
    }

    public static SocialAccess getInstance(SocialNetworkApi api) {
        Log.d(TAG, "[getInstance] api = " + api);
        SocialAccess localInstance = instance;
        boolean changed = false;
        if (localInstance == null) {
            synchronized (SocialAccess.class) {
                localInstance = instance;
                if (localInstance == null) {
                    changed = true;
                    instance = localInstance = new SocialAccess(api);
                }
            }
        }

        if(!changed && SocialAccess.getInstance().getApi() != api )
            SocialAccess.getInstance().setApi(api);

        return localInstance;
    }

    public SocialNetworkApi getApi() {
        if(socialNetworkApi == null)
            Log.d(TAG, "API is Empty, FAIL");
        return socialNetworkApi;
    }

    public void setApi(SocialNetworkApi api) {
        this.socialNetworkApi = api;
    }
}
