package com.rideeaze.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by adventis on 11/30/14.
 */
public class HintAdapter extends ArrayAdapter<String> {
    /*public HintAdapter(Context theContext, List<Object> objects) {
        super(theContext, R.id.text1, R.id.text1, objects);
    }*/

    public HintAdapter(Context theContext, int theLayoutResId,  List<String> objects) {
        super(theContext, theLayoutResId, objects);
    }

    @Override
    public int getCount() {
        // don't display last item. It is used as hint.
        int count = super.getCount();
        return count > 0 ? count - 1 : count;
    }
}
