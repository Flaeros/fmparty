package ru.fmparty;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

public class MyListFragment extends Fragment {
    private final String TAG = "Flashmob MyListFragment";

    private SocialNetworkApi socialNetworkApi;

    ImageView userAvatar;
    TextView userName;
    TextView userDesc;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.my_list_fragment,
                container, false);

        userAvatar = (ImageView) view.findViewById(R.id.userAvatar);
        userName = (TextView) view.findViewById(R.id.userName);
        userDesc = (TextView) view.findViewById(R.id.userDesc);

        socialNetworkApi.populateUserInfo(userName, userDesc, userAvatar);

        ListView usersList = (ListView) view.findViewById(R.id.usersList);
        populateListView(usersList);

        return view;
    }

    public void setSocialNetworkApi(SocialNetworkApi socialNetworkApi){
        this.socialNetworkApi = socialNetworkApi;
    }


    private void populateListView(final ListView usersList) {
        VKRequest request =  VKApi.friends().get( VKParameters.from(VKApiConst.FIELDS, "photo_100"));
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                Log.v(TAG, response.json.toString());

                VKList<VKApiUser> users = (VKList<VKApiUser>) response.parsedModel;

                if (users != null && MyListFragment.this.isVisible()) {
                    ArrayAdapter<VKApiUser> userArrayAdapter = new MyListArrayAdapter(users);
                    usersList.setAdapter(userArrayAdapter);
                } else {
                    Log.v(TAG, "no users loaded");
                }
            }
        });
    }

    private class MyListArrayAdapter extends ArrayAdapter<VKApiUser> {

        VKList<VKApiUser> users;

        public MyListArrayAdapter( VKList<VKApiUser> users) {
            super(getActivity(), R.layout.user_list_item_layout, users);
            this.users = users;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = getActivity().getLayoutInflater().inflate(R.layout.user_list_item_layout, parent, false);
            }

            VKApiUser user = users.get(position);

            TextView textViewName = (TextView) itemView.findViewById(R.id.item_userName);
            textViewName.setText(user.first_name);
            ImageView imageView = (ImageView) itemView.findViewById(R.id.item_userAvatar);

            Log.v(TAG, user.photo_100);

            new DownloadImageTask(imageView)
                    .execute(user.photo_100);


            return itemView;
        }
    }
}
