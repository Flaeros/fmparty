package ru.fmparty.apiaccess;

import android.util.Log;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ru.fmparty.ChatActivity;
import ru.fmparty.MyListFragment;
import ru.fmparty.entity.Chat;
import ru.fmparty.entity.Message;
import ru.fmparty.utils.AsyncResponse;
import ru.fmparty.utils.HttpObjectPair;
import ru.fmparty.utils.PostCallTask;

public class DbApi {
    private final static String TAG = "FlashMob DbApi";

    public static void createUser(int socNetId, long socUserId, String name){

        List<HttpObjectPair> argsList = new ArrayList<>();
        argsList.add(new HttpObjectPair("do", "createUser"));
        argsList.add(new HttpObjectPair("socnetid", String.valueOf(socNetId)));
        argsList.add(new HttpObjectPair("socuserid", String.valueOf(socUserId)));
        argsList.add(new HttpObjectPair("name", name));

        new PostCallTask().execute(argsList.toArray(new HttpObjectPair[argsList.size()]));
    }

    public static void sendMsg(String message, int chatId, int socNetId, long socUserId) {
        List<HttpObjectPair> argsList = new ArrayList<>();
        argsList.add(new HttpObjectPair("do", "sendMsg"));
        argsList.add(new HttpObjectPair("textMsg", message));
        argsList.add(new HttpObjectPair("chatid", String.valueOf(chatId)));
        argsList.add(new HttpObjectPair("socnetid", String.valueOf(socNetId)));
        argsList.add(new HttpObjectPair("socuserid", String.valueOf(socUserId)));
        new PostCallTask().execute(argsList.toArray(new HttpObjectPair[argsList.size()]));
    }

    public static void getMessages(final ChatActivity activity, int chatId, long socUserId, int socNetId, ProgressBar progressBar) {
        List<HttpObjectPair> argsList = new ArrayList<>();
        argsList.add(new HttpObjectPair("do", "getMessages"));
        argsList.add(new HttpObjectPair("chatid", String.valueOf(chatId)));
        argsList.add(new HttpObjectPair("socuserid", String.valueOf(socUserId)));
        argsList.add(new HttpObjectPair("socnetid", String.valueOf(socNetId)));

        new PostCallTask(new AsyncResponse() {
            void onSuccess(ResultObject resultObject) {
                Log.d(TAG, "resultObject = " + resultObject);
                List<Message> messages = new ArrayList<>();
                long user_id = 0;
                try {

                    user_id = resultObject.getJsonObject().getLong("id");
                    JSONArray jsonMsgs = resultObject.getJsonObject().getJSONArray("msgs");

                    for(int i =0; i < jsonMsgs.length(); i++){
                        JSONObject jObj = jsonMsgs.getJSONObject(i);
                        Message msg = new Message(jObj.getLong("id")
                                                 ,jObj.getInt("chatId")
                                                 ,jObj.getLong("userId")
                                                 ,jObj.getString("userName")
                                                 ,jObj.getString("text"));
                        messages.add(msg);
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                    Log.d(TAG, e.toString());
                }

                Log.d(TAG, "messages = " + messages);
                activity.showMessages(messages, user_id);
            }
        }, progressBar).execute(argsList.toArray(new HttpObjectPair[argsList.size()]));
    }

    public static void getChats(final MyListFragment myListFragment, int socNetId, long socUserId, ProgressBar progressBar){
        List<HttpObjectPair> argsList = new ArrayList<>();
        argsList.add(new HttpObjectPair("do", "getChats"));
        argsList.add(new HttpObjectPair("socUserId", String.valueOf(socUserId)));
        argsList.add(new HttpObjectPair("socNetId", String.valueOf(socNetId)));

        progressBar.setVisibility(ProgressBar.VISIBLE);

        new PostCallTask(new AsyncResponse(){
            public void onSuccess(ResultObject resultObject) {
                List<Chat> chatList = new ArrayList<>();
                try {
                    Log.d(TAG, "onSuccess chats=" + resultObject);
                    JSONArray chats = resultObject.getJsonArray();
                    Log.d(TAG, "onSuccess chats=" + chats);



                    for(int i = 0; i < chats.length(); i++){
                        JSONObject row = chats.getJSONObject(i);
                        Chat chat = new Chat(row.getInt("id"), row.getInt("admin"), row.getString("name"));
                        chatList.add(chat);
                    }
                }catch (Exception e){
                    Log.d(TAG, e.toString());
                    Log.d(TAG, e.getMessage());
                    e.printStackTrace();
                }

                Log.d(TAG, "chatList = " + chatList);
                myListFragment.showChats(chatList);
            }
        }, progressBar).execute(argsList.toArray(new HttpObjectPair[argsList.size()]));
    }
}
