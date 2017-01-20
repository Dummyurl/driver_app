package com.rideeaze.views.drivershedule.models;

import com.rideeaze.services.network.model.data.DriverShedule;
import com.rideeaze.services.network.model.data.WeekDaysFlag;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by adventis on 1/27/16.
 */
public class RepeatDriverScheduleTableSource {
    public Hashtable<Integer, List<DriverShedule>> getDaysList() {
        return daysList;
    }

    private Hashtable<Integer,List<DriverShedule>> daysList;
    private int[] daysOfWeek = new int[]{Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY};

    public RepeatDriverScheduleTableSource() {
        daysList = new Hashtable<Integer,List<DriverShedule>>();

        for (int id : daysOfWeek) {
            daysList.put(id, new ArrayList<DriverShedule>());
        }
    }

    public boolean addDriverSchedule(Integer dayCalendarId, DriverShedule driverShedule) {
        if(daysList.containsKey(dayCalendarId)) {
            daysList.get(dayCalendarId).add(driverShedule);
        }

        return false;
    }

    public List<RepeatDriverScheduleItem> generateListFromDictionary() {
        List<RepeatDriverScheduleItem> list = new ArrayList<RepeatDriverScheduleItem>();
        for (int id : daysOfWeek) {
            list.add(new RepeatDriverScheduleItem(id, daysList.get(id)));
        }

        return list;
    }

    public void parseWeekDaysFlagObject(DriverShedule driverShedule) {
        WeekDaysFlag weekDaysFlag = driverShedule.repeat_details.summary.days;
        if(weekDaysFlag.mon) {
            addDriverSchedule(Calendar.MONDAY, driverShedule);
        }
        if(weekDaysFlag.tue) {
            addDriverSchedule(Calendar.TUESDAY, driverShedule);
        }
        if(weekDaysFlag.wed) {
            addDriverSchedule(Calendar.WEDNESDAY, driverShedule);
        }
        if(weekDaysFlag.thu) {
            addDriverSchedule(Calendar.THURSDAY, driverShedule);
        }
        if(weekDaysFlag.fri) {
            addDriverSchedule(Calendar.FRIDAY, driverShedule);
        }
        if(weekDaysFlag.sat) {
            addDriverSchedule(Calendar.SATURDAY, driverShedule);
        }
        if(weekDaysFlag.sun) {
            addDriverSchedule(Calendar.SUNDAY, driverShedule);
        }
    }
}
