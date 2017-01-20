package com.rideeaze.views.drivershedule.tableview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.rideeaze.services.network.model.data.DriverShedule;

import de.codecrafters.tableview.SortableTableView;

/**
 * Created by adventis on 1/27/16.
 */
public class SingleDayScheduleTableView extends SortableTableView<DriverShedule> {
    public SingleDayScheduleTableView(Context context) {
        this(context, null);
    }

    public SingleDayScheduleTableView(Context context, AttributeSet attributes) {
        this(context, attributes, 0);
    }

    public SingleDayScheduleTableView(Context context, AttributeSet attributes, int styleAttributes) {
        super(context, attributes, styleAttributes);

        if(this.getChildCount() > 1) {
            LinearLayout headerView = (LinearLayout)getChildAt(0);
            headerView.setVisibility(GONE);
        }

        setHeaderElevation(1);

        setColumnWeight(0, 2);
        setColumnWeight(1, 2);
        setColumnWeight(2, 2);
        setColumnWeight(3, 2);
        setColumnWeight(4, 2);
    }
}