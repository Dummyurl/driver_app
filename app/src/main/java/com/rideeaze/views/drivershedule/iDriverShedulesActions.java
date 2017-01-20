package com.rideeaze.views.drivershedule;

import com.rideeaze.services.network.model.data.DriverShedule;

/**
 * Created by adventis on 1/20/16.
 */
public interface iDriverShedulesActions {
    public void showModifySheduleFragment(DriverShedule driverShedule);
    public void goToPreviousView();
    public void updateSchedule();
    public void loadSchedule(final boolean isOpenScheduleList);
    public void setMenuVisibility(boolean isVidible);
    public void addSchedule(DriverShedule schedule);
    public void removeSchedule(int idSchedule);
    public void replaceSchedule(int idSchedule, DriverShedule driverShedule);
}
