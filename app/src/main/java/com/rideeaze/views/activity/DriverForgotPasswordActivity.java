package com.rideeaze.views.activity;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.rideeaze.R;

/**
 * Created by kvofreelance on 29.12.2014.
 */
public class DriverForgotPasswordActivity extends DriverActivity{
    EditText edtForgotEmail;
    Button btn_send;

    private Message msg;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_activity_forgot);

        //forbid showing icon on action bar
        getActionBar().setDisplayShowHomeEnabled(false);

        edtForgotEmail = (EditText) findViewById(R.id.edtForgotEmail);

        btn_send = (Button) findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = edtForgotEmail.getText().toString().trim();
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                if (mail.length() == 0) {
                    edtForgotEmail.setError(getString(R.string.driver_error_invalid_email));
                    return;
                }
                if (!mail.matches(emailPattern)) {
                    edtForgotEmail.setError(getString(R.string.driver_error_invalid_validate_email));
                    return;
                }

                //ChangeDriverPassword(getBaseContext(), mail);
            }
        });
    }

    /*
    //@SuppressLint("HandlerLeak")
    private Handler handlerWeb = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {
                if (msg.arg1 == 0) {
                    AlertDailogView
                            .showAlert(
                                    getBaseContext(),
                                    "An email with a link to reset your password has been sent to you",
                                    "Ok", this).show();
                }
            }
        }
    };

    private void ChangeDriverPassword(final Context context,
                                      final String userName) {

        if (!networkService.isNetworkAvailable()) {
            Toast.makeText(
                    context,
                    "There is no Internet Connection. Please try after sometimes",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        //DisplayProcessMessage("Please wait...");

        new Thread() {
            public void run() {

                JsonResponseObj<String> response = userViewModel
                        .ChangeDriverPassword(userName);
                if (response.IsSuccess) {
                    //DisplayProcessMessage(true);
                    msg = new Message();
                    msg.what = 1;
                    msg.arg1 = 0;
                    handlerWeb.sendMessage(msg);
                    this.interrupt();
                    return;
                } else {
                    //DisplayProcessMessage(true);
                    this.interrupt();
                    //getDialog().dismiss();
                    return;
                }

            }
        }.start();

    }*/

}
