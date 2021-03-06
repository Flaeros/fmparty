package ru.fmparty;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ru.fmparty.apiaccess.SocialNetworkApi;
import ru.fmparty.utils.InnerDB;
import ru.fmparty.utils.Nameable;

public class InnerFragmentManager {

    private MainFragment mainFragment;
    private AuthFragment authFragment;
    private MyListFragment myListFragment;
    private FindMobFragment findMobFragment;
    private CreateMobFragment createMobFragment;


    private List<Nameable> fragmentList;
    private AppCompatActivity activity;

    private SocialNetworkApi socialNetworkApi;
    private final String TAG = "FlashMob InnrFrMngr";


    InnerFragmentManager(AppCompatActivity  activity){
        this.activity = activity;
    }

    public void setSocialNetworkApi(SocialNetworkApi socialNetworkApi) {
        this.socialNetworkApi = socialNetworkApi;
    }


    public void initializeMainFragment() {
        Log.d(TAG, "initializeMainFragment");

        mainFragment = new MainFragment();
        Log.d(TAG, "initialize MyList Fragment");
        myListFragment = new MyListFragment();
        Log.d(TAG, "init end");
        findMobFragment = new FindMobFragment();
        createMobFragment = new CreateMobFragment();

        fragmentList = new ArrayList<>();
        fragmentList.add(myListFragment);
        fragmentList.add(findMobFragment);
        fragmentList.add(createMobFragment);

        mainFragment.setFragmentList(fragmentList);

        Log.d(TAG, "set socialNetworkApi = " + socialNetworkApi);
        Log.d(TAG, "myListFragment = " + myListFragment);

        findMobFragment.setSocialNetworkApi(socialNetworkApi);
        createMobFragment.setSocialNetworkApi(socialNetworkApi);


        Log.d(TAG, "start main transaction");
        activity.getFragmentManager().beginTransaction()
                .add(R.id.frgmCont, mainFragment)
                .commit();


    }

    public void startFragmentForAuth() {
        authFragment = new AuthFragment();
        Log.d(TAG, "startFragmentForAuth");
        activity.getFragmentManager().beginTransaction()
                .add(R.id.frgmCont, authFragment)
                .commit();
        Log.d(TAG, "auth fragment added");
    }

    public void endAuthFragment(){
        activity.getFragmentManager().beginTransaction()
                .remove(authFragment)
                .commit();
    }

    public SocialNetworkApi getAuthorizedApi(){
        if(authFragment == null) {
            Log.v(TAG, "Error");
            return null;
        }
        return authFragment.getSocialNetworkApi();
    }

    public void recreateAfterLogOut(){
        if(socialNetworkApi == null)
            return;
        socialNetworkApi.logout();
        mainFragment.destroyPager();
        myListFragment.destroyList();


        activity.getSupportFragmentManager().beginTransaction()
                .remove(myListFragment)
                .remove(findMobFragment)
                .remove(createMobFragment)
                .commit();

        activity.getFragmentManager().beginTransaction()
                .remove(mainFragment)
                .commit();

        InnerDB.getInstance().clearData();
        activity.recreate();
    }


    public CreateMobFragment getCreateMobFragment() { return createMobFragment; }
}
