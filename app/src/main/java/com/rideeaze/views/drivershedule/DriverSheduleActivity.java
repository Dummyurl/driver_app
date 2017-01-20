package com.rideeaze.views.drivershedule;

import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.rideeaze.R;
import com.rideeaze.services.network.NetworkApi;
import com.rideeaze.services.network.NetworkService;
import com.rideeaze.services.network.model.data.DriverShedule;
import com.rideeaze.services.network.model.request.HttpDriverTokenRequest;
import com.rideeaze.services.network.model.request.HttpUpdateDriverschedule;
import com.rideeaze.services.network.model.response.HttpDriverScheduleResponse;
import com.rideeaze.services.network.model.response.JsonResponse;
import com.rideeaze.views.common.DriverAppCompatActivity;
import com.rideeaze.views.common.DriverAppCompatFragment;
import com.util.StorageDataHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by adventis on 1/19/16.
 */
public class DriverSheduleActivity extends DriverAppCompatActivity implements iDriverShedulesActions {

    @Bind(R.id.fragments_container) FrameLayout fragmentsContainer;

    public Menu menu = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_shedule_activity);
        ButterKnife.bind(this);

        StorageDataHelper.getInstance(this).clearTempDriverScheduleList();

        loadSchedule(true);
        //testRequest();
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);

        showListOfShedulesFragment();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.driver_modify_schedule_done, menu);
        this.menu = menu;
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                updateSchedule();
                break;
            default:
                break;
        }

        return true;
    }

    public void setMenuVisibility(boolean isVisible) {
        if(menu != null) {
            for (int i = 0; i < menu.size(); i++) {
                if(menu.getItem(i) !=null)
                    menu.getItem(i).setVisible(isVisible);
            }
        }
    }

    public void showListOfShedulesFragment() {
        showFragment(new DriverListSheduleFragment(), false);
    }

    @Override
    public void showModifySheduleFragment(DriverShedule driverShedule) {
        DriverAddEditSheduleItem fragment = new DriverAddEditSheduleItem();
        if(driverShedule != null) {
            fragment.setEditMode(driverShedule);
        }
        showFragment(fragment, true);
    }

    private void showFragment(DriverAppCompatFragment fragment, boolean isAddToBackStack) {
        if(fragmentsContainer != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragments_container, fragment);
            if(isAddToBackStack)
                transaction.addToBackStack(DriverSheduleActivity.class.getName());
            transaction.commit();
        } else {
            Toast.makeText(this, "Fragment Container is null", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void goToPreviousView() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            finish();
        }
    }

    public void addSchedule(DriverShedule driverShedule) {
        // Added to temp list
        StorageDataHelper.getInstance(this).addItemToTempDriverScheduleList(driverShedule);
        // Added to storage list
        StorageDataHelper.getInstance(this).addItemToDriverScheduleList(driverShedule);
    }

    public void replaceSchedule(int idSchedule, DriverShedule driverShedule) {
        // Added to temp list
        StorageDataHelper.getInstance(this).replaceItemInTempDriverScheduleList(idSchedule, driverShedule);
        // Added to storage list
        StorageDataHelper.getInstance(this).replaceItemInDriverScheduleList(idSchedule, driverShedule);
    }

    public void removeSchedule(int idSchedule) {
        // Remove from temp list
        StorageDataHelper.getInstance(this).deleteItemInTempDriverScheduleList(idSchedule);
        // Remove from storage list
        StorageDataHelper.getInstance(this).deleteItemInDriverScheduleList(idSchedule);
    }


    public void loadSchedule(final boolean isOpenScheduleList) {
        if (!isNetworkAvailable()) return;

        DisplayProcessMessage(getResources().getString(R.string.driver_common_please_wait));

        String token = StorageDataHelper.getInstance(getApplicationContext()).getAuthToken();
        HttpDriverTokenRequest httpDriverTokenRequest = new HttpDriverTokenRequest();
        httpDriverTokenRequest.DriverToken = StorageDataHelper.getInstance(getApplicationContext()).getAuthToken();
        httpDriverTokenRequest.DriverCode = StorageDataHelper.getInstance(getApplicationContext()).getAccountInfoDetails().DriverCode;
        if(httpDriverTokenRequest.DriverCode == null || httpDriverTokenRequest.DriverCode.isEmpty())
            httpDriverTokenRequest.DriverCode = "HERMANN";

        NetworkApi api = (new NetworkService()).getApi();
        api.getDriverSchedule(token, httpDriverTokenRequest)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JsonResponse<HttpDriverScheduleResponse>>() {

                    @Override
                    public void onCompleted() {
                        DisplayProcessMessage(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        DisplayProcessMessage(false);
                        Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(JsonResponse<HttpDriverScheduleResponse> driverSheduleJsonResponse) {
                        if (driverSheduleJsonResponse.IsSuccess) {
                            StorageDataHelper.getInstance(getBaseContext()).setDriverSheduleList(driverSheduleJsonResponse.Content.TimeOff);
                        } else {

                        }
                        if(isOpenScheduleList) {
                            showListOfShedulesFragment();
                        }
                    }
                });
    }

    public void updateSchedule() {
        if (!isNetworkAvailable()) return;

        DisplayProcessMessage(getResources().getString(R.string.driver_common_please_wait));

        String token = StorageDataHelper.getInstance(getApplicationContext()).getAuthToken();
        HttpUpdateDriverschedule httpUpdateDriverschedule = new HttpUpdateDriverschedule();
        httpUpdateDriverschedule.DriverCode = StorageDataHelper.getInstance(getApplicationContext()).getAccountInfoDetails().DriverCode;
        if(httpUpdateDriverschedule.DriverCode == null || httpUpdateDriverschedule.DriverCode.isEmpty())
            httpUpdateDriverschedule.DriverCode = "HERMANN";
        httpUpdateDriverschedule.TimeOff = new ArrayList<DriverShedule>();
        List<DriverShedule> driverSheduleList = StorageDataHelper.getInstance(this).getDriverSheduleList();
        for(int i=0; i < driverSheduleList.size(); i++) {
            httpUpdateDriverschedule.TimeOff.add(driverSheduleList.get(i));
        }

        NetworkApi api = (new NetworkService()).getApi();
        api.updateDriverSchedule(token,httpUpdateDriverschedule)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JsonResponse<String>>() {

                    @Override
                    public void onCompleted() {
                        DisplayProcessMessage(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        DisplayProcessMessage(false);
                        Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onNext(JsonResponse<String> driverSheduleJsonResponse) {
                        if (driverSheduleJsonResponse.IsSuccess) {
                            if(getVisibleFragment() != null && getVisibleFragment() instanceof DriverListSheduleFragment) {
                                goToPreviousView();
                            }
                            Toast.makeText(getBaseContext(), driverSheduleJsonResponse.Message, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
    }
}
