package ru.fmparty;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import ru.fmparty.apiaccess.Consts;
import ru.fmparty.apiaccess.DbApi;
import ru.fmparty.entity.Chat;

public class MobDetailActivity extends Activity{

    private final static String TAG = "FlashMob MobDetail";

    private int chatId;
    private long socUserId;
    private int socNetId;

    private ProgressBar progressBar;

    ImageView imageView;
    TextView title;
    TextView descr;
    TextView mobDate;
    TextView mobCity;

    Chat chat;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mob_detail);

        Log.d(TAG, "enter detail mob activity");

        chatId = Integer.valueOf(getIntent().getExtras().getString("chatId"));
        socUserId = Long.valueOf(getIntent().getExtras().getString("socUserId"));
        socNetId = Integer.valueOf(getIntent().getExtras().getString("socNetId"));
        String chatName = getIntent().getExtras().getString("chatName");

        Button joinMobButton = (Button) findViewById(R.id.joinMob);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        imageView = (ImageView) findViewById(R.id.chatImage);
        title = (TextView) findViewById(R.id.title);
        descr = (TextView) findViewById(R.id.descr);
        mobDate = (TextView) findViewById(R.id.mobDate);
        mobCity = (TextView) findViewById(R.id.mobCity);

        DbApi.getChat(this, chatId, progressBar);

        joinMobButton.setOnClickListener(joinMobButtonnListener);



        title.setText(chatName);
        descr.setText(chatName);

    }

    public void fillChat(Chat chat){
        this.chat = chat;

        descr.setText(chat.getDescr());
        mobDate.setText(chat.getDate());
        mobCity.setText(chat.getCity());
        if(!this.isDestroyed() )
        Glide.with(this).load(Consts.ApiPHP.get() + "uploads/" +chat.getImage()).into(imageView);
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
