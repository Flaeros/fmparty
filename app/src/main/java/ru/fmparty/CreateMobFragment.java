package ru.fmparty;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import ru.fmparty.utils.ImageHelper;
import ru.fmparty.utils.Nameable;
import ru.fmparty.utils.PostCallTask;
import ru.fmparty.utils.UploadImageTask;

public class CreateMobFragment extends Fragment implements Nameable {
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


    private View.OnClickListener selectImageButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ImageHelper.selectImageFromGallery(getActivity());
        }
    };

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
            Toast.makeText(getActivity(), "Введите имя чата", Toast.LENGTH_SHORT).show();
            return;
        }

        List<HttpObjectPair> argsList = new ArrayList<>();
        argsList.add(new HttpObjectPair("do", "createChat"));
        argsList.add(new HttpObjectPair("chatName", String.valueOf(chatName)));
        argsList.add(new HttpObjectPair("chatDescr", String.valueOf(chatDescr)));
        argsList.add(new HttpObjectPair("chatDate", String.valueOf(chatDate)));
        argsList.add(new HttpObjectPair("chatCity", String.valueOf(chatCity)));
        argsList.add(new HttpObjectPair("socuserid", String.valueOf(socialNetworkApi.getUserId())));
        argsList.add(new HttpObjectPair("socnetid", String.valueOf(socialNetworkApi.getSocialCodeId())));
        argsList.add(new HttpObjectPair("token", String.valueOf(socialNetworkApi.getToken())));

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
                Log.d(TAG, "[asyncResponse]filePath = " + filePath);

                String chatName = CreateMobFragment.this.chatName.getText().toString();
                String chatDescr = CreateMobFragment.this.chatDescr.getText().toString();
                String chatDate = String.valueOf(CreateMobFragment.this.chatDate.getDayOfMonth()) +"."+ String.valueOf(CreateMobFragment.this.chatDate.getMonth()+1) +"."+ String.valueOf(CreateMobFragment.this.chatDate.getYear());
                String chatCity = CreateMobFragment.this.chatCity.getText().toString();

                if(filePath != null)
                    new UploadImageTask(progressBar).execute("updateChat", filePath, String.valueOf(id), chatName, chatDescr, chatCity, chatDate);
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

    public void onActivityResultHelp(Intent data, Activity activity) {
        filePath = ImageHelper.onActivityResultHelp(data, activity, imagePreview);
        Log.d(TAG, "[onActivityResultHelp]filePath = " +filePath);
    }

    @Override
    public String getTitle() {
        return FMPartyApp.getContext().getString(R.string.createMobTitle);
    }
}
