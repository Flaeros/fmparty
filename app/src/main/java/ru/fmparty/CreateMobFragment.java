package ru.fmparty;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ru.fmparty.apiaccess.ResultObject;
import ru.fmparty.apiaccess.SocialNetworkApi;
import ru.fmparty.utils.AsyncResponse;
import ru.fmparty.utils.HttpObjectPair;
import ru.fmparty.utils.PostCallTask;
import ru.fmparty.utils.UploadImageTask;

public class CreateMobFragment extends Fragment {
    private final String TAG = "FlashMob CreateMob";

    private EditText chatName;
    private EditText chatDescr;
    private EditText chatCity;
    private DatePicker chatDate;
    private ImageView imagePreview;
    private ProgressBar progressBar;


    private SocialNetworkApi socialNetworkApi;
    private String filePath;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_mob,
                container, false);

        Button createMobButton = (Button) view.findViewById(R.id.createMobButton);
        Button selectImageButton = (Button) view.findViewById(R.id.selectImageButton);
        createMobButton.setOnClickListener(createMobButtonListener);
        selectImageButton.setOnClickListener(selectImageButtonListener);

        imagePreview = (ImageView) view.findViewById(R.id.imagePreview);
        chatName = (EditText) view.findViewById(R.id.mobName);
        chatDescr = (EditText) view.findViewById(R.id.mobDescr);
        chatCity = (EditText) view.findViewById(R.id.mobCity);
        chatDate = (DatePicker) view.findViewById(R.id.mobDate);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        return view;
    }

    private void selectImageFromGallery(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 1);
    }

    private View.OnClickListener selectImageButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            selectImageFromGallery();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
            if (resultCode == Activity.RESULT_OK) {
                Uri selectedImage = data.getData();

                this.filePath = getPath(selectedImage);
                Log.d(TAG, "filePath = " + filePath);
                String file_extn = filePath.substring(filePath.lastIndexOf(".")+1);
                imagePreview.setImageURI(selectedImage);

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
        Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        String imagePath = cursor.getString(column_index);

        return imagePath;
    }

    private View.OnClickListener createMobButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "Click Create Mob");
            createChat();
        }
    };

    private void createChat() {
        String chatName = this.chatName.getText().toString();
        String chatDescr = this.chatDescr.getText().toString();
        String chatDate = String.valueOf(this.chatDate.getDayOfMonth()) +"."+ String.valueOf(this.chatDate.getMonth()+1) +"."+ String.valueOf(this.chatDate.getYear());
        String chatCity = this.chatCity.getText().toString();
        Log.d(TAG, chatName);
        if(chatName.isEmpty()) {
            Toast.makeText(getActivity(), "Пора покормить кота!", Toast.LENGTH_SHORT).show();
            return;
        }

        List<HttpObjectPair> argsList = new ArrayList<>();
        argsList.add(new HttpObjectPair("do", "createChat"));
        argsList.add(new HttpObjectPair("chatName", String.valueOf(chatName)));
        argsList.add(new HttpObjectPair("chatDescr", String.valueOf(chatDescr)));
        argsList.add(new HttpObjectPair("chatDate", String.valueOf(chatDate)));
        argsList.add(new HttpObjectPair("chatCity", String.valueOf(chatCity)));
        argsList.add(new HttpObjectPair("socUserId", String.valueOf(socialNetworkApi.getUserId())));
        argsList.add(new HttpObjectPair("socNetId", String.valueOf(socialNetworkApi.getSocialCodeId())));

        progressBar.setVisibility(ProgressBar.VISIBLE);
        new PostCallTask(asyncResponse, progressBar).execute(argsList.toArray(new HttpObjectPair[argsList.size()]));
    }

    AsyncResponse asyncResponse = new AsyncResponse(){
        public void onSuccess(ResultObject resultObject) {
            try {
                JSONObject jsonObject = resultObject.getJsonObject();
                long id = jsonObject.getLong("id");
                Log.d(TAG, "onSuccess chat id =" + id);
                String name = jsonObject.getString("name");
                Log.d(TAG, "onSuccess chat name =" + name);
                Toast.makeText(getActivity(), "Чат " + name +" создан!", Toast.LENGTH_SHORT).show();

                progressBar.setVisibility(ProgressBar.VISIBLE);
                if(filePath != null)
                    new UploadImageTask(progressBar).execute("updateChatImage", filePath, String.valueOf(id));
            }catch (JSONException e){
                Log.d(TAG, e.toString());
                Log.d(TAG, e.getMessage());
                e.printStackTrace();
            }
        }
    };

    public void setSocialNetworkApi(SocialNetworkApi socialNetworkApi) {
        this.socialNetworkApi = socialNetworkApi;
    }
}
