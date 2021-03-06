package ru.fmparty;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;

import ru.fmparty.apiaccess.Consts;
import ru.fmparty.apiaccess.DbApi;
import ru.fmparty.apiaccess.ResultCode;
import ru.fmparty.entity.Message;
import ru.fmparty.entity.User;
import ru.fmparty.utils.ChatExitable;
import ru.fmparty.utils.ChatRefreshThread;
import ru.fmparty.utils.DatabaseHelper;
import ru.fmparty.utils.GetUserCallback;
import ru.fmparty.utils.InnerDB;

public class ChatActivity extends AppCompatActivity implements ChatExitable{
    private static final String TAG = "FlashMob ChatActivity";

    private int chatId;
    private int chatAdminId;
    private String chatName;
    private long socUserId;
    private int socNetId;
    private AtomicBoolean isRunning;

    private ProgressBar progressBar;
    private EditText msgField;
    private ListView msgList;

    private TreeMapAdapter msgsArrayAdapter;

    private TreeMap<Long, Message> messages;
    private long userId;
    private long lastId;

    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mSqLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatId = Integer.valueOf(getIntent().getExtras().getString("chatId"));
        chatAdminId = Integer.valueOf(getIntent().getExtras().getString("chatAdminId"));
        socUserId = Long.valueOf(getIntent().getExtras().getString("socUserId"));
        socNetId = Integer.valueOf(getIntent().getExtras().getString("socNetId"));
        chatName = getIntent().getExtras().getString("chatName");

        setTitle(chatName);

        Button sendButton = (Button) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(sendButtonListener);

        msgField = (EditText) findViewById(R.id.chatMsg);
        msgList = (ListView) findViewById(R.id.msgList);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

    @Override
    public void onBackPressed() {
        Intent answerIntent = new Intent();
        answerIntent.putExtra("ChatClosed", "Yes");
        setResult(RESULT_OK, answerIntent);
        finish();
        return;
    }

    private void loadMsgsFromSQLite(){
        Cursor cursor = mSqLiteDatabase.query(DatabaseHelper.MSGS_TABLE, new String[]{
                        DatabaseHelper.MSG_ID_COLUMN, DatabaseHelper.MSG_CHAT_ID_COLUMN,
                        DatabaseHelper.MSG_USER_ID_COLUMN, DatabaseHelper.MSG_USER_NAME_COLUMN,
                        DatabaseHelper.MSG_TEXT_COLUMN
                },
                DatabaseHelper.MSG_CHAT_ID_COLUMN + " = " + chatId, null,null, null, null) ;
        TreeMap<Long, Message> msgs = new TreeMap<>();

        while(cursor.moveToNext()){
            int msgId  = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.MSG_ID_COLUMN));
            int chatId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.MSG_CHAT_ID_COLUMN));
            int userId = cursor.getInt (cursor.getColumnIndex(DatabaseHelper.MSG_USER_ID_COLUMN));
            String userName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.MSG_USER_NAME_COLUMN));
            String text = cursor.getString (cursor.getColumnIndex(DatabaseHelper.MSG_TEXT_COLUMN));

            Message msg = new Message(msgId, chatId, userId, userName, text);
            Log.d(TAG, "msg = " + msg);
            msgs.put(msg.getId(), msg);
        }
        if(!msgs.isEmpty()) {
            this.messages = new TreeMap<>(msgs);
            lastId = messages.lastKey();
        }

        cursor.close();

        Log.d(TAG, "socUserId = " + socUserId);

        String user_id  = InnerDB.getInstance().getInnerUserId(socUserId);
        Log.d(TAG, "user_id = " + user_id);
        Log.d(TAG, "CANNOT BE NULL = " + user_id);

        //if(user_id != null)
            userId = Long.valueOf(user_id);
    }

    public void loadMessagesCallback(TreeMap<Long, Message> messageList, long user_id){
        if(messages == null) {
            messages = new TreeMap<>(messageList);
            saveMessages();
        }
        else{
            messages.putAll(messageList);
            saveMessages();
            lastId = messages.lastKey();
        }
        userId = user_id;

        showMessages();
    }

    private void saveMessages() {
        for(Map.Entry<Long, Message> entry : messages.entrySet()){
            if(entry.getKey() <= lastId)
                continue;

            Message msg = entry.getValue();

            ContentValues newValues = new ContentValues();

            newValues.put(DatabaseHelper.MSG_ID_COLUMN, msg.getId());
            newValues.put(DatabaseHelper.MSG_CHAT_ID_COLUMN, msg.getChatId());
            newValues.put(DatabaseHelper.MSG_USER_ID_COLUMN, msg.getUserId());
            newValues.put(DatabaseHelper.MSG_USER_NAME_COLUMN, msg.getUserName());
            newValues.put(DatabaseHelper.MSG_TEXT_COLUMN, msg.getText());

            long result = mSqLiteDatabase.insert(DatabaseHelper.MSGS_TABLE, null, newValues);
            Log.d(TAG, "insert result = " + result);
        }
    }

    public void showMessages(){
        if(msgsArrayAdapter == null) {
            msgsArrayAdapter = new TreeMapAdapter();
            msgList.setAdapter(msgsArrayAdapter);
        }
        else {
            msgsArrayAdapter.updateKeys();
            msgsArrayAdapter.notifyDataSetChanged();
        }
    }

    public void updateMessages() {
        Log.d(TAG, "lastId = " + lastId);
        DbApi.getInstance().getNewMessages(this, lastId, chatId, userId);
    }

    @Override
    public void exitChat() {
        setResult(ResultCode.CHAT_LEFT.get());
        finish();
    }

    private class TreeMapAdapter extends ArrayAdapter<Message> {

        private Long[] mapKeys;

        public TreeMapAdapter() {
            super(ChatActivity.this, R.layout.msg_layout);
            mapKeys = messages.keySet().toArray(new Long[getCount()]);
        }

        public void updateKeys(){
            mapKeys = messages.keySet().toArray(new Long[getCount()]);
        }

        @Override
        public int getCount() {
            return messages.size();
        }

        @Override
        public Message getItem(int position) {
            return messages.get(mapKeys[position]);
        }

        @Override
        public long getItemId(int position) {
            return mapKeys[position];
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = ChatActivity.this.getLayoutInflater().inflate(R.layout.msg_layout, parent, false);
            }
            final Message message = messages.get(mapKeys[position]);

            TextView textMsgText = (TextView) itemView.findViewById(R.id.msgText);
            textMsgText.setText(message.getText());
            Log.d(TAG, "[WTF] text = " + message.getText());
            Log.d(TAG, "[WTF] name = " + message.getUserName());
            Log.d(TAG, "[WTF] id = " + message.getId());

            LinearLayout msgLinear = (LinearLayout) itemView.findViewById(R.id.msgLinear);
            TextView textMsgUser = (TextView) itemView.findViewById(R.id.msgUser);

            ImageView userPic = (ImageView) itemView.findViewById(R.id.profileImage);

            userPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showUserProfile(message.getUserId(), message.getUserId() == userId);
                }
            });

            if(message.getUserId() == userId) {
                Log.d(TAG, "gravity me");
                textMsgText.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                msgLinear.setGravity(Gravity.RIGHT);
                textMsgUser.setText("");
                userPic.setVisibility(View.GONE);
            }
            else {
                Log.d(TAG, "gravity other");
                createOrGetUserPic(userPic, message.getUserId());
                textMsgUser.setText(message.getUserName() + ":");
                textMsgText.setGravity(Gravity.CENTER_VERTICAL);
                msgLinear.setGravity(Gravity.LEFT);
                userPic.setVisibility(View.VISIBLE);
            }

            return itemView;
        }

    }

    public void createOrGetUserPic(final ImageView userPic, int userId) {
        String image = InnerDB.getInstance().getUserImage(userId);

        Log.d(TAG, "image = " + image);
        if(image != null && !image.equals("NULL") && !image.equals("null")) {
            Log.d(TAG, "image null ??");
            try {
                Glide.with(this).load(Consts.ApiPHP.get() + "uploads/" + image).asBitmap().into(userPic);
            }
            catch (Exception e){
                Log.e(TAG, "Cannot set image");
            }
        }
        else{
            Log.d(TAG, "else image null");
            DbApi.getInstance().getUser(userId, new GetUserCallback() {
                @Override
                public void setUser(User user) {
                    Log.d(TAG, "find pic = " + user.getName());
                    if (user.getImage() != null && !user.getImage().isEmpty() && !user.getImage().equals("NULL") && !user.getImage().equals("null")) {
                        Log.d(TAG, "pic set null");
                        Log.d(TAG, "pic = " + user.getImage());
                        InnerDB.getInstance().setUserImage(user);
                        if (ChatActivity.this.isRunning()) {
                            try {
                                Glide.with(ChatActivity.this).load(Consts.ApiPHP.get() + "uploads/" + user.getImage()).asBitmap().into(userPic);
                            } catch (Exception e) {
                                Log.e(TAG, "Cannot set image");
                            }
                        }
                    } else {
                        Log.d(TAG, "pic set default");
                        userPic.setImageResource(R.drawable.default_userpic);
                    }
                }
            }, progressBar);
        }
    }

    private void showUserProfile(long userId, boolean isEditable) {
        Log.d(TAG, "showUserProfile userId = " + userId);
        Intent profileIntent = new Intent(this, ProfileActivity.class);

        profileIntent.putExtra("userId", String.valueOf(userId));
        profileIntent.putExtra("isEditable", String.valueOf(isEditable));

        startActivity(profileIntent);
    }

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
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void confirmLeaveChat() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.action_leave_chat))
                .setMessage(getString(R.string.confirmLeaveChat))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        leaveChat();
                    }

                })
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }

    private void leaveChat() {
        String userId = InnerDB.getInstance().getInnerUserId(socUserId);
        DbApi.getInstance().leaveChat(this, chatId, userId, progressBar);
    }

    private void showChatDetails() {

        Intent mobIntent = new Intent(this, MobDetailActivity.class);

        Boolean isEditable = (userId == chatAdminId);

        mobIntent.putExtra("chatId", String.valueOf(chatId));
        mobIntent.putExtra("chatName", chatName);
        mobIntent.putExtra("socNetId", String.valueOf(socNetId));
        mobIntent.putExtra("socUserId", String.valueOf(socUserId));
        mobIntent.putExtra("joined", "yes");
        mobIntent.putExtra("isEditable", isEditable.toString());

        startActivity(mobIntent);
    }

    View.OnClickListener sendButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String message = msgField.getText().toString();
            if(!message.isEmpty()) {
                DbApi.getInstance().sendMsg(message, chatId, progressBar);
                msgField.setText("");
                loadMessages();
            }
            else
                Toast.makeText(ChatActivity.this, getString(R.string.err_empty_msg), Toast.LENGTH_SHORT).show();
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
