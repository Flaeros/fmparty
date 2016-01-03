package ru.fmparty.apiaccess;

import android.content.Intent;
import android.util.Log;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKList;

import ru.fmparty.MainActivity;

public class VkontakteApi implements SocialNetworkApi{
    MainActivity activity;
    final String TAG = "FlashMob: VkontakteApi";

    private int result;
    private long userId;

    public VkontakteApi(MainActivity act){ this.activity = act; }

    @Override
    public boolean isLoggedIn() { return VKSdk.isLoggedIn(); }

    @Override
    public void login() { VKSdk.login(activity); }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        VKSdk.onActivityResult(requestCode, resultCode, data, vkCallback);
    }
    @Override
    public int getResult() { return result; }

    @Override
    public void setResult(int result) { this.result = result; }

    private VKCallback<VKAccessToken> vkCallback =  new VKCallback<VKAccessToken>() {
        @Override
        public void onResult(VKAccessToken res) {
            VkontakteApi.this.userId = Integer.valueOf(res.userId);
            createUser();
            setResult(ResultCode.SUCCESS.get());
        }
        @Override
        public void onError(VKError error) {}
    };

    @Override
    public void createUser() {
        VKRequest request = VKApi.users().get();
        request.executeWithListener(
                new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                        Log.v(TAG, response.json.toString());

                        VKApiUser user = ((VKList<VKApiUser>) response.parsedModel).get(0);
                        DbApi.getInstance().createUser(user.first_name);
                    }
                });
    }

    @Override
    public int getSocialCodeId() { return SocNetId.VKONTAKTE.get(); }

    @Override
    public String getToken() {
        if(VKAccessToken.currentToken() != null)
            return VKAccessToken.currentToken().accessToken;
        return null;
    }

    @Override
    public long getUserId() { return userId; }

    @Override
    public void setUserId() {
        String userId = VKAccessToken.currentToken().userId;
        Log.d(TAG, "[setUserId] = " + userId);
        this.userId = Integer.valueOf(userId);
    }

    @Override
    public void logout() { VKSdk.logout(); }
}
