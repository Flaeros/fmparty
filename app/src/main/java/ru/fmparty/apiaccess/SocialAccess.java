package ru.fmparty.apiaccess;

import android.util.Log;

public class SocialAccess {
    final String TAG = "FlashMob SocialAccess";

    private SocialNetworkApi socialNetworkApi;

    private static volatile SocialAccess instance;

    private SocialAccess(){}

    private SocialAccess(SocialNetworkApi api){
        this.socialNetworkApi = api;
    }

    public static SocialAccess getInstance() {
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
        SocialAccess localInstance = instance;
        if (localInstance == null) {
            synchronized (SocialAccess.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new SocialAccess(api);
                }
            }
        }
        return localInstance;
    }

    public SocialNetworkApi getApi() {
        if(socialNetworkApi == null)
            Log.d(TAG, "API is Empty, FAIL");
        return socialNetworkApi;
    }
}
