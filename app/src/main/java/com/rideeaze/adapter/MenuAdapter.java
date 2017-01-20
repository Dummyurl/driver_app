package com.rideeaze.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.rideeaze.R;
import com.rideeaze.services.network.model.MenuItemData;
import com.rideeaze.views.fragmentactivity.OnMenuListener;

import java.util.List;

/**
 * Created by adventis on 1/16/16.
 */
public class MenuAdapter extends BaseAdapter {

    private Context mContext;
    private List<MenuItemData> mItems;
    private OnMenuListener mMenuListener;
    public MenuAdapter(Context context, List<MenuItemData> items, OnMenuListener menuListener)
    {
        super();
        mContext=context;
        mItems=items;
        mMenuListener = menuListener;

    }

    public int getCount()
    {
        // return the number of records in cursor
        return mItems.size();
    }

    // getView method is called for each item of ListView
    public View getView(int position,  View view, ViewGroup parent)
    {
        // inflate the layout for each item of listView
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View newView = inflater.inflate(R.layout.main_menu_item, null);

        // move the cursor to required position
        final MenuItemData item = mItems.get(position);

        // get the reference of textViews
        TextView title =(TextView)newView.findViewById(R.id.menuAdapterTitle);
        title.setText(item.getTitle());
//        if(!item.isActive()) {
//            title.setTextColor(Color.RED);
//        }

        if(item.isShowSwitch()) {
            final Switch switchItem = (Switch)newView.findViewById(R.id.menuAdapterSwitch);
            switchItem.setChecked(item.isActive());
            switchItem.setVisibility(View.VISIBLE);
            switchItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mMenuListener !=null) {
                        mMenuListener.onMenuSwitchChanged(switchItem.isChecked(), item.getId());
                    }
                }
            });
        }

        newView.setTag(item.getId());

        return newView;
    }

    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

}
