package ru.fmparty;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import ru.fmparty.apiaccess.Consts;
import ru.fmparty.apiaccess.DbApi;
import ru.fmparty.entity.Message;
import ru.fmparty.entity.User;
import ru.fmparty.utils.ChatRefreshThread;
import ru.fmparty.utils.DatabaseHelper;
import ru.fmparty.utils.GetUserCallback;
import ru.fmparty.utils.InnerDB;

public class ChatActivity extends Activity{
    private static final String TAG = "FlashMob ChatActivity";

    private int chatId;
    private String chatName;
    private long socUserId;
    private int socNetId;
    private AtomicBoolean isRunning;

    private ProgressBar progressBar;
    private EditText msgField;
    private ListView msgList;

    private MyListArrayAdapter msgsArrayAdapter;

    private List<Message> messages;
    private long userId;
    private long lastId;

    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mSqLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatId = Integer.valueOf(getIntent().getExtras().getString("chatId"));
        socUserId = Long.valueOf(getIntent().getExtras().getString("socUserId"));
        socNetId = Integer.valueOf(getIntent().getExtras().getString("socNetId"));
        chatName = getIntent().getExtras().getString("chatName");

        TextView text = (TextView) findViewById(R.id.chatTitle);
        text.setText(chatName);

        Button sendButton = (Button) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(sendButtonListener);

        msgField = (EditText) findViewById(R.id.chatMsg);
        msgList = (ListView) findViewById(R.id.msgList);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        Button menuButton = (Button) findViewById(R.id.chatMenuButton);
        menuButton.setOnClickListener(menuButtonListener);

        mDatabaseHelper = new DatabaseHelper(this, Consts.SQLiteDB.get(), null, Integer.valueOf(Consts.DbVersion.get()));
        mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();

        loadMessages();

        isRunning = new AtomicBoolean(true);
        Thread thread = new ChatRefreshThread(this);
        thread.start();
    }

    public boolean isRunning() {
        return isRunning.get();
    }

    public void loadMessages() {
        if(messages == null)
            loadMsgsFromSQLite();

        if(messages == null)
            DbApi.getInstance().getMessages(this, chatId, progressBar);
        else
            showMessages();
    }

    private void loadMsgsFromSQLite(){
        Cursor cursor = mSqLiteDatabase.query(DatabaseHelper.MSGS_TABLE, new String[]{
                        DatabaseHelper.MSG_ID_COLUMN, DatabaseHelper.MSG_CHAT_ID_COLUMN,
                        DatabaseHelper.MSG_USER_ID_COLUMN, DatabaseHelper.MSG_USER_NAME_COLUMN,
                        DatabaseHelper.MSG_TEXT_COLUMN
                },
                DatabaseHelper.MSG_CHAT_ID_COLUMN + " = " + chatId, null,null, null, null) ;
        List<Message> msgs = new ArrayList<>();

        while(cursor.moveToNext()){
            int msgId  = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.MSG_ID_COLUMN));
            int chatId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.MSG_CHAT_ID_COLUMN));
            int userId = cursor.getInt (cursor.getColumnIndex(DatabaseHelper.MSG_USER_ID_COLUMN));
            String userName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.MSG_USER_NAME_COLUMN));
            String text = cursor.getString (cursor.getColumnIndex(DatabaseHelper.MSG_TEXT_COLUMN));

            Message msg = new Message(msgId, chatId, userId, userName, text);
            Log.d(TAG, "msg = " + msg);
            msgs.add(msg);
        }
        if(!msgs.isEmpty()) {
            this.messages = new ArrayList<>(msgs);
            lastId = messages.get(messages.size()-1).getId();
        }

        cursor.close();

        userId = Long.valueOf(InnerDB.getInstance().getInnerUserId(socUserId));
    }

    public void loadMessagesCallback(List<Message> messageList, long user_id){
        if(messages == null) {
            messages = new ArrayList<>();
            messages.addAll(messageList);
            saveMessages();
        }
        else{
            messageList.removeAll(messages);
            messages.addAll(messageList);
            saveMessages();
            lastId = messages.get(messages.size() - 1).getId();
        }
        userId = user_id;

        showMessages();
    }

    private void saveMessages() {
        for(Message msg : messages){
            if(msg.getId() <= lastId)
                continue;

            ContentValues newValues = new ContentValues();

            newValues.put(DatabaseHelper.MSG_ID_COLUMN, msg.getId());
            newValues.put(DatabaseHelper.MSG_CHAT_ID_COLUMN, msg.getChatId());
            newValues.put(DatabaseHelper.MSG_USER_ID_COLUMN, msg.getUserId());
            newValues.put(DatabaseHelper.MSG_USER_NAME_COLUMN, msg.getUserName());
            newValues.put(DatabaseHelper.MSG_TEXT_COLUMN, msg.getText());

            long result = mSqLiteDatabase.insert(DatabaseHelper.MSGS_TABLE, null, newValues);
            Log.d(TAG, "result = " + result);
        }
    }

    public void showMessages(){
        if(msgsArrayAdapter == null) {
            msgsArrayAdapter = new MyListArrayAdapter(messages);
            msgList.setAdapter(msgsArrayAdapter);
        }
        else
            msgsArrayAdapter.notifyDataSetChanged();
    }

    public void updateMessages() {
        Log.d(TAG, "lastId = " + lastId);
        DbApi.getInstance().getNewMessages(this, lastId, chatId, userId);
    }


    private class MyListArrayAdapter extends ArrayAdapter<Message> {

        public MyListArrayAdapter(List<Message> messages) {
            super(ChatActivity.this, R.layout.msg_layout, messages);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = ChatActivity.this.getLayoutInflater().inflate(R.layout.msg_layout, parent, false);
            }
            final Message message = messages.get(position);

            TextView textMsgText = (TextView) itemView.findViewById(R.id.msgText);
            textMsgText.setText(message.getText());

            TextView textMsgUser = (TextView) itemView.findViewById(R.id.msgUser);

            ImageView userPic = (ImageView) itemView.findViewById(R.id.profileImage);

            userPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showUserProfile(message.getUserId(), message.getUserId() == userId);
                }
            });

            if(message.getUserId() == userId) {
                textMsgText.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
                textMsgUser.setText("");
                userPic.setVisibility(View.GONE);
            }
            else {
                createOrGetUserPic(userPic, message.getUserId());
                textMsgUser.setText(message.getUserName() + ":");
                textMsgText.setGravity(Gravity.CENTER_VERTICAL);
                userPic.setVisibility(View.VISIBLE);
            }

            return itemView;
        }

    }

    public void createOrGetUserPic(final ImageView userPic, int userId) {
        String image = InnerDB.getInstance().getUserImage(userId);

        if(image != null)
            Glide.with(this).load(Consts.ApiPHP.get() + "uploads/" + image ).into(userPic);
        else{
            DbApi.getInstance().getUser(userId, new GetUserCallback() {
                @Override
                public void setUser(User user) {
                    if (user.getImage() != null && !user.getImage().isEmpty()) {
                        Log.d(TAG, "pic set null");
                        InnerDB.getInstance().setUserImage(user);
                        Glide.with(ChatActivity.this).load(Consts.ApiPHP.get() + "uploads/" + user.getImage()).into(userPic);
                    } else {
                        Log.d(TAG, "pic set default");
                        userPic.setImageResource(R.drawable.default_userpic);
                    }
                }
            });
        }
    }

    private void showUserProfile(long userId, boolean isEditable) {
        Log.d(TAG, "showUserProfile userId = " + userId);
        Intent profileIntent = new Intent(this, ProfileActivity.class);

        profileIntent.putExtra("userId", String.valueOf(userId));
        profileIntent.putExtra("isEditable", String.valueOf(isEditable));

        startActivity(profileIntent);
    }

    private View.OnClickListener menuButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            openOptionsMenu();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_chat_info:
                showChatDetails();
                return true;
            case R.id.action_leave_chat:
                confirmLeaveChat();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void confirmLeaveChat() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Leave Chat")
                .setMessage("Are you sure you want to leave this chat?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        leaveChat();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    private void leaveChat() {
        String userId = InnerDB.getInstance().getInnerUserId(socUserId);
        DbApi.getInstance().leaveChat(this, chatId, userId, progressBar);
    }

    private void showChatDetails() {

        Intent mobIntent = new Intent(this, MobDetailActivity.class);

        mobIntent.putExtra("chatId", String.valueOf(chatId));
        mobIntent.putExtra("chatName", chatName);
        mobIntent.putExtra("socNetId", String.valueOf(socNetId));
        mobIntent.putExtra("socUserId", String.valueOf(socUserId));
        mobIntent.putExtra("joined", "yes");

        startActivity(mobIntent);
    }

    View.OnClickListener sendButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String message = msgField.getText().toString();
            if(!message.isEmpty()) {
                DbApi.getInstance().sendMsg(message, chatId);
                msgField.setText("");
                loadMessages();
            }
            else
                Toast.makeText(ChatActivity.this, "Message is empty", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        isRunning.set(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isRunning.set(true);
    }
}
