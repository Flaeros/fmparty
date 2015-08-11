package ru.fmparty;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKList;

import ru.fmparty.apiaccess.SocialNetworkApi;
import ru.fmparty.utils.DownloadImageTask;

public class MainFragment extends Fragment{
    private final String TAG = "Flashmob MainFragment";

    private View.OnClickListener logOutButtonListener;
    private View.OnClickListener myListButtonListener;
    private View.OnClickListener allListButtonListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_main, null);

        Button logOutButton = (Button) view.findViewById(R.id.logOutButton);
        Button myListButton = (Button) view.findViewById(R.id.myList);
        Button allListButton = (Button) view.findViewById(R.id.allList);

        logOutButton.setOnClickListener(logOutButtonListener);
        myListButton.setOnClickListener(myListButtonListener);
        allListButton.setOnClickListener(allListButtonListener);

        return view;
    }

    public void setListeners(View.OnClickListener logOutButtonListener, View.OnClickListener myListButtonListener, View.OnClickListener allListButtonListener) {
        this.logOutButtonListener = logOutButtonListener;
        this.myListButtonListener = myListButtonListener;
        this.allListButtonListener = allListButtonListener;
    }
}
