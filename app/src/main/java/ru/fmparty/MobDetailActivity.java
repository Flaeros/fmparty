package ru.fmparty;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import ru.fmparty.apiaccess.Consts;
import ru.fmparty.apiaccess.DbApi;
import ru.fmparty.entity.Chat;

public class MobDetailActivity extends AppCompatActivity {

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
        String joined = getIntent().getExtras().getString("joined");

        Button joinMobButton = (Button) findViewById(R.id.joinMob);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        imageView = (ImageView) findViewById(R.id.chatImage);
        title = (TextView) findViewById(R.id.title);
        descr = (TextView) findViewById(R.id.descr);
        mobDate = (TextView) findViewById(R.id.mobDate);
        mobCity = (TextView) findViewById(R.id.mobCity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DbApi.getInstance().getChat(this, chatId, progressBar);

        if(joined!= null && joined.equals("yes"))
            joinMobButton.setVisibility(Button.GONE);

        joinMobButton.setOnClickListener(joinMobButtonnListener);

        title.setText(chatName);
        descr.setText(chatName);
    }

    public void fillChat(Chat chat){
        this.chat = chat;

        Log.d(TAG, "chat = " + chat);

        if(!chat.getDescr().isEmpty())
            descr.setText(chat.getDescr());
        if(!chat.getDate().isEmpty())
            mobDate.setText(chat.getDate());
        if(!chat.getCity().isEmpty())
            mobCity.setText(chat.getCity());

        if(!this.isDestroyed() )
            Glide.with(this).load(Consts.ApiPHP.get() + "uploads/" +chat.getImage()).asBitmap().into(imageView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
        }
        return true;
    }

    private View.OnClickListener joinMobButtonnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "Click Create Mob");
            joinMob();
        }
    };

    private void joinMob() {
        DbApi.getInstance().joinMob(this, chatId, progressBar);
    }

}
