package ru.fmparty.apiaccess;

import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ru.fmparty.ChatActivity;
import ru.fmparty.FindMobFragment;
import ru.fmparty.MobDetailActivity;
import ru.fmparty.MyListFragment;
import ru.fmparty.entity.Chat;
import ru.fmparty.entity.Message;
import ru.fmparty.entity.User;
import ru.fmparty.utils.AsyncResponse;
import ru.fmparty.utils.GetUserCallback;
import ru.fmparty.utils.HttpObjectPair;
import ru.fmparty.utils.InnerDB;
import ru.fmparty.utils.PostCallTask;

public class DbApi {
    private final static String TAG = "FlashMob DbApi";
    private SocialNetworkApi socialNetworkApi;

    private static volatile DbApi instance;

    public static DbApi getInstance() {
        DbApi localInstance = instance;
        if (localInstance == null) {
            synchronized (DbApi.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new DbApi();
                }
            }
        }
        return localInstance;
    }

    public void setSocialNetworkApi(SocialNetworkApi socialNetworkApi){
        this.socialNetworkApi = socialNetworkApi;
    }

    private List<HttpObjectPair> defaultArgList(){
        List<HttpObjectPair> argsList = new ArrayList<>();

        argsList.add(new HttpObjectPair("socnetid", String.valueOf(socialNetworkApi.getSocialCodeId())));
        argsList.add(new HttpObjectPair("socuserid", String.valueOf(socialNetworkApi.getUserId())));
        argsList.add(new HttpObjectPair("token", socialNetworkApi.getToken()));

        Log.d(TAG, "socialNetworkApi.getToken() = " + socialNetworkApi.getToken());
        return argsList;
    }

    /*
       Checking if the user already exists in DB
       return ID if yes
       create and return ID if no
    */
    public void createUser(String name){
        List<HttpObjectPair>argsList = defaultArgList();

        argsList.add(new HttpObjectPair("do", "createUser"));
        argsList.add(new HttpObjectPair("name", name));

        new PostCallTask(new AsyncResponse() {
            @Override
            protected void onSuccess(ResultObject resultObject) {
                int id = 0;

                try {
                    id = resultObject.getJsonObject().getInt("id");
                } catch (JSONException e) {
                    Log.d(TAG, "error = " + e.toString());
                    e.printStackTrace();
                }

                String innerUserId = InnerDB.getInstance().getInnerUserId(socialNetworkApi.getUserId());
                if(innerUserId == null && id != 0)
                    InnerDB.getInstance().setInnerUserId(id, socialNetworkApi.getUserId());
            }
        }).execute(argsList.toArray(new HttpObjectPair[argsList.size()]));
    }

    public void getUser(int id, final GetUserCallback getUserCallback){

        List<HttpObjectPair>argsList = defaultArgList();

        argsList.add(new HttpObjectPair("do", "getUser"));
        argsList.add(new HttpObjectPair("id", String.valueOf(id)));

        new PostCallTask(new AsyncResponse() {
            @Override
            protected void onSuccess(ResultObject resultObject) {
                User user = null;
                try {
                    JSONObject jObj = resultObject.getJsonObject();

                    user = new User(jObj.getInt("id")
                            ,jObj.getInt("socNetId")
                            ,jObj.getLong("socUserId")
                            ,jObj.getString("name")
                            ,jObj.getString("image"));

                } catch (JSONException e) {
                    Log.d(TAG, "error = " + e.toString());
                    e.printStackTrace();
                }

                if(user != null)
                    getUserCallback.setUser(user);
            }
        }).execute(argsList.toArray(new HttpObjectPair[argsList.size()]));
    }

    public void sendMsg(String message, int chatId) {
        List<HttpObjectPair>argsList = defaultArgList();

        argsList.add(new HttpObjectPair("do", "sendMsg"));
        argsList.add(new HttpObjectPair("textMsg", message));
        argsList.add(new HttpObjectPair("chatid", String.valueOf(chatId)));

        new PostCallTask().execute(argsList.toArray(new HttpObjectPair[argsList.size()]));
    }

    public void getMessages(final ChatActivity activity, int chatId, ProgressBar progressBar) {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        List<HttpObjectPair>argsList = defaultArgList();

        argsList.add(new HttpObjectPair("do", "getMessages"));
        argsList.add(new HttpObjectPair("chatid", String.valueOf(chatId)));

        new PostCallTask(new AsyncResponse() {
            @Override
            protected void onSuccess(ResultObject resultObject) {
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
                                                 ,jObj.getInt("userId")
                                                 ,jObj.getString("userName")
                                                 ,jObj.getString("text"));
                        messages.add(msg);
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                    Log.d(TAG, e.toString());
                }

                Log.d(TAG, "messages = " + messages);
                activity.loadMessagesCallback(messages, user_id);
            }
        }, progressBar).execute(argsList.toArray(new HttpObjectPair[argsList.size()]));
    }

    public void getChats(final MyListFragment myListFragment, ProgressBar progressBar){
        List<HttpObjectPair>argsList = defaultArgList();
        argsList.add(new HttpObjectPair("do", "getChats"));

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

                        Chat chat = (new Chat.Builder(row.getInt("id"),row.getInt("admin"), row.getString("name")))
                                .image(row.getString("image"))
                                .descr(row.getString("descr"))
                                .date(row.getString("date"))
                                .city(row.getString("city"))
                                .build();

                        chatList.add(chat);
                    }
                }catch (Exception e){
                    Log.d(TAG, e.toString());
                    Log.d(TAG, e.getMessage());
                    e.printStackTrace();
                }

                Log.d(TAG, "chatList = " + chatList);
                myListFragment.updateChatsCallback(chatList);
            }
        }, progressBar).execute(argsList.toArray(new HttpObjectPair[argsList.size()]));
    }

    public void findMobs(final FindMobFragment findMobFragment, String mobNameStr, String mobDescrStr, String mobDateStr, String mobCityStr, String userId, Boolean useDate, ProgressBar progressBar) {
        List<HttpObjectPair>argsList = defaultArgList();

        argsList.add(new HttpObjectPair("do", "findMobs"));
        argsList.add(new HttpObjectPair("mobName", mobNameStr));
        argsList.add(new HttpObjectPair("mobDescr", mobDescrStr));
        argsList.add(new HttpObjectPair("mobDate", mobDateStr));
        argsList.add(new HttpObjectPair("mobCity", mobCityStr));
        argsList.add(new HttpObjectPair("useDate", useDate.toString()));
        argsList.add(new HttpObjectPair("userId", userId));

        progressBar.setVisibility(ProgressBar.VISIBLE);

        new PostCallTask(new AsyncResponse(){
            public void onSuccess(ResultObject resultObject) {
                List<Chat> chatList = new ArrayList<>();
                try {
                    Log.d(TAG, "onSuccess mobs =" + resultObject);
                    JSONArray mobs = resultObject.getJsonArray();
                    Log.d(TAG, "onSuccess chats=" + mobs);

                    for(int i = 0; i < mobs.length(); i++){
                        JSONObject row = mobs.getJSONObject(i);
                        Chat chat = (new Chat.Builder(row.getInt("id"),row.getInt("admin"), row.getString("name")))
                                    .image(row.getString("image"))
                                    .descr(row.getString("descr"))
                                    .date(row.getString("date"))
                                    .city(row.getString("city"))
                                    .build();

                        chatList.add(chat);
                    }
                }catch (Exception e){
                    Log.d(TAG, e.toString());
                    Log.d(TAG, e.getMessage());
                    e.printStackTrace();
                }

                Log.d(TAG, "chatList = " + chatList);
                findMobFragment.showMobs(chatList);
            }
        }, progressBar).execute(argsList.toArray(new HttpObjectPair[argsList.size()]));
    }

    public void joinMob(final MobDetailActivity mobDetailActivity, int chatId, ProgressBar progressBar) {
        List<HttpObjectPair>argsList = defaultArgList();

        argsList.add(new HttpObjectPair("do", "joinMob"));
        argsList.add(new HttpObjectPair("chatid", String.valueOf(chatId)));

        progressBar.setVisibility(ProgressBar.VISIBLE);

        new PostCallTask(new AsyncResponse(){
            public void onSuccess(ResultObject resultObject) {
                Toast.makeText(mobDetailActivity, "Successfully joined! Look at your mob's list!", Toast.LENGTH_LONG).show();
            }
        }, progressBar).execute(argsList.toArray(new HttpObjectPair[argsList.size()]));
    }

    public void updateUser(String userId, String filename, String userName, ProgressBar progressBar, AsyncResponse asyncResponse) {
        List<HttpObjectPair>argsList = defaultArgList();

        argsList.add(new HttpObjectPair("do", "updateUser"));
        argsList.add(new HttpObjectPair("userId", userId));
        argsList.add(new HttpObjectPair("userName", userName));
        argsList.add(new HttpObjectPair("filename", filename));

        new PostCallTask(asyncResponse, progressBar).execute(argsList.toArray(new HttpObjectPair[argsList.size()]));
    }

    public void updateChatImage(String chatId, String filename) {
        List<HttpObjectPair>argsList = defaultArgList();

        argsList.add(new HttpObjectPair("do", "updateChatImage"));
        argsList.add(new HttpObjectPair("chatid", chatId));
        argsList.add(new HttpObjectPair("filename", filename));

        new PostCallTask().execute(argsList.toArray(new HttpObjectPair[argsList.size()]));
    }

    public void getChat(final MobDetailActivity mobDetailActivity, int chatId, ProgressBar progressBar) {
        progressBar.setVisibility(ProgressBar.VISIBLE);

        List<HttpObjectPair>argsList = defaultArgList();

        argsList.add(new HttpObjectPair("do", "getChat"));
        argsList.add(new HttpObjectPair("chatid", String.valueOf(chatId)));

        new PostCallTask(new AsyncResponse(){
            public void onSuccess(ResultObject resultObject) {
                Chat chat = null;
                try {
                    Log.d(TAG, "onSuccess mobs =" + resultObject);
                    JSONObject mob = resultObject.getJsonObject();
                    Log.d(TAG, "onSuccess chats=" + mob);


                    chat = (new Chat.Builder(mob.getInt("id"),mob.getInt("admin"), mob.getString("name")))
                            .image(mob.getString("image")).descr(mob.getString("descr"))
                            .date(mob.getString("date")).city(mob.getString("city"))
                            .build();
                }catch (Exception e){
                    Log.d(TAG, e.toString());
                    Log.d(TAG, e.getMessage());
                    e.printStackTrace();
                }

                Log.d(TAG, "chat = " + chat);
                mobDetailActivity.fillChat(chat);
            }
        }, progressBar).execute(argsList.toArray(new HttpObjectPair[argsList.size()]));
    }

    public void getNewMessages(final ChatActivity chatActivity, long lastId, int chatId, final long userId) {
        List<HttpObjectPair>argsList = defaultArgList();

        argsList.add(new HttpObjectPair("do", "getNewMessages"));
        argsList.add(new HttpObjectPair("chatid", String.valueOf(chatId)));
        argsList.add(new HttpObjectPair("lastid", String.valueOf(lastId)));

        new PostCallTask(new AsyncResponse(){
            public void onSuccess(ResultObject resultObject) {
                Log.d(TAG, "resultObject = " + resultObject);
                List<Message> messages = new ArrayList<>();
                try {
                    JSONArray jsonMsgs = resultObject.getJsonArray();

                    for(int i =0; i < jsonMsgs.length(); i++){
                        JSONObject jObj = jsonMsgs.getJSONObject(i);
                        Message msg = new Message(jObj.getLong("id")
                                ,jObj.getInt("chatId")
                                ,jObj.getInt("userId")
                                ,jObj.getString("userName")
                                ,jObj.getString("text"));
                        messages.add(msg);
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                    Log.d(TAG, e.toString());
                }

                Log.d(TAG, "messages = " + messages);
                chatActivity.loadMessagesCallback(messages, userId);
            }
        }).execute(argsList.toArray(new HttpObjectPair[argsList.size()]));
    }

    public void leaveChat(final ChatActivity chatActivity, int chatId, String userId, ProgressBar progressBar) {
        List<HttpObjectPair>argsList = defaultArgList();

        argsList.add(new HttpObjectPair("do", "leaveChat"));
        argsList.add(new HttpObjectPair("chatid", String.valueOf(chatId)));
        argsList.add(new HttpObjectPair("userid", userId));

        new PostCallTask(new AsyncResponse(){
            public void onSuccess(ResultObject resultObject) {
                Log.d(TAG, "onSuccess resultObject =" + resultObject);
                chatActivity.setResult(ResultCode.CHAT_LEFT.get());
                chatActivity.finish();
            }
        }, progressBar).execute(argsList.toArray(new HttpObjectPair[argsList.size()]));
    }
}
