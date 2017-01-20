package com.rideeaze.widget;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.rideeaze.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by adventis on 1/20/16.
 */
public class DatePickerTextView extends TextView {
    public void setMyCalendar(Calendar myCalendar) {
        if(myCalendar != null) {
            this.myCalendar = myCalendar;
            updateText();
        }
    }

    Calendar myCalendar = Calendar.getInstance();

    String pickerType;

    public DatePickerTextView(Context context) {
        super(context);
        init();
    }

    public DatePickerTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPickerType(attrs);
        init();
    }

    public DatePickerTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPickerType(attrs);
        init();
    }

    private void initPickerType(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.DatePickerTextView, 0, 0);
        pickerType = ta.getString(R.styleable.DatePickerTextView_pickerType);
    }

    private void init() {
        this.setGravity(Gravity.CENTER_VERTICAL);

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(0xFFFFFFFF);
        gd.setCornerRadius(5);
        gd.setStroke(1, 0xFF000000);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            this.setBackground(gd);
        } else {
            this.setBackgroundDrawable(gd);
        }
        this.setTextColor(Color.BLACK);

        this.setPadding(10,0,0,0);

        initCalendarBtn();
        initListeners();
    }

    public boolean isEmpty() {
        return this.getText().toString().isEmpty();
    }
    private void initListeners() {
        if(isDateType()) {
            final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {
                    // TODO Auto-generated method stub
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, monthOfYear);
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateText();
                }

            };

            this.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DatePickerDialog(getContext(), dateSetListener, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });
        } else if(isTimeType()) {
            this.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    final TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            myCalendar.set(Calendar.MINUTE, minute);
                            if(hourOfDay >= 12 ) {
                                myCalendar.set(Calendar.AM_PM, 1);
                                myCalendar.set(Calendar.HOUR, hourOfDay-12);
                            } else {
                                myCalendar.set(Calendar.AM_PM, 0);
                                myCalendar.set(Calendar.HOUR, hourOfDay);
                            }
                            updateText();
                        }
                    };

                    new TimePickerDialog(getContext(),timeSetListener,myCalendar.get(Calendar.HOUR), myCalendar.get(Calendar.MINUTE), false).show();
                }
            });
        }
    }
    private void initCalendarBtn() {
        int drawable = 0;
        if(isDateType()) {
            drawable = android.R.drawable.ic_menu_today;
        } else if(isTimeType()) {
            drawable = android.R.drawable.ic_menu_recent_history;
        } else {
            return;
        }


        Drawable calendarPic = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            calendarPic = getResources().getDrawable(drawable, getContext().getTheme());
        } else {
            calendarPic = getResources().getDrawable(drawable);
        }

        calendarPic.setBounds(0, 0, calendarPic.getIntrinsicWidth(), calendarPic.getIntrinsicHeight());
        this.setCompoundDrawables(null, null, calendarPic, null);
    }

    private void updateText() {
        if(isDateType()) {
            String myFormat = "MM/dd/yy"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

            this.setText(sdf.format(myCalendar.getTime().getTime()));
        } else if(isTimeType()) {
            String myFormat = "hh:mm aa"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            this.setText(sdf.format(myCalendar.getTime().getTime()));
        }
    }

    private boolean isDateType() {
        if(pickerType != null && pickerType.equalsIgnoreCase("date")) {
            return true;
        }
        return false;
    }

    private boolean isTimeType() {
        if(pickerType != null && pickerType.equalsIgnoreCase("time")) {
            return true;
        }
        return false;
    }

    public Calendar getCalendar() {
        if(isEmpty()) {
            return null;
        }
        return myCalendar;
    }
}
