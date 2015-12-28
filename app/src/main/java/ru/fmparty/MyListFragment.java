package ru.fmparty;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import ru.fmparty.apiaccess.ResultCode;
import ru.fmparty.apiaccess.SocialNetworkApi;
import ru.fmparty.entity.Chat;
import ru.fmparty.utils.DatabaseHelper;
import ru.fmparty.utils.Nameable;

public class MyListFragment extends Fragment implements Nameable {

    private final String TAG = "FlashMob MyListFragment";
    private SocialNetworkApi socialNetworkApi;
    private ChatListArrayAdapter chatArrayAdapter;
    private ListView chatListView;
    private ProgressBar progressBar;
    SwipeRefreshLayout mSwipeRefreshLayout;

    private List<Chat> chats;

    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mSqLiteDatabase;


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

        //Update chats algorithm
        if(chats == null)
            loadChatsFromSQLite();

        if(chats == null)
            loadChatsFromServer();
        else
            showChats();

        return view;
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

    public void setSocialNetworkApi(SocialNetworkApi socialNetworkApi){
        this.socialNetworkApi = socialNetworkApi;
    }

    private void loadChatsFromSQLite(){
        Cursor cursor = mSqLiteDatabase.query(DatabaseHelper.CHATS_TABLE, new String[]{DatabaseHelper.CHAT_ID_COLUMN,
                        DatabaseHelper.CHAT_ADMIN_ID_COLUMN, DatabaseHelper.CHAT_NAME_COLUMN,
                        DatabaseHelper.CHAT_IMAGE_COLUMN, DatabaseHelper.CHAT_DESCR_COLUMN,
                        DatabaseHelper.CHAT_FDATE_COLUMN, DatabaseHelper.CHAT_CITY_COLUMN
                },
                null, null,null, null, null) ;
        List<Chat> chats = new ArrayList<>();

        while(cursor.moveToNext()){
            int chatId       = cursor.getInt    (cursor.getColumnIndex(DatabaseHelper.CHAT_ID_COLUMN));
            int chatAdminId  = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.CHAT_ADMIN_ID_COLUMN));
            String chatName  = cursor.getString (cursor.getColumnIndex(DatabaseHelper.CHAT_NAME_COLUMN));
            String chatImage = cursor.getString (cursor.getColumnIndex(DatabaseHelper.CHAT_IMAGE_COLUMN));
            String chatDescr = cursor.getString (cursor.getColumnIndex(DatabaseHelper.CHAT_DESCR_COLUMN));
            String chatDate  = cursor.getString (cursor.getColumnIndex(DatabaseHelper.CHAT_FDATE_COLUMN));
            String chatCity  = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CHAT_CITY_COLUMN));

            Chat chat = (new Chat.Builder(chatId, chatAdminId, chatName))
                    .image(chatImage).descr(chatDescr).date(chatDate).city(chatCity)
                    .build();

            chats.add(chat);
        }
        if(!chats.isEmpty())
            this.chats = new ArrayList<>(chats);

        cursor.close();
    }

    public void loadChatsFromServer() {
        DbApi.getInstance().getChats(this, progressBar);
    }

    public void updateChatsCallback(List<Chat> chats){

        if(this.isAdded()) {
            this.chats = new ArrayList<>(chats);
            updateChatsSQLite();

            showChats();
        }
    }

    private void showChats(){
        chatArrayAdapter = new ChatListArrayAdapter(this.chats);
        chatListView.setAdapter(chatArrayAdapter);
    }

    private void updateChatsSQLite() {

        String deleteQuery = "DELETE FROM " +
                DatabaseHelper.CHATS_TABLE;
        mSqLiteDatabase.execSQL(deleteQuery);

        for(Chat chat : chats){
            ContentValues newValues = new ContentValues();

            newValues.put(DatabaseHelper.CHAT_ID_COLUMN, chat.getId());
            newValues.put(DatabaseHelper.CHAT_ADMIN_ID_COLUMN, chat.getAdmin_id());
            newValues.put(DatabaseHelper.CHAT_NAME_COLUMN, chat.getName());
            newValues.put(DatabaseHelper.CHAT_IMAGE_COLUMN, chat.getImage());
            newValues.put(DatabaseHelper.CHAT_DESCR_COLUMN, chat.getDescr());
            newValues.put(DatabaseHelper.CHAT_FDATE_COLUMN, chat.getDate());
            newValues.put(DatabaseHelper.CHAT_CITY_COLUMN, chat.getCity());

            long result = mSqLiteDatabase.insert(DatabaseHelper.CHATS_TABLE, null, newValues);
        }

    }

    @Override
    public String getTitle() {
        return "My Mobs";
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
                Glide.with(getActivity()).load(Consts.ApiPHP.get() + "uploads/" + chat.getImage()).asBitmap().into(imageView);
            }
            else{
                imageView.setImageResource(R.drawable.default_chat);
            }

            return itemView;
        }
    }
}
