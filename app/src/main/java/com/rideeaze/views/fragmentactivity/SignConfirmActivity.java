package com.rideeaze.views.fragmentactivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.rideeaze.R;
import com.rideeaze.services.network.NetworkApi;
import com.rideeaze.services.network.NetworkService;
import com.rideeaze.services.network.model.data.HttpFeecode;
import com.rideeaze.services.network.model.request.HttpEmailPassengerRequest;
import com.rideeaze.services.network.model.request.HttpEmailReceipt;
import com.rideeaze.services.network.model.response.JsonResponse;
import com.rideeaze.uc.AlertDailogView;
import com.rideeaze.uc.AlertDailogView.OnCustPopUpDialogButoonClickListener;
import com.rideeaze.views.activity.DriverActivity;
import com.util.Const;
import com.util.StorageDataHelper;
import com.util.Utils;

import java.io.ByteArrayOutputStream;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SignConfirmActivity extends DriverActivity implements
		OnCheckedChangeListener, OnClickListener,
		OnCustPopUpDialogButoonClickListener {
	private static final float STROKE_WIDTH = 3f;
	private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
	private RadioButton rbPayCrdtcard;
	private RadioButton rbPayCash;
	private TextView txtSignConfrmNext;
	private ProgressDialog progressDialog = null;
	private static String emailPassenger;
	private List<HttpFeecode> _feeCodeList;
	private static String _signatureIamge;
	private Message msg;
	private LinearLayout llSignCanvas;

	private CanvasView objCanvas;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.sign_confirm_activity);
		msg = new Message();
		rbPayCrdtcard = (RadioButton) findViewById(R.id.rbPayCrdtcard);
		rbPayCash = (RadioButton) findViewById(R.id.rbPayCash);
        rbPayCash.setChecked(true);
		txtSignConfrmNext = (TextView) findViewById(R.id.txtSignConfrmNext);
		llSignCanvas = (LinearLayout) findViewById(R.id.llSignCanvas);

		txtSignConfrmNext.setOnClickListener(this);
		rbPayCrdtcard.setOnCheckedChangeListener(this);
		rbPayCash.setOnCheckedChangeListener(this);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		Const.width = llSignCanvas.getWidth();
		Const.height = llSignCanvas.getHeight();

		if (llSignCanvas.getChildCount() == 0) {
			objCanvas = new CanvasView(SignConfirmActivity.this);
			llSignCanvas.addView(objCanvas);
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.rbPayCrdtcard:

			break;
		case R.id.rbPayCash:

			break;

		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.txtSignConfrmNext:
			CanvasView objSaveView = (CanvasView) llSignCanvas.getChildAt(0);
			objSaveView.saveImage(objSaveView);
			showSendEmailDialog(SignConfirmActivity.this,
                    rbPayCrdtcard.isChecked(), SignConfirmActivity.this);
			break;

		}

	}

	/*private List<HttptFeecode> generateFeeCodeList() {
		_feeCodeList = new ArrayList<HttptFeecode>();
		for (HttptFeecode feeCodeBean : Const.feeCodeList) {
			if (feeCodeBean.Amount != 0.0) {
				_feeCodeList.add(feeCodeBean);
			}
		}
		return _feeCodeList;
	}*/

	public static void showSendEmailDialog(final Context context,
			final boolean paybyCrdtCrd,
			final OnCustPopUpDialogButoonClickListener litner) {
		final Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.send_email_dialog);
		dialog.setCancelable(true);
		dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.getWindow().getAttributes().width = LayoutParams.FILL_PARENT;

		TextView txtEmailRctDone = (TextView) dialog
				.findViewById(R.id.txtEmailRctDone);
		final EditText edtSndEmailId = (EditText) dialog
				.findViewById(R.id.edtSndEmailId);

		txtEmailRctDone.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				if (edtSndEmailId.getText().toString().trim() != null
						&& !edtSndEmailId.getText().toString().trim()
								.equals("")) {
					emailPassenger = edtSndEmailId.getText().toString().trim();
					Const.paymentUserEmail = emailPassenger;
					Const.signature = _signatureIamge;
					if (paybyCrdtCrd) {
						if (litner != null)
							litner.OnButtonClick(0, AlertDailogView.BUTTON_OK);
					} else {
						if (litner != null)
							litner.OnButtonClick(0,
									AlertDailogView.BUTTON_CANCEL);
					}
					dialog.dismiss();
				} else {
					AlertDailogView.showAlert(context,
							"Plaease enter your Email.", "Ok").show();
				}
			}
		});

		dialog.show();
	}

    public void emailReceiptForPassenger(final String transactionid) {
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
						if (stringJsonResponse.IsSuccess) {
							startActivity(new Intent(SignConfirmActivity.this,
									PassengerActivity.class).putExtra("fromPaymentProcess", 1));
							finish();
						}
					}
				});
    }

	public void emailReceiptForUnknownPassenger() {

		if (!isNetworkAvailable()) return;

		DisplayProcessMessage(getResources().getString(R.string.driver_common_please_wait));

		HttpEmailReceipt emailReceipt = new HttpEmailReceipt();
		emailReceipt.DriverToken = StorageDataHelper.getInstance(getApplicationContext()).getAuthToken();
		emailReceipt.TransactionID = Const.paymentPassengerId;
		emailReceipt.PaymentMethod = Const.CASH_PAYMENT_METHOD;
		emailReceipt.LocalTime = Utils.millisToSecond(System
				.currentTimeMillis());
		emailReceipt.LineItems = EnterAmountActivity.generateFeeCodeList();
		emailReceipt.PassengerEmail = emailPassenger;
		emailReceipt.Signature = _signatureIamge;

		NetworkApi api = (new NetworkService()).getApi();
		api.sendEmailReceiptPassenger(emailReceipt)
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
							startActivity(new Intent(SignConfirmActivity.this, PassengerActivity.class)
									.putExtra("fromPaymentProcess",1));
							finish();
						}
					}
				});



	}

	@Override
	public void OnButtonClick(int tag, int buttonIndex) {
		switch (tag) {
		case 0:
			if (buttonIndex == AlertDailogView.BUTTON_OK) {
				startActivity(new Intent(SignConfirmActivity.this,
						PaymentActivityBT.class));
			} else if (buttonIndex == AlertDailogView.BUTTON_CANCEL) {
                if(Const.IS_KNOWN_PASSENGER == 1) {
                    emailReceiptForPassenger(Const.currentRecipt+"");
                } else {
                    emailReceiptForUnknownPassenger();
                }
			}
			break;
		}

	}

	public static class CanvasView extends View {

		private Bitmap mBitmap;
		private Canvas mCanvas;
		private Path mPath = new Path();
		private Paint mPaint;
		private float mCurX, mCurY;
		private static final float TOUCH_TOLERANCE = 1;
		private final RectF mRect = new RectF();

		public CanvasView(Context c) {
			super(c);

			mPaint = new Paint();
			mPaint.setAntiAlias(true);

			mPaint.setColor(Color.BLACK);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStrokeJoin(Paint.Join.ROUND);
			mPaint.setStrokeCap(Paint.Cap.ROUND);
			mPaint.setStrokeWidth(STROKE_WIDTH);
		}

		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			super.onSizeChanged(w, h, oldw, oldh);
		}

		@Override
		protected void onDraw(Canvas canvas) {

			canvas.drawPath(mPath, mPaint);
		}

		private void touch_start(float x, float y) {

			mPath.moveTo(x, y);
			mCurX = x;
			mCurY = y;

		}

		private void touch_move(float x, float y) {
			float dx = Math.abs(x - mCurX);
			float dy = Math.abs(y - mCurY);
			if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
				mPath.quadTo(mCurX, mCurY, (x + mCurX) / 2, (y + mCurY) / 2);
				mCanvas.drawPath(mPath, mPaint);
				mCurX = x;
				mCurY = y;
			}
		}

		private void touch_up(MotionEvent event) {
			float eventX = event.getX();
			float eventY = event.getY();

			resetRect(eventX, eventY);
			int historySize = event.getHistorySize();
			for (int i = 0; i < historySize; i++) {
				float historicalX = event.getHistoricalX(i);
				float historicalY = event.getHistoricalY(i);
				expandDirtyRect(historicalX, historicalY);
				mPath.lineTo(historicalX, historicalY);
			}
			mPath.lineTo(eventX, eventY);
		}

		private void resetRect(float eventX, float eventY) {
			mRect.left = Math.min(mCurX, eventX);
			mRect.right = Math.max(mCurX, eventX);
			mRect.top = Math.min(mCurY, eventY);
			mRect.bottom = Math.max(mCurY, eventY);
		}

		private void expandDirtyRect(float historicalX, float historicalY) {
			if (historicalX < mRect.left) {
				mRect.left = historicalX;
			} else if (historicalX > mRect.right) {
				mRect.right = historicalX;
			}
			if (historicalY < mRect.top) {
				mRect.top = historicalY;
			} else if (historicalY > mRect.bottom) {
				mRect.bottom = historicalY;
			}
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			float x = event.getX();
			float y = event.getY();

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				touch_start(x, y);

				return true;
			case MotionEvent.ACTION_MOVE:

			case MotionEvent.ACTION_UP:
				touch_up(event);
				this.saveImage(this);

				break;
			default:
				return false;
			}
			invalidate((int) (mRect.left - HALF_STROKE_WIDTH),
					(int) (mRect.top - HALF_STROKE_WIDTH),
					(int) (mRect.right + HALF_STROKE_WIDTH),
					(int) (mRect.bottom + HALF_STROKE_WIDTH));

			mCurX = x;
			mCurY = y;

			return true;
		}

		public void saveImage(View view) {
			Bitmap b = Bitmap.createBitmap(Const.width, Const.height,
					Bitmap.Config.ARGB_8888);

			Canvas c = new Canvas(b);
			view.draw(c);
			ByteArrayOutputStream stream = null;
			try {
				stream = new ByteArrayOutputStream();
				b.compress(Bitmap.CompressFormat.PNG, 100, stream);
				_signatureIamge = Base64.encodeToString(stream.toByteArray(),
						Base64.DEFAULT);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}
