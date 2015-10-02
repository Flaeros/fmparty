package ru.fmparty.apiaccess;

import android.content.Intent;

public interface SocialNetworkApi {
    boolean isLoggedIn();
    void login();
    void onActivityResult(int requestCode, int resultCode, Intent data);
    int getResult();
    void setResult(int result);
    long getUserId();
    void setUserId();

    void createUser();
    int getSocialCodeId();

    void logout();
}
