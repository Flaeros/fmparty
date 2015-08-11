package ru.fmparty;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AllListFragment extends Fragment{
    private final String TAG = "Flashmob AuthFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.all_list_fragment,
                container, false);

        TextView testText = (TextView) view.findViewById(R.id.testText);
        testText.setText("Test Text - AllListFragment");

        return view;
    }
}
