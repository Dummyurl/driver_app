package com.util;

import com.rideeaze.services.network.model.data.HttpFeecode;
import com.rideeaze.payment.service.DriverData;

import java.util.ArrayList;
import java.util.List;

public class Const {

	public static String SERVER_ULR = "http://rideeze.com";

	public static String currentRecceiptMsg;
	public static DriverData CurrentLogin;
	public static double currentRecipt;

    public static String SELECTED_CC_READER = "LOCAL_CARD_READER";
    public static String BTMAG_READER = "BTMag";

    public static String CASH_PAYMENT_METHOD = "Cash";
    public static String CREDIT_CARD_PAYMENT_METHOD = "CreditCard";

    public static String GCM_TOKEN = "GCM_TOKEN";

	// For Payment Integration
	public static float fare;
	public static float tip;
	public static int paymentInvoiceId;
	public static String paymentPassengerId;
	public static int paymentReservationId;
	public static double paymentTotalAdjstAmnt;
	public static double paymentSubTotalAmnt;
	public static double paymentTotalAmnt;
	public static int paymentCustomerCode;
	public static boolean IsCaptive;
	public static String MerchantId;
	public static String paymentUserEmail;
	public static int invoiceId;
	public static String signature;
	
	// For BTMagReader Status Message
	public static String BT_MAG_CONNECTING_ACTION = "connecting";
	public static String BT_MAG_CONNECTED_ACTION = "connected";
	public static String BT_MAG_CONNECT_FAILURE_ACTION = "connect_failure";
	public static String BT_MAG_DISCONNECTED_ACTION = "disconnected";
	public static String BT_MAG_RECEIVED_ACTION = "received";
	public static String CONNECT_BT_MAG_ACTION = "connect_btmag";

    public static String GPS_STATUS = "GPS_STATUS";
    public static String Accuracy = "Accuracy";

    public static final String PREF_FILE = "HEREBYGPS_PREF";

	public static List<HttpFeecode> feeCodeList = new ArrayList<HttpFeecode>();

	// For SignArea
	public static int width;
	public static int height;
	
	//0= UNKNOWN PASSENGER AND 1= KNOWN PASSENGER
	public static int IS_KNOWN_PASSENGER = 0;
	
	public static boolean fromUnknown = false;


	public static final String EXTRA_INTENT_LOGIN_BEFORE = "LOGIN_BEFORE";
	public static final String REGISTER_TEMP_USERNAME = "REGISTER_TEMP_USERNAME";
	public static final String REGISTER_TEMP_PASSWORD = "REGISTER_TEMP_PASSWORD";
	public static final String FARE_DETAILS_DATA = "FARE_DETAILS_DATA";
	public static final String LOG_OUT_FROM_ACCOUNT = "LOG_OUT_FROM_ACCOUNT";
	public static final String NO_SELECTED_VEHICLE = "NO_SELECTED_VEHICLE";


}
