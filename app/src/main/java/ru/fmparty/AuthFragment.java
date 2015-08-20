package ru.fmparty;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ru.fmparty.apiaccess.FacebookApi;
import ru.fmparty.apiaccess.SocialNetworkApi;
import ru.fmparty.apiaccess.VkontakteApi;

public class AuthFragment extends Fragment {

    Button vkButton;
    Button fbButton;

    private SocialNetworkApi socialNetworkApi;

    private final String TAG = "FlashMob AuthFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_auth,
                container, false);


        vkButton = (Button) view.findViewById(R.id.vkButton);
        vkButton.setOnClickListener(vkButtonListener);
        fbButton = (Button) view.findViewById(R.id.fbButton);
        fbButton.setOnClickListener(fbButtonListener);

        return view;
    }

    public SocialNetworkApi getSocialNetworkApi(){
        return socialNetworkApi;
    }


    View.OnClickListener vkButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            socialNetworkApi = new VkontakteApi(getActivity());
            socialNetworkApi.login();
        }
    };

    View.OnClickListener fbButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            socialNetworkApi = new FacebookApi(getActivity());
            socialNetworkApi.login();
        }
    };
}
