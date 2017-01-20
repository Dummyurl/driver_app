package com.rideeaze.views.fragmentactivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rideeaze.R;
import com.rideeaze.adapter.MenuAdapter;
import com.rideeaze.gcm.CommonUtilities;
import com.rideeaze.services.network.NetworkApi;
import com.rideeaze.services.network.NetworkService;
import com.rideeaze.services.network.model.MenuItemData;
import com.rideeaze.services.network.model.data.AccountAuthDetails;
import com.rideeaze.services.network.model.data.AccountInfoDetails;
import com.rideeaze.services.network.model.data.HttpLocation;
import com.rideeaze.services.network.model.data.HttpVehicle;
import com.rideeaze.services.network.model.request.HttpDriverTokenRequest;
import com.rideeaze.services.network.model.request.HttpPickUpRequest;
import com.rideeaze.services.network.model.request.HttpRegisterNewAccountRequest;
import com.rideeaze.services.network.model.request.HttpReplyPickupRequest;
import com.rideeaze.services.network.model.request.HttpSendStatusCabRequest;
import com.rideeaze.services.network.model.request.HttpUpdatePositionRequest;
import com.rideeaze.services.network.model.response.HttpPendingRequestPickUp;
import com.rideeaze.services.network.model.response.HttpSendReplyPkUpData;
import com.rideeaze.services.network.model.response.JsonResponse;
import com.rideeaze.views.activity.DriverUpdateProfileActivity;
import com.rideeaze.views.drivershedule.DriverSheduleActivity;
import com.util.Const;
import com.util.StorageDataHelper;
import com.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SplashActivityOld extends DriverFragmentActivity implements
		OnClickListener, LocationListener,
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener,
		com.google.android.gms.location.LocationListener,
		OnMenuListener {

	public static int GLOBAL_STATUS = 0;
	static final int REQUEST_GOOGLE_PLAY_SERVICES = 0;
	Timer timer;

	private GoogleMap map;
	public Button passenger;
    public Button myLocation;
	public Button information;
	@Bind(R.id.main_menu_drawerlayout) DrawerLayout mainMenuDrawerLayout;
	@Bind(R.id.menu_listview) ListView menuListView;

	RadioButton off;
	RadioButton busy;
	RadioButton avaliable;

    Button mainCircleBtn;

	public static Location current_location;

	public static String token = "4b36d74fcbb813b67c9d2f72685ea6c6dfca33f5";
	public static String reservationId = "";

	public static int current_vehicle = 0;

	Marker currentPosition = null;

	public static boolean selected_vehicle = false;

	public static boolean googlePlayService = false;

	int REQUEST_VEHICLE = 1;
	int REQUEST_TURNON_LOCATION = 2;

	public static String pushToken = "";
	String TaxiID = "M001";


	String Status = "Off";
	int current_status = 0;


	public boolean isNavigation = false;

	public boolean requireGNA = false;

	public pushReceiver receiver;

	Location old_location = null;
	Context context;

	String regid;

	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";


	private static final String TAG = "SplashActivity";
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	private CountDownTimer countTimer;

	public static boolean locaAlertFlag = false;

	// New For Location
	private GoogleApiClient mLocationGoogleClient;
	private LocationRequest mLocationRequest;

	Dialog dlg;
    GoogleCloudMessaging gcm;
	public boolean Reply(String token, String reply, String passengerId, String recervationId) {

		boolean isSuccess = false;

		HttpReplyPickupRequest httpReplyPickupRequest = new HttpReplyPickupRequest();
		httpReplyPickupRequest.DriverToken = token;
		httpReplyPickupRequest.PassengerID = passengerId;
		httpReplyPickupRequest.Reply = reply;
		httpReplyPickupRequest.ReservationID = recervationId;

		NetworkApi api = (new NetworkService()).getApi();

		try {
			Response<JsonResponse<HttpSendReplyPkUpData>> sendReplyOfPickupResponse = api.sendReplyOfPickupSync(httpReplyPickupRequest).execute();

			HttpSendReplyPkUpData httpSendReplyPkUpData = sendReplyOfPickupResponse.body().Content;

			if (httpSendReplyPkUpData != null) {
				isSuccess = true;
			} else {
				isSuccess = false;
			}
		} catch (Exception e) {
			isSuccess = false;
		}


		return isSuccess;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        getActionBar().hide();

        setContentView(R.layout.main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		ButterKnife.bind(this);

        googlePlayService = checkGooglePlayServicesAvailable();
		receiver = new pushReceiver();


		registerReceiver(receiver, new IntentFilter(
				CommonUtilities.DISPLAY_MESSAGE_ACTION));
		registerReceiver(receiver, new IntentFilter(
				CommonUtilities.DISPLAY_REPLY_ACTION));
		registerReceiver(receiver, new IntentFilter(
				CommonUtilities.DISPLAY_NAVIGATION_ACTION));
		registerReceiver(receiver, new IntentFilter(
				CommonUtilities.REGISTER_DIALOG_ACTION));
		registerReceiver(receiver, new IntentFilter(
				CommonUtilities.LOGIN_ACTION));

		mLocationGoogleClient = new GoogleApiClient.Builder(getApplicationContext())
				.addApi(LocationServices.API)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.build();

		mLocationRequest = LocationRequest.create();
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		mLocationRequest.setInterval(5000);
		mLocationRequest.setFastestInterval(1000);

		if (googlePlayService) {
			loadMap();

			context = getApplicationContext();
			regid = registerGCM();
            Log.d("REGID", regid);

			if ((regid == null) || regid.equals("")) {

				registerReceiver(OnGCMRegistrationFinish, new IntentFilter(
						CommonUtilities.REGISTRATION_FINISH_ACTION));

				try {
					final Intent registrationIntent = new Intent("com.google.android.c2dm.intent.REGISTER");
					// sets the app name in the intent
					registrationIntent.putExtra("app", PendingIntent.getBroadcast(this, 0, new Intent(), 0));
					registrationIntent.putExtra("sender", CommonUtilities.SENDER_ID);
					startService(registrationIntent);
				} catch (Exception e) {

				}
				System.out.println("===========REGISTER GCM:::::::::::");
			}
		} else {
			Log.i(TAG, "No valid Google Play Services APK found.");
		}

		initUI();

		if (!CommonUtilities.message.equals("")) {
			playsound(0);
			if (dlg != null && dlg.isShowing())
				dlg.dismiss();
			showCancelledDialog(CommonUtilities.message);
			CommonUtilities.pickups = null;
		} else {
			updatePosition();
		}


		HttpVehicle vehicle = StorageDataHelper.getInstance(this).getSelectedVehicle();
		if(vehicle != null) {
			selected_vehicle = true;
			current_vehicle = vehicle.getCarSmallImg();
		}

		token =  SplashActivityOld.getToken(SplashActivityOld.this);
		
		if (current_vehicle != 0 && googlePlayService
				&& current_location != null) {
			show_vehicle(current_vehicle);
		}

		if(getIntent().getBooleanExtra(Const.EXTRA_INTENT_LOGIN_BEFORE,false)) {
			requestGetPendingPickup();
		}

		TextView tvVersion = (TextView)findViewById(R.id.tvVersion);
		try {
			tvVersion.setText(String.format("Version %s",
					getPackageManager().getPackageInfo(getPackageName(), 0).versionName));
		} catch (PackageManager.NameNotFoundException ex) {
			ex.printStackTrace();
		}

		setupLeftMenu();
	}

	public void setupLeftMenu() {
		mainMenuDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		updateMenuListAdapter();
	}

	public void updateMenuListAdapter() {
		List<MenuItemData> listOfMenuItems = new ArrayList<MenuItemData>();
		listOfMenuItems.add(new MenuItemData("UPDATE MY VEHICLE", MenuItemData.MenuItemID.UPDATE_MY_VEHICLE, true));
		listOfMenuItems.add(new MenuItemData("UPDATE MY PROFILE", MenuItemData.MenuItemID.UPDATE_MY_PROFILE, true));
		listOfMenuItems.add(new MenuItemData("VIEW/MODIFY TIME OFF", MenuItemData.MenuItemID.VIEW_OR_MODIFY_TIME_OFF, true));

		MenuItemData itemSwitch = null;
		if(StorageDataHelper.getInstance(this).isUseWakeUpCall()) {
			itemSwitch = new MenuItemData("WAKE UP CALL", MenuItemData.MenuItemID.WAKE_UP_CALL_OFF, true);
		} else {
			itemSwitch = new MenuItemData("WAKE UP CALL", MenuItemData.MenuItemID.WAKE_UP_CALL_OFF, false);
		}
		itemSwitch.setIsShowSwitch(true);
		listOfMenuItems.add(itemSwitch);
		listOfMenuItems.add(new MenuItemData("PAYMENTS", MenuItemData.MenuItemID.PAYMENTS, true));
		listOfMenuItems.add(new MenuItemData("LOG OFF", MenuItemData.MenuItemID.LOGOFF, true));

		menuListView.setAdapter(new MenuAdapter(this, listOfMenuItems, this));
		menuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				MenuItemData.MenuItemID action = (MenuItemData.MenuItemID)view.getTag();
				if(action == MenuItemData.MenuItemID.UPDATE_MY_VEHICLE) {
					Intent intent = new Intent(SplashActivityOld.this, VehicleActivityNew.class);
					startActivity(intent);
				} else if(action == MenuItemData.MenuItemID.UPDATE_MY_PROFILE) {
					openProfilePage();
				} else if(action == MenuItemData.MenuItemID.VIEW_OR_MODIFY_TIME_OFF) {
					Intent intent = new Intent(SplashActivityOld.this, DriverSheduleActivity.class);
					startActivity(intent);
				} else if(action == MenuItemData.MenuItemID.WAKE_UP_CALL_OFF) {
					//openProfilePage();
				} else if(action == MenuItemData.MenuItemID.PAYMENTS) {
					Intent intent = new Intent(SplashActivityOld.this, PassengerListActivity.class);
					startActivity(intent);
				} else if(action == MenuItemData.MenuItemID.LOGOFF) {
					Intent intent = new Intent(SplashActivityOld.this, SettingActivity.class);
					intent.putExtra(Const.LOG_OUT_FROM_ACCOUNT, true);
					startActivity(intent);
				}
				mainMenuDrawerLayout.closeDrawers();
			}
		});
	}

	@Override
	public void onMenuSwitchChanged(boolean isChecked, MenuItemData.MenuItemID id) {
		// run script
		updateDriverProfile(isChecked);
	}

	public void updateDriverProfile(boolean isChecked) {
			if (!isNetworkAvailable()) return;

			final AccountInfoDetails accountInfoDetails = StorageDataHelper.getInstance(getBaseContext()).getAccountInfoDetails();
			accountInfoDetails.UseWakeUpCall = isChecked;

			DisplayProcessMessage(getResources().getString(R.string.driver_common_please_wait));

			NetworkApi api = (new NetworkService()).getApi();
			api.updateUserInfo(StorageDataHelper.getInstance(getBaseContext()).getAuthToken(),accountInfoDetails)
					.subscribeOn(Schedulers.newThread())
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(new Subscriber<JsonResponse<String>>() {
						@Override
						public void onCompleted() {
							DisplayProcessMessage(false);
							updateMenuListAdapter();
						}

						@Override
						public void onError(Throwable e) {
							DisplayProcessMessage(false);
						}

						@Override
						public void onNext(JsonResponse<String> stringJsonResponse) {
							if (stringJsonResponse.IsSuccess) {
								DisplayStringMessage("Update Driver Info Success!");
								StorageDataHelper.getInstance(getApplicationContext()).setAccountInfoDetails(accountInfoDetails);
							} else {
								DisplayStringMessage("UpdateDriver Info failed");
							}
						}
					});
	}

	public void openProfilePage() {
		Intent intent = new Intent(SplashActivityOld.this, DriverUpdateProfileActivity.class);
		startActivity(intent);
	}

	@OnClick(R.id.menuBtn)
	public void openMenu() {
		Intent intent = new Intent(SplashActivityOld.this, PassengerListActivity.class);
		startActivity(intent);
	}

	@OnClick(R.id.setting)
	public void openSetting() {
		mainMenuDrawerLayout.openDrawer(Gravity.START);
	}

	private Boolean exit = false;
	@Override
	public void onBackPressed() {
		if (exit) {
			finish(); // finish activity
		} else {
			Toast.makeText(this, "Press Back again to Exit.",
					Toast.LENGTH_SHORT).show();
			exit = true;
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					exit = false;
				}
			}, 3 * 1000);

		}

	}

    public String registerGCM() {

        gcm = GoogleCloudMessaging.getInstance(this);
        regid = getRegistrationId(context);

        if (TextUtils.isEmpty(regid)) {

            registerInBackground();

            Log.d("RegisterActivity",
                    "registerGCM - successfully registered with GCM server - regId: "
                            + regid);
        } else {
            Log.d("RegisterActivity",
					"RegId already available. RegId: " + regid);
        }
        return regid;
    }

    public static String getToken(Context ctx) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        return preferences.getString("token", "");
    }

    public static String getTaxiID(Context ctx) {
        HttpVehicle vehicle = StorageDataHelper.getInstance(ctx).getSelectedVehicle();
		if(vehicle != null ) {
			return vehicle.VehicleNumber;
		}

		return "";
    }

    public static int getCurrebtVehicleSmallImg(Context ctx) {
		HttpVehicle vehicle = StorageDataHelper.getInstance(ctx).getSelectedVehicle();
		if(vehicle != null) {
			return vehicle.getCarSmallImg();
		}

		return  R.drawable.car;

    }

	/**
	 * Gets the current registration ID for application on GCM service.
	 * <p>
	 * If result is empty, the app needs to register.
	 * 
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	public static String getRegistrationId(Context context) {
		final SharedPreferences prefs = getGCMPreferences(context);
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		if (registrationId.length() == 0) {
			Log.i(TAG, "Registration not found.");
			return "";
		}
		// Check if app was updated; if so, it must clear the registration ID
		// since the existing regID is not guaranteed to work with the new
		// app version.
		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION,
				Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			Log.i(TAG, "App version changed.");
			return "";
		}
		return registrationId;
	}

	// GCM BrodCasrRegisteration UnRegister here when Registration Finish
	private BroadcastReceiver OnGCMRegistrationFinish = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			try {
				unregisterReceiver(OnGCMRegistrationFinish);
			} catch (Exception e) {
			}
			System.out.println("Value of GCM Inside CALL TOKEN::");

		}
	};

	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private static SharedPreferences getGCMPreferences(Context context) {
		// This sample app persists the registration ID in shared preferences,
		// but
		// how you store the regID in your app is up to you.
		return context.getSharedPreferences(
				SplashActivityOld.class.getSimpleName(), Context.MODE_PRIVATE);
	}

	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	public static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and app versionCode in the application's
	 * shared preferences.
	 */
	private void registerInBackground() {
		new AsyncTask<Void, Void, String>() {

			@Override
			protected void onPreExecute() {
				showProcessingDialog("Getting PushToken....");
				super.onPreExecute();
			}

			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {

					if (gcm == null) {
					    gcm = GoogleCloudMessaging.getInstance(context);
					}
					regid = gcm.register(CommonUtilities.SENDER_ID);
					msg = "Device registered, registration ID=" + regid;

					System.out.println("===========GCM TOKEN:::::" + regid);
					storeRegistrationId(context, regid);
				} catch (Exception ex) {
                    msg = "Error :" + ex.getMessage();
                    Log.d("RegisterActivity", "Error: " + msg);
                }
                Log.d("RegisterActivity", "AsyncTask completed: " + msg);
				return msg;
			}

			@Override
			protected void onPostExecute(String result) {
				hideProcessingDialog();
                //Toast.makeText(getApplicationContext(),"Registered with GCM Server." + result, Toast.LENGTH_LONG).show();
			};

		}.execute(null, null, null);

	}

	/**
	 * Stores the registration ID and app versionCode in the application's
	 * {@code SharedPreferences}.
	 * 
	 * @param context
	 *            application's context.
	 * @param regId
	 *            registration ID
	 */
	public static void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getGCMPreferences(context);
		int appVersion = getAppVersion(context);
		Log.i(TAG, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regId);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		editor.commit();
	}

	public void updatePosition() {
		//Intent intent = new Intent(SplashActivityOld.this, UpdatePositionService.class);
		//startService(intent);

	}

	@Override
	protected void onDestroy() {
		ButterKnife.unbind(this);
		//stopService(new Intent(this, UpdatePositionService.class));
		stopUpdatingPosition();
		try {
			unregisterReceiver(receiver);
			unregisterReceiver(OnGCMRegistrationFinish);
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onDestroy();
	}

	/*@Override
	public void onBackPressed() {
		finish();
		super.onBackPressed();
	}*/

	private boolean checkGooglePlayServicesAvailable() {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.i(TAG, "This device is not supported.");
				finish();
			}
			return false;
		}
		return true;

	}

	public void updateStatusVariables() {
		if(GLOBAL_STATUS == 0 || GLOBAL_STATUS == 1) {
			current_status = 0;
			Status = "Off";
			mainCircleBtn.setBackgroundResource(R.drawable.main_circle_minus_clear_off);
		} else if(GLOBAL_STATUS == 2) {
			current_status = 2;
			Status = "Available";
			mainCircleBtn.setBackgroundResource(R.drawable.main_circle_payment_clear_on);
		}
	}

	void doUpdatePosition() {
		AccountAuthDetails accountAuthDetails = StorageDataHelper.getInstance(getApplicationContext()).getAccountAuthDetails();

		if (SplashActivityOld.current_location != null && !accountAuthDetails.token.equals("") && Utils.isOnline(getApplicationContext())) {
			System.out.println("==============TOKEN:UpdateLocation::::::::::"+ accountAuthDetails.token);
			System.out.println("==============TOKEN:UpdateLocation LAtitude::::::::::"+ SplashActivityOld.current_location.getLatitude());
			System.out.println("==============TOKEN:UpdateLocation Longitude::::::::::"+ SplashActivityOld.current_location.getLongitude());

			HttpUpdatePositionRequest updatePositionRequest = new HttpUpdatePositionRequest();
			updatePositionRequest.DriverToken = accountAuthDetails.token;
			updatePositionRequest.Location = new HttpLocation();
			updatePositionRequest.Location.latitude = SplashActivityOld.current_location.getLatitude();
			updatePositionRequest.Location.longitude = SplashActivityOld.current_location.getLongitude();


			NetworkApi api = (new NetworkService()).getApi();
			api.sendUpdatedPosition(updatePositionRequest)
					.subscribeOn(Schedulers.newThread())
					.observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<JsonResponse<String>>() {
				@Override
				public void onCompleted() {

				}

				@Override
				public void onError(Throwable e) {
					GLOBAL_STATUS = 0;
					updateStatusVariables();
					stopUpdatingPosition();
				}

				@Override
				public void onNext(JsonResponse<String> stringJsonResponse) {
					if (stringJsonResponse.ResponseCode == 500) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivityOld.this);
								builder.setTitle("Error");
								builder.setMessage("Please select your vehicle in settings.");
								builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialogInterface, int i) {
										dialogInterface.cancel();
									}
								});
								AlertDialog dialog = builder.create();
								dialog.show();
								//
								GLOBAL_STATUS = 0;
								updateStatusVariables();
								stopUpdatingPosition();
							}
						});
					}
				}
			});

			Log.e("CurrentLocation", String.valueOf(SplashActivityOld.current_location.getLatitude()) + "," + String.valueOf(SplashActivityOld.current_location.getLongitude()));
		}
	}

	void startUpdatingPosition() {
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				doUpdatePosition();
			}
		}, 0, 30000);
	}

	void stopUpdatingPosition() {
		try {
			timer.cancel();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void updateStatus() {
		if (!isNetworkAvailable()) return;

		updateStatusVariables();

		DisplayProcessMessage(getResources().getString(R.string.driver_common_please_wait));

		HttpSendStatusCabRequest sendStatusRequest = new HttpSendStatusCabRequest();
		sendStatusRequest.DriverToken = token;
		sendStatusRequest.TaxiID = TaxiID;
		sendStatusRequest.Status = Status;

		NetworkApi api = (new NetworkService()).getApi();
		api.sendStatusCab(sendStatusRequest)
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
					}

					@Override
					public void onNext(JsonResponse<String> stringJsonResponse) {
						if(stringJsonResponse.IsSuccess && !stringJsonResponse.Content.equals("")) {
							DisplayMessage("Updating Status Success");
							startUpdatingPosition();
						} else {
							DisplayMessage("Updating Status failed");
						}
					}
				});
	}

	void showGooglePlayServicesAvailabilityErrorDialog(
			final int connectionStatusCode) {
		runOnUiThread(new Runnable() {
			public void run() {
				Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
						connectionStatusCode, SplashActivityOld.this,
						REQUEST_GOOGLE_PLAY_SERVICES);
				dialog.show();
			}
		});
	}

	public void initUI() {
		passenger = (Button) findViewById(R.id.acceptCall);
		passenger.setOnClickListener(this);

//		setting = (Button) findViewById(R.id.setting);
//		setting.setOnClickListener(this);

		information = (Button) findViewById(R.id.information);
		information.setOnClickListener(this);

        myLocation = (Button) findViewById(R.id.indicator);
		myLocation.setOnClickListener(this);

        mainCircleBtn = (Button) findViewById(R.id.circle);
        mainCircleBtn.setOnClickListener(this);

		off = (RadioButton) findViewById(R.id.radio_off);
		off.setOnClickListener(this);

		busy = (RadioButton) findViewById(R.id.radio_Busy);
		busy.setOnClickListener(this);

		avaliable = (RadioButton) findViewById(R.id.radio_avaliable);
		avaliable.setOnClickListener(this);

	}

	@Override
	protected void onStart() {
		mLocationGoogleClient.connect();
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
		try {
			LocationServices.FusedLocationApi.removeLocationUpdates(mLocationGoogleClient, this);
			mLocationGoogleClient.disconnect();
		}catch (Exception e) {}
	}

	public void requestGetPendingPickup() {
		if(!isNetworkAvailable()) return;

		HttpPickUpRequest httpPickUpRequest = new HttpPickUpRequest();
		httpPickUpRequest.DriverToken = StorageDataHelper.getInstance(getApplicationContext()).getAuthToken();

		NetworkApi api = (new NetworkService()).getApi();
		api.getPickupsForDriver(httpPickUpRequest)
				.subscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Subscriber<JsonResponse<List<HttpPendingRequestPickUp>>>() {
					@Override
					public void onCompleted() {

					}

					@Override
					public void onError(Throwable e) {
						StorageDataHelper.getInstance(getApplicationContext()).setPendingRequestData(null);
					}

					@Override
					public void onNext(JsonResponse<List<HttpPendingRequestPickUp>> listJsonResponse) {
						if (listJsonResponse.IsSuccess) {
							StorageDataHelper.getInstance(getApplicationContext()).setPendingRequestData(listJsonResponse.Content);
						} else {
							StorageDataHelper.getInstance(getApplicationContext()).setPendingRequestData(null);
						}
					}
				});
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (GLOBAL_STATUS == 2) {
			updateStatusVariables();
			updateStatus();
		} else {
			if (CommonUtilities.appBackgroung == true) {
				showPendingRequestAlert(SplashActivityOld.this);
				CommonUtilities.appBackgroung = false;
			} else {
				requestCabStatus();
			}
		}
		checkGooglePlayServicesAvailable();

        TaxiID = getTaxiID(SplashActivityOld.this);
        current_vehicle = getCurrebtVehicleSmallImg(SplashActivityOld.this);

        show_vehicle(current_vehicle);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

	}

    public void showSimpleInfoMessage() {
        Intent intent = new Intent(SplashActivityOld.this, PaymentActivityBT.class);
        startActivity(intent);
    }

	@Override
	public void onClick(View v) {
		if (v == passenger) {
			Intent intent = new Intent(SplashActivityOld.this, PassengerListActivity.class);
			startActivity(intent);
		}

		if (v == information) {
			try {
//				if (CommonUtilities.pendingreauestData != null && CommonUtilities.pendingreauestData.size() > 0) {
//					PassengerListActivity.pickUpArray = CommonUtilities.pendingreauestData;
//				}
//
//				int index = 0;
//				if (PassengerListActivity.pickUpArray != null && PassengerListActivity.pickUpArray.size() > 0) {
//					for (int i = 0; i < PassengerListActivity.pickUpArray.size(); i++) {
//						if (PassengerListActivity.pickUpArray.get(i).ReservationStatus.contentEquals("Active")) {
//							index = i;
//							break;
//						}
//					}
//				}

				Intent intent = new Intent(SplashActivityOld.this, PassengerActivity.class);
//				intent.putExtra("searchPassangerIDd", PassengerListActivity.pickUpArray.get(0).ReservationID);
				startActivity(intent);
			} catch (Exception e) {

			}
		}

//		if (v == setting) {
//            Intent intent = new Intent(SplashActivityOld.this, SettingActivity.class);
//            startActivity(intent);
//		}

        if (v == myLocation) {

			gotoCurrentPosition(true);
		}

        if (v == mainCircleBtn) {

            if (GLOBAL_STATUS == 0 || GLOBAL_STATUS == 1) {
                GLOBAL_STATUS = 2;
                mainCircleBtn.setBackgroundResource(R.drawable.main_circle_payment_clear_on);
			} else if (GLOBAL_STATUS == 2) {
                GLOBAL_STATUS = 0;
                mainCircleBtn.setBackgroundResource(R.drawable.main_circle_minus_clear_off);
			}

            if((GLOBAL_STATUS == 2) && !selected_vehicle){
				Intent intent = new Intent(SplashActivityOld.this, VehicleActivityNew.class);
				intent.putExtra(Const.NO_SELECTED_VEHICLE, true);
				startActivity(intent);
            } else {
                updateStatus();
            }
        }
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == REQUEST_VEHICLE) {
			if (resultCode == RESULT_OK) {

				/*current_vehicle = data.getIntExtra("CurrentCar", R.drawable.car);
                TaxiID = data.getStringExtra("TaxiID");
				if (current_vehicle != 0 && current_location != null) {
					show_vehicle(current_vehicle);
				}

				SharedPreferences preferences = PreferenceManager
						.getDefaultSharedPreferences(SplashActivityOld.this);
				SharedPreferences.Editor editor = preferences.edit();
				editor.putInt("current_vehicle", current_vehicle);
				editor.commit();
                */
				//makeNewVehicle(data);
				//if (!selected_vehicle) {
					// updateStatus();
				//}

				selected_vehicle = true;

			}
		}

		if (requestCode == REQUEST_TURNON_LOCATION) {
			locaAlertFlag = true;
			Intent home = new Intent(SplashActivityOld.this, SplashActivityOld.class);
			home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(home);
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	public void loadMap() {
		FragmentManager myFragmentManager = getSupportFragmentManager();
		SupportMapFragment mySupportMapFragment = (SupportMapFragment) myFragmentManager
				.findFragmentById(R.id.map);

		map = mySupportMapFragment.getMap();

		if (map != null) {
			map.setMyLocationEnabled(true);
			map.getUiSettings().setMyLocationButtonEnabled(false);
			   //map.setIndoorEnabled(true);
            map.getUiSettings().setZoomControlsEnabled(false);
            map.getUiSettings().setZoomGesturesEnabled(true);

			// replace of the zoom control to the left,top of the screen

			View zoomControls = mySupportMapFragment.getView().findViewById(0x1);

			if (zoomControls != null
					&& zoomControls.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
				// ZoomControl is inside of RelativeLayout
				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) zoomControls
						.getLayoutParams();

				// Align it to - parent top|left
				params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

				// Update margins, set to 10dp
				final int margin = (int) TypedValue.applyDimension(
						TypedValue.COMPLEX_UNIT_DIP, 10, getResources()
								.getDisplayMetrics());
				params.setMargins(margin, 90, margin, margin);

				LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

				// Creating a criteria object to retrieve provider
				Criteria criteria = new Criteria();

				boolean statusOfGPS = locationManager
						.isProviderEnabled(LocationManager.GPS_PROVIDER);
				boolean statusOfwifi = locationManager
						.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

				if (statusOfGPS && statusOfwifi) {
					// Getting the name of the best provider
					String provider = locationManager.getBestProvider(criteria,
							true);

					// Getting Current Location

					current_location = locationManager
							.getLastKnownLocation(provider);

					// current_location = new Location(provider);
					// current_location.setLatitude(current_location.getLatitude());
					// current_location.setLongitude(current_location.getLongitude());

					    //old_location = current_location;

					if (current_location != null) {
						  gotoCurrentPosition(false);
					}

					/*
					 * else { Location
					 * wifi_loc=locationManager.getLastKnownLocation(
					 * LocationManager.NETWORK_PROVIDER); if(wifi_loc!=null) {
					 * current_location=wifi_loc; gotoCurrentPosition(false); }
					 * }
					 */

					// locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
					// 1000, 1.0f, this);

					locationManager.requestLocationUpdates(provider, 1000, 1.0f, this);

				} else {
					if (locaAlertFlag == false) {
						showLocationAlertDialog();
					} else {
						showLocationAlertResultDialog();
					}

				}
			}

			else {

				Toast.makeText(
						this,
						"An error occured loading Google Map. Please retry Again!",
						Toast.LENGTH_SHORT).show();
			}

		}

	}

	public void showCancelledDialog(String msg) {

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		// set title
		alertDialogBuilder.setTitle("HereByGPS");

		// set dialog message
		alertDialogBuilder
				.setMessage(
						"Please ignore request...Passenger has cancelled pickup.")
				.setCancelable(false)
				.setPositiveButton("Close",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

								CommonUtilities.message = "";
								dialog.cancel();

							}
						});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}

	public void gotoCurrentPosition(boolean user) {

		if (current_location != null) {
			if (user) {
				map.animateCamera(CameraUpdateFactory.newLatLngZoom(
						new LatLng(current_location.getLatitude(),
								current_location.getLongitude()), map
								.getCameraPosition().zoom));
			}

			else {
				map.animateCamera(CameraUpdateFactory.newLatLngZoom(
						new LatLng(current_location.getLatitude(),
								current_location.getLongitude()), 15));

			}

			// isRotateEnable=true;
		}

	}

	public void show_vehicle(int vehicle_image) {
        if(current_location != null) {
            map.clear();
            currentPosition = map.addMarker(new MarkerOptions().position(
                    new LatLng(current_location.getLatitude(), current_location
                            .getLongitude())).icon(
                    BitmapDescriptorFactory.fromResource(vehicle_image)));
        }

	}

	@Override
	public void onLocationChanged(Location location) {

		current_location = location;
		show_vehicle(current_vehicle);

		if (old_location == null)
			old_location = location;
		else {
			double delta_latitude = old_location.getLatitude()
					- current_location.getLatitude();
			double delta_longitude = old_location.getLongitude()
					- current_location.getLongitude();

			if (Math.abs(delta_latitude) > 0.001
					|| Math.abs(delta_longitude) > 0.001) {
				gotoCurrentPosition(true);
				old_location = current_location;
			}

		}

	}

	protected Handler handler = new Handler();

	public void DisplayMessage(final String msg) {
		final Context context = getApplicationContext();
		handler.post(new Runnable() {
			public void run() {
				try {
					int duration = Toast.LENGTH_LONG;
					Toast.makeText(context, msg, duration).show();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
	}



	public class pushReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

            Log.d("PUSH_NOTIF","GET PUSH NOTIF");

			if (intent.getAction().equals(
					CommonUtilities.DISPLAY_MESSAGE_ACTION)) {
				String message = intent.getStringExtra("message");
				reservationId = intent.getStringExtra("rsrvid");
				String msgCode = intent.getStringExtra("msgcod");

                Log.d("PUSH_NOTIF","GET PUSH NOTIF " +msgCode+" "+reservationId+" "+message);
				if (msgCode.equals("66")) {
					playsound(1);
//					requestGetPickup();
					//startActivity(new Intent(SplashActivityOld.this,PassengerActivity.class).putExtra("pushFlag", true));
					startActivity(new Intent(SplashActivityOld.this, PassengerListActivity.class));
					
				} else {

					playsound(0);
					if (dlg != null && dlg.isShowing()) {
						dlg.dismiss();
					}
					showCancelledDialog(message);

				}
			}

			if (intent.getAction().equals(CommonUtilities.DISPLAY_REPLY_ACTION)) {
				// showConfirmDialog();
				System.out.println("========DISPLAY REPLY::::::::");

				startActivity(new Intent(SplashActivityOld.this,PassengerActivity.class).putExtra("pushFlag", true));
			}

			if (intent.getAction().equals(
					CommonUtilities.DISPLAY_NAVIGATION_ACTION)) {
				if (CommonUtilities.pickups != null && requireGNA) {
					// double latitude = CommonUtilities.pickups
					// .get(CommonUtilities.pickups.size() -
					// 1).PickupLocation.latitude;
					// double longitude = CommonUtilities.pickups
					// .get(CommonUtilities.pickups.size() -
					// 1).PickupLocation.longitude;
					//
					// Intent intent1 = new Intent(Intent.ACTION_VIEW,
					// Uri.parse("google.navigation:q=" + latitude + ","
					// + longitude));
					// // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					// startActivity(intent1);

					startActivity(new Intent(SplashActivityOld.this,
							PassengerActivity.class));

					isNavigation = true;
				}
			}

		}

	}

	private void playsound(int flag) {

		if (flag == 1) {
			countTimer = new CountDownTimer(90 * 1000, 15 * 1000) {

				public void onTick(long millisUntilFinished) {
					MediaPlayer mPlayer = MediaPlayer.create(
							SplashActivityOld.this, R.raw.dings);
					mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
					mPlayer.start();
				}

				public void onFinish() {

				}
			}.start();
		} else {
			if (countTimer != null) {
				countTimer.cancel();
				countTimer = null;
			}
		}

	}


	public void requestCabStatus() {
		if (!isNetworkAvailable()) return;

		HttpDriverTokenRequest httpDriverTokenRequest = new HttpDriverTokenRequest();
		httpDriverTokenRequest.DriverToken = StorageDataHelper.getInstance(getApplicationContext()).getAuthToken();

		NetworkApi api = (new NetworkService()).getApi();
		api.getStatusCab(httpDriverTokenRequest)
				.subscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Subscriber<JsonResponse<String>>() {
					@Override
					public void onCompleted() {

					}

					@Override
					public void onError(Throwable e) {

					}

					@Override
					public void onNext(JsonResponse<String> stringJsonResponse) {
						if(stringJsonResponse.IsSuccess) {
							String status = stringJsonResponse.Content;
							if (status != null && !status.equals("")) {
								updtStatus(status);
							}
						}
					}
				});
	}

	private void updtStatus(String status) {
		if (status.equals("Offline") || status.equals("Busy")) {
			GLOBAL_STATUS = 0;
			updateStatusVariables();
		} else if (status.equals("Available")) {
            GLOBAL_STATUS = 2;
            updateStatusVariables();
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	public boolean isConnectingToInternet() {
		ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
		}
		return false;
	}

	public void showLocationAlertDialog() {
	}

	public void showPendingRequestAlert(Context context) {

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);

		// set title
		alertDialogBuilder.setTitle("Pending request");

		// set dialog message
		alertDialogBuilder
				.setMessage(
						"You have pending pickup requests. Please go to Details to review.")
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						dialog.cancel();
					}
				});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}

	public void showLocationAlertResultDialog() {

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		// set title
		alertDialogBuilder.setTitle(null);

		// set dialog message
		alertDialogBuilder
				.setMessage("Acquiring GPS signal, please wait.")
				.setCancelable(false)
				.setPositiveButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								locaAlertFlag = false;
								dialog.cancel();
								finish();
							}
						});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {

	}

	@Override
	public void onConnected(Bundle arg0) {
		current_location = LocationServices.FusedLocationApi.getLastLocation(mLocationGoogleClient);
		if (current_location != null) {

			gotoCurrentPosition(false);

		}
		LocationServices.FusedLocationApi.requestLocationUpdates(mLocationGoogleClient, mLocationRequest, this);
	}

	@Override
	public void onConnectionSuspended(int i) {
		Log.e(TAG, "Disconnected. Please re-connect.");
	}


	@OnClick(R.id.callOfficeBtn)
	public void callToOffice() {
		String companyPhoneNumber = StorageDataHelper.getInstance(this).getAccountAuthDetails().CompanyPhoneNumber;
		if(companyPhoneNumber != null && !companyPhoneNumber.isEmpty()) {
			Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + companyPhoneNumber));
			startActivity(intent);
		} else {
			Toast.makeText(this, "Can't found company number. Please reauth", Toast.LENGTH_SHORT).show();
		}
	}

}