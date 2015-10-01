package ru.fmparty;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ru.fmparty.utils.InnerDB;

public class MainFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private final String TAG = "FlashMob MainFragment";

    private View.OnClickListener myListButtonListener;
    private View.OnClickListener allListButtonListener;
    private View.OnClickListener createMobButtonListener;
    private View.OnClickListener menuButtonListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_main, null);

        Button myListButton = (Button) view.findViewById(R.id.myList);
        Button allListButton = (Button) view.findViewById(R.id.allList);
        Button createMobButton = (Button) view.findViewById(R.id.createMob);
        Button menuButton = (Button) view.findViewById(R.id.menuButton);

        myListButton.setOnClickListener(myListButtonListener);
        allListButton.setOnClickListener(allListButtonListener);
        createMobButton.setOnClickListener(createMobButtonListener);
        menuButton.setOnClickListener(menuButtonListener);

        setHasOptionsMenu(true);

        // Регистрируем этот OnSharedPreferenceChangeListener
        Context context = getActivity().getApplicationContext();
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(context);
        prefs.registerOnSharedPreferenceChangeListener(this);

        return view;
    }

    public void setListeners(View.OnClickListener... listeners)  {
        this.myListButtonListener = listeners[0];
        this.allListButtonListener = listeners[1];
        this.createMobButtonListener = listeners[2];
        this.menuButtonListener = listeners[3];
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_settings:
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
        String userIdStr = InnerDB.getInnerUserId(getActivity(), ((MainActivity) getActivity()).getSocialNetworkApi().getUserId());
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
    }
}