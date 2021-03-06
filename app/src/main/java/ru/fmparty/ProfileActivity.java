package ru.fmparty;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import ru.fmparty.apiaccess.Consts;
import ru.fmparty.apiaccess.DbApi;
import ru.fmparty.apiaccess.ResultObject;
import ru.fmparty.entity.User;
import ru.fmparty.utils.AsyncResponse;
import ru.fmparty.utils.GetUserCallback;
import ru.fmparty.utils.ImageHelper;
import ru.fmparty.utils.UploadImageTask;

public class ProfileActivity extends AppCompatActivity {

    private EditText profileName;
    private EditText profileDesc;
    private ImageView profileImage;
    private Button updateProfileButton;
    private Button selectImageButton;
    private ProgressBar progressBar;

    User user;

    private final String TAG = "FlashMob ProfileAct";
    private String filePath;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        profileName = (EditText) findViewById(R.id.profileName);
        profileDesc = (EditText) findViewById(R.id.profileDesc);
        profileImage = (ImageView) findViewById(R.id.profileImage);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        updateProfileButton = (Button) findViewById(R.id.updateProfileButton);
        selectImageButton = (Button) findViewById(R.id.selectImageButton);

        profileImage.setClickable(true);
        profileImage.setOnClickListener(selectImageButtonListener);
        updateProfileButton.setOnClickListener(updateProfileButtonListener);
        selectImageButton.setOnClickListener(selectImageButtonListener);

        userId = Integer.valueOf(getIntent().getExtras().getString("userId"));
        boolean isEditable = Boolean.valueOf(getIntent().getExtras().getString("isEditable"));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Log.d(TAG, "onCreate userId = " + userId);
        DbApi.getInstance().getUser(userId, new GetUserCallback() {
            @Override
            public void setUser(User user) {
                ProfileActivity.this.user = user;
                Log.d(TAG, "user = " + user);
                fillInfo();
            }
        }, progressBar);

        if(isEditable)
            setEditable();
        else
            setUneditable();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
        }
        return true;
    }

    private void setUneditable() {
        profileName.setKeyListener(null);
        profileDesc.setKeyListener(null);
    }

    private void updateProfile() {
        Log.d(TAG, "updateProfile");

        String userName = profileName.getText().toString();
        String userDesc = profileDesc.getText().toString();

        if(userName.isEmpty()) {
            Toast.makeText(this, getString(R.string.enter_chat_name), Toast.LENGTH_SHORT).show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);

        if(filePath == null)
            DbApi.getInstance().updateUser(String.valueOf(userId), user.getImage(), userName, userDesc, progressBar, null);
        else
            new UploadImageTask(progressBar,
                    new AsyncResponse(){
                        @Override
                        protected void onSuccess(ResultObject resultObject) {
                            Toast.makeText(ProfileActivity.this, getString(R.string.msg_profile_updated), Toast.LENGTH_LONG).show();
                        }
                    }
            ).execute("updateUser", filePath, String.valueOf(userId), userName, userDesc);
    }

    private void setEditable() {
        Log.d(TAG, "setEditable");
        updateProfileButton.setVisibility(View.VISIBLE);
        selectImageButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
            if (resultCode == Activity.RESULT_OK) {
                filePath = ImageHelper.onActivityResultHelp(data, this, profileImage);
            }
    }

    private void fillInfo() {
        profileName.setText(user.getName());
        profileDesc.setText(user.getDesc());
        if(!this.isDestroyed() && user.getImage() != null && !user.getImage().isEmpty())
            Glide.with(this).load(Consts.ApiPHP.get() + "uploads/" + user.getImage()).asBitmap().into(profileImage);
        else
            profileImage.setImageResource(R.drawable.default_userpic);

    }

    private View.OnClickListener updateProfileButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "Click Update Profile");
            updateProfile();
        }
    };

    private View.OnClickListener selectImageButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ImageHelper.selectImageFromGallery(ProfileActivity.this);
        }
    };
}
