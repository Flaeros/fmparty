package ru.fmparty;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MainFragment extends Fragment{
    private final String TAG = "FlashMob MainFragment";

    private View.OnClickListener logOutButtonListener;
    private View.OnClickListener myListButtonListener;
    private View.OnClickListener allListButtonListener;
    private View.OnClickListener createMobButtonListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_main, null);

        Button logOutButton = (Button) view.findViewById(R.id.logOutButton);
        Button myListButton = (Button) view.findViewById(R.id.myList);
        Button allListButton = (Button) view.findViewById(R.id.allList);
        Button createMobButton = (Button) view.findViewById(R.id.createMob);

        logOutButton.setOnClickListener(logOutButtonListener);
        myListButton.setOnClickListener(myListButtonListener);
        allListButton.setOnClickListener(allListButtonListener);
        createMobButton.setOnClickListener(createMobButtonListener);

        return view;
    }

    public void setListeners(View.OnClickListener... listeners)  {
        this.logOutButtonListener = listeners[0];
        this.myListButtonListener = listeners[1];
        this.allListButtonListener = listeners[2];
        this.createMobButtonListener = listeners[3];
    }
}