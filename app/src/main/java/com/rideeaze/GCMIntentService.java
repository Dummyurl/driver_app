package com.rideeaze;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.rideeaze.views.fragmentactivity.AddTipActivity;
import com.rideeaze.views.fragmentactivity.SplashActivityOld;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.util.Const;
import com.util.Pref;

import java.io.IOException;

import static com.rideeaze.gcm.CommonUtilities.SENDER_ID;
import static com.rideeaze.gcm.CommonUtilities.displayMessage;

//import com.google.android.gcm.GCMRegistrar;

//import com.google.android.gcm.GCMRegistrar;

/**
 * {@link IntentService} responsible for handling GCM messages.
 */
public class GCMIntentService extends IntentService {
	public static String TAG = "GCMIntentService";
	static int cur_count;
	static int update_count;
	public static String action = "";

    GoogleCloudMessaging gcm;
    String regid;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";

	// GcmTokenId
	public GCMIntentService() {
		super(SENDER_ID);
        Log.d(TAG, "GCMIntentService start");
	}

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate start");
        onRegistered();
    }


    private boolean checkPlayServices() {
        /*int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if (resultCode != ConnectionResult.SUCCESS) {
            return false;
        }*/
        return true;
    }

    protected void onRegistered() {
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(getBaseContext());
            Log.d(TAG, "checkPlayServices true");
        } else {
            Log.d(TAG, "checkPlayServices false");
        }

        if (regid.isEmpty()) {
            registerInBackground();
        }
    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getBaseContext());
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    android.util.Log.d(TAG, "=======================================");
                    android.util.Log.d(TAG, "Device registered: regId = " + regid);
                    android.util.Log.d(TAG, "=======================================");
                    Log.d(TAG, "Pref.setValue(");
                    Pref.setValue(getApplicationContext(), Const.GCM_TOKEN, regid);

                    System.out.println("=============ON GCM REGISTERED::::::::::");
                    Log.e(TAG, "Device registered: regId = " + regid);

                    Log.d(TAG, msg);
                    storeRegistrationId(getBaseContext(), regid);
                } catch (IOException ex) {
                    msg = "Error onRegistered :" + ex.getMessage();
                    android.util.Log.d(TAG, msg);
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }

                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                //mDisplay.append(msg + "\n");
                Log.d(TAG, msg);
            }
        }.execute();
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(SplashActivityOld.class.getSimpleName(),Context.MODE_PRIVATE);
    }

    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (Exception e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

	/*// register GCM
	protected void onRegistered(Context context, String registrationId) {
		// sendBroadcast(new
		// Intent(CommonUtilities.REGISTRATION_FINISH_ACTION));
		android.util.Log.d(TAG, "=======================================");
		android.util.Log.d(TAG, "Device registered: regId = " + registrationId);
		android.util.Log.d(TAG, "=======================================");
		SplashActivityOld.storeRegistrationId(context, registrationId);
		System.out.println("=============ON GCM REGISTERED::::::::::");
		Log.e(TAG, "Device registered: regId = " + registrationId);
		// displayMessage(context, "From GCM: device successfully registered!");
		// ServerUtilities.register(context, registrationId);
	}

	// UnRegister GCM
	protected void onUnregistered(Context context, String registrationId) {
		android.util.Log.d(TAG, "=======================================");
		android.util.Log.d(TAG, "Device unregistered");
		android.util.Log.d(TAG, "=======================================");
		System.out.println("=============ON GCM UNREGISTER::::::::::");

        try {
            gcm.unregister();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // displayMessage(context,
		// "From GCM: device successfully unregistered!");
		if (GCMRegistrar.isRegisteredOnServer(context)) {
			android.util.Log.d(TAG, "=======================================");
			android.util.Log.d(TAG, "device successfully unregistered!");
			android.util.Log.d(TAG, "=======================================");
		} else {
			// This callback results from the call to unregister made on
			// ServerUtilities when the registration to the server failed.
			Log.e(TAG, "Ignoring unregister callback");
		}
	}*/

	// Get PushNotification
	protected void onMessage(Context context, Intent intent) {

		try {
			System.out
					.println("==============BUNDAL================================");
			System.out.println("Bundle:::" + intent.getExtras());
			System.out
					.println("==============================================");

			String messageCode = intent.getExtras().getString("msgcod");
			String message = intent.getExtras().getString("notification");

            Log.d("PUSH_NOTIF", "Received "+message+" "+messageCode);

            if (messageCode.equals("4098")) {
				Log.d("PUSH_NOTIF", "Received 4098");
				startActivity(new Intent(GCMIntentService.this,
						AddTipActivity.class)
                        .putExtra("fromPush",true)
                        .putExtra("reservation_id",intent.getExtras().getString("reservation_id"))
                        .putExtra("approved",intent.getExtras().getString("approved"))
                        .putExtra("tip_amount",intent.getExtras().getString("tip_amount"))
                        .putExtra("message",message)
                        .putExtra("invoice_id",intent.getExtras().getString("invoice_id"))
                        .putExtra("fare_amount",intent.getExtras().getString("fare_amount"))
                        .putExtra("MerchantId",intent.getExtras().getString("merchant_id"))
                        .putExtra("IsCaptive",intent.getExtras().getString("is_captive")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
			} else {

				String recervationId = intent.getExtras().getString("rsrvid");

				displayMessage(context, message, recervationId, messageCode);

			}

			generateNotification(context, message);
		} catch (Exception e) {
            Log.d("PUSH_NOTIF", "Catch Exception e");
			e.printStackTrace();
			Log.e(this.getClass() + "::onMessage( )::", e + "");
		}
	}


    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                onError(getApplicationContext(), extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                onDeletedMessages(getApplicationContext(), Integer.parseInt(extras.toString()));
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {

                onMessage(getApplicationContext(), intent);
            }
        }

        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }


	protected void onDeletedMessages(Context context, int total) {
		System.out.println("=============ON GCM OnDELETED MESSAGES::::::::::");
		String message = "From GCM: server deleted " + total
				+ " pending messages!";

		generateNotification(context, message);

		System.out.println("=============ON GCM DELETE MESSAGE::::::::::");

		Log.e("GCM onDeletedMessages", message);

	}

	// Error From GCM

	public void onError(Context context, String errorId) {
		// sendBroadcast(new
		// Intent(CommonUtilities.REGISTRATION_FINISH_ACTION));
		System.out.println("=============ON GCM OnERROR::::::::::" + errorId);
		// displayMessage(context, "From GCM: error (" + errorId + ")");

		Log.e("GCM onError", errorId);
	}

	/*protected boolean onRecoverableError(Context context, String errorId) {
		// sendBroadcast(new
		// Intent(CommonUtilities.REGISTRATION_FINISH_ACTION));

		// displayMessage(context, "From GCM: error (" + errorId + ")");
		return super.onRecoverableError(context, errorId);
	}*/

	/**
	 * Issues a notification to inform the user that server has sent a message.
	 */
	@SuppressWarnings("deprecation")
	private static void generateNotification(Context context, String message) {
		NotificationManager notificationManager = null;

		int icon = R.drawable.app_icon;
		long when = System.currentTimeMillis();

		notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(icon, message, when);
		String title = context.getResources().getString(R.string.app_name);

		Intent notificationIntent;

		notificationIntent = new Intent(context, SplashActivityOld.class);
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		// notificationIntent.putExtra("message", message);

		PendingIntent intent = null;
		intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

		notification.setLatestEventInfo(context, title, message, intent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.sound = Uri.parse("android.resource://"
				+ context.getPackageName() + "/" + R.raw.dings);
		notificationManager.notify(0, notification);

	}
}
