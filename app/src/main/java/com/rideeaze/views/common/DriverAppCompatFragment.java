package com.rideeaze.views.common;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * Created by adventis on 1/20/16.
 */
public class DriverAppCompatFragment extends Fragment {
    public void setFragmentViewLayoutId(int fragmentViewLayoutId) {
        this.fragmentViewLayoutId = fragmentViewLayoutId;
    }

    int fragmentViewLayoutId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(fragmentViewLayoutId, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
