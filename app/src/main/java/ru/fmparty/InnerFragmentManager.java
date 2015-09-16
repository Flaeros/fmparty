package ru.fmparty;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import ru.fmparty.apiaccess.SocialNetworkApi;

public class InnerFragmentManager {

    private MainFragment mainFragment;
    private AuthFragment authFragment;
    private MyListFragment myListFragment;
    private FindMobFragment findMobFragment;
    private CreateMobFragment createMobFragment;

    private Activity activity;
    private SocialNetworkApi socialNetworkApi;


    private final String TAG = "InnerFragmentManager";

    InnerFragmentManager(Activity activity){
        this.activity = activity;
    }


    public void setSocialNetworkApi(SocialNetworkApi socialNetworkApi) {
        this.socialNetworkApi = socialNetworkApi;
    }

    public void initializeMainFragment() {
        mainFragment = new MainFragment();
        myListFragment = new MyListFragment();
        myListFragment.setSocialNetworkApi(socialNetworkApi);
        mainFragment.setListeners( myListButtonListener,allListButtonListener, createMobButtonListener, menuButtonListener);

        activity.getFragmentManager().beginTransaction()
                .replace(R.id.frgmCont, mainFragment)
                .add(R.id.mainFragCont, myListFragment)
                .commit();
    }

    public void startFragmentForAuth() {
        authFragment = new AuthFragment();
        Log.d(TAG, "startFragmentForAuth");
        activity.getFragmentManager().beginTransaction()
                .add(R.id.frgmCont, authFragment)
                .commit();
    }

    public SocialNetworkApi getAuthorizedApi(){
        return authFragment.getSocialNetworkApi();
    }

    public void recreateAfterLogOut(){
        if(socialNetworkApi == null)
            return;
        socialNetworkApi.logout();

        activity.getFragmentManager().beginTransaction()
                .remove(myListFragment)
                .remove(mainFragment)
                .commit();

        activity.recreate();
    }

    private void showAllList(){
        if(findMobFragment == null)
            findMobFragment = new FindMobFragment();
        findMobFragment.setSocialNetworkApi(socialNetworkApi);

        activity.getFragmentManager().beginTransaction()
                .replace(R.id.mainFragCont, findMobFragment)
                .commit();
    }

    private void showMyList(){
        activity.getFragmentManager().beginTransaction()
                .replace(R.id.mainFragCont, myListFragment)
                .commit();
    }

    private void showCreateMob() {
        if(createMobFragment == null)
            createMobFragment = new CreateMobFragment();
        createMobFragment.setSocialNetworkApi(socialNetworkApi);

        activity.getFragmentManager().beginTransaction()
                .replace(R.id.mainFragCont, createMobFragment)
                .commit();
    }

    View.OnClickListener allListButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showAllList();
        }
    };

    View.OnClickListener myListButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showMyList();
        }
    };

    View.OnClickListener createMobButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showCreateMob();
        }
    };

    View.OnClickListener menuButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            activity.openOptionsMenu();
        }
    };
}
