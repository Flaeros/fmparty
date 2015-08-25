package ru.fmparty;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import ru.fmparty.apiaccess.DbApi;

public class MobDetailActivity extends Activity{

    private final static String TAG = "FlashMob MobDetail";

    private int chatId;
    private long socUserId;
    private int socNetId;

    private ProgressBar progressBar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mob_detail);

        Log.d(TAG, "enter detail mob activity");

        chatId = Integer.valueOf(getIntent().getExtras().getString("chatId"));
        socUserId = Long.valueOf(getIntent().getExtras().getString("socUserId"));
        socNetId = Integer.valueOf(getIntent().getExtras().getString("socNetId"));
        String chatName = getIntent().getExtras().getString("chatName");


        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        Button joinMobButton = (Button) findViewById(R.id.joinMob);

        joinMobButton.setOnClickListener(joinMobButtonnListener);

        TextView titleView = (TextView) findViewById(R.id.title);
        TextView adminView = (TextView) findViewById(R.id.admin);

        titleView.setText(chatName);
        adminView.setText(chatName);

    }

    private View.OnClickListener joinMobButtonnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "Click Create Mob");
            joinMob();
        }
    };

    private void joinMob() {
        DbApi.joinMob(this, chatId, socNetId, socUserId, progressBar);
    }

}
