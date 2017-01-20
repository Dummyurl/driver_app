package com.rideeaze.services.network.model.response;

import com.rideeaze.services.network.model.data.HttpGetInvoice;

import java.io.Serializable;
import java.util.List;

public class HttpGetInvoiceData implements Serializable {

	public List<HttpGetInvoice> InvoiceItems;
	public int PaymentID;
	public String PaymentMethod;
	public String MerchantId;
	public String FulFillingMerchantId;
	public String TransactionId;
	public String PassengerName;
	public String UserName;
	public int MostRecentStateTimestamp;
	public boolean DepositExported;
	public boolean Exists;
	public boolean IsCaptive;
	public boolean IsFarmed;
}
