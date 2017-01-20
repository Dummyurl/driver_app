package com.rideeaze.views.drivershedule.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rideeaze.services.network.model.data.DriverShedule;
import com.rideeaze.views.drivershedule.models.RepeatDriverScheduleItem;
import com.util.Utils;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.codecrafters.tableview.TableDataAdapter;

/**
 * Created by adventis on 1/27/16.
 */
public class WeeklyScheduleAdapter extends TableDataAdapter<RepeatDriverScheduleItem> {

    private static final int TEXT_SIZE = 10;
    private static final NumberFormat PRICE_FORMATTER = NumberFormat.getNumberInstance();

    private final String dateInputFormat = "yyyy-MM-dd";
    private final String dateOutputFormat = "MM/dd/yyyy";
    private final String timeInputFormat = "yyyy-MM-dd HH:mm:ss";
    private final String timeOutputFormat = "hh:mm a";

    public WeeklyScheduleAdapter(Context context, List<RepeatDriverScheduleItem> data) {
        super(context, data);
    }

    @Override
    public View getCellView(int rowIndex, int columnIndex, ViewGroup parentView) {
        RepeatDriverScheduleItem repeatDriverScheduleItem = getRowData(rowIndex);
        DriverShedule driverSchedule = null;
        if(repeatDriverScheduleItem.driverSheduleList.size() > 0) {
            driverSchedule = repeatDriverScheduleItem.driverSheduleList.get(0);
        }

        View renderedView = null;


        switch (columnIndex) {
            case 0:
                renderedView = renderString(Utils.getTitleForDayOfWeek(repeatDriverScheduleItem.dayOfWeek));
                break;
            case 1:
                renderedView = renderString((driverSchedule == null) ? "" : convertDateString(driverSchedule.from, dateInputFormat, dateOutputFormat));
                break;
            case 2:
                renderedView = renderString((driverSchedule == null) ? "" : convertDateString(driverSchedule.to, dateInputFormat, dateOutputFormat));
                break;
            case 3:
                renderedView = renderString((driverSchedule == null) ? "" : ((driverSchedule.whole) ? "" : convertDateString(driverSchedule.from, timeInputFormat, timeOutputFormat)));
                break;
            case 4:
                renderedView =  renderString((driverSchedule == null) ? "" : ((driverSchedule.whole) ? "" : convertDateString(driverSchedule.to, timeInputFormat, timeOutputFormat)));
                break;
        }

        renderedView = Utils.setBorderToView(renderedView);
        return renderedView;
    }




    private String convertDateString(String dateText, String inputFormat, String outputFormat) {
        SimpleDateFormat inputSdf = new SimpleDateFormat(inputFormat, Locale.US);
        try {
            Calendar finalCalendar = Calendar.getInstance();
            finalCalendar.setTime(inputSdf.parse(dateText));

            SimpleDateFormat outputSdf = new SimpleDateFormat(outputFormat, Locale.US);

            return outputSdf.format(finalCalendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }

    private View renderString(String value) {
        TextView textView = new TextView(getContext());
        textView.setText(value);
        textView.setPadding(20, 10, 20, 10);
        textView.setTextSize(TEXT_SIZE);
        return textView;
    }

}
