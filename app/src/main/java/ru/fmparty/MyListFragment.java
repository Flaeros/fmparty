package ru.fmparty;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
    private final String TAG = "FlashMob MyListFragment";

    private SocialNetworkApi socialNetworkApi;

    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.my_list_fragment,
                container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        ListView chatList = (ListView) view.findViewById(R.id.usersList);
        populateListView(chatList);
        chatList.setOnItemClickListener(onChatItemClickListener);

        return view;
    }

    AdapterView.OnItemClickListener onChatItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            TextView chatNameView = (TextView) view.findViewById(R.id.item_chatName);
            TextView chatIdView = (TextView) view.findViewById(R.id.chatId);
            String chatName = chatNameView.getText().toString();
            int chatId = Integer.valueOf(chatIdView.getText().toString());
            showChat(chatId, chatName);
        }
    };

    private void showChat(int chatId, String chatName) {
        Intent chatIntent = new Intent(getActivity(),
                ChatActivity.class);
        chatIntent.putExtra("chatId", String.valueOf(chatId));
        chatIntent.putExtra("chatName", chatName);
        chatIntent.putExtra("socUserId", String.valueOf(socialNetworkApi.getUserId()));
        chatIntent.putExtra("socNetId", String.valueOf(socialNetworkApi.getSocialCodeId()));

        startActivity(chatIntent);
    }

    public void setSocialNetworkApi(SocialNetworkApi socialNetworkApi){
        this.socialNetworkApi = socialNetworkApi;
    }

    private void populateListView(final ListView usersList) {

        List<HttpObjectPair> argsList = new ArrayList<>();
        argsList.add(new HttpObjectPair("do", "getChats"));
        argsList.add(new HttpObjectPair("socUserId", String.valueOf(socialNetworkApi.getUserId())));
        argsList.add(new HttpObjectPair("socNetId", String.valueOf(socialNetworkApi.getSocialCodeId())));

        progressBar.setVisibility(ProgressBar.VISIBLE);

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
        }, progressBar).execute(argsList.toArray(new HttpObjectPair[argsList.size()]));

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

            TextView textViewName = (TextView) itemView.findViewById(R.id.item_chatName);
            textViewName.setText(chat.getName());

            TextView chatIdView = (TextView) itemView.findViewById(R.id.chatId);
            chatIdView.setText(String.valueOf(chat.getId()));


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
