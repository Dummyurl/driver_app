package com.rideeaze.services.network.model.request;

import com.rideeaze.services.network.model.data.HttpFeecode;

import java.util.List;

public class HttpEmailReceipt {
	public String DriverToken;
    public String PaymentMethod;
	public String PassengerEmail;
	public int LocalTime;
    public String TransactionID;
	public List<HttpFeecode> LineItems;
	public String Signature;
}
