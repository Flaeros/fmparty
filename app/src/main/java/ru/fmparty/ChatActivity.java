package ru.fmparty;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ru.fmparty.apiaccess.DbApi;
import ru.fmparty.apiaccess.SocialNetworkApi;

public class ChatActivity extends Activity{
    private static final String TAG = "FlashMob ChatActivity";
    private SocialNetworkApi socialNetworkApi;

    private EditText msgField;
    private int chatId;
    private int socUserId;
    private int socNetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatId = Integer.valueOf(getIntent().getExtras().getString("chatId"));
        socUserId = Integer.valueOf(getIntent().getExtras().getString("socUserId"));
        socNetId = Integer.valueOf(getIntent().getExtras().getString("socNetId"));

        String log = chatId + " . " + socUserId + " . " + socNetId;
        Log.d(TAG, log);


        TextView text = (TextView) findViewById(R.id.chatTitle);
        text.setText(log);

        Button sendButton = (Button) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(sendButtonListener);

        msgField = (EditText) findViewById(R.id.chatMsg);

        loadMessages();
    }

    private void loadMessages() {
        DbApi.getMessages(this, chatId, socUserId, socNetId);
    }

    View.OnClickListener sendButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String message = msgField.getText().toString();
            if(message != null && !message.isEmpty())
                DbApi.sendMsg(message, chatId, socNetId, socUserId);
            else
                Toast.makeText(ChatActivity.this, "Message is empty", Toast.LENGTH_SHORT).show();
        }
    };
}
