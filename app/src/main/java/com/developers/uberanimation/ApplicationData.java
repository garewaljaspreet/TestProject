package com.developers.uberanimation;

import android.content.Context;
import android.graphics.Typeface;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.widget.TextView;

import com.developers.uberanimation.network.NetworkService;


/**
 * Created by navdeepg on 11/23/2015.
 */
public class ApplicationData extends MultiDexApplication{

    public static boolean activityVisible=false;
    public static boolean isChatShown=false;
    private static ApplicationData mInstance;
    private NetworkService networkService;

    public String getChatChannelActive() {
        return chatChannelActive;
    }

    public void setChatChannelActive(String chatChannelActive) {
        this.chatChannelActive = chatChannelActive;
    }

    private String chatChannelActive;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        networkService = new NetworkService(getApplicationContext());
    }
    public NetworkService getNetworkService(){
        return networkService;
    }
    @Override
    protected void attachBaseContext(Context base) {
        MultiDex.install(this);
        super.attachBaseContext(base);

    }

    public static synchronized ApplicationData getInstance() {
        return mInstance;
    }


}
