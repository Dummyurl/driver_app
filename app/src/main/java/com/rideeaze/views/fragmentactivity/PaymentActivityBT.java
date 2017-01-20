package com.rideeaze.views.fragmentactivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.idtech.bluetooth.IDTechOpenHelper;
import com.idtech.bluetooth.IDTechUtils;
import com.idtech.bluetooth.OnReceiveListener;
import com.rideeaze.R;
import com.rideeaze.payment.service.DataServices;
import com.rideeaze.services.network.NetworkApi;
import com.rideeaze.services.network.NetworkService;
import com.rideeaze.services.network.model.data.HttpFeecode;
import com.rideeaze.services.network.model.request.HttpEmailPassengerRequest;
import com.rideeaze.services.network.model.response.JsonResponse;
import com.rideeaze.uc.AlertDailogView.OnCustPopUpDialogButoonClickListener;
import com.rideeaze.views.activity.DriverActivity;
import com.rideeaze.views.dialog.ProcessingDialog;
import com.rideeaze.views.dialog.SimpleInfoDialog;
import com.util.Const;
import com.util.Utils;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Set;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PaymentActivityBT extends DriverActivity implements OnReceiveListener,
		OnCustPopUpDialogButoonClickListener {

	String _strAmount;
	String _strTip;

	IDTechOpenHelper mIDTechHelper = null;
	public BluetoothAdapter mBluetoothAdapter = null;

	String _strMSRData = "";

	int nCount = 0;

	private TextView txtScanCardQuotedAmnt;
	private TextView txtScanCardSubTotal;
	private TextView txtScanCardTip;
	private TextView txtScanCardTotal;
	private TextView txtScanCardStatus;
	private TextView txtScanCardMessage;
	private TextView txtRetryCardReader;
	private TextView txtKeyCardMan;
	private TextView txtScanCardSubmit;
	private LinearLayout llScanCardBtns;
	private TextView txtScanCardCancel;
	private LinearLayout llScanCardAd;

    // card reader staus element
    private ImageView statusCardReaderImageView;
    private TextView statusCardReaderTextView, instructionCardReaderTextView;

	private ProgressDialog progressDialog = null;
	private Message msg;

	// BTMagReceiver receiver;

	// For Row File
	private TextView txtAdjustDesc;
	private EditText edtAdjstRowAmnt;
	private RelativeLayout rlRowMain;
	private EditText adjstTemp;

	private double callWebService(String tip, String cardData, String amount) {
		return DataServices.getDataServiceObject(getApplicationContext())
				.processCardPayment(cardData, amount, tip);
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scan_card_activity);
		Log.i("log_info", "create of payment activity");

		txtScanCardQuotedAmnt = (TextView) findViewById(R.id.txtScanCardQuotedAmnt);
		txtScanCardSubTotal = (TextView) findViewById(R.id.txtScanCardSubTotal);
		txtScanCardTip = (TextView) findViewById(R.id.txtScanCardTip);
		txtScanCardTotal = (TextView) findViewById(R.id.txtScanCardTotal);
		txtScanCardStatus = (TextView) findViewById(R.id.txtScanCardStatus);
		txtScanCardMessage = (TextView) findViewById(R.id.txtScanCardMessage);
		txtRetryCardReader = (TextView) findViewById(R.id.txtRetryCardReader);
		txtKeyCardMan = (TextView) findViewById(R.id.txtKeyCardMan);
		txtScanCardSubmit = (TextView) findViewById(R.id.txtScanCardSubmit);
		llScanCardBtns = (LinearLayout) findViewById(R.id.llScanCardBtns);
		txtScanCardCancel = (TextView) findViewById(R.id.txtScanCardCancel);
		llScanCardAd = (LinearLayout) findViewById(R.id.llScanCardAd);

        statusCardReaderImageView = (ImageView) findViewById(R.id.statusCardReaderImageView);
        statusCardReaderTextView = (TextView) findViewById(R.id.statusCardReaderTextView);
        instructionCardReaderTextView = (TextView) findViewById(R.id.instructionCardReaderTextView);

		msg = new Message();

		mIDTechHelper = new IDTechOpenHelper(this);
		mIDTechHelper.setOnReceiveListener(this);

		txtScanCardQuotedAmnt
				.setText(String.format("%.2f", (float) Const.fare));
		txtScanCardSubTotal.setText(String.format("%.2f",
				(float) Const.paymentSubTotalAmnt));
		txtScanCardTip.setText(String.format("%.2f", (float) Const.tip));
		txtScanCardTotal.setText(String.format("%.2f",
				(float) Const.paymentTotalAmnt));

		if (Const.feeCodeList != null && Const.feeCodeList.size() > 0) {
			generateFeecodeListUi(Const.feeCodeList);
		} else {
			llScanCardAd.setVisibility(View.GONE);
		}

		ConnectBTMag();
		txtScanCardQuotedAmnt.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable arg0) {
				if (_strMSRData.length() > 0) {
					// /showDoneButton();
				}
			}

			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				if (arg0.toString().equalsIgnoreCase(".")) {

					return;
				}
				double valAmount = Double.parseDouble(arg0.toString()
						.equalsIgnoreCase("") ? "0" : arg0.toString());
				double valTip = Double.parseDouble(txtScanCardTip.getText()
						.toString().equalsIgnoreCase("") ? "0" : txtScanCardTip
						.getText().toString());
				double valTotal = valAmount + valTip;
				txtScanCardTotal.setText(String.valueOf(valTotal));
			}

		});

		txtScanCardTip.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable arg0) {
			}

			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				if (arg0.toString().equalsIgnoreCase(".")) {

					return;
				}
				double valAmount = Double.parseDouble(arg0.toString()
						.equalsIgnoreCase("") ? "0" : arg0.toString());
				double valTip = Double.parseDouble(txtScanCardQuotedAmnt
						.getText().toString().equalsIgnoreCase("") ? "0"
						: txtScanCardQuotedAmnt.getText().toString());
				double valTotal = valAmount + valTip;
				txtScanCardTotal.setText(String.valueOf(valTotal));
			}

		});

		txtScanCardSubmit.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				if (BTMagService.bIsConnected == true) {
					if (BTMagService.bIsSwiped) {

						_strAmount = txtScanCardTotal.getText().toString();
						_strTip = txtScanCardTip.getText().toString();
						ProcessPay();
					} else {
						if (txtScanCardQuotedAmnt.getText().toString().length() == 0) {
							Utils.showToastAlert(PaymentActivityBT.this,
									"Please input Fare!");
							return;
						} else {
							DisplayProcessMessage("Please Scan Card!");
							txtScanCardMessage.setText("Please Scan Card!");
						}

					}

				} else {
					ConnectBTMag();
				}
			}
		});

		txtKeyCardMan.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(PaymentActivityBT.this,
						HandKeyedTransactionActivity.class);
				intent.putExtra("amount", txtScanCardTotal.getText().toString());
				startActivity(intent);
			}
		});

		txtScanCardCancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent myIntent = new Intent(PaymentActivityBT.this,
						SplashActivityOld.class);
				startActivity(myIntent);
				finish();
			}
		});

		txtRetryCardReader.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				ConnectBTMag();
			}
		});

        getActionBar().setDisplayShowHomeEnabled(false);

	}

    private void setUICardStatusWarning() {
        statusCardReaderImageView.setImageResource(R.drawable.credit_car_warning_image_status);
        statusCardReaderTextView.setText(getString(R.string.status_card_reader_text_no_card));
        instructionCardReaderTextView.setText(getString(R.string.instruction_card_reader_text_no_card));
    }

    private void setUICardStatusAccepted() {
        statusCardReaderImageView.setImageResource(R.drawable.credit_car_success_image_status);
        statusCardReaderTextView.setText(getString(R.string.status_card_reader_text_accept_card));
        instructionCardReaderTextView.setText(getString(R.string.instruction_card_reader_text_accept_card));
    }

	private void generateFeecodeListUi(List<HttpFeecode> feeCodeList) {
		llScanCardAd.removeAllViews();
		int i = 0;
		for (HttpFeecode httpFeecode : feeCodeList) {
			if (httpFeecode.Amount > 0
					&& !httpFeecode.Description.equals("Estimated Fare")) {
				View view = LayoutInflater.from(PaymentActivityBT.this)
						.inflate(R.layout.adjustment_row, null);
				LinearLayout llRow = (LinearLayout) view
						.findViewById(R.id.llAdjstRow);
				txtAdjustDesc = (TextView) view
						.findViewById(R.id.txtAdjustDesc);
				edtAdjstRowAmnt = (EditText) view
						.findViewById(R.id.edtAdjstRowAmnt);
				View viewRow = (View) view.findViewById(R.id.viewRow);

				txtAdjustDesc.setText(httpFeecode.Description);
				edtAdjstRowAmnt.setText(httpFeecode.Amount + "");
				llRow.setVisibility(View.VISIBLE);
				if (i == (feeCodeList.size() - 1)) {
					viewRow.setVisibility(View.VISIBLE);
				} else {
					viewRow.setVisibility(View.GONE);
				}
				edtAdjstRowAmnt.addTextChangedListener(new TextWatcher() {

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
						Const.paymentTotalAdjstAmnt = getAllAdjustMentTotal();
						calculateTotalFare(Const.fare
								+ Const.paymentTotalAdjstAmnt);
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {

					}

					@Override
					public void afterTextChanged(Editable s) {

					}
				});

				llScanCardAd.addView(view);

			} else if (i == (feeCodeList.size() - 1)) {
				View view = LayoutInflater.from(PaymentActivityBT.this)
						.inflate(R.layout.adjustment_row, null);

				View viewRow = (View) view.findViewById(R.id.viewRow);
				viewRow.setVisibility(View.VISIBLE);
				llScanCardAd.addView(view);
			}
			i++;
		}

	}

	private double getAllAdjustMentTotal() {
		double adjustTotal = 0;
		for (int i = 0; i < llScanCardAd.getChildCount(); i++) {
			adjstTemp = (EditText) llScanCardAd.getChildAt(i).findViewById(
					R.id.edtAdjstRowAmnt);
			if (!Const.feeCodeList.get(i).Description.equals("Estimated Fare")) {
				Const.feeCodeList.get(i).Amount = (float) Double
						.parseDouble((adjstTemp.getText().toString().trim() != null && !adjstTemp
								.getText().toString().trim().equals("")) ? adjstTemp
								.getText().toString().trim()
								: "0.0");
				adjustTotal = (Const.feeCodeList.get(i).Amount + adjustTotal);
			}
		}

		return adjustTotal;

	}

	private void calculateTotalFare(double quoted) {
		Const.paymentSubTotalAmnt = quoted;
		txtScanCardSubTotal.setText(new DecimalFormat("#####.00")
				.format((quoted)) + "");
	}

	protected Handler handler = new Handler();

	private void showProcessingDialog(String msg) {
		if (progressDialog == null)
			progressDialog = ProgressDialog.show(this, "", msg, true, false);
	}

	public void DisplayProcessMessage(final String msg) {
		showProcessingDialog(msg);
	}

	private void hideProcessingDialog() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}
    public void DisplayProcessMessage(final boolean hide) {
        hideProcessingDialog();
    }

    private ProcessingDialog authDialog = null;
    private void showAuthDialog() {
        if(authDialog == null) {
            authDialog = new ProcessingDialog(this);
            authDialog.show();
        }
    }

    private void hideAuthDialog() {
        if (authDialog != null) {
            authDialog.dismiss();
            authDialog = null;
        }
    }



	private void ConnectBTMag() {
		System.out.println("=============Is Available::::::::::::::::" + mIDTechHelper.isAvailable());

		if (mIDTechHelper.isAvailable())
			return;

		if (EnableBluetooth()) {
			Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
					.getBondedDevices();

			for (BluetoothDevice device : pairedDevices) {
				System.out.println("=============Devices::::::::::::::::"+ device.getName());

				if (device.getName().contains("BT MAG")) {
					mIDTechHelper.connect(device);
					// mIDTechHelper.setDefault();
					//
					onConnecting();
					return;
				}
			}

		}

	}

	@Override
	protected void onDestroy() {

		mIDTechHelper.close();
		// unregisterReceiver(receiver);
		super.onDestroy();
	}

	public void onConnecting() {
		DisplayProcessMessage("Connecting to BT Mag Reader!");
		txtScanCardMessage.setText("Connecting Card Reader!");
		txtScanCardStatus.setText("Initializing...");
		hideButton();
	}

	@Override
	public void onConnectedError(int arg0, String arg1) {
		System.out.println("=======EROR MESSAGE BTMAG:::::::::" + arg1);
		DisplayProcessMessage(true);
		txtScanCardMessage.setText(arg1);

        Utils.showToastAlert(PaymentActivityBT.this, arg1);

        setUICardStatusWarning();

		showReconnectButton();
	}

	@Override
	public void onReceivedData(int arg0, byte[] arg1) {

		DisplayProcessMessage(true);
		BTMagService.bIsSwiped = true;
		_strMSRData = IDTechUtils.byteToHexString(arg1);

		String strMessage = "Credit card swiped OK!\n";
		if (txtScanCardQuotedAmnt.getText().toString().length() == 0) {
			txtScanCardMessage.setText(strMessage
					+ "Please click on Submit button.");
			showDoneButton();
			return;
		}
		txtScanCardMessage.setText(strMessage
				+ "Please click on Submit button.");
		showDoneButton();
	}

	@Override
	public void onReceivedFailed(int arg0) {
        //show Dialog unale
        SimpleInfoDialog unableReadCard = new SimpleInfoDialog(PaymentActivityBT.this, SimpleInfoDialog.FAIL_DIALOG);
        unableReadCard.show();
	}

	@Override
	public void onReceivedSuccess(int arg0) {

	}

	public void onConnectedError(String errorMsg) {

		DisplayProcessMessage(true);

		if (nCount < 3) {
			Intent intent = new Intent(Const.CONNECT_BT_MAG_ACTION);
			startService(intent);
		} else {
			showReconnectButton();
			nCount = 0;
			txtScanCardMessage.setText(errorMsg);
		}

	}

	public void onDisconnected() {
		showReconnectButton();

		Utils.showToastAlert(PaymentActivityBT.this, "BTMag Disconnected.");
		txtScanCardMessage.setText("Card Reader is disconnected!");

        setUICardStatusWarning();
	}

	private void showReconnectButton() {

		txtScanCardMessage.setText("BTMag is not connected,\n Please try Reconnect!");

		BTMagService.bIsConnected = false;
		llScanCardBtns.setVisibility(View.VISIBLE);
		txtScanCardSubmit.setVisibility(View.GONE);

        setUICardStatusWarning();
	}

	private void showDoneButton() {
		if (BTMagService.bIsConnected == false)
			return;

		txtScanCardSubmit.setText("Submit");
		txtScanCardSubmit.setVisibility(View.VISIBLE);
		llScanCardBtns.setVisibility(View.GONE);
	}

	private void hideButton() {
		txtScanCardSubmit.setVisibility(View.GONE);
		llScanCardBtns.setVisibility(View.GONE);
	}

	public void onConnected() {

		BTMagService.bIsConnected = true;

		DisplayProcessMessage(true);
		hideButton();
		txtScanCardMessage.setText("Please Scan card.");
		txtScanCardStatus.setText("Ready");

        setUICardStatusAccepted();

		Utils.showToastAlert(PaymentActivityBT.this, "BTMag Connected.");

	}

	public boolean EnableBluetooth() {
		this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (this.mBluetoothAdapter != null) {
			if (this.mBluetoothAdapter.isEnabled()) {
				return true;
			}
			this.mBluetoothAdapter.enable();

			if (!this.mBluetoothAdapter.isEnabled()) {

				Toast.makeText(this, "Bluetooth is turned off",
						Toast.LENGTH_LONG).show();
				return false;
			}
		}

		else {
			Toast.makeText(this, "Bluetooth is turned off", Toast.LENGTH_LONG)
					.show();
			return false;
		}

		return false;
	}

	public static void showTransactionCompleteAlert(final Context context, final int flagFrom, String msg,final OnCustPopUpDialogButoonClickListener lisner) {
		/*final Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.trasaction_complete_dialog);
		dialog.setCancelable(true);
		dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.getWindow().getAttributes().width = LayoutParams.FILL_PARENT;

		TextView txtHeader = (TextView) dialog.findViewById(R.id.txtHeader);
		TextView txtApprvMessage = (TextView) dialog
				.findViewById(R.id.txtApprvMessage);
		TextView txtTraCmpMessage = (TextView) dialog
				.findViewById(R.id.txtTraCmpMessage);
		TextView txtTraCmpDone = (TextView) dialog
				.findViewById(R.id.txtTraCmpDone);

		if (flagFrom == 0) {
			txtHeader.setText("Card Declined");
			txtApprvMessage.setVisibility(View.INVISIBLE);
			txtTraCmpMessage.setText(msg);
		} else {
			txtHeader.setText("Card has been charged");
			txtApprvMessage.setVisibility(View.INVISIBLE);
			txtTraCmpMessage.setVisibility(View.INVISIBLE);
		}

		txtTraCmpDone.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (flagFrom == 0) {
					if (lisner != null)
						lisner.OnButtonClick(flagFrom,
								AlertDailogView.BUTTON_OK);
				} else {

					((Activity) context).finish();
				}
				dialog.dismiss();

			}
		});

		dialog.show();*/
        SimpleInfoDialog dialog = null;
        if (flagFrom == 0) {
            dialog = new SimpleInfoDialog(context, SimpleInfoDialog.DECLINE_DIALOG);
        } else {
            dialog = new SimpleInfoDialog(context, SimpleInfoDialog.SUCCESS_DIALOG);
        }
	}

	@SuppressLint("HandlerLeak")
	private Handler handlerWeb = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {
				if (msg.arg1 == 0) {
					if(Const.IS_KNOWN_PASSENGER == 1)
					{
						sendEmailToPassenger(Const.currentRecipt+"");
					}
					else{
					    showTransactionCompleteAlert(PaymentActivityBT.this, 1, "",
							PaymentActivityBT.this);
					}
				} else {
					BTMagService.bIsSwiped = false;
					// clear Card data - The user should reset the card
					// again.
					_strMSRData = "";
					txtScanCardMessage.setText("Error found while processing!\nPlease try again");
					showTransactionCompleteAlert(
							PaymentActivityBT.this,
							0,
							"Last transaction was unsuccessful. Please try again",
							PaymentActivityBT.this);
				}
			}
			else if (msg.what == 2) {
				if (msg.arg1 == 0) {
					showTransactionCompleteAlert(PaymentActivityBT.this, 1, "",
							PaymentActivityBT.this);
				} else {
					BTMagService.bIsSwiped = false;
					// clear Card data - The user should reset the card
					// again.
					_strMSRData = "";
					txtScanCardMessage
							.setText("Error found while processing!\nPlease try again");
					showTransactionCompleteAlert(
							PaymentActivityBT.this,
							0,
							"Last transaction was unsuccessful. Please try again",
							PaymentActivityBT.this);
				}
			}
		}
	};

	public void sendEmailToPassenger(final String transactionid) {
		if (!isNetworkAvailable()) return;

		DisplayProcessMessage(getResources().getString(R.string.driver_common_please_wait));

		HttpEmailPassengerRequest httpEmailPassengerRequest = new HttpEmailPassengerRequest();
		httpEmailPassengerRequest.LocalTime = Utils.millisToSecond(System.currentTimeMillis());
		httpEmailPassengerRequest.InvoiceID = Const.paymentInvoiceId;
		httpEmailPassengerRequest.TransactionID = transactionid;
		httpEmailPassengerRequest.ReservationID = Const.paymentReservationId;
		httpEmailPassengerRequest.PaymentMethod = Const.CREDIT_CARD_PAYMENT_METHOD;

		NetworkApi api = (new NetworkService()).getApi();
		api.sendEmailToPassenger(httpEmailPassengerRequest)
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
						if(stringJsonResponse.IsSuccess) {
							showTransactionCompleteAlert(PaymentActivityBT.this, 1, "",
									PaymentActivityBT.this);
						} else {
								BTMagService.bIsSwiped = false;
								// clear Card data - The user should reset the card
								// again.
								_strMSRData = "";
								txtScanCardMessage
										.setText("Error found while processing!\nPlease try again");
								showTransactionCompleteAlert(
										PaymentActivityBT.this,
										0,
										"Last transaction was unsuccessful. Please try again",
										PaymentActivityBT.this);
						}
					}
				});
	}

	public void ProcessPay() {
		BTMagService.bIsSwiped = false;
		if (!Utils.isOnline(PaymentActivityBT.this)) {
			return;
		}

		//DisplayProcessMessage("Processing Transaction..");
        showAuthDialog();

		new Thread() {
			public void run() {

				double data = callWebService(_strTip, _strMSRData, _strAmount);

				Log.i("log_info", "data is " + data);

				if (data != 0) {
					//DisplayProcessMessage(true);
                    hideAuthDialog();
					Log.i("log_info", "transaction successful");
					Const.currentRecipt = data;
					// mIDTechHelper.close();

					// ClearValues();

					msg.what = 1;
					msg.arg1 = 0;
					handlerWeb.sendMessage(msg);
					// finish();
					this.interrupt();
					return;
				}

				else {

					//DisplayProcessMessage(true);
                    hideAuthDialog();
					msg.what = 1;
					msg.arg1 = 1;
					handlerWeb.sendMessage(msg);
					this.interrupt();
					return;

				}

			}

		}.start();
	}

	@Override
	public void OnButtonClick(int tag, int buttonIndex) {
		switch (tag) {
		case 0:
			hideButton();
			ConnectBTMag();
			break;

		}

	}

}
