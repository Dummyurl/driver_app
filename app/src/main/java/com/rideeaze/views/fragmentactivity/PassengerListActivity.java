package com.rideeaze.views.fragmentactivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.rideeaze.R;
import com.rideeaze.services.network.NetworkApi;
import com.rideeaze.services.network.NetworkService;
import com.rideeaze.services.network.model.request.HttpPickUpRequest;
import com.rideeaze.services.network.model.request.HttpReplyPickupRequest;
import com.rideeaze.services.network.model.response.HttpPendingRequestPickUp;
import com.rideeaze.services.network.model.response.HttpSendReplyPkUpData;
import com.rideeaze.services.network.model.response.JsonResponse;
import com.rideeaze.views.activity.DriverActivity;
import com.util.Const;
import com.util.StorageDataHelper;
import com.util.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by adventis on 1/31/15.
 */
public class PassengerListActivity extends DriverActivity {

    private TextView column_1_name;
    private TextView column_2_date;
    private TextView column_3_id;
    private TextView column_4_fare;
    private RadioGroup rbGroup;
    private Button main_circle_payment;

    private LinearLayout sQuotes_data_table;
    private LinearLayout allTitleRow;

    public static List<HttpPendingRequestPickUp> pickUpArray;
    private Message msg;

    public static Context ctx = null;
    public final String TAG = "PassengerListActivity";

    private LinearLayout llPickupsActions;
    private Button btnAcceptAll, btnDeclineAll;

    private int pendingCount = 0;

    private static final int RUN_PASSANGER_ACTIVITY_CODE = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passengers_list);

        //forbid showing icon on action bar
        getActionBar().setDisplayShowHomeEnabled(false);

        getActionBar().setTitle("Today Trips");

        if(PassengerListActivity.ctx == null) {
            PassengerListActivity.ctx = getApplicationContext();
        }

        msg = new Message();
        initView();
        requestPickup();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.passenger_list_action_bar, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_pay_without_ivite:
                payWithoutInvite();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }

        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RUN_PASSANGER_ACTIVITY_CODE) {
            //createTable();
            requestPickup();
        }
    }

    private void payWithoutInvite() {
        Const.feeCodeList.clear();
        Const.fare = (float) 0.00;
        Const.paymentTotalAdjstAmnt = (float)0.00;
        Const.paymentSubTotalAmnt = (float)0.00;
        Const.paymentTotalAmnt = (float)0.00;
        Const.IS_KNOWN_PASSENGER = 0;
        startActivity(new Intent(PassengerListActivity.this, EnterAmountActivity.class).putExtra("fromFlag", 2).putExtra("psngrName", ""));
    }

    private void initView() {
        View.OnClickListener columnClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setQuoteTableColumn(view);
            }
        };

        // allTitleRow
        column_1_name = (TextView)findViewById(R.id.column_1_name);
        column_1_name.setOnClickListener(columnClick);

        column_2_date = (TextView)findViewById(R.id.column_2_date);
        column_2_date.setOnClickListener(columnClick);

        column_3_id = (TextView)findViewById(R.id.column_3_id);
        column_3_id.setOnClickListener(columnClick);

        column_4_fare = (TextView)findViewById(R.id.column_4_fare);
        column_4_fare.setOnClickListener(columnClick);

        rbGroup = (RadioGroup) findViewById(R.id.rgroupFilter);

        rbGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if(original_adapter == null) return;


                List<HttpPendingRequestPickUp> tmpOriginalList = getParsedFilteredAdapter(original_adapter);
                List<HttpPendingRequestPickUp> pendingList = new ArrayList<HttpPendingRequestPickUp>();
                List<HttpPendingRequestPickUp> otherPickups = new ArrayList<HttpPendingRequestPickUp>();

                for (int i = 0; i < tmpOriginalList.size(); i++) {
                    HttpPendingRequestPickUp _pickup = tmpOriginalList.get(i);
                    if (_pickup.ReservationStatus.equals("Pending"))
                        pendingList.add(_pickup);
                    else
                        otherPickups.add(_pickup);
                }

                if(checkedId == R.id.rbToday){
                    getActionBar().setTitle("Today Trips");
                    List<HttpPendingRequestPickUp> filteredList = new ArrayList<HttpPendingRequestPickUp>();
                    List<HttpPendingRequestPickUp> sourcePickups = otherPickups;

                    Calendar calendar = GregorianCalendar.getInstance(TimeZone.getDefault());
                    //Get now date
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    //Get today's 00:00:00 date
                    calendar.set(Calendar.HOUR, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.AM_PM, 0);
                    Date startDate = calendar.getTime();
                    //Get today's 23:59:59 date
                    calendar.add(Calendar.HOUR, 24);
                    calendar.add(Calendar.SECOND, -1);
                    Date endDate = calendar.getTime();

                    for(HttpPendingRequestPickUp pickup : sourcePickups) {
                        //Get Pickup time
                        calendar.setTimeInMillis(pickup.TimeOfPickup * 1000);
                        Date date = calendar.getTime();
                        //Do filtering
                        if (date.after(startDate) && date.before(endDate)) {
                            filteredList.add(pickup);
                        }
                    }
                    filtered_adapter = filteredList;
                }
                if(checkedId == R.id.rbPending){
                    getActionBar().setTitle("Pending Trips");
                    //List<HttpPendingRequestPickUp> filteredList = new ArrayList<HttpPendingRequestPickUp>();
                    List<HttpPendingRequestPickUp> sourcePickups = pendingList;
                    filtered_adapter = sourcePickups;
                }
                if(checkedId == R.id.rbAll){
                    getActionBar().setTitle("All Trips");
                    filtered_adapter = otherPickups;
                }
                if(checkedId == R.id.rbTommorrow){
                    getActionBar().setTitle("Tomorrow Trips");
                    List<HttpPendingRequestPickUp> filteredList = new ArrayList<HttpPendingRequestPickUp>();
                    List<HttpPendingRequestPickUp> sourcePickups = otherPickups;

                    Calendar calendar = GregorianCalendar.getInstance(TimeZone.getDefault());
                    //Get now date
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    //Get today 23:59:59 date
                    calendar.set(Calendar.HOUR, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.AM_PM, 0);
                    calendar.add(Calendar.HOUR, 24);
                    calendar.add(Calendar.SECOND, -1);
                    Date startDate = calendar.getTime();

                    for(HttpPendingRequestPickUp pickup : sourcePickups) {
                        //Get Pickup time
                        calendar.setTimeInMillis(pickup.TimeOfPickup * 1000);
                        Date date = calendar.getTime();
                        //Do filtering
                        if (date.after(startDate)) {
                            filteredList.add(pickup);
                        }
                    }
                    filtered_adapter = filteredList;
                }

                if (checkedId == R.id.rbPending) {
                    llPickupsActions.setVisibility(View.VISIBLE);
                } else {
                    llPickupsActions.setVisibility(View.GONE);
                }

                isASC_date = false;
                dateSort(isASC_date);
                createTable();
            }
        });


        sQuotes_data_table = (LinearLayout) findViewById(R.id.quotes_main_data_layout);
        allTitleRow = (LinearLayout) findViewById(R.id.allTitleRow);

        main_circle_payment = (Button) findViewById(R.id.main_circle_payment);
        main_circle_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pickUpArray != null && pickUpArray.size() > 0) {
                    int index = -1;
                    for(int i=0; i < PassengerListActivity.pickUpArray.size();i++) {
                        if(PassengerListActivity.pickUpArray.get(i).ReservationStatus.contentEquals("Active")) {
                            index = i;
                            break;
                        }
                    }

                    if(index > -1) {
                        Intent intent = new Intent(PassengerListActivity.this,
                                PassengerActivity.class);
                        intent.putExtra("searchPassangerIDd", PassengerListActivity.pickUpArray.get(index).ReservationID);
                        intent.putExtra("willPaid", true);
                        startActivity(intent);
                    }
                }


            }
        });

        llPickupsActions = (LinearLayout)findViewById(R.id.llPickupActionsLayout);
        btnAcceptAll = (Button)findViewById(R.id.btnAcceptPickups);
        btnDeclineAll = (Button)findViewById(R.id.btnDeclinePickups);

        btnAcceptAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doActionWithPickups("Accept");
            }
        });

        btnDeclineAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doActionWithPickups("Decline");
            }
        });
    }

    protected void doActionWithPickups(final String action) {
        if (!hasPendingPickups())
            return;
        if (filtered_adapter == null)
            return;
        if (filtered_adapter.size() == 0)
            return;

        boolean isReturn = true;
        for (int i = 0; i < filtered_adapter.size(); i++) {
            HttpPendingRequestPickUp _pickup = filtered_adapter.get(i);
            if (_pickup.ReservationStatus.equals("Pending")) {
                isReturn = false;
                break;
            }
        }

        if (isReturn)
            return;

        DisplayProcessMessage("Please wait...");

        ArrayList<Pair<HttpPendingRequestPickUp, String>> requestList = new ArrayList<Pair<HttpPendingRequestPickUp, String>>();
        for (int i = 0; i < filtered_adapter.size(); i++) {
            requestList.add(new Pair<HttpPendingRequestPickUp, String>(filtered_adapter.get(i), action));
        }

        for(int i=0; i<original_adapter.size(); i++) {
            if(original_adapter.get(i).ReservationStatus.equals("Pending")) {
                boolean isInDeclinedArray = false;
                ArrayList<Integer> declinedId = StorageDataHelper.getInstance(getBaseContext()).getDeclinedPendingReservationIdsList();
                for (Integer id : declinedId) {
                    if (id.intValue() == original_adapter.get(i).ReservationID) {
                        isInDeclinedArray = true;
                        break;
                    }
                }

                if(isInDeclinedArray) {
                    requestList.add(new Pair<HttpPendingRequestPickUp, String>(original_adapter.get(i), "Decline"));
                }
            }
        }


        for (int i = 0; i < requestList.size(); i++) {
            final Pair<HttpPendingRequestPickUp, String> pickup = requestList.get(i);
            if (pickup.first.ReservationStatus.equals("Pending")) {

               if(!isNetworkAvailable()) return;

                pendingCount++;

                HttpReplyPickupRequest httpReplyPickupRequest = new HttpReplyPickupRequest();
                httpReplyPickupRequest.DriverToken = StorageDataHelper.getInstance(getApplicationContext()).getAuthToken();
                httpReplyPickupRequest.PassengerID = pickup.first.PassengerID;
                httpReplyPickupRequest.Reply = pickup.second;
                httpReplyPickupRequest.ReservationID = pickup.first.ReservationID + "";

                NetworkApi api = (new NetworkService()).getApi();
                api.sendReplyOfPickup(httpReplyPickupRequest)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<JsonResponse<HttpSendReplyPkUpData>>() {
                            @Override
                            public void onCompleted() {
                                pendingCount--;
                                if (pendingCount <= 0) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            StorageDataHelper.getInstance(getBaseContext()).setDeclinedPendingReservationIdsList(null);
                                            DisplayProcessMessage(false);
                                            requestPickup();
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                DisplayProcessMessage(false);
                            }

                            @Override
                            public void onNext(JsonResponse<HttpSendReplyPkUpData> httpSendReplyPkUpDataJsonResponse) {

                            }
                        });
            }
        }
    }

    public void requestPickup() {
        pickUpArray = null;
        original_adapter = new ArrayList<>();
        filtered_adapter = new ArrayList<>();

        createTable();

        if(!isNetworkAvailable()) return;

        DisplayProcessMessage(getResources().getString(R.string.driver_common_please_wait));

        HttpPickUpRequest httpPickUpRequest = new HttpPickUpRequest();
        httpPickUpRequest.DriverToken = StorageDataHelper.getInstance(getApplicationContext()).getAuthToken();

        NetworkApi api = (new NetworkService()).getApi();
        api.getPickupsForDriver(httpPickUpRequest)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JsonResponse<List<HttpPendingRequestPickUp>>>() {
                    @Override
                    public void onCompleted() {
                        DisplayProcessMessage(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        StorageDataHelper.getInstance(getApplicationContext()).setPendingRequestData(null);
                        pickUpArray = null;
                        DisplayProcessMessage(false);
                    }

                    @Override
                    public void onNext(JsonResponse<List<HttpPendingRequestPickUp>> listJsonResponse) {
                        if (listJsonResponse.IsSuccess) {
                            StorageDataHelper.getInstance(getApplicationContext()).setPendingRequestData(listJsonResponse.Content);
                            pickUpArray = listJsonResponse.Content;
                            Utils.showToastAlert(PassengerListActivity.this,
                                    "GetPickups Success");
                            if (pickUpArray != null && pickUpArray.size() > 0) {
                                PassengerListActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        List<HttpPendingRequestPickUp> sourcePickups = new ArrayList<HttpPendingRequestPickUp>();

                                        boolean _hasPending = hasPendingPickups();

                                        RadioButton rbPending = (RadioButton) findViewById(R.id.rbPending);

                                        List<HttpPendingRequestPickUp> tmpList = new ArrayList<HttpPendingRequestPickUp>();
                                        tmpList.addAll(pickUpArray);

                                        tmpList = getParsedFilteredAdapter(tmpList);

                                        List<HttpPendingRequestPickUp> filteredPendingList = new ArrayList<HttpPendingRequestPickUp>();
                                        for (HttpPendingRequestPickUp __pickup : tmpList) {
                                            if (__pickup.ReservationStatus.equals("Pending"))
                                                filteredPendingList.add(__pickup);
                                        }

                                        if (_hasPending && filteredPendingList.size() > 0) {
                                            llPickupsActions.setVisibility(View.VISIBLE);
                                            ((RadioButton) findViewById(R.id.rbPending)).setEnabled(true);
                                        } else {
                                            llPickupsActions.setVisibility(View.GONE);
                                            ((RadioButton) findViewById(R.id.rbPending)).setEnabled(false);
                                        }

                                        sourcePickups.addAll(pickUpArray);
                                        setData(sourcePickups);

                                        if (_hasPending && filteredPendingList.size() > 0) {
                                            rbGroup.clearCheck();
                                            ((RadioButton) findViewById(R.id.rbPending)).setChecked(true);
                                        } else {
                                            rbGroup.clearCheck();
                                            ((RadioButton) findViewById(R.id.rbToday)).setChecked(true);
                                        }

                                    }
                                });
                            }
                        } else {
                            StorageDataHelper.getInstance(getApplicationContext()).setPendingRequestData(null);
                            pickUpArray = null;
                                Utils.showToastAlert(PassengerListActivity.this, "GetPickups failed");
                        }
                    }
                });
    }

    protected boolean hasPendingPickups() {
        if (pickUpArray != null && pickUpArray.size() > 0) {
            for (int i = 0; i < pickUpArray.size(); i++) {
                HttpPendingRequestPickUp pickup = pickUpArray.get(i);
                if (pickup.ReservationStatus.equals("Pending")) {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean hasPendingPickupsInList(List<HttpPendingRequestPickUp> list) {
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                HttpPendingRequestPickUp pickup = list.get(i);
                if (pickup.ReservationStatus.equals("Pending")) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isASC_name;
    private boolean isASC_date;
    private boolean isASC_id;
    private boolean isASC_fare;

    private void clearASCParams() {
        isASC_name = true;
        isASC_date = true;
        isASC_id = true;
        isASC_fare = true;
    }

    public int lastSelectedTab = 0;
    private void setQuoteTableColumn(View view) {
        if(lastSelectedTab != view.getId()) {
            clearASCParams();
        }

        if(view.getId() == R.id.column_1_name) {
            setQuoteTableColumnInActiveColor();
            column_1_name.setBackgroundColor(getResources().getColor(R.color.dark_gray));
            nameSort(isASC_name);
            isASC_name = !isASC_name;
            createTable();
            lastSelectedTab = view.getId();
        } else if(view.getId() == R.id.column_2_date) {
            setQuoteTableColumnInActiveColor();
            column_2_date.setBackgroundColor(getResources().getColor(R.color.dark_gray));
            dateSort(isASC_date);
            isASC_date = !isASC_date;
            createTable();
            lastSelectedTab = view.getId();
        } else if(view.getId() == R.id.column_3_id) {
            setQuoteTableColumnInActiveColor();
            column_3_id.setBackgroundColor(getResources().getColor(R.color.dark_gray));
            idSort(isASC_id);
            isASC_id = !isASC_id;
            createTable();
            lastSelectedTab = view.getId();
        } else if(view.getId() == R.id.column_4_fare) {
            setQuoteTableColumnInActiveColor();
            column_4_fare.setBackgroundColor(getResources().getColor(R.color.dark_gray));
            fareSort(isASC_fare);
            isASC_fare = !isASC_fare;
            createTable();
            lastSelectedTab =view.getId();
        }
    }

    private void setQuoteTableColumnInActiveColor() {
        column_1_name.setBackgroundColor(getResources().getColor(R.color.dark_graylight));
        column_2_date.setBackgroundColor(getResources().getColor(R.color.dark_graylight));
        column_3_id.setBackgroundColor(getResources().getColor(R.color.dark_graylight));
        column_4_fare.setBackgroundColor(getResources().getColor(R.color.dark_graylight));
    }

    private void nameSort(final boolean isASC) {
        filtered_adapter = original_adapter;
        if(filtered_adapter == null) {
            return;
        }
        Collections.sort(filtered_adapter, new Comparator<HttpPendingRequestPickUp>() {
            @Override
            public int compare(HttpPendingRequestPickUp searchDriverData, HttpPendingRequestPickUp searchDriverData2) {
                int value = searchDriverData.PassengerName.compareToIgnoreCase(searchDriverData2.PassengerName);
                if (isASC) {
                    return value;
                } else {
                    return value * (-1);
                }
            }
        });
    }

    private void dateSort(final boolean isASC) {
        RadioButton rbAll = (RadioButton)findViewById(R.id.rbAll);

        final boolean asc = !rbAll.isChecked();

        //filtered_adapter = original_adapter;
        if(filtered_adapter == null) {
            return;
        }

        List<HttpPendingRequestPickUp> pendingList = new ArrayList<>();
        List<HttpPendingRequestPickUp> otherPickups = new ArrayList<>();
        List<HttpPendingRequestPickUp> resultList = new ArrayList<>();

        for (int i = 0; i < filtered_adapter.size(); i++) {
            HttpPendingRequestPickUp _pickup = filtered_adapter.get(i);
            if (_pickup.ReservationStatus.equals("Pending"))
                pendingList.add(_pickup);
            else
                otherPickups.add(_pickup);
        }

        Collections.sort(pendingList, new Comparator<HttpPendingRequestPickUp>() {
            @Override
            public int compare(HttpPendingRequestPickUp searchDriverData, HttpPendingRequestPickUp searchDriverData2) {
                long timeOfPickup1 = searchDriverData.TimeOfPickup;
                long timeOfPickup2 = searchDriverData2.TimeOfPickup;
                if(timeOfPickup1 > timeOfPickup2) {
                    if(asc) {
                        return 1;
                    } else {
                        return -1;
                    }
                } else if(timeOfPickup1 == timeOfPickup2) {
                    return 0;
                }

                if(asc) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });

        Collections.sort(otherPickups, new Comparator<HttpPendingRequestPickUp>() {
            @Override
            public int compare(HttpPendingRequestPickUp searchDriverData, HttpPendingRequestPickUp searchDriverData2) {
                long timeOfPickup1 = searchDriverData.TimeOfPickup;
                long timeOfPickup2 = searchDriverData2.TimeOfPickup;
                if(timeOfPickup1 > timeOfPickup2) {
                    if(asc) {
                        return 1;
                    } else {
                        return -1;
                    }
                } else if(timeOfPickup1 == timeOfPickup2) {
                    return 0;
                }

                if(asc) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });

        resultList.addAll(pendingList);
        resultList.addAll(otherPickups);

        filtered_adapter = resultList;
    }

    private void fareSort(final boolean isASC) {
        filtered_adapter = original_adapter;
        if(filtered_adapter == null) {
            return;
        }
        Collections.sort(filtered_adapter, new Comparator<HttpPendingRequestPickUp>() {
            @Override
            public int compare(HttpPendingRequestPickUp searchDriverData, HttpPendingRequestPickUp searchDriverData2) {
                double fare1 = searchDriverData.EstimateFare;
                double fare2 = searchDriverData2.EstimateFare;
                if(fare1 > fare2) {
                    if(isASC) {
                        return 1;
                    } else {
                        return -1;
                    }
                } else if(fare1 == fare2) {
                    return 0;
                }

                if(isASC) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
    }

    private void idSort(final boolean isASC) {
        filtered_adapter = original_adapter;
        if(filtered_adapter == null) {
            return;
        }
        Collections.sort(filtered_adapter, new Comparator<HttpPendingRequestPickUp>() {
            @Override
            public int compare(HttpPendingRequestPickUp searchDriverData, HttpPendingRequestPickUp searchDriverData2) {
                int timeOfPickup1 = searchDriverData.ReservationID;
                int timeOfPickup2 = searchDriverData2.ReservationID;
                if(timeOfPickup1 > timeOfPickup2) {
                    if(isASC) {
                        return 1;
                    } else {
                        return -1;
                    }
                } else if(timeOfPickup1 == timeOfPickup2) {
                    return 0;
                }

                if(isASC) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
    }

    private List<HttpPendingRequestPickUp> original_adapter;
    private List<HttpPendingRequestPickUp> filtered_adapter;

    public List<HttpPendingRequestPickUp> getParsedFilteredAdapter( List<HttpPendingRequestPickUp> origList) {
        List<HttpPendingRequestPickUp> parsedList = new ArrayList<HttpPendingRequestPickUp>();

        for (int current = 0; current < origList.size(); current++) {
            boolean isInDeclinedArray = false;
            ArrayList<Integer> declinedId = StorageDataHelper.getInstance(getBaseContext()).getDeclinedPendingReservationIdsList();
            for (Integer id : declinedId) {
                if (id.intValue() == origList.get(current).ReservationID) {
                    isInDeclinedArray = true;
                    break;
                }
            }

            if(!isInDeclinedArray) {
                parsedList.add(origList.get(current));
            }
        }

        return parsedList;
    }

    public void setData(List<HttpPendingRequestPickUp> list) {
        original_adapter = list;
        filtered_adapter = getParsedFilteredAdapter(original_adapter);

        isASC_date = false;
        setQuoteTableColumn(column_2_date);

        createTable();
    }

    private void createTable() {
        sQuotes_data_table.removeAllViews();

        LinearLayout lRow;

        TextView new_column_1_name, new_column_2_date, new_column_3_id, new_column_4_fare;
        LinearLayout new_column_3_id_layout, new_column_1_name_layout, new_column_2_date_layout, new_column_4_fare_layout;

        if(filtered_adapter == null) {
            return;
        }

        //filtered_adapter = getParsedFilteredAdapter(filtered_adapter);

        //RadioButton rbPending = (RadioButton)findViewById(R.id.rbPending);

        if(filtered_adapter.size() == 0) {
            llPickupsActions.setVisibility(View.INVISIBLE);
            //((RadioButton)findViewById(R.id.rbPending)).setEnabled(false);
        } /*else {
            if(hasPendingPickups() && hasPendingPickupsInList(filtered_adapter)) {
                if (rbPending.isChecked()) {
                    llPickupsActions.setVisibility(View.VISIBLE);
                    ((RadioButton) findViewById(R.id.rbPending)).setEnabled(true);
                }
            }
        }*/

        for (int current = 0; current < filtered_adapter.size(); current++) {
           /* boolean isInDeclinedArray = false;
            ArrayList<Integer> declinedId = StorageDataHelper.getInstance(getBaseContext()).getDeclinedPendingReservationIdsList();
            for(Integer id : declinedId) {
                if(id.intValue() == filtered_adapter.get(current).ReservationID) {
                    isInDeclinedArray = true;
                    break;
                }
            }

            if(isInDeclinedArray) {
                continue;
            }*/

            lRow = new LinearLayout(PassengerListActivity.ctx);
            lRow.setWeightSum(12);

            lRow.setOrientation(LinearLayout.HORIZONTAL);
            lRow.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            final HttpPendingRequestPickUp passenger = filtered_adapter.get(current);

            lRow.setPadding(20,20,20,20);

            int fareColor = 0;
            int rowColor = 0;
            if (passenger.ReservationStatus.trim().contentEquals("Active")) {
                rowColor = getResources().getColor(R.color.passenger_status_active);
                fareColor = Color.RED;
            } else if(passenger.ReservationStatus.trim().contentEquals("Completed")) {
                if(passenger.InvoicePad) {
                    rowColor = getResources().getColor(R.color.passenger_status_complete_payed);
                } else {
                    rowColor = getResources().getColor(R.color.passenger_status_complete_not_payed);
                }
                fareColor = 0;
            }  else if(passenger.ReservationStatus.trim().contentEquals("Pending")) {
                rowColor = getResources().getColor(R.color.passenger_status_pending);
                fareColor = 0;
            }  else if(passenger.ReservationStatus.trim().contentEquals("Cancelled")) {
                rowColor = getResources().getColor(R.color.passenger_status_canceled);
                fareColor = 0;
            }

                View.OnClickListener clickRow = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //if (!passenger.ReservationStatus.equals("Pending")) {
                            /*PassengerActivity.declinePickupCallback = new PassengerActivity.DeclinePickupCallback() {
                                @Override
                                public void onDeclinePuickup() {
                                    Intent intent;
                                    requestPickup();
                                }
                            };*/
                            Intent intent = new Intent(PassengerListActivity.this, PassengerActivity.class);
                            intent.putExtra("searchPassangerIDd", passenger.ReservationID);
                            startActivityForResult(intent, RUN_PASSANGER_ACTIVITY_CODE);
                        //}
                    }
                };

                int paddingBorder = 2;

            lRow.setOnClickListener(clickRow);
            lRow.setBackgroundColor(rowColor);

            new_column_1_name_layout = new LinearLayout(PassengerListActivity.ctx);
            new_column_1_name_layout.setOrientation(LinearLayout.VERTICAL);
            new_column_1_name_layout.setBackgroundColor(rowColor);
            new_column_1_name = new TextView(PassengerListActivity.ctx);
            new_column_1_name.setGravity(Gravity.LEFT);
            new_column_1_name.setPadding(5,0,0,0);
            new_column_1_name.setText(passenger.PassengerName);
            new_column_1_name.setTextColor(getResources().getColor(R.color.dark_graylight));

            new_column_1_name_layout.addView(new_column_1_name);

            // ------------------------ //

            new_column_2_date_layout = new LinearLayout(PassengerListActivity.ctx);
            new_column_2_date_layout.setOrientation(LinearLayout.VERTICAL);
            new_column_2_date_layout.setBackgroundColor(rowColor);
            new_column_2_date = new TextView(PassengerListActivity.ctx);
            new_column_2_date.setGravity(Gravity.CENTER_HORIZONTAL);
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy");
            formatter.setTimeZone(TimeZone.getDefault());
            String dateString = formatter.format(new Date(passenger.TimeOfPickup * 1000L));
            new_column_2_date.setText(dateString);
            new_column_2_date.setTextColor(getResources().getColor(R.color.dark_graylight));

            new_column_2_date_layout.addView(new_column_2_date);

            // ------------------------ //

            new_column_3_id_layout = new LinearLayout(PassengerListActivity.ctx);
            new_column_3_id_layout.setOrientation(LinearLayout.VERTICAL);
            new_column_3_id_layout.setBackgroundColor(rowColor);
            new_column_3_id = new TextView(PassengerListActivity.ctx);
            new_column_3_id.setGravity(Gravity.CENTER_HORIZONTAL);
            new_column_3_id.setText(String.format("%d", (passenger.TripNumber > 0)
                    ? passenger.TripNumber : passenger.ReservationID));
            if (passenger.TripNumber > 0) {
                float sz = new_column_3_id.getTextSize();
                sz -= 8.0f;
                new_column_3_id.setTextSize(TypedValue.COMPLEX_UNIT_PX, sz);
            }
            new_column_3_id.setTextColor(getResources().getColor(R.color.dark_graylight));

            new_column_3_id_layout.addView(new_column_3_id);

            // ------------------------ //

            new_column_4_fare_layout = new LinearLayout(PassengerListActivity.ctx);
            new_column_4_fare_layout.setOrientation(LinearLayout.VERTICAL);
            new_column_4_fare_layout.setBackgroundColor(rowColor);
            new_column_4_fare = new TextView(PassengerListActivity.ctx);
            String fare = String.format("$%.2f", passenger.EstimateFare);
            new_column_4_fare.setText(fare);
            new_column_4_fare.setGravity(Gravity.CENTER_HORIZONTAL);
            if(fareColor != 0) {
                new_column_4_fare.setTextColor(fareColor);
            } else {
                new_column_4_fare.setTextColor(getResources().getColor(R.color.dark_graylight));
            }

            new_column_4_fare_layout.addView(new_column_4_fare);


                new_column_1_name_layout.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
                new_column_2_date_layout.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
                new_column_3_id_layout.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
                new_column_4_fare_layout.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);

            new_column_1_name_layout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 5));
            new_column_2_date_layout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 3));
            new_column_3_id_layout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2));
            new_column_4_fare_layout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2));


            new_column_1_name_layout.setPadding(paddingBorder, paddingBorder, paddingBorder, paddingBorder);
            new_column_2_date_layout.setPadding(paddingBorder, paddingBorder, paddingBorder, paddingBorder);
            new_column_3_id_layout.setPadding(paddingBorder, paddingBorder, paddingBorder, paddingBorder);
            new_column_4_fare_layout.setPadding(paddingBorder, paddingBorder, paddingBorder, paddingBorder);

            lRow.addView(new_column_1_name_layout);
            lRow.addView(new_column_2_date_layout);
            lRow.addView(new_column_3_id_layout);
            lRow.addView(new_column_4_fare_layout);

            sQuotes_data_table.addView(lRow, new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        }
    }
}
