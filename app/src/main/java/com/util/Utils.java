package com.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.rideeaze.R;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@SuppressLint("SimpleDateFormat")
public class Utils {
	public static final String SIM_ABSENT = "Absent";
	public static final String SIM_READY = "Ready";
	public static final String SIM_UNKNOWN = "Unknown";

	private static final int MAX_METERS_GOOD = 20, MAX_METERS_OK = 40,
			MAX_METERS_WEAK = 100, MAX_METERS_BAD = 200;

	public static int indexOfStringArray(String[] strArray, String strFind) {
		int index;

		for (index = 0; index < strArray.length; index++)
			if (strArray[index].equals(strFind))
				break;
		return index;
	}
	
	public static boolean validateEmail(String email) {

		if (email
				.matches("\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*")
				&& email.length() > 0) {
			return true;
		}

		return false;
	}
	public final static Pattern EMAIL_ADDRESS_PATTERN = Pattern
			.compile("\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
	public static String getLocNameFromLatLong(double lat, double Long,
			Context contx) {

		Geocoder coder = new Geocoder(contx);
		List<Address> address;
		String location = null;

		try {

			address = coder.getFromLocation(lat, Long, 3);

			if (address != null && address.size() != 0) {
				location = address.get(0).getLocality() + ","
						+ address.get(0).getCountryName();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return location;
	}
	
	
	
	public static void showToastAlert(Context context,String msg){  
		Toast.makeText(context,msg, Toast.LENGTH_LONG).show();
    }
	
	public static String formatPhoneNumber(String number){  
        number  =   number.substring(0, number.length()-4) + "-" + number.substring(number.length()-4, number.length());
        number  =   number.substring(0,number.length()-8)+")"+number.substring(number.length()-8,number.length());
        number  =   number.substring(0, number.length()-12)+"("+number.substring(number.length()-12, number.length());
        return number;
    }
	public static String getDeviceId(Context context) {
		  TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return manager.getDeviceId();
	}
	
	// Get the Distance from LAt Long.
	public static int CalculationByDistance(LatLng StartP, LatLng EndP) {
		int Radius = 6371;// radius of earth in Km
		double lat1 = StartP.latitude;
		double lat2 = EndP.latitude;
		double lon1 = StartP.longitude;
		double lon2 = EndP.longitude;
		double dLat = Math.toRadians(lat2 - lat1);
		double dLon = Math.toRadians(lon2 - lon1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
				* Math.sin(dLon / 2);
		double c = 2 * Math.asin(Math.sqrt(a));
		double valueResult = Radius * c;
		double km = valueResult / 1;
		DecimalFormat newFormat = new DecimalFormat("####");
		int kmInDec = Integer.valueOf(newFormat.format(km));
		double meter = valueResult % 1000;
		int meterInDec = Integer.valueOf(newFormat.format(meter));
		System.out.println("=======DISTANCE::::::::RadiusValue+" + valueResult
				+ "   KM  " + kmInDec + " Meter " + meterInDec);

		System.out.println("=======DISTANCE::::::::RadiusValue two +"
				+ new DecimalFormat("####.##").format(Radius * c));

		return meterInDec;
	}

	public static void turnGPSOn(Context ctx) {
		if (android.os.Build.VERSION.SDK_INT >= 19) {

		} else {
			Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
			intent.putExtra("enabled", true);
			ctx.sendBroadcast(intent);

			String provider = Settings.Secure.getString(
					ctx.getContentResolver(),
					Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
			if (!provider.contains("gps")) { // if gps is disabled
				final Intent poke = new Intent();
				poke.setClassName("com.android.settings",
						"com.android.settings.widget.SettingsAppWidgetProvider");
				poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
				poke.setData(Uri.parse("3"));
				ctx.sendBroadcast(poke);

			}
		}
	}

    public static void turnGPSOff(Context ctx) {
        if (android.os.Build.VERSION.SDK_INT >= 19) {

        } else {
            String provider = Settings.Secure.getString(
                    ctx.getContentResolver(),
                    Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            if (provider.contains("gps")) { // if gps is enabled
                final Intent poke = new Intent();
                poke.setClassName("com.android.settings",
                        "com.android.settings.widget.SettingsAppWidgetProvider");
                poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
                poke.setData(Uri.parse("3"));
                ctx.sendBroadcast(poke);
            }
        }
    }

	public enum GPSReception {
		GOOD, OK, WEAK, BAD, NONE;
	}

	private String toString(GPSReception reception) {
		switch (reception) {
		case GOOD:
			return "" + 6;
		case OK:
			return "" + 4;
		case WEAK:
			return "" + 2;
		case BAD:
			return "" + 1;
		case NONE:
			return "" + 0;
		default:
			return "" + 0;
		}
	}

    public static String toSignalTextValue(GPSReception reception) {
        switch (reception) {
            case GOOD:
                return "Good";
            case OK:
                return "Average";
            case WEAK:
                return "Fair";
            case BAD:
                return "Bad";
            case NONE:
                return "No signal";
        }
        return "No signal";
    }

    public static String modifyFare(String fare) {
        int position = fare.indexOf(".")+1;
        if(position > 0) {
            String afterPoint = fare.substring(position, fare.length());
            if (afterPoint.length() == 1) {
                fare += "0";
            }
        }

        return fare;
    }

    public static int toSignalImageResource(GPSReception reception) {
        switch (reception) {
            case GOOD:
                return R.drawable.setting_gpsgood;
            case OK:
                return R.drawable.setting_gpsaverage;
            case WEAK:
                return R.drawable.setting_gpsfair;
            case BAD:
                return R.drawable.setting_gpsbad;
            case NONE:
                return R.drawable.setting_gpsno;
        }
        return R.drawable.setting_gpsno;
    }

	public static GPSReception getGPSReception(float accuracy) {
		if (accuracy == 0.0) {
			return GPSReception.NONE;
		}
		if (accuracy <= MAX_METERS_GOOD) {
			return GPSReception.GOOD;
		} else if (accuracy <= MAX_METERS_OK) {
			return GPSReception.OK;
		} else if (accuracy <= MAX_METERS_WEAK) {
			return GPSReception.WEAK;
		} else if (accuracy <= MAX_METERS_BAD) {
			return GPSReception.BAD;
		} else {
			return GPSReception.NONE;
		}
	}

	

	

	// To check that Any Internet service or wifi available before using process
	// that make connection to server.
	public static boolean isOnline(Context context) {

		try {
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);

			if (cm != null) {
				return cm.getActiveNetworkInfo().isConnected();
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	// Used to conver Date object to string
	public static String convertDateToString(Date objDate, String parseFormat) {
		try {
			return new SimpleDateFormat(parseFormat).format(objDate);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	// Used to convert String to date
	public static Date convertStringToDate(String strDate, String parseFormat) {
		try {
			return new SimpleDateFormat(parseFormat).parse(strDate);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// Used to convert Date string to string
	public static String convertDateStringToString(String strDate,
			String currentFormat, String parseFormat) {
		try {
			return convertDateToString(
					convertStringToDate(strDate, currentFormat), parseFormat);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// Used to convert milliseconds to date
	public static String millisToDate(long millis, String format) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millis);
		return new SimpleDateFormat(format).format(calendar.getTime());
	}
	
	// Used to convert milliseconds to date
		public static int millisToSecond(long millis) {

			return (int)(millis/1000);
		}
		
	
	public static String DateStringToSeconds(String dateString, String format) {
		String timeInMilliseconds = null;
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
		    Date mDate = sdf.parse(dateString);
		     timeInMilliseconds = (mDate.getTime()/1000)+"";
		    System.out.println("Date in milli :: " + timeInMilliseconds);
		} catch (ParseException e) {
		            e.printStackTrace();
		}

		return timeInMilliseconds;
	}
	
	public static long converDatetomillis(String dateString, String dateFormate) {
		long timeInMilliseconds = 0;
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormate);
		try {
			Date mDate = sdf.parse(dateString);
			timeInMilliseconds = mDate.getTime();
			System.out.println("Date in milli :: " + timeInMilliseconds);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return timeInMilliseconds;
	}

    public static final String reviewSetKey = "reviews";
    public static void saveReviewToLocal(ReviewObject obj, Context ctx) {
        SharedPreferences settingsObj = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = settingsObj.edit();
        Set<String> set = settingsObj.getStringSet(reviewSetKey, null);
        if(set == null) {
            set = new HashSet<String>();
        }
        set.add(obj.convertToString());

        //Log.d(reviewSetKey, "Record review "+obj.convertToString());
        editor.putStringSet(reviewSetKey, set);
        editor.apply();
    }

    public static ReviewObject getReviewObject(String id, Context ctx) {
        SharedPreferences settingsObj = PreferenceManager.getDefaultSharedPreferences(ctx);
        Set<String> set = settingsObj.getStringSet(reviewSetKey, null);
        if(set == null) {
            return null;
        }



        String[] reviewArray = set.toArray(new String[set.size()]);
        for (int i=0; i < reviewArray.length; i++) {
            String objString = reviewArray[i];
            Log.d("REVIEW_LOG", objString);
            ReviewObject obj = new ReviewObject();
            obj.convertFromString(objString);

            if(obj.id.contentEquals(id)) {
                return obj;
            }
        }

        return null;
    }

	public static  String getTitleForDayOfWeek(int dayOfWeek) {
		switch (dayOfWeek) {
			case Calendar.MONDAY:
				return "Mon";
			case Calendar.TUESDAY:
				return "Tue";
			case Calendar.WEDNESDAY:
				return "Wed";
			case Calendar.THURSDAY:
				return "Thu";
			case Calendar.FRIDAY:
				return "Fri";
			case Calendar.SATURDAY:
				return "Sat";
			case Calendar.SUNDAY:
				return "Sun";
		}

		return "";
	}

	public static View setBorderToView(View view) {
		GradientDrawable border = new GradientDrawable();
		border.setColor(0xFFFFFFFF); //white background
		border.setStroke(1, 0xFF000000); //black border with full opacity
		if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
			view.setBackground(border);
		} else {
			view.setBackgroundDrawable(border);
		}

		return view;
	}
}