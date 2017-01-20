package com.util;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rideeaze.services.network.model.data.AccountAuthDetails;
import com.rideeaze.services.network.model.data.AccountInfoDetails;
import com.rideeaze.services.network.model.data.DriverShedule;
import com.rideeaze.services.network.model.data.HttpVehicle;
import com.rideeaze.services.network.model.response.HttpPendingRequestPickUp;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by adventis on 10/8/15.
 */
public class StorageDataHelper {
    private static StorageDataHelper instance;

    private Context mCtx;

    private static final String SELECTED_VEHICLE = "SELECTED_VEHICLE";
    public HttpVehicle selectedVehicle;
    public HttpVehicle getSelectedVehicle() {
        if(selectedVehicle == null) {
            Type typeOfObject = new TypeToken<HttpVehicle>(){}.getType();
            this.selectedVehicle = (HttpVehicle)readFromStorage(SELECTED_VEHICLE, typeOfObject);
        }
        return selectedVehicle;
    }

    public void setSelectedVehicle(HttpVehicle selectedVehicle) {
        this.selectedVehicle = selectedVehicle;
        writeToStorageObject(this.selectedVehicle, SELECTED_VEHICLE);
    }


    private static final String DRIVER_SCHEDULE_LIST = "DRIVER_SCHEDULE_LIST";
    public List<DriverShedule> driverSheduleList;
    public List<DriverShedule> getDriverSheduleList() {
        if(driverSheduleList == null) {
            Type typeOfObject = new TypeToken<List<DriverShedule>>(){}.getType();
            this.driverSheduleList = (List<DriverShedule>)readFromStorage(DRIVER_SCHEDULE_LIST, typeOfObject);
            if(this.driverSheduleList == null)
                this.driverSheduleList = new ArrayList<DriverShedule>();
        }
        return driverSheduleList;
    }

    public void setDriverSheduleList(List<DriverShedule> driverSheduleList) {
        for(int i=0; i< driverSheduleList.size(); i++) {
            driverSheduleList.get(i).id = i;
        }
        this.driverSheduleList = driverSheduleList;
        writeToStorageObject(this.driverSheduleList, DRIVER_SCHEDULE_LIST);
    }

    public void addItemToDriverScheduleList(DriverShedule driverSheduleItem) {
        List<DriverShedule> driverSheduleList = getDriverSheduleList();
        driverSheduleList.add(driverSheduleItem);
        setDriverSheduleList(driverSheduleList);
    }

    public void replaceItemInDriverScheduleList(int id, DriverShedule driverSheduleItem) {
        List<DriverShedule> driverSheduleList = getDriverSheduleList();
        driverSheduleItem.id = id;
        if(driverSheduleList.size() > id) {
            driverSheduleList.set(id, driverSheduleItem);
            setDriverSheduleList(driverSheduleList);
        }
    }

    public void deleteItemInDriverScheduleList(int id) {
        List<DriverShedule> driverSheduleList = getDriverSheduleList();
        driverSheduleList.remove(id);
        setDriverSheduleList(driverSheduleList);
    }

    private static final String TEMP_DRIVER_SCHEDULE_LIST = "TEMP_DRIVER_SCHEDULE_LIST";
    public List<DriverShedule> tempDriverSheduleList;
    public List<DriverShedule> getTempDriverSheduleList() {
        if(tempDriverSheduleList == null) {
            Type typeOfObject = new TypeToken<List<DriverShedule>>(){}.getType();
            this.tempDriverSheduleList = (List<DriverShedule>)readFromStorage(TEMP_DRIVER_SCHEDULE_LIST, typeOfObject);
            if(this.tempDriverSheduleList == null)
                this.tempDriverSheduleList = new ArrayList<DriverShedule>();
        }
        return tempDriverSheduleList;
    }

    public void setTempDriverSheduleList(List<DriverShedule> driverSheduleList) {
        for(int i=0; i< driverSheduleList.size(); i++) {
            driverSheduleList.get(i).id = i;
        }
        this.tempDriverSheduleList = driverSheduleList;
        writeToStorageObject(this.tempDriverSheduleList, TEMP_DRIVER_SCHEDULE_LIST);
    }

    public void clearTempDriverScheduleList() {
        setTempDriverSheduleList(new ArrayList<DriverShedule>());
    }

    public void addItemToTempDriverScheduleList(DriverShedule driverSheduleItem) {
        List<DriverShedule> driverSheduleList = getTempDriverSheduleList();
        driverSheduleList.add(driverSheduleItem);
        setTempDriverSheduleList(driverSheduleList);
    }

    public void replaceItemInTempDriverScheduleList(int id, DriverShedule driverSheduleItem) {
        List<DriverShedule> driverSheduleList = getTempDriverSheduleList();
        driverSheduleItem.id = id;
        if(driverSheduleList.size() > id) {
            driverSheduleList.set(id, driverSheduleItem);
            setTempDriverSheduleList(driverSheduleList);
        }
    }

    public void deleteItemInTempDriverScheduleList(int id) {
        List<DriverShedule> driverSheduleList = getTempDriverSheduleList();
        if(driverSheduleList.size() > id) {
            driverSheduleList.remove(id);
            setTempDriverSheduleList(driverSheduleList);
        }
    }



//    private static final String DRIVER_SCHEDULE = "DRIVER_SCHEDULE";
//    private DriverShedule driverShedule;
//    public DriverShedule getDriverShedule() {
//        if(driverShedule == null) {
//            Type typeOfObject = new TypeToken<DriverShedule>(){}.getType();
//            this.driverShedule = (DriverShedule)readFromStorage(DRIVER_SCHEDULE, typeOfObject);
//        }
//        return driverShedule;
//    }
//
//    public void setDriverShedule(DriverShedule driverShedule) {
//        this.driverShedule = driverShedule;
//        writeToStorageObject(driverShedule, DRIVER_SCHEDULE);
//    }

    private boolean useWakeUpCall;
    public boolean isUseWakeUpCall() {
        AccountInfoDetails accountInfoDetails = getAccountInfoDetails();
        if(accountInfoDetails != null) {
            return accountInfoDetails.UseWakeUpCall;
        }
        return false;
    }

    private static final String PENDING_REQUEST_DATA = "PENDING_REQUEST_DATA";
    public List<HttpPendingRequestPickUp> pendingRequestData;
    public List<HttpPendingRequestPickUp> getPendingRequestData() {
        if(pendingRequestData == null) {
            Type typeOfObject = new TypeToken<List<HttpPendingRequestPickUp>>(){}.getType();
            this.pendingRequestData = (List<HttpPendingRequestPickUp>)readFromStorage(PENDING_REQUEST_DATA, typeOfObject);
            if(this.pendingRequestData == null)
                this.pendingRequestData = new ArrayList<HttpPendingRequestPickUp>();
        }
        return pendingRequestData;
    }

    public void setPendingRequestData(List<HttpPendingRequestPickUp> pendingRequestData) {
        this.pendingRequestData = pendingRequestData;
        writeToStorageObject(pendingRequestData, PENDING_REQUEST_DATA);
    }

    private static final String ACCOUNT_INFO_DETAILS = "ACCOUNT_INFO_DETAILS";
    AccountInfoDetails accountInfoDetails;
    public AccountInfoDetails getAccountInfoDetails() {
        if(this.accountInfoDetails == null) {
            Type typeOfObject = new TypeToken<AccountInfoDetails>(){}.getType();
            this.accountInfoDetails  = (AccountInfoDetails)readFromStorage(ACCOUNT_INFO_DETAILS, typeOfObject);
            if(this.accountInfoDetails == null)
                return new AccountInfoDetails();
        }
        return accountInfoDetails;
    }

    public void setAccountInfoDetails(AccountInfoDetails accountInfoDetails) {
        this.accountInfoDetails = accountInfoDetails;
        writeToStorageObject(accountInfoDetails, ACCOUNT_INFO_DETAILS);
    }

    private static final String ACCOUNT_AUTH_DETAILS = "ACCOUNT_AUTH_DETAILS";
    private AccountAuthDetails accountAuthDetails;

    public String getAuthToken() {
        return getAccountAuthDetails().token;
    }

    public AccountAuthDetails getAccountAuthDetails() {
        if(this.accountAuthDetails == null) {
            Type typeOfObject = new TypeToken<AccountAuthDetails>(){}.getType();
            this.accountAuthDetails  = (AccountAuthDetails)readFromStorage(ACCOUNT_AUTH_DETAILS, typeOfObject);
            if(this.accountAuthDetails == null)
                return new AccountAuthDetails();
        }
        return accountAuthDetails;
    }

    public void setAccountAuthDetails(AccountAuthDetails accountAuthDetails) {
        this.accountAuthDetails = accountAuthDetails;
        writeToStorageObject(accountAuthDetails, ACCOUNT_AUTH_DETAILS);
    }

    private static final String DECLINED_PENDINGIDS_LIST = "DECLINED_PENDINGIDS_LIST";
    private ArrayList<Integer> declinedPendingReservationIdsList;
    public ArrayList<Integer> getDeclinedPendingReservationIdsList() {
        if(declinedPendingReservationIdsList == null) {
            Type typeOfObject = new TypeToken<ArrayList<Integer>>(){}.getType();
            this.declinedPendingReservationIdsList = (ArrayList<Integer>)readFromStorage(DECLINED_PENDINGIDS_LIST, typeOfObject);
            if(this.declinedPendingReservationIdsList == null)
                this.declinedPendingReservationIdsList = new ArrayList<Integer>();
        }
        return declinedPendingReservationIdsList;
    }

    public void addDeclinedPendingReservationId(Integer id) {
        ArrayList<Integer> list = getDeclinedPendingReservationIdsList();
        list.add(id);

        setDeclinedPendingReservationIdsList(list);
    }

    public void setDeclinedPendingReservationIdsList(ArrayList<Integer> searchDriversCurrentDataForRequest) {
        this.declinedPendingReservationIdsList = searchDriversCurrentDataForRequest;
        writeToStorageObject(searchDriversCurrentDataForRequest, DECLINED_PENDINGIDS_LIST);
    }

    public StorageDataHelper(){}

    public static StorageDataHelper getInstance(Context ctx) {
        if(instance == null) {
            instance = new StorageDataHelper();
        }
        instance.mCtx = ctx;
        return instance;
    }

    private void writeToStorageObject(Object data, String flag) {
        String dataToSave = "";
        if(data != null) {
            if (data.getClass() == String.class) {
                dataToSave = (String) data;
            } else {
                dataToSave = convertToJson(data);
            }
        }

        try {
            Pref.setValue(mCtx, flag, dataToSave);
        }catch (Exception e) {

        }
    }

    private Object readFromStorage(String flag, Type type) {
        String value = Pref.getValue(mCtx, flag, "");
        return (new Gson()).fromJson(value, type);
    }

    private String convertToJson(Object object) {
        return (new Gson()).toJson(object);
    }



}
