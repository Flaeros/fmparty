package ru.fmparty;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import ru.fmparty.apiaccess.SocialNetworkApi;

public class ChatActivity extends Activity{
    private static final String TAG = "FlashMob ChatActivity";
    private SocialNetworkApi socialNetworkApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        int chatId = Integer.valueOf(getIntent().getExtras().getString("chatId"));
        int socUserId = Integer.valueOf(getIntent().getExtras().getString("socUserId"));
        int socNetId = Integer.valueOf(getIntent().getExtras().getString("socNetId"));

        String log = chatId + " . " + socUserId + " . " + socNetId;
        Log.d(TAG, log);


        TextView text = (TextView) findViewById(R.id.chatTitle);
        text.setText(log);

        ChatFragment chatFragment = new ChatFragment();

        this.getFragmentManager().beginTransaction()
                .replace(R.id.chatMsgsCont, chatFragment)
                .commit();
    }
}
