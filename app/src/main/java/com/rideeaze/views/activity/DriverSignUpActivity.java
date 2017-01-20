package com.rideeaze.views.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.rideeaze.R;
import com.util.Const;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by kvofreelance on 18.12.2014.
 */
public class DriverSignUpActivity extends DriverActivity{

    @Bind(R.id.driver_activity_signup_email)
    EditText et_email;
    @Bind(R.id.driver_activity_signup_password)
    EditText et_password;
    @Bind(R.id.driver_activity_signup_confirm_password)
    EditText et_confirm_password;

    @OnClick(R.id.driver_activity_signup_next)
    public void goToNextAction() {
        String email = et_email.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        String passwordConfirm = et_confirm_password.getText().toString().trim();

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (email.length() == 0) {
            et_email.setError(getString(R.string.driver_error_invalid_email));
            return;
        }
        if (!email.matches(emailPattern)) {
            et_email.setError(getString(R.string.driver_error_invalid_validate_email));
            return;
        }
        if (password.length() == 0) {
            et_password.setError(getString(R.string.driver_error_invalid_password));
            return;
        }
        if (passwordConfirm.length() == 0 || !password.equals(passwordConfirm)) {
            et_confirm_password.setError(getString(R.string.driver_error_non_match_password));
            return;
        }

        Intent intent = new Intent(DriverSignUpActivity.this, DriverSignUpRegisterActivity.class)
                .putExtra(Const.REGISTER_TEMP_USERNAME, et_email.getText().toString())
                .putExtra(Const.REGISTER_TEMP_PASSWORD, et_password.getText().toString());
        startActivity(intent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_activity_signup);

        ButterKnife.bind(this);

        getActionBar().setDisplayShowHomeEnabled(false);
    }
}
