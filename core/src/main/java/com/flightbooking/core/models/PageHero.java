package com.flightbooking.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

@Model(adaptables= Resource.class)
public class PageHero {

    @Inject
    @Optional
    @Named("jcr:title")
    private String title;

    @Inject
    @Optional
    @Named("heading")
    private String heading;

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

    public String getTitle() {
        return title;
    }

    public String getHeading() {
        return heading;
    }

    public String getButtonLabel() {
        return buttonLabel;
    }

    public String getButtonLinkTo() {
        return buttonLinkTo;
    }
}
