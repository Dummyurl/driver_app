package com.rideeaze.views.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.rideeaze.R;

public class DriverCreateAccountActivity extends DriverActivity implements OnClickListener {

    EditText companyName;
    EditText companyTelNo;
    EditText mobileNo;
    EditText driverName;

    Button ok;

    @Override

    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_create_account);

        //companyName=(EditText)findViewById(R.id.companyName);
        //companyTelNo=(EditText)findViewById(R.id.companyNumber);
        //mobileNo=(EditText)findViewById(R.id.mobileNumber);
        //driverName=(EditText)findViewById(R.id.driverName);

        //ok=(Button)findViewById(R.id.createAccount);
        ok.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

        if (v == ok) {
            String comName = companyName.getText().toString();
            String comNo = companyTelNo.getText().toString();
            String mobileNumber = mobileNo.getText().toString();
            String drvName = driverName.getText().toString();

            if (comName.length() == 0) {
                companyName.setError("Please Input Company Name");
                return;
            }


            if (comNo.length() == 0) {
                companyTelNo.setError("Please Input Company Tel Number");
                return;
            }

            if (mobileNumber.length() == 0) {
                mobileNo.setError("Please Input Mobile Number");
                return;
            }


            if (drvName.length() == 0) {
                driverName.setError("Please Input Driver Name");
                return;
            }


            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isLoggedIn", true);
            editor.commit();
            finish();

        }

    }

}
