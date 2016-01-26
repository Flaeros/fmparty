package ru.fmparty;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import ru.fmparty.apiaccess.Consts;
import ru.fmparty.apiaccess.DbApi;
import ru.fmparty.apiaccess.ResultCode;
import ru.fmparty.apiaccess.SocialAccess;
import ru.fmparty.apiaccess.SocialNetworkApi;
import ru.fmparty.entity.Chat;
import ru.fmparty.utils.ChatExitable;
import ru.fmparty.utils.DatabaseHelper;
import ru.fmparty.utils.InnerDB;
import ru.fmparty.utils.Nameable;

public class MyListFragment extends Fragment implements Nameable, ChatExitable {

    private final String TAG = "FlashMob MyListFragment";
    private SocialNetworkApi socialNetworkApi;
    private ChatListArrayAdapter chatArrayAdapter;
    private ListView chatListView;
    private ProgressBar progressBar;
    SwipeRefreshLayout mSwipeRefreshLayout;

    private List<Chat> chats;

    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mSqLiteDatabase;
    private String title;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        View view = inflater.inflate(R.layout.my_list_fragment,
                container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        chatListView = (ListView) view.findViewById(R.id.chatList);

        chatListView.setOnItemClickListener(onChatItemClickListener);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.mSwipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(listRefreshListener);

        mDatabaseHelper = new DatabaseHelper(this.getActivity(), Consts.SQLiteDB.get(), null, Integer.valueOf(Consts.DbVersion.get()));
        mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();

        registerForContextMenu(chatListView);

        if(chats == null)
            loadChatsFromSQLite();

        socialNetworkApi = SocialAccess.getInstance().getApi();
        if(chats == null)
            loadChatsFromServer();
        else
            showChats();

        return view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_chat, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();

        switch (item.getItemId()) {
            case R.id.action_chat_info:
                showChatDetails(info);
                return true;
            case R.id.action_leave_chat:
                confirmLeaveChat(info);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void confirmLeaveChat(final AdapterView.AdapterContextMenuInfo info) {
        new AlertDialog.Builder(getActivity())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.action_leave_chat))
                .setMessage(getString(R.string.confirmLeaveChat))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        leaveChat(info);
                    }

                })
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }

    private void leaveChat(AdapterView.AdapterContextMenuInfo info) {
        String userId = InnerDB.getInstance().getInnerUserId(socialNetworkApi.getUserId());
        TextView chatIdView = (TextView)info.targetView.findViewById(R.id.chatId);
        String chatIdStr = chatIdView.getText().toString();
        int chatId = Integer.valueOf(chatIdStr);

        DbApi.getInstance().leaveChat(this, chatId, userId, progressBar);
    }

    private void showChatDetails(AdapterView.AdapterContextMenuInfo info) {

        String chatIdStr = ((TextView)info.targetView.findViewById(R.id.chatId)).getText().toString();
        String chatAdminIdStr = ((TextView)info.targetView.findViewById(R.id.chatAdminId)).getText().toString();;
        String chatName = ((TextView)info.targetView.findViewById(R.id.item_chatName)).getText().toString();;
        String userIdStr = InnerDB.getInstance().getInnerUserId(socialNetworkApi.getUserId());

        int chatId = Integer.valueOf(chatIdStr);
        Boolean isEditable = (userIdStr.equals(chatAdminIdStr));

        Intent mobIntent = new Intent(getActivity(), MobDetailActivity.class);

        mobIntent.putExtra("chatId", String.valueOf(chatId));
        mobIntent.putExtra("chatName", chatName);
        mobIntent.putExtra("socNetId", String.valueOf(socialNetworkApi.getSocialCodeId()));
        mobIntent.putExtra("socUserId", String.valueOf(socialNetworkApi.getUserId()));
        mobIntent.putExtra("joined", "yes");
        mobIntent.putExtra("isEditable", isEditable.toString());

        startActivity(mobIntent);
    }


    private SwipeRefreshLayout.OnRefreshListener listRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            mSwipeRefreshLayout.setRefreshing(false);
            loadChatsFromServer();
        }
    };

    AdapterView.OnItemClickListener onChatItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String userId = InnerDB.getInstance().getInnerUserId(SocialAccess.getInstance().getApi().getUserId());
            Log.d(TAG, "[onItemClick] userId = " + userId);
            if(userId == null)
                Toast.makeText(getActivity().getApplicationContext(), "Please wait. Application is loading", Toast.LENGTH_SHORT).show();
            else {
                TextView chatNameView = (TextView) view.findViewById(R.id.item_chatName);
                TextView chatIdView = (TextView) view.findViewById(R.id.chatId);
                TextView chatAdminIdView = (TextView) view.findViewById(R.id.chatAdminId);
                String chatName = chatNameView.getText().toString();
                int chatId = Integer.valueOf(chatIdView.getText().toString());
                int chatAdminId = Integer.valueOf(chatAdminIdView.getText().toString());
                showChat(chatId, chatName, chatAdminId);
            }
        }
    };

    private void showChat(int chatId, String chatName, int chatAdminId) {
        Intent chatIntent = new Intent(getActivity(),
                ChatActivity.class);
        chatIntent.putExtra("chatId", String.valueOf(chatId));
        chatIntent.putExtra("chatAdminId", String.valueOf(chatAdminId));
        chatIntent.putExtra("chatName", chatName);
        Log.d(TAG, "socialNetworkApi = " + socialNetworkApi);
        chatIntent.putExtra("socUserId", String.valueOf(SocialAccess.getInstance().getApi().getUserId()));
        chatIntent.putExtra("socNetId", String.valueOf(SocialAccess.getInstance().getApi().getSocialCodeId()));

        startActivityForResult(chatIntent, 2409);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult");
        Log.d(TAG, "requestCode = " + requestCode + " resultCode = " + resultCode);

        if(requestCode == 2409 && resultCode == ResultCode.CHAT_LEFT.get()){
            Log.v(TAG, "ChatLeft");
            loadChatsFromServer();
        }
    }

    private void loadChatsFromSQLite(){
        List<Chat> chats = InnerDB.getInstance().loadChats();
        if(!chats.isEmpty())
            this.chats = new ArrayList<>(chats);
    }

    public void loadChatsFromServer() {
        DbApi.getInstance().getChats(this, progressBar);
    }

    public void updateChatsCallback(List<Chat> chats){

        if(this.isAdded()) {
            this.chats = new ArrayList<>(chats);
            InnerDB.getInstance().updateChats(chats);
            showChats();
        }
    }

    private void showChats(){
        chatArrayAdapter = new ChatListArrayAdapter(this.chats);
        chatListView.setAdapter(chatArrayAdapter);
    }


    @Override
    public String getTitle() {
        return FMPartyApp.getContext().getString(R.string.myListTitle);
    }

    public void destroyList() {
        chats = null;
    }

    @Override
    public void exitChat() {
        loadChatsFromServer();
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

            TextView chatAdminIdView = (TextView) itemView.findViewById(R.id.chatAdminId);
            chatAdminIdView.setText(String.valueOf(chat.getAdmin_id()));

            ImageView imageView = (ImageView) itemView.findViewById(R.id.item_chatImage);
            if(chat.getImage() != null && MyListFragment.this.isVisible()) {
                Glide.with(getActivity()).load(Consts.ApiPHP.get() + "uploads/" + chat.getImage()).asBitmap().into(imageView);
            }
            else{
                imageView.setImageResource(R.drawable.default_chat);
            }

            return itemView;
        }
    }
}
