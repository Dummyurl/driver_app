package com.rideeaze.gcm;

import android.content.Context;
import android.content.Intent;

import com.rideeaze.services.network.model.data.HttpPickUp;
import com.rideeaze.services.network.model.response.HttpGetInvoiceData;

import java.util.List;

public final class CommonUtilities {

	// give your server registration url here
	static final String SERVER_URL = "http://192.168.1.100";

	// Google project id
	public static final String SENDER_ID = "430959191370";

	// public static final String SENDER_ID = "183947987799";
	// API key for browser
	static final String GOOGLE_API_KEY = "AIzaSyAwI1cPb44Bo3s0z47Zvs938HSRbuIgOt0";

	/**
	 * Tag used on log messages.
	 */
	static final String TAG = "Android GCM";

	// public static String DISPLAY_MESSAGE_ACTION = "notification";

	public static String DISPLAY_REPLY_ACTION = "reply";
	static final String EXTRA_MESSAGE = "message";
	static final String EXTRA_RESERVATIONID = "rsrvid";
	static final String EXTRA_MESSAGECODE = "msgcod";

	public static HttpGetInvoiceData invoiceData;
	

	public static String DISPLAY_NAVIGATION_ACTION = "navigation";

	public static String REGISTER_DIALOG_ACTION = "register";

	//public static String UPDATE_PROFILE_ACTION = "updata";

	public static String LOGIN_ACTION = "login";

	public static String UPDATE_INTERVAL_ACTION = "update";

	public static List<HttpPickUp> pickups = null;
	
	//public static List<HttpPendingRequestPickUp> pendingreauestData = null;
	//public static HttpGetInvoiceData invoiceData = null;
	//public static int currentItem = 0;

	public static String message = "";

	public static String WAKE_APP_ACTION = "wake";
	
	public static boolean appBackgroung;

	/*
	 * Intent used to display a message in the screen.
	 */
	public static String DISPLAY_MESSAGE_ACTION = "com.google.android.gcm.demo.app.DISPLAY_MESSAGE";

	/*
	 * Intent used to GCM registration finish
	 */
	public static String REGISTRATION_FINISH_ACTION = "com.google.android.gcm.app.REGISTRATION_DONE";

	/**
	 * Notifies UI to display a message.
	 * <p/>
	 * This method is defined in the common helper because it's used both by the
	 * UI and the background service.
	 * 
	 * @param context
	 *            application's context.
	 * @param message
	 *            message to be displayed.
	 */
	public static void displayMessage(Context context, String message,
			String recervationId,String msgCode) {
		Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
		intent.putExtra(EXTRA_MESSAGE, message);
		intent.putExtra(EXTRA_RESERVATIONID, recervationId);
		intent.putExtra(EXTRA_MESSAGECODE, msgCode);
		context.sendBroadcast(intent);

		if (msgCode.equals("66")) {
			Intent wakeIntent = new Intent(CommonUtilities.WAKE_APP_ACTION);
			context.sendBroadcast(wakeIntent);
		}
	}
}
