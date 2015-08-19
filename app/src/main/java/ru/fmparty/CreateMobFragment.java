package ru.fmparty;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

public class CreateMobFragment extends Fragment {
    private final String TAG = "Flashmob CreateMob";

    EditText editText;
    private SocialNetworkApi socialNetworkApi;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_mob,
                container, false);

        Button createMobButton = (Button) view.findViewById(R.id.createMobButton);
        createMobButton.setOnClickListener(createMobButtonListener);

        editText = (EditText) view.findViewById(R.id.mobName);

        return view;
    }

    private View.OnClickListener createMobButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "Click Create Mob");
            createChat();
        }
    };

    private void createChat() {
        String chatName = editText.getText().toString();
        Log.d(TAG, chatName);
        if(chatName.isEmpty()) {
            Toast.makeText(getActivity(), "Пора покормить кота!", Toast.LENGTH_SHORT).show();
            return;
        }

        List<HttpObjectPair> argsList = new ArrayList<>();
        argsList.add(new HttpObjectPair("do", "createChat"));
        argsList.add(new HttpObjectPair("chatName", String.valueOf(chatName)));
        argsList.add(new HttpObjectPair("socUserId", String.valueOf(socialNetworkApi.getUserId())));
        argsList.add(new HttpObjectPair("socNetId", String.valueOf(socialNetworkApi.getSocialCodeId())));
        new PostCallTask(asyncResponse).execute(argsList.toArray(new HttpObjectPair[argsList.size()]));
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
