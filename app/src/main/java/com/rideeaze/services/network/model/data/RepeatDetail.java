package com.rideeaze.services.network.model.data;

/**
 * Created by bog on 26.01.2016.
 */
public class RepeatDetail {

    public RepeatDetail() {
        this.summary = new SummaryDetail();
    }

    public String dateFrom;
    public String dateTo;
    public SummaryDetail summary;

    public class SummaryDetail {
        public SummaryDetail() {
            this.days = new WeekDaysFlag();
        }

        public WeekDaysFlag days;
    }
}
