package com.rideeaze.services.network.model.request;

import com.rideeaze.services.network.model.data.HttpFeecode;

import java.util.List;

public class HttpSendInvoiceToPassengerRequest {
	public int ReservationID;
	public String PassengerID;
	public boolean Tip;
	public List<HttpFeecode> LineItems;
    public boolean UpdateInvoiceOnly;
}
