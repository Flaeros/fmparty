package ru.fmparty;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import ru.fmparty.apiaccess.Consts;
import ru.fmparty.apiaccess.DbApi;
import ru.fmparty.apiaccess.ResultObject;
import ru.fmparty.entity.Chat;
import ru.fmparty.utils.AsyncResponse;
import ru.fmparty.utils.ImageHelper;
import ru.fmparty.utils.UploadImageTask;

public class MobDetailActivity extends AppCompatActivity {

    private final static String TAG = "FlashMob MobDetail";

    private int chatId;
    private long socUserId;
    private int socNetId;
    private String filePath;

    private ProgressBar progressBar;

    ImageView imageView;
    EditText title;
    EditText descr;
    TextView mobDate;
    EditText mobCity;

    Chat chat;

    private Button updateChatButton;
    private Button selectImageButton;
    private DatePicker mobDatePicker;

    boolean isEditable;

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
        isEditable = Boolean.valueOf(getIntent().getExtras().getString("isEditable"));

        setTitle(chatName);

        Button joinMobButton = (Button) findViewById(R.id.joinMob);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        imageView = (ImageView) findViewById(R.id.chatImage);
        title = (EditText) findViewById(R.id.title);
        descr = (EditText) findViewById(R.id.descr);
        mobDate = (TextView) findViewById(R.id.mobDate);
        mobCity = (EditText) findViewById(R.id.mobCity);
        mobDatePicker = (DatePicker) findViewById(R.id.mobDatePicker);

        updateChatButton = (Button) findViewById(R.id.updateChatButton);
        selectImageButton = (Button) findViewById(R.id.selectImageButton);

        AdView mAdView = (AdView) findViewById(R.id.adView);
        //AdRequest adRequest = new AdRequest.Builder().build();

        AdRequest request = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice("AC98C820A50B4AD8A2106EDE96FB87D4")  // An example device ID
                .build();
        mAdView.loadAd(request);

        if(isEditable)
            setEditable();
        else
            setUneditable();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DbApi.getInstance().getChat(this, chatId, progressBar);

        if(joined!= null && joined.equals("yes"))
            joinMobButton.setVisibility(Button.GONE);

        joinMobButton.setOnClickListener(joinMobButtonnListener);

        title.setText(chatName);
        descr.setText(chatName);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data != null)
            filePath = ImageHelper.onActivityResultHelp(data, this, imageView);
        Log.d(TAG, "[onActivityResultHelp]filePath = " +filePath);
    }

    private void setEditable() {
        Log.d(TAG, "setEditable");
        selectImageButton.setVisibility(View.VISIBLE);

        mobDate.setVisibility(View.GONE);
        selectImageButton.setOnClickListener(selectImageButtonListener);
        imageView.setClickable(true);
        imageView.setOnClickListener(selectImageButtonListener);
        updateChatButton.setOnClickListener(updateChatButtonListener);
    }

    private void setUneditable() {
        title.setKeyListener(null);
        descr.setKeyListener(null);
        mobCity.setKeyListener(null);
        mobDatePicker.setVisibility(View.GONE);
        updateChatButton.setVisibility(View.GONE);
    }

    public void fillChat(Chat chat){
        this.chat = chat;

        Log.d(TAG, "chat = " + chat);

        if(!chat.getName().isEmpty())
            title.setText(chat.getName());
        if(!chat.getDescr().isEmpty())
            descr.setText(chat.getDescr());
        if(!chat.getDate().isEmpty()) {
            mobDate.setText(chat.getDate());

            if(isEditable) {
                String[] date = chat.getDate().split("\\.");
                mobDatePicker.updateDate(Integer.valueOf(date[2]), Integer.valueOf(date[1]) - 1, Integer.valueOf(date[0]));
            }
        }
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

    private View.OnClickListener updateChatButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "Click Update Profile");
            updateChat();
        }
    };

    private void updateChat() {
        String chatName = this.title.getText().toString();
        String chatDescr = this.descr.getText().toString();
        String chatDate = String.valueOf(this.mobDatePicker.getDayOfMonth()) +"."+ String.valueOf(this.mobDatePicker.getMonth()+1) +"."+ String.valueOf(this.mobDatePicker.getYear());
        String chatCity = this.mobCity.getText().toString();

        if(chatName.isEmpty()) {
            Toast.makeText(this, getString(R.string.enter_chat_name), Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        if(filePath != null)
            new UploadImageTask(hideProgressBarAR).execute("updateChat", filePath, String.valueOf(chatId), chatName, chatDescr, chatCity, chatDate);
        else
            DbApi.getInstance().updateChat(String.valueOf(chatId), chat.getImage(), chatName, chatDescr, chatCity, chatDate, hideProgressBarAR);
    }

    AsyncResponse hideProgressBarAR = new AsyncResponse() {
        @Override
        protected void onSuccess(ResultObject resultObject) {
            progressBar.setVisibility(View.GONE);
        }
    };

    private View.OnClickListener selectImageButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ImageHelper.selectImageFromGallery(MobDetailActivity.this);
        }
    };

}
