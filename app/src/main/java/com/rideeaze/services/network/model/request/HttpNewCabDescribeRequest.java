package com.rideeaze.services.network.model.request;

import com.rideeaze.services.network.model.data.HttpAccepts;
import com.rideeaze.services.network.model.data.HttpLocation;
import com.rideeaze.services.network.model.data.HttpVehicleShape;

public class HttpNewCabDescribeRequest {

    public String Token;
    public String TaxiID;

    public HttpVehicleShape VehicleShape;

    public String Year;
    public String Model;
    public String Make;
    public String MaxPassenger;

    public HttpAccepts Accepts;
    public HttpLocation Location;

    public String DevOS;
    public String PhoneToken;
}
