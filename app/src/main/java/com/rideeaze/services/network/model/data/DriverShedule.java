package com.rideeaze.services.network.model.data;

/**
 * Created by bog on 26.01.2016.
 */
public class DriverShedule {
    public DriverShedule() {
        this.repeat_details = new RepeatDetail();
    }

    public int id;
    public String reason_for_absence;
    public String from;
    public String to;
    public boolean repeat;
    public String rctype;
    public boolean whole;
    public RepeatDetail repeat_details;
}
