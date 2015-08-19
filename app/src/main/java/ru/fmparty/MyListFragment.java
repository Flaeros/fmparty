package ru.fmparty;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ru.fmparty.apiaccess.ResultObject;
import ru.fmparty.apiaccess.SocialNetworkApi;
import ru.fmparty.entity.Chat;
import ru.fmparty.utils.AsyncResponse;
import ru.fmparty.utils.HttpObjectPair;
import ru.fmparty.utils.PostCallTask;

public class MyListFragment extends Fragment {
    private final String TAG = "Flashmob MyListFragment";

    private SocialNetworkApi socialNetworkApi;

    ImageView userAvatar;
    TextView userName;
    TextView userDesc;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.my_list_fragment,
                container, false);

        userAvatar = (ImageView) view.findViewById(R.id.userAvatar);
        userName = (TextView) view.findViewById(R.id.userName);
        userDesc = (TextView) view.findViewById(R.id.userDesc);

        socialNetworkApi.populateUserInfo(userName, userDesc, userAvatar);

        ListView chatList = (ListView) view.findViewById(R.id.usersList);
        populateListView(chatList);
        chatList.setOnItemClickListener(onChatItemClickListener);

        return view;
    }

    AdapterView.OnItemClickListener onChatItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            TextView textView = (TextView) view.findViewById(R.id.item_userName);
            String text = textView.getText().toString();
            Toast.makeText(getActivity(), "Item "+ text, Toast.LENGTH_SHORT).show();
        }
    };

    public void setSocialNetworkApi(SocialNetworkApi socialNetworkApi){
        this.socialNetworkApi = socialNetworkApi;
    }

    private void populateListView(final ListView usersList) {

        List<HttpObjectPair> argsList = new ArrayList<>();
        argsList.add(new HttpObjectPair("do", "getChats"));
        argsList.add(new HttpObjectPair("socUserId", String.valueOf(socialNetworkApi.getUserId())));
        argsList.add(new HttpObjectPair("socNetId", String.valueOf(socialNetworkApi.getSocialCodeId())));

        new PostCallTask(new AsyncResponse(){
            public void onSuccess(ResultObject resultObject) {
                try {
                    Log.d(TAG, "onSuccess users=" + resultObject);
                    JSONArray users = resultObject.getJsonArray();
                    Log.d(TAG, "onSuccess users=" + users);

                    if (users != null && MyListFragment.this.isVisible()) {

                        List<Chat> chatList = new ArrayList<Chat>();
                        for(int i = 0; i < users.length(); i++){
                            JSONObject row = users.getJSONObject(i);
                            Chat chat = new Chat(row.getInt("id"), row.getInt("admin"), row.getString("name"));
                            chatList.add(chat);
                        }

                        ArrayAdapter<Chat> userArrayAdapter = new MyListArrayAdapter(chatList);
                        usersList.setAdapter(userArrayAdapter);
                    } else {
                        Log.v(TAG, "no users loaded");
                    }

                }catch (Exception e){
                    Log.d(TAG, e.toString());
                    Log.d(TAG, e.getMessage());
                    e.printStackTrace();
                }
            }
            public void onError(){
                Log.d(TAG, "onError populate error");
            }
        }).execute(argsList.toArray(new HttpObjectPair[argsList.size()]));

    }

    private class MyListArrayAdapter extends ArrayAdapter<Chat> {

        List<Chat> chatList;

        public MyListArrayAdapter(List<Chat> chatList) {
            super(getActivity(), R.layout.user_list_item_layout, chatList);
            this.chatList = chatList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = getActivity().getLayoutInflater().inflate(R.layout.user_list_item_layout, parent, false);
            }

            Chat chat = chatList.get(position);

            TextView textViewName = (TextView) itemView.findViewById(R.id.item_userName);
            textViewName.setText(chat.getId() + ". " + chat.getName());

/*
            ImageView imageView = (ImageView) itemView.findViewById(R.id.item_userAvatar);
            Log.v(TAG, user.photo_100);
            new DownloadImageTask(imageView)
                    .execute(user.photo_100);
*/


            return itemView;
        }
    }
}
