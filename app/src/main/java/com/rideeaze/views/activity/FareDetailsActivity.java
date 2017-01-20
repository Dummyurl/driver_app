package com.rideeaze.views.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.rideeaze.R;
import com.rideeaze.gcm.CommonUtilities;
import com.rideeaze.services.network.model.data.HttpGetInvoice;
import com.rideeaze.services.network.model.response.HttpGetInvoiceData;
import com.util.Const;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dns on 03.10.15.
 */
public class FareDetailsActivity extends Activity {
    WebView webView;
    HttpGetInvoiceData fareDetails = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fare_details);

        fareDetails = CommonUtilities.invoiceData;

        webView = (WebView)findViewById(R.id.webView);
        webView.setBackgroundColor(getResources().getColor(R.color.white));

        TextView tvFareOk = (TextView)findViewById(R.id.tvFareOk);
        tvFareOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (fareDetails != null) {
            List<String> feeDetailsList = new ArrayList<>();
            List<Float> fareList = new ArrayList<>();
            List<HttpGetInvoice> invoiceList = fareDetails.InvoiceItems;
            for (HttpGetInvoice invoiceItem : invoiceList) {
                feeDetailsList.add(invoiceItem.FeeDescription);
                fareList.add(invoiceItem.Amount);
            }
            fillFareDetails(feeDetailsList, fareList);
        } else {
            finish();
        }
    }

    protected void fillFareDetails(List<String> feeDetailList, List<Float> fareList) {
        String body = "<html><body><font color=red><p>Fare:</p></font>";
        Double totalFare = 0.0;
        for (int i = 0; i < feeDetailList.size(); i++) {
            String feeDesc = feeDetailList.get(i);
            float amount = fareList.get(i);
            totalFare += amount;

            body += "<p>" + feeDesc + ": " + String.format("$%.2f", amount) + "</p>";
        }
        body += "<hr><font color=red><p>Total Due: " + String.format("$%.2f", totalFare) + "</p></font>";
        body += "</body></html>";

        webView.loadData(body, "text/html", "UTF-8");
    }
}