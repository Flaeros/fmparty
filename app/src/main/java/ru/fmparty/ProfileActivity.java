package ru.fmparty;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import ru.fmparty.apiaccess.Consts;
import ru.fmparty.apiaccess.DbApi;
import ru.fmparty.apiaccess.ResultObject;
import ru.fmparty.entity.User;
import ru.fmparty.utils.AsyncResponse;
import ru.fmparty.utils.GetUserCallback;
import ru.fmparty.utils.UploadImageTask;

public class ProfileActivity extends Activity {

    private EditText profileName;
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
        profileImage = (ImageView) findViewById(R.id.profileImage);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        updateProfileButton = (Button) findViewById(R.id.updateProfileButton);
        selectImageButton = (Button) findViewById(R.id.selectImageButton);
        updateProfileButton.setOnClickListener(updateProfileButtonListener);
        selectImageButton.setOnClickListener(selectImageButtonListener);

        userId = Integer.valueOf(getIntent().getExtras().getString("userId"));
        boolean isEditable = Boolean.valueOf(getIntent().getExtras().getString("isEditable"));


        Log.d(TAG, "onCreate userId = " + userId);
        DbApi.getUser(userId, new GetUserCallback() {
            @Override
            public void setUser(User user) {
                ProfileActivity.this.user = user;
                Log.d(TAG, "user = " + user);
                fillInfo();
            }
        });

        if(isEditable)
            setEditable();
        else
            profileName.setKeyListener(null);
    }

    private void updateProfile() {
        Log.d(TAG, "updateProfile");
        progressBar.setVisibility(View.VISIBLE);
        String userName = profileName.getText().toString();
        new UploadImageTask(progressBar,
                new AsyncResponse(){
                    @Override
                    protected void onSuccess(ResultObject resultObject) {
                        Toast.makeText(ProfileActivity.this, "Profile has been updated!", Toast.LENGTH_LONG).show();
                    }
                }
        ).execute("updateUser", filePath, String.valueOf(userId), userName);
    }

    private void setEditable() {
        Log.d(TAG, "setEditable");
        updateProfileButton.setVisibility(View.VISIBLE);
        selectImageButton.setVisibility(View.VISIBLE);
    }

    private void selectImageFromGallery(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
            if (resultCode == Activity.RESULT_OK) {
                Uri selectedImage = data.getData();

                this.filePath = getPath(selectedImage);
                Log.d(TAG, "filePath = " + filePath);
                String file_extn = filePath.substring(filePath.lastIndexOf(".")+1);
                profileImage.setImageURI(selectedImage);

                try {
                    if (file_extn.equals("img") || file_extn.equals("jpg") || file_extn.equals("jpeg") || file_extn.equals("gif") || file_extn.equals("png")) {
                        //FINE
                    }
                    else{
                        //NOT IN REQUIRED FORMAT
                        filePath = null;
                    }
                } catch (Exception e) { //FileNotFoundException
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.MediaColumns.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        String imagePath = cursor.getString(column_index);

        return imagePath;
    }

    private void fillInfo() {
        profileName.setText(user.getName());
        if(!this.isDestroyed() && user.getImage() != null && !user.getImage().isEmpty())
            Glide.with(this).load(Consts.ApiPHP.get() + "uploads/" + user.getImage()).into(profileImage);
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
            selectImageFromGallery();
        }
    };
}
