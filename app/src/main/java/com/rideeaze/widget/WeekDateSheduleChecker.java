package com.rideeaze.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rideeaze.R;
import com.rideeaze.services.network.model.data.WeekDaysFlag;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by adventis on 1/20/16.
 */
public class WeekDateSheduleChecker  extends LinearLayout{

    private int[] daysOfWeek = new int[]{Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY};
    List<DayOfWeekSheduleChecker> daysView = new ArrayList<DayOfWeekSheduleChecker>();

    public WeekDateSheduleChecker(Context context) {
        super(context);
        init();
    }

    public WeekDateSheduleChecker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WeekDateSheduleChecker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        for (int dayOfWeek : daysOfWeek) {
            String day = getTitleForDayOfWeek(dayOfWeek);
            daysView.add(new DayOfWeekSheduleChecker(getContext(), day, dayOfWeek));
        }
        for(View dayView: daysView) {
            dayView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1f));
            this.addView(dayView);
        }
    }

    private String getTitleForDayOfWeek(int dayOfWeek) {
        switch (dayOfWeek) {
            case Calendar.MONDAY:
                return "Mon";
            case Calendar.TUESDAY:
                return "Tue";
            case Calendar.WEDNESDAY:
                return "Wed";
            case Calendar.THURSDAY:
                return "Thu";
            case Calendar.FRIDAY:
                return "Fri";
            case Calendar.SATURDAY:
                return "Sat";
            case Calendar.SUNDAY:
                return "Sun";
        }

        return "nothing";
    }

    public class DayOfWeekSheduleChecker extends LinearLayout {
        TextView titleView;
        CheckBox checkBox;

        public boolean isChecked() {
            return checkBox.isChecked();
        }
        public DayOfWeekSheduleChecker(Context context, String title, int id) {
            super(context);
            init();
            this.setTag(id);
            titleView.setText(title);
        }

        public DayOfWeekSheduleChecker(Context context) {
            super(context);
            init();
        }

        public DayOfWeekSheduleChecker(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public DayOfWeekSheduleChecker(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init();
        }

        public void init() {
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.week_date_shedule_checker,null);
            titleView = (TextView)view.findViewById(R.id.dataNameTitle);
            checkBox = (CheckBox)view.findViewById(R.id.dateCheckBox);
            checkBox.setHintTextColor(Color.BLACK);

            this.removeAllViews();
            this.addView(view);
        }
    }
    public void setWeekDaysFlag(WeekDaysFlag weekDaysFlag) {
        if(weekDaysFlag != null) {
            for(DayOfWeekSheduleChecker dayView: daysView) {
                switch ((int)dayView.getTag()) {
                    case Calendar.MONDAY:
                        if(weekDaysFlag.mon) {
                            dayView.checkBox.setChecked(true);
                        }
                        break;
                    case Calendar.TUESDAY:
                        if(weekDaysFlag.tue) {
                            dayView.checkBox.setChecked(true);
                        }
                        break;
                    case Calendar.WEDNESDAY:
                        if(weekDaysFlag.wed) {
                            dayView.checkBox.setChecked(true);
                        }
                        break;
                    case Calendar.THURSDAY:
                        if(weekDaysFlag.thu) {
                            dayView.checkBox.setChecked(true);
                        }
                        break;
                    case Calendar.FRIDAY:
                        if(weekDaysFlag.fri) {
                            dayView.checkBox.setChecked(true);
                        }
                        break;
                    case Calendar.SATURDAY:
                        if(weekDaysFlag.sat) {
                            dayView.checkBox.setChecked(true);
                        }
                        break;
                    case Calendar.SUNDAY:
                        if(weekDaysFlag.sun) {
                            dayView.checkBox.setChecked(true);
                        }
                        break;
                }
            }
        }
    }

    public WeekDaysFlag getWeekDaysFlag() {
        WeekDaysFlag weekDaysFlag = new WeekDaysFlag();
        for(DayOfWeekSheduleChecker dayView: daysView) {
            if(!dayView.isChecked()) {
                continue;
            }
            switch ((int)dayView.getTag()) {
                case Calendar.MONDAY:
                    weekDaysFlag.mon = true;
                    break;
                case Calendar.TUESDAY:
                    weekDaysFlag.tue = true;
                    break;
                case Calendar.WEDNESDAY:
                    weekDaysFlag.wed = true;
                    break;
                case Calendar.THURSDAY:
                    weekDaysFlag.thu = true;
                    break;
                case Calendar.FRIDAY:
                    weekDaysFlag.fri = true;
                    break;
                case Calendar.SATURDAY:
                    weekDaysFlag.sat = true;
                    break;
                case Calendar.SUNDAY:
                    weekDaysFlag.sun = true;
                    break;
            }
        }

        return weekDaysFlag;
    }

    public boolean isSmthChecked() {
        for(DayOfWeekSheduleChecker dayView: daysView) {
            if (dayView.isChecked()) {
                return true;
            }
        }

        return false;
    }
}
