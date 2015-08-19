package ru.fmparty.apiaccess;

import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;

import ru.fmparty.utils.AsyncResponse;

public interface SocialNetworkApi {
    boolean isLoggedIn();
    void login();
    void onActivityResult(int requestCode, int resultCode, Intent data);
    int getResult();
    void setResult(int result);
    void populateUserInfo(TextView userName, TextView userDesc, ImageView userAvatar);
    long getUserId();
    void setUserId();

    void createUser();
    int getSocialCodeId();

    void logout();
}
