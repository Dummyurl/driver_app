package com.rideeaze.views.drivershedule;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.rideeaze.R;
import com.rideeaze.services.network.model.data.DriverShedule;
import com.rideeaze.views.common.DriverAppCompatFragment;
import com.rideeaze.widget.DatePickerTextView;
import com.rideeaze.widget.WeekDateSheduleChecker;
import com.util.StorageDataHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by adventis on 1/20/16.
 */
public class DriverAddEditSheduleItem extends DriverAppCompatFragment {
    iDriverShedulesActions driverShedulesActions;

    boolean isEditMode = false;
    DriverShedule driverSheduleEditMode = null;

    @Bind(R.id.startDatePicker) DatePickerTextView startDatePicker;
    @Bind(R.id.endDatePicker) DatePickerTextView endDatePicker;
    @Bind(R.id.startTimePicker) DatePickerTextView startTimePicker;
    @Bind(R.id.endTimePicker) DatePickerTextView endTimePicker;
    @Bind(R.id.week_date_schedule_checker)
    WeekDateSheduleChecker weekDateSheduleChecker;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFragmentViewLayoutId(R.layout.driver_modify_layout_fragment);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(isEditMode) {
            updateEditModeView();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.driverShedulesActions = (iDriverShedulesActions)getActivity();
        if (getActivity() instanceof iDriverShedulesActions) {
            this.driverShedulesActions = (iDriverShedulesActions)getActivity();
            this.driverShedulesActions.setMenuVisibility(true);
        } else {
            throw new ClassCastException(getActivity().toString() + " must implement iDriverShedulesActions");
        }


    }

    @OnClick(R.id.shedule_delete_btn)
    public void deleteAction() {
        if(isEditMode) {
            // run delete schedules
            this.driverShedulesActions.removeSchedule(driverSheduleEditMode.id);
        }

        goBack();
    }

    @OnClick(R.id.schedule_save_btn)
    public void saveSchedule() {
        DriverShedule driverShedule = getCurrentDriverSchedule();
        if(driverShedule == null) {
            Toast.makeText(getContext(), "Wrong parameters", Toast.LENGTH_SHORT).show();
            return;
        }

        if(isEditMode) {
            // modify
            this.driverShedulesActions.replaceSchedule(driverSheduleEditMode.id, driverShedule);
        } else {
            // add new
            this.driverShedulesActions.addSchedule(driverShedule);
        }

        goBack();
    }

    public void setEditMode(DriverShedule driverShedule) {
        isEditMode = true;
        driverSheduleEditMode = driverShedule;
    }

    public void updateEditModeView() {
        if(driverSheduleEditMode.repeat) {
            updateDateTimeView();
            weekDateSheduleChecker.setWeekDaysFlag(driverSheduleEditMode.repeat_details.summary.days);
        } else {
            updateDateTimeView();
        }
    }

    private void updateDateTimeView() {
        Calendar from = getCalendarFromString(driverSheduleEditMode.from);
        Calendar to = getCalendarFromString(driverSheduleEditMode.to);
        startDatePicker.setMyCalendar(from);
        endDatePicker.setMyCalendar(to);

        if(!driverSheduleEditMode.whole) {
            startTimePicker.setMyCalendar(from);
            endTimePicker.setMyCalendar(to);
        }
    }

    public void goBack() {
        driverShedulesActions.goToPreviousView();
    }

    public DriverShedule getCurrentDriverSchedule() {
        DriverShedule current = null;
        if(isSingleSchedule()) {
            current = generateSingleSchedule();
        } else if(isRepeatSchedule()) {
            current = generateRepeatSchedule();
        }

        if(current != null) {
            current.rctype = "w";

        }

        return current;
    }


    public boolean isSingleSchedule() {
        if(!weekDateSheduleChecker.isSmthChecked()) {
            if(!startDatePicker.isEmpty()) {
                return true;
            }
        }

        return false;
    }
    private DriverShedule generateSingleSchedule() {
        DriverShedule driverShedule = new DriverShedule();

        Calendar date = startDatePicker.getCalendar();
        Calendar time = (startTimePicker.isEmpty()) ? startDatePicker.getCalendar():startTimePicker.getCalendar();
        driverShedule.from = formatRawCalendarData(date, time);

        date = (endDatePicker.isEmpty()) ? startDatePicker.getCalendar(): endDatePicker.getCalendar();
        time = (endTimePicker.isEmpty()) ? startDatePicker.getCalendar(): endTimePicker.getCalendar();
        driverShedule.to = formatRawCalendarData(date, time);

        driverShedule.whole = (startTimePicker.isEmpty()) ? true: false;

        driverShedule.repeat = false;

        return driverShedule;
    }

    public boolean isRepeatSchedule() {
        if(weekDateSheduleChecker.isSmthChecked()) {
            if(!startDatePicker.isEmpty() && !endDatePicker.isEmpty()) {
                return true;
            }
        }
        return false;
    }
    private DriverShedule generateRepeatSchedule() {
        DriverShedule driverShedule = new DriverShedule();
        driverShedule.whole = (startTimePicker.isEmpty() || endTimePicker.isEmpty()) ? true : false;
        driverShedule.from = formatRawCalendarData(startDatePicker.getCalendar(), startTimePicker.getCalendar());
        driverShedule.to = formatRawCalendarData(endDatePicker.getCalendar(), endTimePicker.getCalendar());
        driverShedule.repeat = true;
        driverShedule.repeat_details.summary.days = weekDateSheduleChecker.getWeekDaysFlag();
        driverShedule.repeat_details.dateFrom = driverShedule.from;
        driverShedule.repeat_details.dateTo = driverShedule.to;

        return driverShedule;
    }

    private String formatRawCalendarData(Calendar date, Calendar time) {
        String myFormat = "yyyy-MM-dd HH:mm:ss"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        Calendar finalCalendar = date;
        if(time != null) {
            finalCalendar.set(Calendar.HOUR, time.get(Calendar.HOUR));
            finalCalendar.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
            finalCalendar.set(Calendar.AM_PM, time.get(Calendar.AM_PM));
        } else {
            finalCalendar.set(Calendar.HOUR, 0);
            finalCalendar.set(Calendar.MINUTE, 0);
            finalCalendar.set(Calendar.AM_PM, 0);
        }

        return sdf.format(finalCalendar.getTime());
    }

    private Calendar getCalendarFromString(String data) {
        SimpleDateFormat inputSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        try {
            Calendar finalCalendar = Calendar.getInstance();
            finalCalendar.setTime(inputSdf.parse(data));

            return finalCalendar;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }
}
