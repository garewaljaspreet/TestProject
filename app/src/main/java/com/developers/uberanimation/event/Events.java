package com.developers.uberanimation.event;

import android.os.Bundle;

import com.developers.uberanimation.BeansMessage;


/**
 * Created by navdeepg on 3/2/2017.
 */

public class Events {
    private BeansMessage message;
    private String eventType;
    private Bundle bundle;

    public Events(String eventType, BeansMessage message, Bundle infoBundle)
    {
        this.bundle=infoBundle;
        this.eventType=eventType;
        this.message=message;
    }
    public BeansMessage getMessage() {
        return message;
    }
    public String getEventType() {
        return eventType;
    }
    public Bundle getInfoBundle() {
        return bundle;
    }
}
