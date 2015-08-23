package ru.fmparty;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import ru.fmparty.apiaccess.DbApi;
import ru.fmparty.entity.Message;
import ru.fmparty.utils.ChatRefreshThread;

public class ChatActivity extends Activity{
    private static final String TAG = "FlashMob ChatActivity";

    private EditText msgField;
    private ListView msgList;
    private int chatId;
    private long socUserId;
    private int socNetId;
    private AtomicBoolean isRunning;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatId = Integer.valueOf(getIntent().getExtras().getString("chatId"));
        socUserId = Long.valueOf(getIntent().getExtras().getString("socUserId"));
        socNetId = Integer.valueOf(getIntent().getExtras().getString("socNetId"));

        String log = chatId + " . " + socUserId + " . " + socNetId;
        Log.d(TAG, log);


        TextView text = (TextView) findViewById(R.id.chatTitle);
        text.setText(log);

        Button sendButton = (Button) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(sendButtonListener);

        msgField = (EditText) findViewById(R.id.chatMsg);
        msgList = (ListView) findViewById(R.id.msgList);

        loadMessages();

        isRunning = new AtomicBoolean(true);
        Thread thread = new ChatRefreshThread(this);
        thread.start();
    }

    public boolean isRunning() {
        return isRunning.get();
    }

    public void loadMessages() {
        DbApi.getMessages(this, chatId, socUserId, socNetId);
    }

    View.OnClickListener sendButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String message = msgField.getText().toString();
            if(message != null && !message.isEmpty()) {
                DbApi.sendMsg(message, chatId, socNetId, socUserId);
                msgField.setText("");
            }
            else
                Toast.makeText(ChatActivity.this, "Message is empty", Toast.LENGTH_SHORT).show();
        }
    };

    public void showMessages(List<Message> messages, long userId){
        ArrayAdapter<Message> userArrayAdapter = new MyListArrayAdapter(messages, userId);
        msgList.setAdapter(userArrayAdapter);
    }

    private class MyListArrayAdapter extends ArrayAdapter<Message> {

        List<Message> messageList;
        long userId;

        public MyListArrayAdapter(List<Message> messageList, long userId) {
            super(ChatActivity.this, R.layout.msg_layout, messageList);
            this.messageList = messageList;
            this.userId = userId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = ChatActivity.this.getLayoutInflater().inflate(R.layout.msg_layout, parent, false);
            }

            Message message = messageList.get(position);

            TextView textMsgUser = (TextView) itemView.findViewById(R.id.msgUser);
            textMsgUser.setText(String.valueOf(message.getUser_id()));

            if(message.getUser_id() == userId) {
                textMsgUser.setGravity(Gravity.RIGHT);
                textMsgUser.setTextColor(Color.RED);
            }

            TextView textMsgText = (TextView) itemView.findViewById(R.id.msgText);
            textMsgText.setText(message.getText());

            return itemView;
        }
    }
}
