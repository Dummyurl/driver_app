package com.rideeaze.views.drivershedule.tableview;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.rideeaze.views.drivershedule.models.RepeatDriverScheduleItem;
import com.util.Utils;

import java.util.ArrayList;
import java.util.List;

import de.codecrafters.tableview.SortableTableView;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;

/**
 * Created by adventis on 1/27/16.
 */
public class WeeklyScheduleTableView extends SortableTableView<RepeatDriverScheduleItem> {
    private static final int TEXT_SIZE = 13;

    public WeeklyScheduleTableView(Context context) {
        this(context, null);
    }

    public WeeklyScheduleTableView(Context context, AttributeSet attributes) {
        this(context, attributes, 0);
    }

    public WeeklyScheduleTableView(Context context, AttributeSet attributes, int styleAttributes) {
        super(context, attributes, styleAttributes);

        SimpleTableHeaderAdapter simpleTableHeaderAdapter = new SimpleTableHeaderAdapter(context, "", "Start Date", "End Date", "Start Time", "End Time");
        simpleTableHeaderAdapter.setTextColor(Color.BLACK);
        simpleTableHeaderAdapter.setTextSize(TEXT_SIZE);
        setHeaderAdapter(simpleTableHeaderAdapter);

        setColumnWeight(0, 2);
        setColumnWeight(1, 2);
        setColumnWeight(2, 2);
        setColumnWeight(3, 2);
        setColumnWeight(4, 2);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if(this.getChildCount() > 1) {
            LinearLayout headerView = (LinearLayout)getChildAt(0);
            List<View> newViews = new ArrayList<View>();
            for(int i=0; i< headerView.getChildCount(); i++) {
                View item = headerView.getChildAt(i);
                item = Utils.setBorderToView(item);
                newViews.add(item);
            }

            headerView.removeAllViews();
            for (View view : newViews) {
                headerView.addView(view);
            }
        }
    }
}

