package ru.fmparty.apiaccess;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKList;

import org.json.JSONException;

import ru.fmparty.utils.DownloadImageTask;

public class VkontakteApi implements SocialNetworkApi{
    Activity activity;
    final String TAG = "Flashmob: VkontakteApi";

    private int result;

    private DbApi dbApi;
    private int userId;

    public VkontakteApi(Activity act){
        this.activity = act;
        dbApi = new DbApi(this);
    }

    @Override
    public boolean isLoggedIn() {
        return VKSdk.isLoggedIn();
    }

    @Override
    public void login() {
        VKSdk.login(activity);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        VKSdk.onActivityResult(requestCode, resultCode, data, vkCallback);
    }
    @Override
    public int getResult() {
        return result;
    }
    @Override
    public void setResult(int result) {
        this.result = result;
    }
    private VKCallback<VKAccessToken> vkCallback =  new VKCallback<VKAccessToken>() {
        @Override
        public void onResult(VKAccessToken res) {
            createOrGetUser();
            setResult(ResultCode.SUCCESS.get());
        }
        @Override
        public void onError(VKError error) {}
    };

    private void createOrGetUser() {
        VKRequest request = VKApi.users().get();
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                Log.v(TAG, response.json.toString());

                VKApiUser user = ((VKList<VKApiUser>) response.parsedModel).get(0);

                if (user != null) {
                    Log.d(TAG, "user.getId() = " + user.getId());
                    dbApi.createOrGetUser(SocNetId.VKONTAKTE.get(), user.getId(), user.first_name);
                }
            }
        });
    }

    @Override
    public void populateUserInfo(final TextView userName, final TextView userDesc, final ImageView userAvatar) {

        VKRequest request = VKApi.users().get( VKParameters.from(VKApiConst.FIELDS, "sex,bdate,home_town,photo_100"));
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                Log.v(TAG, response.json.toString());

                VKApiUser user = ((VKList<VKApiUser>) response.parsedModel).get(0);

                if (user != null) {
                    userName.setText(user.first_name + " " + user.last_name);
                    try {
                        String bdate = (String) user.fields.get("bdate");
                        String home_town = (String) user.fields.get("home_town");
                        userDesc.setText(bdate + " " + home_town);
                    } catch (JSONException e) {
                        Log.v(TAG, "JSON error");
                        e.printStackTrace();
                    }
                    new DownloadImageTask(userAvatar)
                            .execute(user.photo_100);

                } else {
                    Log.v(TAG, "no users loaded");
                }
            }
        });
    }

    @Override
    public int getUserId() {
        return userId;
    }

    @Override
    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public void logout() {
        VKSdk.logout();
    }
}
