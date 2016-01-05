package ru.fmparty;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ru.fmparty.apiaccess.SocialAccess;
import ru.fmparty.tabs.SlidingTabLayout;
import ru.fmparty.utils.InnerDB;
import ru.fmparty.utils.Nameable;

public class MainFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private final String TAG = "FlashMob MainFragment";

    ViewPager pager;
    SlidingTabLayout tabs;

    private List<Nameable> fragmentList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView");
        Log.v(TAG, "fragmentList = " + fragmentList);
        if(fragmentList == null )
            return null;
        View view = inflater.inflate(R.layout.fragment_main, null);

        setHasOptionsMenu(true);

        Context context = getActivity().getApplicationContext();
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(context);
        prefs.registerOnSharedPreferenceChangeListener(this);

        pager = (ViewPager) view.findViewById(R.id.pager);
        Log.d(TAG, "[onCreateView] pager = " + pager );
        tabs = (SlidingTabLayout) view.findViewById(R.id.tabs);

        PagerAdapter pagerAdapter = new MyFragmentPagerAdapter(((FragmentActivity)getActivity()).getSupportFragmentManager(), fragmentList);
        Log.d(TAG, "pager = " + pager);
        Log.d(TAG, "pagerAdapter = " + pagerAdapter);
        pager.setAdapter(pagerAdapter);
        pager.setOffscreenPageLimit(3);

        tabs.setDistributeEvenly(true);
        tabs.setViewPager(pager);

        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.white);
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_settings:
                Log.d(TAG, "fragmentList = " + fragmentList);
                Log.d(TAG, "pager adapter = " + pager.getAdapter());
                openSettings();
                return true;
            case R.id.action_open_profile:
                openProfile();
                return true;
            case R.id.action_logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openProfile() {
        String userIdStr = InnerDB.getInstance().getInnerUserId(SocialAccess.getInstance().getApi().getUserId());
        int userId = Integer.valueOf(userIdStr);

        Log.d(TAG, "openProfile userId = " + userId);
        Intent profileIntent = new Intent(getActivity(), ProfileActivity.class);

        profileIntent.putExtra("userId", String.valueOf(userId));
        profileIntent.putExtra("isEditable", Boolean.TRUE.toString());

        startActivity(profileIntent);
    }

    private void openSettings() {
        Intent i = new Intent(this.getActivity(), AppPreferenceActivity.class);
        startActivity(i);
    }

    private void logout() {
        MainActivity activity = (MainActivity) getActivity();
        activity.getManager().recreateAfterLogOut();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(TAG, "Settings changed");
        if(key.equals("allow_sound")) {
            Boolean current = sharedPreferences.getBoolean(key, false);
            Log.d(TAG, "key = " + key + "; current value = " + current);
        }
        if(key.equals("language")) {
            String current = sharedPreferences.getString(key, "English");
            Log.d(TAG, "key = " + key + "; current value = " + current);
        }
    }

    public void setFragmentList(List<Nameable> fragmentList) {
        Log.d(TAG, "[setFragmentList] fragmentList = " + fragmentList);
        this.fragmentList = fragmentList;
    }

    public void destroyPager() {
        pager.setAdapter(null);
    }

    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        private final List<Nameable> fragmentList;

        public MyFragmentPagerAdapter(FragmentManager fm, List<Nameable> fragmentList) {
            super(fm);
            Log.d(TAG, "[MyFragmentPagerAdapter] fragmentList = " + fragmentList);
            this.fragmentList = fragmentList;
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            Log.d(TAG, "[getItem] position = " + position);
            return (android.support.v4.app.Fragment) this.fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return this.fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return this.fragmentList.get(position).getTitle();
        }
    }
}