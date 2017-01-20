package com.rideeaze.services.network.model.response;

import com.rideeaze.services.network.model.data.HttpDistance;
import com.rideeaze.services.network.model.data.HttpFormOfPayment;
import com.rideeaze.services.network.model.data.HttpLocation;

import java.io.Serializable;

public class HttpPendingRequestPickUp  implements Serializable {

	public int ReservationID;
	public String PassengerName;
	public String PassengerID;

	public HttpLocation DestinationLocation;
	public HttpLocation PickupLocation;
	public boolean HaveFlightInfo;
	public long TimeOfPickup;

    public boolean OKToSendEmailReceipt;

	public int NumberOfPassenger;
	public Integer NumberOfPassSuit;
	public double EstimateFare;
	public long EstimateTime;
	public String PassengerPhoneNumber;

	public int Rating;
	public int ReviewNumber;

	public HttpDistance Distance;
	public String ReservationStatus;
	public String ReservationSubStatus;
	public String SpecialInstructions;
    public boolean InvoicePad;
	public boolean IsCaptive;
	public String MerchantId;
	public String PaymentID;
	public int InvoiceID;
	public int TripNumber;

	public String VehicleDescription;

    public HttpFormOfPayment PayCollectMethod;

}
