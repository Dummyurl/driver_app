package com.rideeaze.uc;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Window;
import android.widget.TextView;

import com.rideeaze.R;

import static android.app.AlertDialog.THEME_HOLO_DARK;

public class AlertDailogView {
	// Handle Ok Button by current activity
	public static final int BUTTON_OK = 1;
	// Handle Cancel Button by current activity
	public static final int BUTTON_CANCEL = 2;
	// Handle Done Button by current activity
	public static final int BUTTON_DONE = 3;

	public static Dialog showAlert(Context context, String message) {
		return showAlert(context, context.getString(R.string.alert), message,
				context.getString(R.string.ok), false, "", "", false, null, 1);
	}

	public static Dialog showAlertTitle(Context context, String title,
			String message) {
		return showAlert(context, title, message,
				context.getString(R.string.ok), false, "", "", false, null, 1);
	}

	public static Dialog showAlert(Context context, String message,
			String btnText) {
		return showAlert(context, context.getString(R.string.alert), message,
				btnText, false, "", "", false, null, 1);
	}

	public static Dialog showAlert(Context context, String message,
			String btnText, OnCustPopUpDialogButoonClickListener clickListener) {
		return showAlert(context, context.getString(R.string.alert), message,
				btnText, false, "", "", false, clickListener, 1);
	}

	public static Dialog showAlert(Context context, String Title,
			String message, String btnText,
			OnCustPopUpDialogButoonClickListener clickListener, int tag) {
		return showAlert(context, Title, message, btnText, false, "", "",
				false, clickListener, tag);
	}

	public static Dialog showAlert(Context context, String message,
			String btnText, OnCustPopUpDialogButoonClickListener clickListener,
			int tag) {
		return showAlert(context, context.getString(R.string.alert), message,
				btnText, false, "", "", false, clickListener, tag);
	}

	public static Dialog showAlert(Context context, String title,
			String message, String btnText,
			OnCustPopUpDialogButoonClickListener clickListener) {
		return showAlert(context, title, message, btnText, false, "", "",
				false, clickListener, 1);
	}

	public static Dialog showAlert(Context context, String message,
			String btnTitle_1, boolean isCancelButton, String btnTitle_2,
			final OnCustPopUpDialogButoonClickListener clickListener,
			final int tag) {
		return showAlert(context, context.getString(R.string.alert), message,
				btnTitle_1, isCancelButton, btnTitle_2, "", false,
				clickListener, tag);
	}

	public static Dialog showAlert(Context context, String message,
			String btnTitle_1, String btnTitle_2, String btnTitle_3,
			final OnCustPopUpDialogButoonClickListener clickListener,
			final int tag) {
		return showAlert(context, context.getString(R.string.alert), message,
				btnTitle_1, true, btnTitle_2, btnTitle_3, true, clickListener,
				tag);
	}

	// Create Custom popup Alert dailog here
	@SuppressWarnings("deprecation")
	public static Dialog showAlert(Context context, String title,
			String message, String btnTitle_1, boolean isCancelButton,
			String btnTitle_2, String btnTitle_3, boolean isThirdButton,
			final OnCustPopUpDialogButoonClickListener clickListener,
			final int tag) {

		TextView remtitle = null;

		TextView remMsg = new TextView(context);
		remMsg.setText(message);
		remMsg.setGravity(Gravity.CENTER_HORIZONTAL);
		remMsg.setTextColor(Color.WHITE);

		if (!title.equals("")) {
			remtitle = new TextView(context);
			remtitle.setText(" " + title + " ");
			remtitle.setGravity(Gravity.CENTER_HORIZONTAL
					| Gravity.CENTER_VERTICAL);
			remtitle.setTextColor(Color.WHITE);
			remtitle.setTypeface(null, Typeface.BOLD);
			remtitle.setPadding(0, 5, 0, 5);
		}

		final AlertDialog alertDialog = new AlertDialog.Builder(context, THEME_HOLO_DARK)
				.create();
		alertDialog.setCancelable(false);

		if (!title.equals(""))
			alertDialog.setCustomTitle(remtitle);
		else
			alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		alertDialog.setView(remMsg);

		alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				alertDialog.getButton(Dialog.BUTTON1).setTextSize(
						TypedValue.COMPLEX_UNIT_SP, 11);

				alertDialog.getButton(Dialog.BUTTON2).setTextSize(
						TypedValue.COMPLEX_UNIT_SP, 11);

				alertDialog.getButton(Dialog.BUTTON3).setTextSize(
						TypedValue.COMPLEX_UNIT_SP, 11);
			}
		});

		alertDialog.setButton(btnTitle_1,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						if (clickListener != null)
							clickListener.OnButtonClick(tag,
									AlertDailogView.BUTTON_OK);

					}
				});

		if (isCancelButton) {
			alertDialog.setButton2(btnTitle_2,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

							dialog.dismiss();

							if (clickListener != null)
								clickListener.OnButtonClick(tag,
										AlertDailogView.BUTTON_CANCEL);

						}
					});
		}
		if (isThirdButton) {
			alertDialog.setButton3(btnTitle_3,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();

							if (clickListener != null)
								clickListener.OnButtonClick(tag,
										AlertDailogView.BUTTON_DONE);

						}
					});
		}
		return alertDialog;
	}

	// define Listener here
	public interface OnCustPopUpDialogButoonClickListener {
		public abstract void OnButtonClick(int tag, int buttonIndex);
	}

}
