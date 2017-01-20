package com.rideeaze.views.drivershedule.models;

import com.rideeaze.services.network.model.data.DriverShedule;

import java.util.List;

/**
 * Created by adventis on 1/27/16.
 */
public class RepeatDriverScheduleItem {
    public int dayOfWeek;
    public List<DriverShedule> driverSheduleList;

    public RepeatDriverScheduleItem(int dayOfWeek, List<DriverShedule> driverSheduleList) {
        this.dayOfWeek = dayOfWeek;
        this.driverSheduleList = driverSheduleList;
    }
}
