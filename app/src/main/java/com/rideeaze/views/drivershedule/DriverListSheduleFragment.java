package com.rideeaze.views.drivershedule;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.rideeaze.R;
import com.rideeaze.services.network.model.data.DriverShedule;
import com.rideeaze.views.common.DriverAppCompatFragment;
import com.rideeaze.views.drivershedule.adapters.SingleDayScheduleAdapter;
import com.rideeaze.views.drivershedule.adapters.WeeklyScheduleAdapter;
import com.rideeaze.views.drivershedule.models.RepeatDriverScheduleItem;
import com.rideeaze.views.drivershedule.models.RepeatDriverScheduleTableSource;
import com.rideeaze.views.drivershedule.tableview.SingleDayScheduleTableView;
import com.rideeaze.views.drivershedule.tableview.WeeklyScheduleTableView;
import com.util.StorageDataHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import de.codecrafters.tableview.listeners.TableDataClickListener;

/**
 * Created by adventis on 1/20/16.
 */
public class DriverListSheduleFragment extends DriverAppCompatFragment {
    iDriverShedulesActions driverShedulesActions;

    @Bind(R.id.weekly_schedule_table) WeeklyScheduleTableView weeklyScheduleTableView;
    @Bind(R.id.single_day_schedule_table) SingleDayScheduleTableView singleDayScheduleTableView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFragmentViewLayoutId(R.layout.driver_list_shedule_fragment);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        updateData();
    }

    @Override
    public void onResume() {
        super.onResume();
        this.driverShedulesActions.setMenuVisibility(true);
    }

    private void initView() {
        weeklyScheduleTableView.addDataClickListener(tableDataClickListener);
        singleDayScheduleTableView.addDataClickListener(tableDataClickListener);
    }

    private TableDataClickListener tableDataClickListener = new TableDataClickListener() {
        @Override
        public void onDataClicked(int rowIndex, Object clickedData) {
            if(clickedData instanceof DriverShedule) {
                driverShedulesActions.showModifySheduleFragment((DriverShedule) clickedData);
            } else if(clickedData instanceof RepeatDriverScheduleItem) {
                if(!((RepeatDriverScheduleItem) clickedData).driverSheduleList.isEmpty()) {
                    driverShedulesActions.showModifySheduleFragment(((RepeatDriverScheduleItem) clickedData).driverSheduleList.get(0));
                } else {
                    Toast.makeText(getContext(), "No data to edit", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.driverShedulesActions = (iDriverShedulesActions)getActivity();
        if (getActivity() instanceof iDriverShedulesActions) {
            this.driverShedulesActions = (iDriverShedulesActions)getActivity();
        } else {
            throw new ClassCastException(getActivity().toString() + " must implement iDriverShedulesActions");
        }

    }

    @OnClick(R.id.openModifySheduleBtn)
    public void openModifyShedule() {
        this.driverShedulesActions.showModifySheduleFragment(null);
    }

    public void updateData(){
        weeklyScheduleTableView.setDataAdapter(new WeeklyScheduleAdapter(getActivity(), getDataForWeeklySchedule().generateListFromDictionary()));
        singleDayScheduleTableView.setDataAdapter(new SingleDayScheduleAdapter(getActivity(), getDataForSingleSchedule()));
    }

    public List<DriverShedule> getRawDataSchedules() {
        List<DriverShedule> driverSheduleList = StorageDataHelper.getInstance(getContext()).getDriverSheduleList();
        if (driverSheduleList == null) {
            return new ArrayList<DriverShedule>();
        }
        return driverSheduleList;
    }

    public RepeatDriverScheduleTableSource getDataForWeeklySchedule() {
        RepeatDriverScheduleTableSource repeatDriverScheduleTableSource = new RepeatDriverScheduleTableSource();
        List<DriverShedule> rawList = getRawDataSchedules();

        for (DriverShedule driverSchedule : rawList) {
            if(driverSchedule.repeat) {
                repeatDriverScheduleTableSource.parseWeekDaysFlagObject(driverSchedule);
            }
        }

        return repeatDriverScheduleTableSource;
    }

    private List<DriverShedule> getDataForSingleSchedule() {
        List<DriverShedule> newList = new ArrayList<DriverShedule>();
        List<DriverShedule> rawList = getRawDataSchedules();

        for (DriverShedule driverSchedule : rawList) {
            if(!driverSchedule.repeat) {
                newList.add(driverSchedule);
            }
        }

        return newList;

    }
}
