package ru.fmparty;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import ru.fmparty.apiaccess.Consts;
import ru.fmparty.apiaccess.DbApi;
import ru.fmparty.apiaccess.SocialNetworkApi;
import ru.fmparty.entity.Chat;
import ru.fmparty.utils.InnerDB;
import ru.fmparty.utils.Nameable;

public class FindMobFragment extends Fragment implements Nameable {
    private final String TAG = "FlashMob FindMob";

    private SocialNetworkApi socialNetworkApi;


    private EditText mobName;
    private EditText mobDescr;
    private DatePicker mobDate;
    private EditText mobCity;
    private CheckBox useDate;
    private ProgressBar progressBar;
    private ListView mobsListView;
    private ScrollView scrollView;
    private MobListArrayAdapter mobListArrayAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.find_mob_fragment,
                container, false);

        Button findMobsButton = (Button) view.findViewById(R.id.findMobButton);
        findMobsButton.setOnClickListener(findMobsButtonListener);

        mobName = (EditText) view.findViewById(R.id.mobName);
        mobDescr = (EditText) view.findViewById(R.id.mobDescr);
        mobDate = (DatePicker) view.findViewById(R.id.mobDate);
        mobCity = (EditText) view.findViewById(R.id.mobCity);
        useDate = (CheckBox) view.findViewById(R.id.useDate);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        scrollView = (ScrollView) view.findViewById(R.id.scrollView);
        mobsListView = (ListView) view.findViewById(R.id.mobList);
        mobsListView.setOnItemClickListener(onMobItemClickListener);

        return view;
    }

    private View.OnClickListener findMobsButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String mobNameStr = mobName.getText().toString();
            String mobDescrStr = mobDescr.getText().toString();
            String mobDateStr = String.valueOf(mobDate.getDayOfMonth()) +"."+ String.valueOf(mobDate.getMonth()+1) +"."+ String.valueOf(mobDate.getYear());
            String mobCityStr = mobCity.getText().toString();
            boolean isUseDate = useDate.isChecked();

            findMobs(mobNameStr, mobDescrStr, mobDateStr, mobCityStr, isUseDate);
        }
    };

    AdapterView.OnItemClickListener onMobItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            TextView chatNameView = (TextView) view.findViewById(R.id.item_chatName);
            TextView chatIdView = (TextView) view.findViewById(R.id.chatId);
            String chatName = chatNameView.getText().toString();
            int chatId = Integer.valueOf(chatIdView.getText().toString());

            showMobDetail(chatId, chatName);
        }
    };

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) { return; }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private void showMobDetail(int chatId, String chatName) {

        Intent mobIntent = new Intent(getActivity(), MobDetailActivity.class);

        mobIntent.putExtra("chatId", String.valueOf(chatId));
        mobIntent.putExtra("chatName", chatName);
        mobIntent.putExtra("socNetId", String.valueOf(socialNetworkApi.getSocialCodeId()));
        mobIntent.putExtra("socUserId", String.valueOf(socialNetworkApi.getUserId()));

        startActivity(mobIntent);
    }

    private void findMobs(String mobNameStr, String mobDescrStr, String mobDateStr, String mobCityStr, boolean useDate) {

        String userId = InnerDB.getInstance().getInnerUserId(socialNetworkApi.getUserId());
        Log.d(TAG, "userId = " + userId);
        DbApi.getInstance().findMobs(this, mobNameStr, mobDescrStr, mobDateStr, mobCityStr, userId, useDate, progressBar);
    }

    public void showMobs(List<Chat> chats){
        if(this.isVisible()) {
            mobListArrayAdapter = new MobListArrayAdapter(chats);
            mobsListView.setAdapter(mobListArrayAdapter);
            setListViewHeightBasedOnChildren(mobsListView);

            focusOnResultsView();
        }
    }

    private final void focusOnResultsView(){
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                scrollView.smoothScrollTo(0, mobsListView.getTop());
            }
        });
    }

    public void setSocialNetworkApi(SocialNetworkApi socialNetworkApi) {
        this.socialNetworkApi = socialNetworkApi;
    }

    @Override
    public String getTitle() {
        return "Find Mob";
    }

    private class MobListArrayAdapter extends ArrayAdapter<Chat> {

        private List<Chat> chats;

        public MobListArrayAdapter(List<Chat> chatList) {
            super(getActivity(), R.layout.chat_list_item_layout, chatList);
            this.chats = new ArrayList<>(chatList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = getActivity().getLayoutInflater().inflate(R.layout.chat_list_item_layout, parent, false);
            }

            Chat chat = chats.get(position);

            TextView textViewName = (TextView) itemView.findViewById(R.id.item_chatName);
            textViewName.setText(chat.getName());

            TextView chatIdView = (TextView) itemView.findViewById(R.id.chatId);
            chatIdView.setText(String.valueOf(chat.getId()));
            ImageView imageView = (ImageView) itemView.findViewById(R.id.item_chatImage);


            if(chat.getImage() != null && FindMobFragment.this.isVisible()) {
                Glide.with(getActivity()).load(Consts.ApiPHP.get() + "uploads/" +chat.getImage()).into(imageView);
            }
            else{
                imageView.setImageResource(R.drawable.default_chat);
            }

            return itemView;
        }


    }
}
