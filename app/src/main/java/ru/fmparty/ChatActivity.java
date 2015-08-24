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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import ru.fmparty.apiaccess.DbApi;
import ru.fmparty.entity.Message;
import ru.fmparty.utils.ChatRefreshThread;

public class ChatActivity extends Activity{
    private static final String TAG = "FlashMob ChatActivity";

    private EditText msgField;
    private int chatId;
    private long socUserId;
    private int socNetId;
    private AtomicBoolean isRunning;

    private ListView msgList;

    private MyListArrayAdapter msgsArrayAdapter;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatId = Integer.valueOf(getIntent().getExtras().getString("chatId"));
        socUserId = Long.valueOf(getIntent().getExtras().getString("socUserId"));
        socNetId = Integer.valueOf(getIntent().getExtras().getString("socNetId"));
        String chatName = getIntent().getExtras().getString("chatName");

        TextView text = (TextView) findViewById(R.id.chatTitle);
        text.setText(chatName);

        Button sendButton = (Button) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(sendButtonListener);

        msgField = (EditText) findViewById(R.id.chatMsg);
        msgList = (ListView) findViewById(R.id.msgList);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(ProgressBar.VISIBLE);

        loadMessages();

        isRunning = new AtomicBoolean(true);
        Thread thread = new ChatRefreshThread(this);
        thread.start();
    }

    public boolean isRunning() {
        return isRunning.get();
    }

    public void loadMessages() {
        DbApi.getMessages(this, chatId, socUserId, socNetId, progressBar);
    }

    View.OnClickListener sendButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String message = msgField.getText().toString();
            if(!message.isEmpty()) {
                DbApi.sendMsg(message, chatId, socNetId, socUserId);
                msgField.setText("");
                loadMessages();
            }
            else
                Toast.makeText(ChatActivity.this, "Message is empty", Toast.LENGTH_SHORT).show();
        }
    };

    public void showMessages(List<Message> messages, long userId){
        if(msgsArrayAdapter == null){
            msgsArrayAdapter = new MyListArrayAdapter(messages, userId);
            msgList.setAdapter(msgsArrayAdapter);
        }

        if(msgsArrayAdapter.getData().equals(messages)){
            return;
        }

        msgsArrayAdapter.getData().clear();
        msgsArrayAdapter.getData().addAll(messages);
        msgsArrayAdapter.setUserId(userId);
        msgsArrayAdapter.notifyDataSetChanged();
    }

    private class MyListArrayAdapter extends ArrayAdapter<Message> {

        private List<Message> messageList;
        long userId;

        public MyListArrayAdapter(List<Message> messageList, long userId) {
            super(ChatActivity.this, R.layout.msg_layout, messageList);
            this.messageList = messageList;
            this.userId = userId;
        }

        public void setUserId(long userId){
            this.userId = userId;
        }

        public List<Message> getData(){
            return messageList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = ChatActivity.this.getLayoutInflater().inflate(R.layout.msg_layout, parent, false);
            }

            Message message = messageList.get(position);

            TextView textMsgText = (TextView) itemView.findViewById(R.id.msgText);
            textMsgText.setText(message.getText());

            if(message.getUserId() == userId) {
                textMsgText.setGravity(Gravity.RIGHT);
            }
            else{
                TextView textMsgUser = (TextView) itemView.findViewById(R.id.msgUser);
                textMsgUser.setText(message.getUserName()+":");
            }

            return itemView;
        }

    }
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
