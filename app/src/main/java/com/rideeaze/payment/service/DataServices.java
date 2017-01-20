package com.rideeaze.payment.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.rideeaze.services.network.model.data.AccountAuthDetails;
import com.util.Const;
import com.util.StorageDataHelper;
import com.util.Utils;

public class DataServices {
	private HttpClient _httpclient;
	HttpGet getClient = null;
	HttpPost postClient = null;
	List<Cookie> cookies;
	private static DataServices _obj;

	// private String baseURI = "http://192.168.137.1/nopcom/gateway/";
	// private String baseURI = "http://192.168.0.102/nopcom/gateway/";
	// private String baseURI = "http://169.254.69.201/nopcom/gateway/";
	private String baseURI = "https://hereapproved.com/gateway/";
	private Context context;

	private DataServices(Context context) {

		/*
		 * HostnameVerifier hostnameVerifier =
		 * org.apache.http.conn.ssl.SSLSocketFactory
		 * .ALLOW_ALL_HOSTNAME_VERIFIER;
		 * 
		 * DefaultHttpClient client = new DefaultHttpClient();
		 * 
		 * SchemeRegistry registry = new SchemeRegistry(); SSLSocketFactory
		 * socketFactory = SSLSocketFactory.getSocketFactory();
		 * socketFactory.setHostnameVerifier((X509HostnameVerifier)
		 * hostnameVerifier);
		 * 
		 * registry.register(new Scheme("https", socketFactory, 443));
		 * 
		 * SingleClientConnManager mgr = new
		 * SingleClientConnManager(client.getParams(), registry); _httpclient =
		 * new DefaultHttpClient(mgr, client.getParams());
		 * 
		 * // Set verifier
		 * HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
		 */

		_httpclient = getNewHttpClient();

		// _httpclient=new DefaultHttpClient();
		this.context = context;

	}

	public static DataServices getDataServiceObject(Context context) {
		if (_obj == null) {
			_obj = new DataServices(context);
		}
		return _obj;
	}

	private HttpResponse initialiseHTTPClient(String Url, String serviceType,
			AbstractHttpEntity entity) {
		HttpResponse response = null;

		AccountAuthDetails accountAuthDetails = StorageDataHelper.getInstance(context).getAccountAuthDetails();

		if (serviceType.equals("GET")) {
			getClient = new HttpGet(Url);
			if (accountAuthDetails.paymentToken != null)
				getClient.setHeader("token", accountAuthDetails.paymentToken);
			try {
				response = _httpclient.execute(getClient);
			} catch (ClientProtocolException e) {
				Log.e("DataServices", "Error executing http client !");
				return null;
			} catch (IOException e) {
				Log.e("DataServices", "Error executing http client !");
				return null;
			}
		} else if (serviceType.equals("POST")) {
			postClient = new HttpPost(Url);
			if (entity != null)
				postClient.setEntity(entity);
			postClient.setHeader("Content-Type", "application/json");
			if (accountAuthDetails.paymentToken != null)
				postClient.setHeader("token", accountAuthDetails.paymentToken);

			try {
				response = _httpclient.execute(postClient);
			} catch (ClientProtocolException e) {
				Log.e("DataServices", "Error executing http client !");
				return null;
			} catch (IOException e) {
				Log.e("DataServices", e.getMessage());
				return null;
			}
		}
		return response;
	}

	@SuppressWarnings({ "unchecked", "unused" })
	private <T> T processHttpResponse(String Url, String serviceType,
			AbstractHttpEntity entity, T returnEntity) {
		HttpResponse response = initialiseHTTPClient(Url, serviceType, entity);
		System.out.println("=========RESPONCE:::::::::::"
				+ response.getStatusLine());

		StatusLine statusLine = response.getStatusLine();
		System.out.println("=========RESPONCE:::::::::::"
				+ statusLine.getStatusCode());
		if (statusLine.getStatusCode() == HttpStatus.SC_OK) {

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			try {
				response.getEntity().writeTo(out);
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			String responseString = out.toString();

			Log.e("result", responseString);
			Gson gson = new Gson();

			JsonPaymentResponse<T> status = new JsonPaymentResponse<T>();
			// status.content = returnEntity;
			JsonParser p = new JsonParser();
			JsonElement jelement = p.parse(responseString).getAsJsonObject()
					.get("content");
			// Type typ = status.getClass();
			// Class<T> clazz = new Class<T>();
			// Type myType = (new JsonResponse<T>()).getType();
			// status.content = returnEntity;
			// status = (JsonResponse<T>) gson.fromJson(responseString,
			// status.getClass());

			returnEntity = (T) gson.fromJson(jelement, returnEntity.getClass());
			// Log.d("returnEntity",(String) returnEntity);
			// processResponse(status);
			// returnEntity = status.content;

		}
		return returnEntity;
	}

	private PrintData processHttpResponse1(String Url) {
		HttpResponse response = initialiseHTTPClient(Url, "GET", null);
		StatusLine statusLine = response.getStatusLine();
		if (statusLine.getStatusCode() == HttpStatus.SC_OK) {

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			try {
				response.getEntity().writeTo(out);
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			String responseString = out.toString();

			Log.e("result", responseString);

			// status.content = returnEntity;
			JsonParser p = new JsonParser();
			JsonElement jelement = p.parse(responseString).getAsJsonObject()
					.get("Content");

			PrintData data = new PrintData();

			data.customerCopy = p.parse(jelement.toString()).getAsJsonObject()
					.get("CustomerCopy").getAsString();
			data.merchantCopy = p.parse(jelement.toString()).getAsJsonObject()
					.get("MerchantCopy").getAsString();

			return data;

		}

		return null;
	}

	private <T> T processHandKeyTraResponse(String Url, String serviceType,
			AbstractHttpEntity entity, T returnEntity) {
		HttpResponse response = initialiseHTTPClient(Url, serviceType, entity);
		System.out.println("=========RESPONCE:::::::::::"
				+ response.getStatusLine());

		StatusLine statusLine = response.getStatusLine();
		System.out.println("=========RESPONCE:::::::::::"
				+ statusLine.getStatusCode());
		if (statusLine.getStatusCode() == HttpStatus.SC_OK) {

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			try {
				response.getEntity().writeTo(out);
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			String responseString = out.toString();

			Log.e("result", responseString);
			Gson gson = new Gson();

			JsonParser p = new JsonParser();
			JsonElement jelement = p.parse(responseString).getAsJsonObject();
			returnEntity = (T) gson.fromJson(jelement,returnEntity.getClass());


		}
		return returnEntity;
	}

	@SuppressWarnings("unused")
	private <T> void processResponse(
			@SuppressWarnings("rawtypes") JsonPaymentResponse status) {
		if (status.isSuccess.equals(false))
			Toast.makeText(context, status.message, Toast.LENGTH_LONG).show();
		Const.currentRecceiptMsg = status.message;
	}

	public String getLogin(String uname, String pwd) {
		String loginEntity = "";

		JSONObject objLogin = new JSONObject();
		try {

			try {

				objLogin.put("UserName", uname);
				objLogin.put("Password", pwd);
				StringEntity entity;
				entity = new StringEntity(objLogin.toString(), HTTP.UTF_8);

				Log.e("Login", objLogin.toString());

				entity.setContentType("application/json");
				loginEntity = processHttpResponse(baseURI + "login", "POST",
						entity, loginEntity);

			} catch (UnsupportedEncodingException e) {
				Log.e("error", "Unsupported Encoding Exception");
				// e.printStackTrace();
			}

		} catch (JSONException e) {
			Log.e("error", "JSONException");
			// e.printStackTrace();
		}

		// Log.d("reply", loginEntity);

		return loginEntity;
	}

	public String createAccount(String uname, String pwd, String mail) {
		String loginEntity = "";

		JSONObject objLogin = new JSONObject();
		try {
			objLogin.put("Email", mail);
			objLogin.put("UserName", uname);
			objLogin.put("Password", pwd);
			objLogin.put("UniqueId", "1");

			StringEntity entity = null;
			try {
				entity = new StringEntity(objLogin.toString(), HTTP.UTF_8);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			Log.e("createAccount", objLogin.toString());
			entity.setContentType("application/json");
			loginEntity = processHttpResponse(baseURI + "CreateAccount",
					"POST", entity, loginEntity);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// Log.d("status", loginEntity);

		return loginEntity;
	}

	public boolean verifyLogin(String token) {
		boolean verifyEntity = false;

		verifyEntity = processHttpResponse(baseURI + "Verify?token=" + token,
				"GET", null, verifyEntity);

		return verifyEntity;
	}

	public boolean registerNew(String mobileNo, String firstName,
			String midName, String lastName, String merchantId)
			throws UnsupportedEncodingException, JSONException {
		boolean registerEntity = false;

		JSONObject register = new JSONObject();
		// register.put("token", token);
		register.put("MobilePhone", mobileNo);

		register.put("FirstName", firstName);
		register.put("MiddleName", midName);
		register.put("LastName", lastName);

		register.put("MerchantId", merchantId);

		StringEntity entity = new StringEntity(register.toString(), HTTP.UTF_8);

		Log.e("registerNew", register.toString());

		entity.setContentType("application/json");

		registerEntity = processHttpResponse(baseURI + "RegisterNew", "POST",
				entity, registerEntity);
		return registerEntity;
	}

	public boolean registerExisting(String token, String mobileNo)
			throws UnsupportedEncodingException, JSONException {
		boolean registerEntity = false;

		JSONObject register = new JSONObject();
		register.put("token", token);
		register.put("MobilePhone", mobileNo);

		register.put("UniqueId", 1);

		StringEntity entity = new StringEntity(register.toString(), HTTP.UTF_8);

		entity.setContentType("application/json");

		Log.e("registerExisting", register.toString());

		registerEntity = processHttpResponse(baseURI + "RegisterExisting",
				"POST", entity, registerEntity);
		return registerEntity;
	}

	public boolean emailReceipt(String emailId, String transactionId) {
		boolean isSent = false;
		Double d = Double.parseDouble(transactionId);
		int transId = d.intValue();
		try {
			Log.i("log_info", "transaction id  is " + transactionId
					+ " email is " + emailId);
			isSent = processHttpResponse(baseURI + "SendEmail?transactionId="
					+ transId + "&emailId=" + emailId, "GET", null, isSent);
			Log.i("log_info", "sent mail " + isSent);
		} catch (Exception e) {
			return isSent;
		}
		return isSent;
	}

	public SignUpStatus getSignUp(String uname, String pwd, String skypeId,
			String mobNo) {
		SignUpStatus signUpEntity = new SignUpStatus("", false);
		try {

			String strData = "<SignUpData xmlns=\"http://schemas.datacontract.org/2004/07/Mapex.RestServices.RestService\"><IMEI>"
					+ "</IMEI><MobileNo>"
					+ mobNo
					+ "</MobileNo><Password>"
					+ pwd
					+ "</Password><SkypeId>"
					+ skypeId
					+ "</SkypeId><Username>"
					+ uname
					+ "</Username></SignUpData>";

			StringEntity entity = new StringEntity(strData, HTTP.UTF_8);

			entity.setContentType("application/xml");
			signUpEntity = processHttpResponse(baseURI + "CreatAccount",
					"POST", entity, signUpEntity);
		} catch (Exception e) {
			return signUpEntity;
		}
		return signUpEntity;
	}

	public double processCardPayment(String cardData, String amount, String tip) {
		double transactionId = 0;
		try {
			JSONObject objLogin = new JSONObject();
			// objLogin.getJSONObject("content").getString("CustomerCopy");
			if (tip.equals(""))
				tip = "0.0";
			Log.i("log_info", "calling payment service: card data is "
					+ cardData + " amount is " + amount + " tip is " + tip);
			objLogin.put("cardData", cardData);
			objLogin.put("amount", amount);
			objLogin.put("tip", tip);
			objLogin.put("localDate", GetDateTime());

			Log.e("processCardPayment Request", objLogin.toString());

			StringEntity entity = new StringEntity(objLogin.toString(),
					HTTP.UTF_8);

			entity.setContentType("application/json");
			transactionId = processHttpResponse(baseURI + "ProcessPay", "POST",
					entity, transactionId);
			Log.i("log_info", "transaction id is " + transactionId);
		} catch (Exception e) {
			Log.i("log_info", "service exception" + e);
			return transactionId;
		}
		return transactionId;
	}

	public int processPayEnrolledCustomer(String amount, String tip,
			JSONArray list) {
		int transactionId = 0;
		try {
			JSONObject objLogin = new JSONObject();
			// objLogin.getJSONObject("content").getString("CustomerCopy");

			Log.i("log_info", "calling payment service: card data is " + amount
					+ " amount is " + amount + " tip is " + tip);

			objLogin.put("amount", amount);
			objLogin.put("tip", tip);
			objLogin.put("localDate", GetDateTime());
			objLogin.put("additionalPay", list);
			objLogin.put("customerCode", Const.paymentCustomerCode);
			objLogin.put("merchantId", Const.MerchantId);
			objLogin.put("isCaptive", Const.IsCaptive);

			Log.e("=====processPayEnrolledCustomer===", objLogin.toString());

			StringEntity entity = new StringEntity(objLogin.toString(),
					HTTP.UTF_8);

			entity.setContentType("application/json");
			transactionId = processHttpResponse(baseURI
					+ "ProcessPayEnrolledCustomer", "POST", entity,
					transactionId);
			Log.i("log_info", "transaction id is " + transactionId);
		} catch (Exception e) {
			Log.i("log_info", "service exception" + e);
			return transactionId;
		}
		return transactionId;
	}

	public PrintData getPrintData(String transactionId) {
		PrintData printData = new PrintData();

		try {
			printData = processHttpResponse1(baseURI
					+ "GetPrintData?transactionId=" + transactionId);

		} catch (Exception e) {
			return printData;
		}

		return printData;

	}

	private String GetDateTime() {
		Calendar c = Calendar.getInstance();
		String year = String.valueOf(c.get(Calendar.YEAR));
		String month = String.valueOf(c.get(Calendar.MONTH));
		String date = String.valueOf(c.get(Calendar.DATE));
		String hour = String.valueOf(c.get(Calendar.HOUR));
		String minute = String.valueOf(c.get(Calendar.MINUTE));
		String sec = String.valueOf(c.get(Calendar.SECOND));

		month = month.length() < 2 ? "0" + month : month;
		date = date.length() < 2 ? "0" + date : date;
		hour = hour.length() < 2 ? "0" + hour : hour;
		minute = minute.length() < 2 ? "0" + minute : minute;
		sec = sec.length() < 2 ? "0" + sec : sec;

		// String localDate = year + "-" + month + "-" + date + "T" + hour + ":"
		// + minute + ":" + sec;

		String localDate = Utils.millisToDate(System.currentTimeMillis(),
				"yyyy-MM-dd'T'hh:mm:ss");
		return localDate;
	}

	public JsonPaymentResponse<String> ProcessHandKeyPay(String CardNumber, String CVV,
			String ExpDt, String Amount, String Tip, String CardType) {
		
		
		JsonPaymentResponse<String> payEntity = new JsonPaymentResponse<String>();

		JSONObject objLogin = new JSONObject();
		try {
			try {
				Amount = Amount.equals("") ? "0" : Amount;
				Tip = Tip.equals("") ? "0" : Tip;
				objLogin.put("CardNumber", CardNumber);
				objLogin.put("CVV", CVV);
				objLogin.put("ExpDt", ExpDt);
				objLogin.put("Amount", Amount);
				objLogin.put("Tip", Tip);

				objLogin.put("LocalDate", GetDateTime());

				if (CardType.equals("Visa")) {
					CardType = "V";
				} else if (CardType.equals("Master Card")) {
					CardType = "M";
				} else if (CardType.equals("American Express")) {
					CardType = "X";
				} else if (CardType.equals("Discover")) {
					CardType = "R";
				}

				objLogin.put("CardType", CardType);

				Log.e("ProcessHandKeyPay Request", objLogin.toString());

				StringEntity entity;
				entity = new StringEntity(objLogin.toString(), HTTP.UTF_8);

				entity.setContentType("application/json");
				payEntity = processHandKeyTraResponse(baseURI
						+ "ProcessHandKeyedPay", "POST", entity, payEntity);

			} catch (UnsupportedEncodingException e) {
				Log.e("error", "Unsupported Encoding Exception");
				// e.printStackTrace();
			}

		} catch (JSONException e) {
			Log.e("error", "JSONException");
			// e.printStackTrace();
		}

		Log.d("reply", payEntity.toString());

		return payEntity;
	}

	public HttpClient getNewHttpClient() {

		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			// registry.register(new Scheme("http",
			// PlainSocketFactory.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(
					params, registry);

			return new DefaultHttpClient(ccm, params);
		} catch (Exception e) {
			return new DefaultHttpClient();
		}
	}

	public class MySSLSocketFactory extends SSLSocketFactory {
		SSLContext sslContext = SSLContext.getInstance("TLS");

		public MySSLSocketFactory(KeyStore truststore)
				throws NoSuchAlgorithmException, KeyManagementException,
				KeyStoreException, UnrecoverableKeyException {
			super(truststore);

			TrustManager tm = new X509TrustManager() {

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				@Override
				public void checkClientTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {

				}

				@Override
				public void checkServerTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {

				}
			};

			sslContext.init(null, new TrustManager[] { tm }, null);
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port,
				boolean autoClose) throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host,
					port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		}
	}

	public DriverData GetUserInfo() {
		DriverData userInfo = new DriverData();
		userInfo = processHttpResponse(baseURI + "GetUserInfo", "GET", null,
				userInfo);
		Const.CurrentLogin = userInfo;
		return userInfo;
	}

}
