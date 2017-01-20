package com.rideeaze.views.fragmentactivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rideeaze.R;
import com.rideeaze.gcm.CommonUtilities;
import com.rideeaze.services.network.model.data.HttpAlertPassengerData;
import com.rideeaze.services.network.model.data.HttpPickUp;
import com.rideeaze.services.network.model.request.HttpUpdateResStatusRequest;
import com.rideeaze.services.network.NetworkApi;
import com.rideeaze.services.network.NetworkService;
import com.rideeaze.services.network.model.request.HttpAlertPassenger;
import com.rideeaze.services.network.model.request.HttpGetInvoiceRequest;
import com.rideeaze.services.network.model.request.HttpPickUpRequest;
import com.rideeaze.services.network.model.request.HttpReplyPickupRequest;
import com.rideeaze.services.network.model.response.HttpGetInvoiceData;
import com.rideeaze.services.network.model.response.HttpPendingRequestPickUp;
import com.rideeaze.services.network.model.response.HttpSendReplyPkUpData;
import com.rideeaze.services.network.model.response.JsonResponse;
import com.rideeaze.views.activity.DriverActivity;
import com.rideeaze.views.activity.FareDetailsActivity;
import com.util.Const;
import com.util.ReviewObject;
import com.util.StorageDataHelper;
import com.util.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PassengerActivity extends DriverActivity implements
		OnClickListener {

    public interface DeclinePickupCallback {
        public void onDeclinePuickup();
    }

	private TextView rate;
    private TextView numberOfPassenger;
    private TextView numberOfSuits;
    private TextView formOfPayment;
	private TextView cancel_pickup;
    private TextView notes;
	private Button search;
    private TextView alertPassanger;
    private TextView flightInfo;
	private Button close;
	private Button pay;
	private ImageView route;
	private ImageView routeToPickup;
    private TextView accept_pending;
    private TextView decline_pending;
    private FrameLayout frameLayout;

    private TextView tvFareDetails;

	private RatingBar passengerRating;

	private TextView name;
	private TextView review;
    private TextView reservetionId;
    private TextView pageIndicator;
    private TextView trip_length;
    private TextView pickUpTime;
	private TextView phoneNo;
	private TextView pickupLoc;
	private TextView dropLoc;
	private TextView time;
	private TextView fare;
    private TextView statusView;
    private TextView statusViewPending;
    private TextView vehicleDesc;

	private ImageView btnPassFront;
	private ImageView btnPassPrevious;
	private ImageView btnPassNext;
	private ImageView btnPassEnd;

    private ImageView btnToHome;
    private ImageView btnToList;
    private ImageView btnToPay;

	private Button payUnknownCC;

    private ArrayList<HttpPickUp> currentArray;
    private HttpPickUp pickup;

	private List<HttpPendingRequestPickUp> pickUpArray;
	private int currentItem = 0;
    private int selectedReservationID = 0;
    private int fromPaymentProcess = 0;
    private boolean fromPush;
    private boolean willPaid;
    public boolean requireGNA = false;
    private String passengerNotes;
    public ReviewObject reviewObject;

    public static DeclinePickupCallback declinePickupCallback;

	@Override
	public void onCreate(Bundle arg0) {
        super.onCreate(arg0);
		setContentView(R.layout.activity_passenger);

        getActionBar().setDisplayShowHomeEnabled(false);

		initUI();

        selectedReservationID = getIntent().getIntExtra("searchPassangerIDd", 0);
        fromPush = getIntent().getBooleanExtra("pushFlag", false);
        fromPaymentProcess = getIntent().getIntExtra("fromPaymentProcess", 0);
        willPaid = getIntent().getBooleanExtra("willPaid", false);


		attach_listener();
		requestPickup();
		Const.feeCodeList.clear();
		Const.fare = (float) 0.00;
		Const.paymentTotalAdjstAmnt = (float)0.00;
		Const.paymentSubTotalAmnt = (float)0.00;
		Const.paymentTotalAmnt = (float)0.00;

        if(fromPaymentProcess > 0) {
            showSuccessPaymentDialog();
        }
	}

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        List<HttpPendingRequestPickUp> httpPendingRequestPickUpList = StorageDataHelper.getInstance(getApplicationContext()).getPendingRequestData();

        if(isDataInPendingPickUpList(currentItem)) {
            for(int i=0; i< httpPendingRequestPickUpList.size(); i++)
                if(httpPendingRequestPickUpList.get(i).ReservationStatus.contentEquals("Pending")) {
                    currentItem = i;
                    break;
                }
            loadPassengerInfo(httpPendingRequestPickUpList.get(currentItem));
        }
    }

    public void showSuccessPaymentDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Transaction Approved")
                .setMessage("Receipt email sent to passanger")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

	public void requestPickup() {
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
                        DisplayProcessMessage(false);
                    }

                    @Override
                    public void onNext(JsonResponse<List<HttpPendingRequestPickUp>> listJsonResponse) {
                        if (listJsonResponse.IsSuccess) {
                            StorageDataHelper.getInstance(getApplicationContext()).setPendingRequestData(listJsonResponse.Content);

                            if (isDataInPendingPickUpList()) {
                                if (fromPush) {
                                    currentItem = 0;
                                } else {
                                    if (selectedReservationID != 0) {
                                        for (int i = 0; i < listJsonResponse.Content.size(); i++) {
                                            if (selectedReservationID == listJsonResponse.Content.get(i).ReservationID) {
                                                currentItem = i;
                                                break;
                                            }
                                        }
                                    }
                                }
                                setPageIndicator(currentItem + 1, listJsonResponse.Content.size());
                                loadPassengerInfo(listJsonResponse.Content.get(currentItem));
                            }

                        } else {
                            StorageDataHelper.getInstance(getApplicationContext()).setPendingRequestData(null);
                            Utils.showToastAlert(PassengerActivity.this, "GetPickups failed");
                            pickupLoc.setText("PickupLocation:");
                            dropLoc.setText("DropLocation:");
                            cancel_pickup.setEnabled(false);
                        }
                    }
                });
	}

    public void displayFareDetails() {
        if (!isNetworkAvailable()) return;

        DisplayProcessMessage(getResources().getString(R.string.driver_common_please_wait));

        if(!isDataInPendingPickUpList(currentItem)) {
            return;
        }

        HttpPendingRequestPickUp _data = StorageDataHelper.getInstance(getApplicationContext()).getPendingRequestData().get(currentItem);

        if (_data != null) {
            HttpGetInvoiceRequest requestData = new HttpGetInvoiceRequest();
            requestData.InvoiceID = Const.invoiceId;
            requestData.ReservationID = _data.ReservationID;

            NetworkApi api = (new NetworkService()).getApi();
            api.getInvoiceDetails(requestData)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<JsonResponse<HttpGetInvoiceData>>() {
                        @Override
                        public void onCompleted() {
                            DisplayProcessMessage(true);
                        }

                        @Override
                        public void onError(Throwable e) {
                            DisplayProcessMessage(false);
                        }

                        @Override
                        public void onNext(JsonResponse<HttpGetInvoiceData> httpGetInvoiceDataJsonResponse) {
                            if (httpGetInvoiceDataJsonResponse.IsSuccess && httpGetInvoiceDataJsonResponse.Content != null) {

                                Intent fareDetailsActivity = new Intent(PassengerActivity.this, FareDetailsActivity.class);
                                CommonUtilities.invoiceData = httpGetInvoiceDataJsonResponse.Content;
                                startActivity(fareDetailsActivity);
                            }
                        }
                    });
        }
    }


    public void SendReply(final String token, final String reply, final String PasssengerID, final String recervationId) {
        if(!isNetworkAvailable()) return;

        DisplayProcessMessage(getResources().getString(R.string.driver_common_please_wait));

        final HttpReplyPickupRequest httpReplyPickupRequest = new HttpReplyPickupRequest();
        httpReplyPickupRequest.DriverToken = token;
        httpReplyPickupRequest.PassengerID = PasssengerID;
        httpReplyPickupRequest.Reply = reply;
        httpReplyPickupRequest.ReservationID = recervationId;

        NetworkApi api = (new NetworkService()).getApi();
        api.sendReplyOfPickup(httpReplyPickupRequest)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JsonResponse<HttpSendReplyPkUpData>>() {
                    @Override
                    public void onCompleted() {
                        DisplayProcessMessage(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        DisplayProcessMessage(false);
                    }

                    @Override
                    public void onNext(JsonResponse<HttpSendReplyPkUpData> httpSendReplyPkUpDataJsonResponse) {
                        if(httpSendReplyPkUpDataJsonResponse.IsSuccess && httpSendReplyPkUpDataJsonResponse.Content!=null) {

                            List<HttpPendingRequestPickUp> httpPendingRequestPickUpList = StorageDataHelper.getInstance(getApplicationContext()).getPendingRequestData();

                            if (reply.equals("Accept")) {
                                httpPendingRequestPickUpList.get(currentItem).ReservationStatus = "Active";
                                loadPassengerInfo(httpPendingRequestPickUpList.get(currentItem));
                            } else {
                                httpPendingRequestPickUpList.remove(currentItem);
                                finish();
                            }
                            if (checkPendingresrv()) {
                                showDialog("Pending Request!", "You have another pending reservation, Please accept or Decline.");
                            }
                        }
                    }
                });
    }

    private boolean checkPendingresrv() {
        List<HttpPendingRequestPickUp> pendingRequestPickUpList = StorageDataHelper.getInstance(getApplicationContext()).getPendingRequestData();
        for (int i = 0; i < pendingRequestPickUpList.size(); i++) {
            if (pendingRequestPickUpList.get(i).ReservationStatus.equals("Pending")) {
                return true;
            }
        }
        return false;

    }

	public void initUI() {
		rate = (TextView) findViewById(R.id.rate_passenger);
		cancel_pickup = (TextView) findViewById(R.id.cancelPickup);
        alertPassanger = (TextView) findViewById(R.id.tvAlertPassenger);
        flightInfo = (TextView) findViewById(R.id.flight_info_bnt);
        pageIndicator = (TextView) findViewById(R.id.mytrip_text_main);
		close = (Button) findViewById(R.id.close_accept);
		pay = (Button) findViewById(R.id.payCC);
		route = (ImageView) findViewById(R.id.routeToDest);
        routeToPickup = (ImageView) findViewById(R.id.routeToPickup);
        notes = (TextView) findViewById(R.id.tvNotes);
        tvFareDetails = (TextView) findViewById(R.id.tvFareDetails);

        frameLayout = (FrameLayout) findViewById(R.id.frameLayout);

        statusView = (TextView)findViewById(R.id.statusView);
        statusViewPending = (TextView)findViewById(R.id.statusViewPending);
		btnPassFront = (ImageView) findViewById(R.id.btnPassFront);
		btnPassPrevious = (ImageView) findViewById(R.id.btnPassPrevious);
		btnPassNext = (ImageView) findViewById(R.id.btnPassNext);
		btnPassEnd = (ImageView) findViewById(R.id.btnPassEnd);

		payUnknownCC = (Button) findViewById(R.id.payUnknownCC);

		passengerRating = (RatingBar) findViewById(R.id.passengerRating);
		name = (TextView) findViewById(R.id.passengerName);
		review = (TextView) findViewById(R.id.review);
        reservetionId = (TextView) findViewById(R.id.reservationId_value);
        trip_length = (TextView) findViewById(R.id.trip_length_value);
        pickUpTime = (TextView) findViewById(R.id.pick_up_time_value);
		phoneNo = (TextView) findViewById(R.id.phoneNo);
		pickupLoc = (TextView) findViewById(R.id.pickLocation);
        numberOfPassenger = (TextView) findViewById(R.id.numberOfPassengers);
        numberOfSuits = (TextView) findViewById(R.id.numberOfSuitcases);
        formOfPayment = (TextView) findViewById(R.id.txtFormOfPayment);
		dropLoc = (TextView) findViewById(R.id.dropLocation);
		time = (TextView) findViewById(R.id.time);
		fare = (TextView) findViewById(R.id.fare);

        vehicleDesc = (TextView) findViewById(R.id.vehicle_desc_value);

        accept_pending = (TextView) findViewById(R.id.accept_pending);
        decline_pending = (TextView) findViewById(R.id.decline_pending);

        btnToHome = (ImageView)findViewById(R.id.btn_to_home);
        btnToList  = (ImageView)findViewById(R.id.btn_to_list);
        btnToPay = (ImageView)findViewById(R.id.btn_to_dollars);

	}

    public int getDisabledColor()
    {
        return Color.LTGRAY;
    }

    public int getEnabledColor()
    {
        return getResources().getColor(R.color.dark_graylight);
    }

	public void loadPassengerInfo(final HttpPendingRequestPickUp data) {
		name.setText(data.PassengerName);
		review.setText(String.valueOf(data.ReviewNumber) + "Reviews");
        reservetionId.setText(String.format("%d", (data.TripNumber > 0) ? data.TripNumber : data.ReservationID));
        trip_length.setText(data.Distance.Value + data.Distance.Unit);
        long mills = data.TimeOfPickup;
        mills = mills * 1000;
        pickUpTime.setText(Utils.millisToDate(mills, "h:mm a EEEE, d MMM, yyyy"));
		passengerRating.setRating((float) data.Rating);
		phoneNo.setText(data.PassengerPhoneNumber);

        if (data.VehicleDescription.equals(""))
            vehicleDesc.setText("Not Available");
        else
            vehicleDesc.setText(data.VehicleDescription);

        numberOfPassenger.setText(data.NumberOfPassenger + " Passenger(s)");
        if (data.NumberOfPassSuit > 0)
            numberOfSuits.setText(data.NumberOfPassSuit + " Suitcase(s)");
        else
            numberOfSuits.setText("");
		pickupLoc.setText(data.PickupLocation.location_name);

        if(data.SpecialInstructions !=null & data.SpecialInstructions.length() > 0) {
            notes.setEnabled(true);
            notes.setTextColor(Color.parseColor("#0080ff"));
            passengerNotes = data.SpecialInstructions;
        }
        else {
            notes.setTextColor(getDisabledColor());

            notes.setEnabled(false);
        }
		dropLoc.setText(data.DestinationLocation.location_name);
		time.setText(secondsToHours(data.EstimateTime));
		fare.setText("$" + String.format("%.2f", data.EstimateFare));
        formOfPayment.setText(data.PayCollectMethod.Description);
        Log.d("ReservationStatus", data.ReservationStatus);
        if (data.ReservationStatus.equals("Active") && !data.ReservationSubStatus.equals("")) {
            statusView.setText("Status: " + data.ReservationStatus + " - " + data.ReservationSubStatus);
        } else {
            statusView.setText("Status: " + data.ReservationStatus);
        }
        statusViewPending.setText("Status: " + data.ReservationStatus);

        if(!data.ReservationStatus.toLowerCase().equals("pending"))
        {
            accept_pending.setVisibility(View.INVISIBLE);
            decline_pending.setVisibility(View.INVISIBLE);
        }
        else
        {
            accept_pending.setVisibility(View.VISIBLE);
            decline_pending.setVisibility(View.VISIBLE);
        }

        LinearLayout statusLayout = (LinearLayout)findViewById(R.id.statusViewLayout);
        LinearLayout statusPendingLayout = (LinearLayout)findViewById(R.id.statusPendingLayout);

        final HttpPendingRequestPickUp pickup = data;

        statusLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pickup.ReservationStatus.equals("Active")) {
                    final String[] items;

                    if (pickup.ReservationSubStatus.equals("At Location")) {
                        items = new String[2];
                        items[0] = "In Progress";
                        items[1] = "Completed";
                    } else if (pickup.ReservationSubStatus.equals("In Progress")) {
                        items = new String[1];
                        items[0] = "Completed";
                    } else {
                        items = new String[3];
                        items[0] = "At Location";
                        items[1] = "In Progress";
                        items[2] = "Completed";
                    }

                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(PassengerActivity.this);
                    dialogBuilder.setTitle("Select status");
                    dialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String status = data.ReservationStatus;
                            String subStatus = items[i];

                            if (subStatus.equals("Completed"))
                                status = "Completed";

                            Calendar calendar = GregorianCalendar.getInstance();

                            if(!isNetworkAvailable()) return;

                            HttpUpdateResStatusRequest resStatusRequest = new HttpUpdateResStatusRequest();
                            resStatusRequest.ReservationID = pickup.ReservationID;
                            resStatusRequest.StatusChangeDateTime = Math.round((float)calendar.getTimeInMillis() / 1000.0f);
                            resStatusRequest.ReservationStatus = status;
                            resStatusRequest.ReservationSubStatus = subStatus;

                            final String _status = status;
                            final String _subStatus = subStatus;


                            NetworkApi api = (new NetworkService()).getApi();
                            api.updateReservationStatus(resStatusRequest)
                                    .subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Subscriber<JsonResponse<String>>() {

                                        public void showErrorMsg() {
                                            Toast.makeText(PassengerActivity.this, "Error. Please, try again", Toast.LENGTH_LONG).show();
                                        }

                                        @Override
                                        public void onCompleted() {
                                            DisplayProcessMessage(true);
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            showErrorMsg();
                                            DisplayProcessMessage(false);
                                        }

                                        @Override
                                        public void onNext(JsonResponse<String> stringJsonResponse) {
                                            if (stringJsonResponse.IsSuccess && stringJsonResponse.ResponseCode == 200) {
                                                List<HttpPendingRequestPickUp> httpPendingRequestPickUpList = StorageDataHelper.getInstance(getApplicationContext()).getPendingRequestData();
                                                httpPendingRequestPickUpList.get(currentItem).ReservationStatus = _status;
                                                httpPendingRequestPickUpList.get(currentItem).ReservationSubStatus = _subStatus;

                                                loadPassengerInfo(httpPendingRequestPickUpList.get(currentItem));
                                            } else {
                                                showErrorMsg();
                                            }
                                        }
                                    });
                        }
                    });
                    AlertDialog dialog = dialogBuilder.create();
                    dialog.show();
                }
            }
        });

        //Set default to alert passenger and flight info
        if(data.HaveFlightInfo) {
            flightInfo.setEnabled(false);
            flightInfo.setTextColor(Color.parseColor("#0080ff"));
        }
        else{

            flightInfo.setEnabled(false);
            flightInfo.setTextColor(getDisabledColor());
        }


        route.setEnabled(false);
        route.setBackgroundColor(getDisabledColor());
        routeToPickup.setEnabled(false);
        routeToPickup.setBackgroundColor(getDisabledColor());

        RelativeLayout reviewCancelLayout = (RelativeLayout)findViewById(R.id.reviewCancelLayout);
        RelativeLayout pendingLayout = (RelativeLayout)findViewById(R.id.pendingLayout);
        if(data.ReservationStatus.trim().contentEquals("Cancelled") || data.ReservationStatus.trim().contentEquals("Completed")) {
            statusLayout.setBackgroundColor(getResources().getColor(R.color.passenger_status_canceled));

            if(data.ReservationStatus.trim().contentEquals("Completed")) {
                statusLayout.setBackgroundColor(getResources().getColor(R.color.passenger_status_complete_not_payed));
            }

            reviewCancelLayout.setVisibility(View.VISIBLE);
            frameLayout.setBackgroundColor(getResources().getColor(R.color.dark_graylight));
            pendingLayout.setVisibility(View.GONE);

            rate.setVisibility(View.VISIBLE);
            reviewObject = Utils.getReviewObject(data.ReservationID+"", getApplicationContext());
            if(reviewObject == null) {
                rate.setText("Write Review");
            } else {
                rate.setText("View Review");
            }
            cancel_pickup.setVisibility(View.INVISIBLE);
        } else if(data.ReservationStatus.trim().contentEquals("Pending")) {
            statusPendingLayout.setBackgroundColor(getResources().getColor(R.color.passenger_status_pending));

            pendingLayout.setVisibility(View.VISIBLE);
            reviewCancelLayout.setVisibility(View.GONE);

        } else if(data.ReservationStatus.trim().contentEquals("Active")) {
            statusLayout.setBackgroundColor(getResources().getColor(R.color.passenger_status_active));

            reviewCancelLayout.setVisibility(View.VISIBLE);
            pendingLayout.setVisibility(View.GONE);
            rate.setVisibility(View.INVISIBLE);
            cancel_pickup.setVisibility(View.VISIBLE);
            frameLayout.setBackgroundColor(getResources().getColor(R.color.dark_graylight));

            route.setEnabled(true);
            route.setBackgroundColor(getEnabledColor());
            routeToPickup.setEnabled(true);
            routeToPickup.setBackgroundColor(getEnabledColor());

            //As in ios version. Need check if is in request debugger
            /*if (!pickup.isAirport || !pickup.haveFlightInfo)
            {
                m_flightInfo.enabled = NO;
                m_flightInfo.userInteractionEnabled = NO;
            }
            else
            {
                m_flightInfo.enabled = YES;
                m_flightInfo.userInteractionEnabled = YES;
            }*/

//            flightInfo.setEnabled(true);
//            flightInfo.setTextColor(getEnabledColor());
        }

        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(PassengerActivity.this);
        String ccReader = preferences.getString(Const.SELECTED_CC_READER,"");
        if(ccReader.equalsIgnoreCase(Const.BTMAG_READER)) {
            payUnknownCC.setText(getResources().getString(R.string.new_transaction_btn_label));
        } else {
            payUnknownCC.setText(getResources().getString(R.string.send_email_receipt_btn_label));
        }

        payUnknownCC.setEnabled(data.OKToSendEmailReceipt);

        if(willPaid) {
            willPaid = false;
            btnToPay.performClick();
        }
	}

    private void makeCurrentRequest() {
        currentArray = new ArrayList<HttpPickUp>();
        pickup = new HttpPickUp();

        List<HttpPendingRequestPickUp> httpPendingRequestPickUpList = StorageDataHelper.getInstance(getApplicationContext()).getPendingRequestData();
        HttpPendingRequestPickUp pickupData = httpPendingRequestPickUpList.get(currentItem);
        pickup.UserName = pickupData.PassengerName;
        pickup.Distance = pickupData.Distance;
        pickup.EstimateTime = pickupData.EstimateTime;
        pickup.DestinationLocation = pickupData.DestinationLocation;
        pickup.PhoneNumber = pickupData.PassengerPhoneNumber;
        pickup.ReviewNumber = pickupData.ReviewNumber;
        pickup.PassengerID = pickupData.PassengerID;
        pickup.Rating = pickupData.Rating;

        pickup.EstimateFare = pickupData.EstimateFare;
        pickup.TimeOfPickup = pickupData.TimeOfPickup;
        pickup.NumberOfPassenger = pickupData.NumberOfPassenger;
        pickup.PickupLocation = pickupData.PickupLocation;
        SplashActivityOld.reservationId = (pickupData.ReservationID) + "";
        currentArray.add(pickup);
        CommonUtilities.pickups = currentArray;
    }

	public static String secondsToHours(long seconds) {
        if(seconds/3600 == 0) {
            return String.format("%02d minutes", (seconds % 3600) / 60);
        }
		return String.format("%02d hrs,%02d minutes", seconds / 3600,
				(seconds % 3600) / 60);
	}

	public void attach_listener() {
		rate.setOnClickListener(this);
		cancel_pickup.setOnClickListener(this);
		//search.setOnClickListener(this);
        alertPassanger.setOnClickListener(this);
        close.setOnClickListener(this);
		pay.setOnClickListener(this);
		route.setOnClickListener(this);
		phoneNo.setOnClickListener(this);
		routeToPickup.setOnClickListener(this);
        notes.setOnClickListener(this);
		btnPassFront.setOnClickListener(this);
		btnPassPrevious.setOnClickListener(this);
		btnPassNext.setOnClickListener(this);
		btnPassEnd.setOnClickListener(this);
		payUnknownCC.setOnClickListener(this);
        btnToHome.setOnClickListener(this);
        btnToList.setOnClickListener(this);
        btnToPay.setOnClickListener(this);
        accept_pending.setOnClickListener(this);
        decline_pending.setOnClickListener(this);
        tvFareDetails.setOnClickListener(this);
	}

    public void setPageIndicator(int currentPosition, int size ) {
        pageIndicator.setText(currentPosition + "/" + size);
    }

    public void stopActivity() {
        Handler handler = new Handler();
        handler.post(new Runnable() {
            public void run() {
                finish();
                if (declinePickupCallback != null) {
                    declinePickupCallback.onDeclinePuickup();
                }
            }
        });
    }

    public boolean isDataInPendingPickUpList(int index){
        List<HttpPendingRequestPickUp> httpPendingRequestPickUpList = StorageDataHelper.getInstance(getApplicationContext()).getPendingRequestData();
        if(httpPendingRequestPickUpList != null && httpPendingRequestPickUpList.size()>index)
            return true;

        return false;
    }

    public boolean isDataInPendingPickUpList(){
        List<HttpPendingRequestPickUp> httpPendingRequestPickUpList = StorageDataHelper.getInstance(getApplicationContext()).getPendingRequestData();
        if(httpPendingRequestPickUpList != null && httpPendingRequestPickUpList.size()>0)
            return true;

        return false;
    }

	@Override
	public void onClick(View v) {
        List<HttpPendingRequestPickUp> httpPendingRequestPickUpList = StorageDataHelper.getInstance(getApplicationContext()).getPendingRequestData();

		if (v == rate) {
            makeCurrentRequest();
			Intent intent = new Intent(PassengerActivity.this, PassengerRatingActivity.class);
            if(reviewObject != null) {
                intent.putExtra("reviewObject", reviewObject.convertToString());
            }
			startActivity(intent);
		}

        if( v == btnToHome) {
            Intent intent = new Intent(PassengerActivity.this, SplashActivityOld.class);
            startActivity(intent);
        }

        if( v == btnToList) {
            finish();
        }

		if (v == cancel_pickup) {
            makeCurrentRequest();

            final HttpPendingRequestPickUp pickUp = httpPendingRequestPickUpList.get(currentItem);

            long delta = 1000 * 60 * 60; //Milliseconds
            long beforeDate = pickUp.TimeOfPickup * 1000 - delta;
            long aboveDate = pickUp.TimeOfPickup * 1000 + delta;

            Calendar calendar = GregorianCalendar.getInstance();
            Date now = new Date();
            calendar.setTime(now);
            long nowDate = calendar.getTimeInMillis();

            if ((pickUp.ReservationStatus.equals("Active") && pickUp.ReservationSubStatus.equals("Confirmed")) &&
                    (nowDate < beforeDate || nowDate > aboveDate)) {

                AlertDialog.Builder builder = new AlertDialog.Builder(PassengerActivity.this);
                builder.setTitle("Warning");
                builder.setMessage("This run will be deleted from your trips");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();

                        if(!isNetworkAvailable()) return;

                        DisplayProcessMessage(getResources().getString(R.string.driver_common_please_wait));

                        HttpReplyPickupRequest httpReplyPickupRequest = new HttpReplyPickupRequest();
                        httpReplyPickupRequest.DriverToken = StorageDataHelper.getInstance(getApplicationContext()).getAuthToken();
                        httpReplyPickupRequest.PassengerID = pickUp.PassengerID;
                        httpReplyPickupRequest.Reply = "Decline";
                        httpReplyPickupRequest.ReservationID = pickUp.ReservationID + "";

                        NetworkApi api = (new NetworkService()).getApi();
                        api.sendReplyOfPickup(httpReplyPickupRequest)
                                        .subscribeOn(Schedulers.newThread())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Subscriber<JsonResponse<HttpSendReplyPkUpData>>() {

                                            public void showTryAgainMsg() {
                                                Toast.makeText(
                                                        PassengerActivity.this,
                                                        "Please try again",
                                                        Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onCompleted() {
                                                DisplayProcessMessage(false);
                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                showTryAgainMsg();
                                                DisplayProcessMessage(false);
                                            }

                                            @Override
                                            public void onNext(JsonResponse<HttpSendReplyPkUpData> httpSendReplyPkUpDataJsonResponse) {
                                                if(httpSendReplyPkUpDataJsonResponse.IsSuccess) {
                                                    if(httpSendReplyPkUpDataJsonResponse.ResponseCode != 0) {
                                                        stopActivity();
                                                    } else {
                                                        showTryAgainMsg();
                                                    }
                                                } else {
                                                    showTryAgainMsg();
                                                }
                                            }
                                        });

                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
		}

        if(v == accept_pending) {
            finish();
        }

        if( v == decline_pending) {
            try {
                SendReply(StorageDataHelper.getInstance(getApplicationContext()).getAuthToken(), "Decline",
                        httpPendingRequestPickUpList.get(currentItem).PassengerID,
                        httpPendingRequestPickUpList.get(currentItem).ReservationID + "");
            } catch (Exception e) {
                Toast.makeText(PassengerActivity.this,
                        "Error occurred. Please open this pickup from main screen and try again",
                        Toast.LENGTH_LONG).show();
            }

            //StorageDataHelper.getInstance(getBaseContext()).addDeclinedPendingReservationId(new Integer(selectedReservationID));

            //finish();
        }

        if (v == alertPassanger) {
            sentAlertPassenger();
		}

        if(v == notes){
                showDialog("Comments", passengerNotes.toString());
        }
		if (v == btnToPay) {

			if(isDataInPendingPickUpList())
			{
                HttpPendingRequestPickUp pickUp = httpPendingRequestPickUpList.get(currentItem);

				Const.fare = (float)pickUp.EstimateFare;
				Const.paymentReservationId = pickUp.ReservationID;
				Const.paymentPassengerId = pickUp.PassengerID;
				Const.MerchantId = pickUp.MerchantId;
				Const.IsCaptive = pickUp.IsCaptive;
				Const.IS_KNOWN_PASSENGER = 1;

                Intent intent = new Intent(PassengerActivity.this,EnterAmountActivity.class)
                        .putExtra("fromFlag", 1)
                        .putExtra("psngrName", pickUp.PassengerName);
				startActivity(intent);
			}
		}

		if (v == phoneNo) {
			String phone_number = phoneNo.getText().toString();
			Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone_number));
			startActivity(intent);
		}

		if (v == route) {
            makeCurrentRequest();
			try {
				if (CommonUtilities.pickups != null) {
					double latitude = CommonUtilities.pickups.get(CommonUtilities.pickups.size() - 1).DestinationLocation.latitude;
					double longitude = CommonUtilities.pickups.get(CommonUtilities.pickups.size() - 1).DestinationLocation.longitude;

					Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + latitude + "," + longitude))
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent1);

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (v == routeToPickup) {
            makeCurrentRequest();
			try {

				if (CommonUtilities.pickups != null) {
					double latitude = CommonUtilities.pickups.get(CommonUtilities.pickups.size() - 1).PickupLocation.latitude;
					double longitude = CommonUtilities.pickups.get(CommonUtilities.pickups.size() - 1).PickupLocation.longitude;

					Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + latitude + "," + longitude))
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent1);

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (v == btnPassFront && (isDataInPendingPickUpList())) {
            currentItem = 0;
            setPageIndicator(currentItem+1, httpPendingRequestPickUpList.size());
			loadPassengerInfo(httpPendingRequestPickUpList.get(currentItem));
		}

		if (v == btnPassPrevious && (isDataInPendingPickUpList())) {
			if (currentItem > 0) {
                currentItem -= 1;
                setPageIndicator(currentItem+1, httpPendingRequestPickUpList.size());
				loadPassengerInfo(httpPendingRequestPickUpList.get(currentItem));
			}
		}

		if (v == btnPassNext && (isDataInPendingPickUpList())) {
			if (currentItem < httpPendingRequestPickUpList.size() - 1) {
                currentItem += 1;
                setPageIndicator(currentItem+1, httpPendingRequestPickUpList.size());
				loadPassengerInfo(httpPendingRequestPickUpList.get(currentItem));
			}
		}

		if (v == btnPassEnd && (isDataInPendingPickUpList())) {
			currentItem = httpPendingRequestPickUpList.size() - 1;
            setPageIndicator(currentItem+1, httpPendingRequestPickUpList.size());
			loadPassengerInfo(httpPendingRequestPickUpList.get(currentItem));
		}

		if (v == payUnknownCC) {
			Const.IS_KNOWN_PASSENGER = 0;
			startActivity(new Intent(PassengerActivity.this, EnterAmountActivity.class)
                    .putExtra("fromFlag", 2)
                    .putExtra("psngrName", ""));
		}

        if (v == tvFareDetails) {
            displayFareDetails();
        }

	}

    public void sentAlertPassenger() {
        if (!isNetworkAvailable()) return;

        DisplayProcessMessage(getResources().getString(R.string.driver_common_please_wait));

        HttpAlertPassenger alertPassenger = new HttpAlertPassenger();
        if(!isDataInPendingPickUpList(currentItem)) {
            return;
        }

        alertPassenger.ReservationID = StorageDataHelper.getInstance(getApplicationContext()).getPendingRequestData().get(currentItem).ReservationID + "";

        NetworkApi api = (new NetworkService()).getApi();
        api.alertPassenger(alertPassenger)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JsonResponse<HttpAlertPassengerData>>() {
                    @Override
                    public void onCompleted() {
                        DisplayProcessMessage(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        DisplayProcessMessage(false);
                    }

                    @Override
                    public void onNext(JsonResponse<HttpAlertPassengerData> jsonResponse) {
                        if (jsonResponse.IsSuccess) {
                            alertPassanger.setTextColor(getResources().getColor(R.color.dark_gray));
                            alertPassanger.setEnabled(false);
                        }
                    }
                });
    }

    public void showDialog(String title, String msg) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle(title);

        // set dialog message
        alertDialogBuilder.setMessage(msg).setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
}
