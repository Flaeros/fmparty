package ru.fmparty;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ru.fmparty.apiaccess.DbApi;
import ru.fmparty.apiaccess.SocialNetworkApi;
import ru.fmparty.entity.Chat;

public class FindMobFragment extends Fragment{
    private final String TAG = "FlashMob FindMob";

    private SocialNetworkApi socialNetworkApi;


    private EditText mobName;
    private ProgressBar progressBar;
    private ListView mobsListView;
    private MobListArrayAdapter mobListArrayAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.find_mob_fragment,
                container, false);

        Button findMobsButton = (Button) view.findViewById(R.id.findMobButton);
        findMobsButton.setOnClickListener(findMobsButtonListener);

        mobName = (EditText) view.findViewById(R.id.mobName);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        mobsListView = (ListView) view.findViewById(R.id.mobList);
        mobsListView.setOnItemClickListener(onMobItemClickListener);

        return view;
    }

    AdapterView.OnItemClickListener onMobItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            TextView chatNameView = (TextView) view.findViewById(R.id.item_chatName);
            TextView chatIdView = (TextView) view.findViewById(R.id.chatId);
            String chatName = chatNameView.getText().toString();
            int chatId = Integer.valueOf(chatIdView.getText().toString());
            Toast.makeText(getActivity(), "Пора покормить кота! " + chatId + " " + chatName, Toast.LENGTH_SHORT).show();
            showMobDetail(chatId, chatName);
        }
    };

    private void showMobDetail(int chatId, String chatName) {

        Intent mobIntent = new Intent(getActivity(), MobDetailActivity.class);

        mobIntent.putExtra("chatId", String.valueOf(chatId));
        mobIntent.putExtra("chatName", chatName);
        mobIntent.putExtra("socNetId", String.valueOf(socialNetworkApi.getSocialCodeId()));
        mobIntent.putExtra("socUserId", String.valueOf(socialNetworkApi.getUserId()));

        startActivity(mobIntent);
    }

    private View.OnClickListener findMobsButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String mobNameStr = mobName.getText().toString();
            if(!mobNameStr.isEmpty())
                findMobs(mobNameStr);
            else
                Toast.makeText(getActivity(), "Пора покормить кота!", Toast.LENGTH_SHORT).show();
        }
    };

    private void findMobs(String search) {
        DbApi.findMobs(this, search, progressBar);
    }

    public void showMobs(List<Chat> chats){
        if(this.isVisible()) {
            mobListArrayAdapter = new MobListArrayAdapter(chats);
            mobsListView.setAdapter(mobListArrayAdapter);
        }
    }

    public void setSocialNetworkApi(SocialNetworkApi socialNetworkApi) {
        this.socialNetworkApi = socialNetworkApi;
    }

    private class MobListArrayAdapter extends ArrayAdapter<Chat> {

        private List<Chat> chats;

        public MobListArrayAdapter(List<Chat> chatList) {
            super(getActivity(), R.layout.chat_list_item_layout, chatList);
            this.chats = new ArrayList<>(chatList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = getActivity().getLayoutInflater().inflate(R.layout.chat_list_item_layout, parent, false);
            }

            Chat chat = chats.get(position);

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
