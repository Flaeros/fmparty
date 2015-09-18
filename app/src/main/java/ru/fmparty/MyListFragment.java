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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import ru.fmparty.apiaccess.Consts;
import ru.fmparty.apiaccess.DbApi;
import ru.fmparty.apiaccess.SocialNetworkApi;
import ru.fmparty.entity.Chat;
import ru.fmparty.utils.DownloadImageTask;

public class MyListFragment extends Fragment {

    private final String TAG = "FlashMob MyListFragment";
    private SocialNetworkApi socialNetworkApi;
    private ChatListArrayAdapter chatArrayAdapter;
    private ListView chatListView;
    private ProgressBar progressBar;

    private List<Chat> chats;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.my_list_fragment,
                container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        chatListView = (ListView) view.findViewById(R.id.chatList);

        chatListView.setOnItemClickListener(onChatItemClickListener);

        if(chats == null)
            loadChats();
        else
            showChats(chats);

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

    private void loadChats() {
        DbApi.getChats(this, socialNetworkApi.getSocialCodeId(), socialNetworkApi.getUserId(), progressBar);
    }

    public void showChats(List<Chat> chats){

        if(this.isAdded()) {
            if(this.chats == null)
                this.chats = new ArrayList<>(chats);
            chatArrayAdapter = new ChatListArrayAdapter(this.chats);
            chatListView.setAdapter(chatArrayAdapter);
        }
    }

    private class ChatListArrayAdapter extends ArrayAdapter<Chat> {

        public ChatListArrayAdapter(List<Chat> chatList) {
            super(getActivity(), R.layout.chat_list_item_layout, chatList);
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

            ImageView imageView = (ImageView) itemView.findViewById(R.id.item_chatImage);
            if(chat.getImage() != null && MyListFragment.this.isVisible()) {
                Glide.with(getActivity()).load(Consts.ApiPHP.get() + "uploads/" +chat.getImage()).into(imageView);
            }
            else{
                imageView.setImageResource(R.drawable.default_chat);
            }

            return itemView;
        }


    }
}
