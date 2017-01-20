package com.rideeaze.services.network.model.request;

import com.rideeaze.services.network.model.data.DriverShedule;

import java.util.List;

/**
 * Created by bog on 26.01.2016.
 */
public class HttpUpdateDriverschedule {
    public String DriverCode;
    public List<DriverShedule> TimeOff;
}
