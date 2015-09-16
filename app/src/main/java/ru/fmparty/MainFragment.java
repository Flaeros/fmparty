package ru.fmparty;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MainFragment extends Fragment{
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
            case R.id.action_logout:
                MainActivity activity = (MainActivity) getActivity();
                activity.getManager().recreateAfterLogOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}