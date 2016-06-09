package kj.buddylocator.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kj.buddylocator.R;

/**
 * Created by Kapil on 3/14/2015.
 */
public class Contacts extends Fragment {

    public static Contacts getInstance(){
        return new Contacts();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.profile_frag, container, false);

        return layout;
    }
}
