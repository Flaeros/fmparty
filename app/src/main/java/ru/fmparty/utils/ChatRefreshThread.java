package ru.fmparty.utils;

import android.util.Log;

import ru.fmparty.ChatActivity;
import ru.fmparty.apiaccess.SocialAccess;

public class ChatRefreshThread extends Thread {

    private ChatActivity chatActivity;
    private static final String TAG = "FlashMob ChatThread";
    private static final long delay = 5000;

    public ChatRefreshThread(ChatActivity chatActivity){
        this.chatActivity = chatActivity;
    }

    @Override
    public void run() {
        try {
            while (chatActivity.isRunning() && !chatActivity.isFinishing()) {
                Thread.sleep(delay);
                if(SocialAccess.getInstance().getApi().isLoggedIn())
                    chatActivity.updateMessages();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d(TAG, "Thread fall");
        }
    }
}
