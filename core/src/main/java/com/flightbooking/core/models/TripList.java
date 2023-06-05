package com.flightbooking.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

@Model(adaptables= Resource.class)
public class TripList {

    @Inject
    @Optional
    @Named("tripName")
    private String tripName;
    @Inject
    @Optional
    @Named("flightName")
    private String flightName;
    @Inject
    @Optional
    @Named("tripDate")
    private String tripDate;
    @Inject
    @Optional
    @Named("tripTime")
    private String tripTime;
    @Inject
    @Optional
    @Named("tripDestination")
    private String tripDestination;
    @Inject
    @Optional
    @Named("tripOrigin")
    private String tripOrigin;
    @Inject
    @Optional
    @Named("seatAvailability")
    private String seatAvailability;
    @Inject
    @Optional
    @Named("tripDescription")
    private String tripDescription;
    @Inject
    @Optional
    @Named("buttonLabel")
    private String buttonLabel;
    @Inject
    @Optional
    @Named("buttonLinkTo")
    private String buttonLinkTo;


    @PostConstruct
    public void init(){

    }

    public String getTripName() {
        return tripName;
    }

    public String getFlightName() {
        return flightName;
    }

    public String getTripDate() {
        return tripDate;
    }

    public String getTripTime() {
        return tripTime;
    }

    public String getTripDestination() {
        return tripDestination;
    }

    public String getTripOrigin() {
        return tripOrigin;
    }

    public String getSeatAvailability() {
        return seatAvailability;
    }

    public String getTripDescription() {
        return tripDescription;
    }

    public String getButtonLabel() {
        return buttonLabel;
    }

    public String getButtonLinkTo() {
        return buttonLinkTo;
    }
}
