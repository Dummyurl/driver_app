package com.rideeaze.payment.service;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="Result")
public class CardPaymentResponse {
	
	@Element(required=false)
	public String AuthCode;
     
	@Element(required=false)
	public String Approved;
     
	@Element(required=false)
	public String Cvv2Reply;
     
	@Element(required=false)
	public String AvsReply;
     
	@Element(required=false)
	public long GateId;
     
	@Element(required=false)
	public long BankId;
    
	@Element(required=false)
	public String CorporateCardIndicator;
     
	@Element(required=false)
	public long InvoiceNo;
     
	@Element(required=false)
	public String FirstName;
     
	@Element(required=false)
	public String LastName;
     
	@Element(required=false)
	public String CardType;
	
	@Element(required=false)
	public int Last4;
    
	@Element(required=false)
	public int ExpMonth;
     
	@Element(required=false)
	public int ExpYear;
     
	@Element(required=false)
	public String ProcessorToken;
    
	@Element(required=false)
	public String ApprovedHsaAmt;
     
	@Element(required=false)
	public double ApprovedAmt;
     
	@Element(required=false)
	public double Bal;
     
	@Element(required=false)
	public int ResponseCode;
     
	@Element(required=false)
	public String Response;
     
	@Element(required=false)
	public String Description;
    
	@Element(required=false)
	public String ResponseUrl;
	
	@Element(required=false)
	public String UserId;
	
	@Element(required=false)
	public String ReceiptId;

	public CardPaymentResponse(@Element(name="AuthCode")String authCode, 
			@Element(name="Approved") String approved,
			@Element(name="Cvv2Reply") String cvv2Reply, 
			@Element(name="AvsReply") String avsReply, 
			@Element(name="GateId") long gateId, 
			@Element(name="BankId") long bankId,
			@Element(name="CorporateCardIndicator") String corporateCardIndicator, 
			@Element(name="InvoiceNo") long invoiceNo, 
			@Element(name="FirstName") String firstName,
			@Element(name="LastName") String lastName, 
			@Element(name="CardType") String cardType, 
			@Element(name="Last4") int last4, 
			@Element(name="ExpMonth") int expMonth,
			@Element(name="ExpYear") int expYear, 
			@Element(name="ProcessorToken") String processorToken, 
			@Element(name="ApprovedHsaAmt") String approvedHsaAmt,
			@Element(name="ApprovedAmt") double approvedAmt, 
			@Element(name="Bal") double bal, 
			@Element(name="ResponseCode") int responseCode, 
			@Element(name="Response") String response,
			@Element(name="Description") String description, 
			@Element(name="ResponseUrl") String responseUrl,
			@Element(name="UserId") String userId,
			@Element(name="ReceiptId") String receiptId) {
		
		AuthCode = authCode;
		Approved = approved;
		Cvv2Reply = cvv2Reply;
		AvsReply = avsReply;
		GateId = gateId;
		BankId = bankId;
		CorporateCardIndicator = corporateCardIndicator;
		InvoiceNo = invoiceNo;
		FirstName = firstName;
		LastName = lastName;
		CardType = cardType;
		Last4 = last4;
		ExpMonth = expMonth;
		ExpYear = expYear;
		ProcessorToken = processorToken;
		ApprovedHsaAmt = approvedHsaAmt;
		ApprovedAmt = approvedAmt;
		Bal = bal;
		ResponseCode = responseCode;
		Response = response;
		Description = description;
		ResponseUrl = responseUrl;
		UserId = userId;
		ReceiptId = receiptId;
	}
    
	
}
