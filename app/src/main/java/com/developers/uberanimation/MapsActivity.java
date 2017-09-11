package com.developers.uberanimation;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.developers.uberanimation.event.Events;
import com.developers.uberanimation.event.GlobalBus;
import com.developers.uberanimation.models.BeansPickAddress;
import com.developers.uberanimation.models.DirectionResults;
import com.developers.uberanimation.models.Route;
import com.developers.uberanimation.models.Steps;
import com.developers.uberanimation.network.NetworkService;
import com.developers.uberanimation.service.ChatterBoxService;
import com.developers.uberanimation.service.DefaultChatterBoxCallback;
import com.developers.uberanimation.service.binder.ChatterBoxClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MapsActivity extends FragmentActivity implements View.OnClickListener,OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = MapsActivity.class.getSimpleName();
    String startAdd,endAdd;
    SupportMapFragment mapFragment;
    private GoogleMap mMap;
    ImageView imgMenu,imgLoc;
    private Button btnFrom,btnTo;
    private AlertDialog dialog;
    int APP_STATE=0;
    CustomTextView txtCurrentLocation,txtStateUpdate;
    ImageView imgTaxi,imgRideShare,imgMyCar,imgPartition;
    CustomTextView txtClose,txtPrice,txtRequestRide,txtTaxi,txtTimeTaxi,txtRideShare,txtTimeRideshare,txtMyCar,txtChooseDriver,txtLocDest,txtCurrentLocPopup,txtDestLocPopup;
    RelativeLayout rlPickDest,rlPickStart,rlRating,rlMainRequestTaxiLay,rlPriceInfo,rlMainSetLocationLay,rlDriverFound,rlButtonMain;
    RelativeLayout rlRatingSubmit,rlSelect,rlProgress,rlCurrentLocation,rlDestLoc,rlSelectMain,rlTop;
    private List<LatLng> polyLineList;
    BeansMessage beansMessage;
    private Marker marker1,marker2,marker3,marker4,marker5;
    boolean IS_ADDRESS_SEARCHED=false;// check is address search is in process or not to avoid multiple calls
    String addressText;
    private Address addressObj;
    private Float zoomLevel=17f;//initial map zoom level
    private Location mLastLocation,mCurrentLoc;//Represents last known location
    ImageView imgCenterPin;
    ImageView btnPickDest,btnPick;
    private BeansPickAddress locationObj,oldLocationObj;
    private float v;
    private ChatterBoxClient chatterBoxServiceClient;       //To access service methods
    BeansAPNS beansAPNS;
    private double polyStartLat, polyStartLng,polyEndLat,polyEndLng;
    LatLng startPostion,endPosition;
    LatLng startLat,startLng,endLat,endLng;
    boolean IsStartSet=false,IsEndSet=false;
    Resources resources;
    private GoogleApiClient mGoogleApiClient;//Provides the entry point to Google Play services.
    private Handler handler;
    private Runnable progressUpdater;
    private AddressResultReceiver mResultReceiver;//Receiver registered with this activity to get the response from FetchAddressIntentService.
    private LatLng startPosition1,startPosition2, startPosition3,startPosition4,startPosition5;
    private int index, next;
    private LatLng sydney;
    private Button button;
    String url1="49.03475205301813,-122.80113101005554|49.03475205301813,-122.80068039894104|49.03444959709725,-122.80039072036743|49.03401349462516,-122.80037999153137|49.03384467973859,-122.8006911277771|49.03408383399211,-122.80110955238342|49.03458324063788,-122.80110955238342";
    String url2="49.03506857468707,-122.80115246772766|49.03587745424663,-122.80116319656372|49.03655971634473,-122.80116319656372|49.03655971634473,-122.80261158943176|49.03638387649291,-122.80413508415222|49.03574381418296,-122.80401706695557|49.03488569574625,-122.80338406562805|49.034745019180384,-122.80247211456299|49.03475205301813,-122.80115246772766";
    String url3="49.031129494946306,-122.80116319656372|49.03219870457269,-122.8011417388916|49.03331009548515,-122.80120611190796|49.034322987095955,-122.80120611190796|49.03475908685489,-122.80118465423584";
    String url4="49.032944324001825,-122.80037999153137|49.03359145633461,-122.80036926269531|49.034365190465515,-122.80039072036743|49.03475205301813,-122.80043363571167|49.03476612069063,-122.80100226402283|49.034745019180384,-122.80256867408752|49.03433705488978,-122.80373811721802|49.03369696624291,-122.8043282032013";
    ArrayList<LatLng> firstRouteList,secondRouteList,thirdRouteList,fourthRouteList;
    private LocationRequest mLocationRequest;
    private PolylineOptions polylineOptions, blackPolylineOptions;
    private Polyline blackPolyline, greyPolyLine;
    RoadsPresenterInteractor presenter;
    int i=0,j=0,k=0,l=0,m=0;
    boolean isMarkerRotating=false;
    NetworkService service;
    RatingBar ratingBar;

    /**
     * Service to connect with Pubnub
     */
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            chatterBoxServiceClient = (ChatterBoxClient) service;
            initListenerPubnub();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

    };


    @Override
    protected void onDestroy() {
        GlobalBus.getBus().unregister(this);
        unbindService(serviceConnection);
        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mGoogleApiClient!=null)
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, mLocationCallback);
                mGoogleApiClient.disconnect();
            }
        /*if(mCurrentLoc!=null)
            saveLastLoc(mCurrentLoc.getLatitude(),mCurrentLoc.getLongitude(),this);*/
        else {
            if(mLastLocation!=null)
                saveLastLoc(mLastLocation.getLatitude(),mLastLocation.getLongitude(),this);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        GlobalBus.getBus().register(this);
        Intent intent = new Intent(this, ChatterBoxService.class); //Bind service for Pubnub
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        resources=getResources();
        mResultReceiver = new AddressResultReceiver(new Handler());
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(3 * 1000).setNumUpdates(1); // 1 second, in milliseconds; // 1 second, in milliseconds
        service = ApplicationData.getInstance().getNetworkService();
        presenter=new RoadsPresenter(this,service);
        presenter.getRoute(url1,1);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        polyLineList = new ArrayList<>();
        btnFrom= (Button) findViewById(R.id.btnFrom);
        imgCenterPin= (ImageView) findViewById(R.id.imgCenterPin);
        imgMenu= (ImageView) findViewById(R.id.imgMenu);
        imgLoc= (ImageView) findViewById(R.id.imgLoc);
        imgLoc.setOnClickListener(this);
        btnPick= (ImageView) findViewById(R.id.btnPick);
        btnPickDest= (ImageView) findViewById(R.id.btnPickDest);
        rlRatingSubmit= (RelativeLayout) findViewById(R.id.rlRatingSubmit);
        rlPickDest= (RelativeLayout) findViewById(R.id.rlPickDest);
        rlPickStart= (RelativeLayout) findViewById(R.id.rlPickStart);
        rlRatingSubmit.setOnClickListener(this);
        rlSelect= (RelativeLayout) findViewById(R.id.rlSelect);
        rlSelectMain= (RelativeLayout) findViewById(R.id.rlSelectMain);
        rlRating= (RelativeLayout) findViewById(R.id.rlRating);
        rlProgress= (RelativeLayout) findViewById(R.id.rlProgress);
        rlCurrentLocation= (RelativeLayout) findViewById(R.id.rlCurrentLocation);
        rlDriverFound= (RelativeLayout) findViewById(R.id.rlDriverFound);
        rlButtonMain= (RelativeLayout) findViewById(R.id.rlButtonMain);
        rlTop= (RelativeLayout) findViewById(R.id.rlTop);
        rlTop.setOnClickListener(this);
        txtCurrentLocation= (CustomTextView) findViewById(R.id.txtCurrentLocation);
        txtStateUpdate= (CustomTextView) findViewById(R.id.txtStateUpdate);
        rlDestLoc= (RelativeLayout) findViewById(R.id.rlDestLoc);
        btnTo= (Button) findViewById(R.id.btnTo);
        rlMainRequestTaxiLay=(RelativeLayout)findViewById(R.id.rlMainRequestTaxiLay);

        imgTaxi=(ImageView)findViewById(R.id.imgTaxi);
        imgMyCar=(ImageView)findViewById(R.id.imgMyCar);
        imgRideShare=(ImageView)findViewById(R.id.imgRideshare);
        imgPartition=(ImageView)findViewById(R.id.imgPartition);
        ratingBar=(RatingBar) findViewById(R.id.ratingBar);
        rlPriceInfo=(RelativeLayout)findViewById(R.id.rlpriceInfo);
        txtPrice=(CustomTextView)findViewById(R.id.txtPrice);
        txtRequestRide=(CustomTextView)findViewById(R.id.txtRequestRide);
        txtLocDest=(CustomTextView)findViewById(R.id.txtLocDest);
        txtLocDest.setOnClickListener(this);
        txtDestLocPopup=(CustomTextView)findViewById(R.id.txtDestLocPopup);
        txtCurrentLocPopup=(CustomTextView)findViewById(R.id.txtCurrentLocPopup);
        txtTaxi=(CustomTextView)findViewById(R.id.txtTaxi);
        txtTimeTaxi=(CustomTextView)findViewById(R.id.txtTimeTaxi);
        txtRideShare=(CustomTextView)findViewById(R.id.txtRideShare);
        txtTimeRideshare=(CustomTextView)findViewById(R.id.txtTimeRideshare);
        txtMyCar=(CustomTextView)findViewById(R.id.txtMyCar);
        txtChooseDriver=(CustomTextView)findViewById(R.id.txtChooseDriver);
        txtClose=(CustomTextView)findViewById(R.id.txtClose);
        rlPriceInfo.setBackgroundResource(R.drawable.rounded_border);
        imgTaxi.setBackgroundResource(R.drawable.taxi_icon_on);
        txtClose.setOnClickListener(this);
        imgTaxi.setOnClickListener(this);
        imgMyCar.setOnClickListener(this);
        imgRideShare.setOnClickListener(this);
        rlMainRequestTaxiLay.setOnClickListener(this);
        txtCurrentLocation.setOnClickListener(this);
        rlPriceInfo.setOnClickListener(this);
        txtPrice.setText("$6.00");
        txtRequestRide.setText("Request Taxi");
        txtTaxi.setTextColor(Color.parseColor("#272727"));
        txtTimeTaxi.setTextColor(Color.parseColor("#272727"));

        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.parseColor("#0077A6"), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);

        ratingBar.setRating(Float.parseFloat("5.0"));
        mapFragment.getMapAsync(MapsActivity.this);

        rlPickStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(IsStartSet) {
                    initView();
                    btnPick.setBackgroundResource(R.drawable.btn_pickup);
                }
                else
                {
                    subscribePubnub();
                    txtCurrentLocPopup.setText(startAdd);
                    btnPick.setBackgroundResource(R.drawable.close_icon);
                    mMap.addMarker(new MarkerOptions().position(new LatLng(startPostion.latitude,startPostion.longitude))
                            .flat(true)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_client_pin_spc)));
                    imgCenterPin.setBackgroundResource(R.drawable.icon_destination_flag);
                    rlDestLoc.setVisibility(View.VISIBLE);
                    IsStartSet=true;
                }

            }
        });
        rlPickDest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent intent=new Intent(MapsActivity.this,FindAddress.class);
                startActivityForResult(intent,1);*/
                txtDestLocPopup.setText(endAdd);
                mMap.addMarker(new MarkerOptions().position(new LatLng(endPosition.latitude,endPosition.longitude))
                        .flat(true)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_destination_flag_spc)));
                imgCenterPin.setVisibility(View.GONE);
                IsEndSet=true;
                setPolyInfo(startPostion.latitude,startPostion.longitude,endPosition.latitude,endPosition.longitude);
                rlSelect.setVisibility(View.GONE);
                rlSelectMain.setVisibility(View.GONE);
                rlMainRequestTaxiLay.setVisibility(View.VISIBLE);
            }
        });



    }

    public void subscribePubnub()
    {
        if(chatterBoxServiceClient!=null)
        {
            chatterBoxServiceClient.subscribeChat("TestChatNew");
        }

    }

    /**
     * Method to initialize pubnub listener. Called only once from this base activity.
     */
    public void initListenerPubnub()
    {
        if(chatterBoxServiceClient!=null)
        {
            chatterBoxServiceClient.initListener(chatListener);
        }

    }

    /**
     * Callback for pubnub chat listener
     */

    private DefaultChatterBoxCallback chatListener =new DefaultChatterBoxCallback(){

        @Override
        public void onMessage(BeansMessage message) {
            super.onMessage(message);

            if(message.getMessage().equals("DriverFound"))
            {

                GlobalBus.getBus().post(new Events("DriverFound",message,null));

            }
            else if(message.getMessage().equals("TripStarted"))
            {
                GlobalBus.getBus().post(new Events("TripStarted",message,null));
            }
            else if(message.getMessage().equals("TripCompleted"))
            {
                GlobalBus.getBus().post(new Events("TripCompleted",message,null));
            }
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Events eventInfo) {
        if(eventInfo.getEventType().equals("DriverFound"))
        {
            beansMessage=eventInfo.getMessage();
            APP_STATE=2;
            changeViewState(2);
        }
        else if(eventInfo.getEventType().equals("TripStarted"))
        {
            beansMessage=eventInfo.getMessage();
            APP_STATE=4;
            changeViewState(4);
        }
        else if(eventInfo.getEventType().equals("TripCompleted"))
        {
            beansMessage=eventInfo.getMessage();
            APP_STATE=5;
            changeViewState(5);
        }

    }

    private void changeViewState(int state)
    {
        if(state==2)
        {
            rlDriverFound.setVisibility(View.VISIBLE);
            rlMainRequestTaxiLay.setVisibility(View.GONE);
            txtStateUpdate.setText("Driver En Route");
            txtStateUpdate.setVisibility(View.VISIBLE);
            rlTop.setBackgroundColor(resources.getColor(R.color.colorEnRoute));
            imgLoc.setVisibility(View.GONE);
            rlProgress.setVisibility(View.GONE);
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(new LatLng(beansMessage.getDrivarLat(),beansMessage.getDriverLng()))
                    .flat(true)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.uber)));
            mMap.addMarker(new MarkerOptions().position(new LatLng(startPostion.latitude,startPostion.longitude))
                    .flat(true)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_client_pin_spc)));
             mMap.addMarker(new MarkerOptions().position(new LatLng(endPosition.latitude,endPosition.longitude))
                    .flat(true)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_destination_flag_spc)));
            setPolyInfo(beansMessage.getDrivarLat(),beansMessage.getDriverLng(),startPostion.latitude,startPostion.longitude);


        }
        else if(state==4)
        {
            rlDriverFound.setVisibility(View.VISIBLE);
            rlMainRequestTaxiLay.setVisibility(View.GONE);
            txtStateUpdate.setText("Driving To Destination");
            txtStateUpdate.setVisibility(View.VISIBLE);
            rlButtonMain.setVisibility(View.GONE);
            rlTop.setBackgroundColor(resources.getColor(R.color.colorEnRoute));
            imgLoc.setVisibility(View.GONE);
            rlProgress.setVisibility(View.GONE);
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(new LatLng(beansMessage.getDrivarLat(),beansMessage.getDriverLng()))
                    .flat(true)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.uber)));
            mMap.addMarker(new MarkerOptions().position(new LatLng(endPosition.latitude,endPosition.longitude))
                    .flat(true)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_destination_flag_spc)));
            setPolyInfo(beansMessage.getDrivarLat(),beansMessage.getDriverLng(),endPosition.latitude,endPosition.longitude);
        }
        else if(state==5)
        {
            rlDriverFound.setVisibility(View.GONE);
            rlTop.setVisibility(View.GONE);
            imgLoc.setVisibility(View.GONE);
            rlProgress.setVisibility(View.GONE);
            rlRating.setVisibility(View.VISIBLE);
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(new LatLng(startPostion.latitude,startPostion.longitude))
                    .flat(true)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_client_pin_spc)));
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgTaxi:
                rlPriceInfo.setBackgroundResource(R.drawable.rounded_border);
                imgTaxi.setBackgroundResource(R.drawable.taxi_icon_on);
                imgRideShare.setBackgroundResource(R.drawable.rideshare_icon_off);
                imgMyCar.setBackgroundResource(R.drawable.chauffeur_grey);
                txtPrice.setVisibility(View.VISIBLE);
                imgPartition.setVisibility(View.VISIBLE);
                txtPrice.setText("$6.00");
                txtRequestRide.setText("Request Taxi");
                txtTaxi.setTextColor(Color.parseColor("#272727"));
                txtTimeTaxi.setTextColor(Color.parseColor("#272727"));
                txtRideShare.setTextColor(Color.parseColor("#cdcdcd"));
                txtTimeRideshare.setTextColor(Color.parseColor("#cdcdcd"));
                txtMyCar.setTextColor(Color.parseColor("#cdcdcd"));
                txtChooseDriver.setTextColor(Color.parseColor("#cdcdcd"));
                break;
            case R.id.imgRideshare:
                rlPriceInfo.setBackgroundResource(R.drawable.rounded_border_blue);
                imgTaxi.setBackgroundResource(R.drawable.taxi_icon_off);
                imgRideShare.setBackgroundResource(R.drawable.rideshare_icon_on);
                imgMyCar.setBackgroundResource(R.drawable.chauffeur_grey);
                txtPrice.setVisibility(View.VISIBLE);
                imgPartition.setVisibility(View.VISIBLE);
                txtPrice.setText("$6.00");
                txtRequestRide.setText("Request Rideshare");
                txtTaxi.setTextColor(Color.parseColor("#cdcdcd"));
                txtTimeTaxi.setTextColor(Color.parseColor("#cdcdcd"));
                txtRideShare.setTextColor(Color.parseColor("#272727"));
                txtTimeRideshare.setTextColor(Color.parseColor("#272727"));
                txtMyCar.setTextColor(Color.parseColor("#cdcdcd"));
                txtChooseDriver.setTextColor(Color.parseColor("#cdcdcd"));
                break;
            case R.id.imgMyCar:
                rlPriceInfo.setBackgroundResource(R.drawable.rounded_border_green);
                imgTaxi.setBackgroundResource(R.drawable.taxi_icon_off);
                imgRideShare.setBackgroundResource(R.drawable.rideshare_icon_off);
                imgMyCar.setBackgroundResource(R.drawable.chauffeur_icon_on);
                txtPrice.setVisibility(View.GONE);
                imgPartition.setVisibility(View.GONE);
                txtRequestRide.setText("Choose Driver");
                txtTaxi.setTextColor(Color.parseColor("#cdcdcd"));
                txtTimeTaxi.setTextColor(Color.parseColor("#cdcdcd"));
                txtRideShare.setTextColor(Color.parseColor("#cdcdcd"));
                txtTimeRideshare.setTextColor(Color.parseColor("#cdcdcd"));
                txtMyCar.setTextColor(Color.parseColor("#272727"));
                txtChooseDriver.setTextColor(Color.parseColor("#272727"));
                break;

            case R.id.rlMainRequestTaxiLay:
                break;

            case R.id.txtCurrentLocation:
                  Intent intent=new Intent(MapsActivity.this,FindAddress.class);
                startActivityForResult(intent,1);
                break;

            case R.id.txtLocDest:
                Intent intent1=new Intent(MapsActivity.this,FindAddress.class);
                startActivityForResult(intent1,1);
                break;

            case R.id.txtClose:
                btnPick.setBackgroundResource(R.drawable.btn_pickup);
               initView();

            case R.id.imgLoc:
                if(mCurrentLoc!=null)
                {
                    CameraPosition position = CameraPosition.builder()
                            .target( new LatLng( mCurrentLoc.getLatitude(),
                                    mCurrentLoc.getLongitude()))
                            .zoom(zoomLevel)
                            .bearing( 0.0f )
                            .tilt(0.0f )
                            .build();
                    mMap.animateCamera(CameraUpdateFactory
                            .newCameraPosition(position), null);
                }

            break;

            case R.id.rlpriceInfo:

                if(txtRequestRide.getText().toString().equals("Request Rideshare"))
                {
                    rlMainRequestTaxiLay.setVisibility(View.GONE);
                    rlProgress.setVisibility(View.VISIBLE);
                    BeansMessage beansMessage=new BeansMessage();
                    beansMessage.setUserStartLat(startPostion.latitude);
                    beansMessage.setUserStartLng(startPostion.longitude);
                    beansMessage.setUserDestLat(endPosition.latitude);
                    beansMessage.setUserDestLng(endPosition.longitude);
                    beansMessage.setMessage("FindDriver");
                    beansMessage.setType("Customer");
                    chatterBoxServiceClient.publishHybrid("",beansMessage);
                }

                break;

            case R.id.rlTop:
                if(APP_STATE==2)
                {
                    BeansMessage beansMessage=new BeansMessage();
                    beansMessage.setUserStartLat(startPostion.latitude);
                    beansMessage.setUserStartLng(startPostion.longitude);
                    beansMessage.setUserDestLat(endPosition.latitude);
                    beansMessage.setUserDestLng(endPosition.longitude);
                    beansMessage.setMessage("StartTrip");
                    beansMessage.setType("Customer");
                    chatterBoxServiceClient.publishHybrid("",beansMessage);
                }
                else if(APP_STATE==4)
                {
                    BeansMessage beansMessage=new BeansMessage();
                    beansMessage.setUserStartLat(startPostion.latitude);
                    beansMessage.setUserStartLng(startPostion.longitude);
                    beansMessage.setUserDestLat(endPosition.latitude);
                    beansMessage.setUserDestLng(endPosition.longitude);
                    beansMessage.setMessage("CompleteTrip");
                    beansMessage.setType("Customer");
                    chatterBoxServiceClient.publishHybrid("",beansMessage);
                }

                break;
            case R.id.rlRatingSubmit:
                rlRating.setVisibility(View.GONE);
                rlDriverFound.setVisibility(View.GONE);
                rlMainRequestTaxiLay.setVisibility(View.GONE);
                imgLoc.setVisibility(View.VISIBLE);
                rlProgress.setVisibility(View.GONE);
                imgCenterPin.setVisibility(View.VISIBLE);
                initView();
                break;

        }
    }

    private void initView()
    {
        rlMainRequestTaxiLay.setVisibility(View.GONE);
        rlSelectMain.setVisibility(View.VISIBLE);
        mMap.clear();
        imgCenterPin.setVisibility(View.VISIBLE);
        imgCenterPin.setBackgroundResource(R.drawable.icon_client_pin);
        CameraPosition position = CameraPosition.builder()
                .target( new LatLng( mCurrentLoc.getLatitude(),
                        mCurrentLoc.getLongitude()))
                .zoom(zoomLevel)
                .bearing( 0.0f )
                .tilt(0.0f )
                .build();
        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(position), null);
        rlDestLoc.setVisibility(View.GONE);
        IsStartSet=false;
        IsEndSet=false;
        rlProgress.setVisibility(View.GONE);
        animationStart();
    }


    private double getBearing(LatLng latLng1,LatLng latLng2) {

        double PI = 3.14159;
        double lat1 = latLng1.latitude * PI / 180;
        double long1 = latLng1.longitude * PI / 180;
        double lat2 = latLng2.latitude * PI / 180;
        double long2 = latLng2.longitude * PI / 180;

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;

        return brng;
    }



    private void rotateMarker(final Marker marker, final LatLng destination, final float rotation) {

        if (marker != null) {

            final LatLng startPosition = marker.getPosition();
            final float startRotation = marker.getRotation();

            final LatLngInterpolator latLngInterpolator = new LatLngInterpolator.Spherical();
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(2500); // duration 3 second
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {

                    try {
                        float v = animation.getAnimatedFraction();
                        LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, destination);
                        float bearing = computeRotation(v, startRotation, rotation);

                        marker.setRotation(bearing);
                        marker.setPosition(newPosition);

                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
            valueAnimator.start();
        }
    }
    private static float computeRotation(float fraction, float start, float end) {
        float normalizeEnd = end - start; // rotate start to 0
        float normalizedEndAbs = (normalizeEnd + 360) % 360;

        float direction = (normalizedEndAbs > 180) ? -1 : 1; // -1 = anticlockwise, 1 = clockwise
        float rotation;
        if (direction > 0) {
            rotation = normalizedEndAbs;
        } else {
            rotation = normalizedEndAbs - 360;
        }

        float result = fraction * rotation + start;
        return (result + 360) % 360;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==2)
        {
            rlSelect.setVisibility(View.GONE);
            rlSelectMain.setVisibility(View.GONE);
            rlProgress.setVisibility(View.VISIBLE);
            imgCenterPin.setVisibility(View.GONE);
            rlMainRequestTaxiLay.setVisibility(View.VISIBLE);
            beansAPNS=data.getParcelableExtra("addressInfo");
            startAdd=beansAPNS.getStrStartAdd();
            endAdd=beansAPNS.getStrEndAdd();
            txtCurrentLocPopup.setText(startAdd);
            txtDestLocPopup.setText(endAdd);
            Log.e("STRATRT",beansAPNS.getStartLatitude()+"   "+beansAPNS.getStartLongitude());
            Marker markerStart = mMap.addMarker(new MarkerOptions().position(new LatLng(beansAPNS.getStartLatitude(),beansAPNS.getStartLongitude()))
                    .flat(true)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_client_pin_spc)));
            Marker markerend = mMap.addMarker(new MarkerOptions().position(new LatLng(beansAPNS.getEndLatitude(),beansAPNS.getEndLongitude()))
                    .flat(true)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_destination_flag_spc)));
            startPostion=new LatLng(beansAPNS.getStartLatitude(),beansAPNS.getStartLongitude());
            endPosition=new LatLng(beansAPNS.getEndLatitude(),beansAPNS.getEndLongitude());
            setPolyInfo(beansAPNS.getStartLatitude(),beansAPNS.getStartLongitude(),beansAPNS.getEndLatitude(),beansAPNS.getEndLongitude());
        }

    }

    private void setPolyInfo(double polyStartLatitude,double polyStartLong,double polyEndLatitude,double polyEndLong)
    {
        this.polyStartLat=polyStartLatitude;
        this.polyStartLng=polyStartLong;
        this.polyEndLat=polyEndLatitude;
        this.polyEndLng=polyEndLong;
        showPolyAnim(polyStartLat,polyStartLng,polyEndLat,polyEndLng);
    }

    private void showPolyAnim(double startLat,double startLong,double endLat,double endLng)
    {
        LatLngBounds.Builder b = new LatLngBounds.Builder();
        b.include(new LatLng(startLat,startLong));
        b.include(new LatLng(endLat,endLng));
        LatLngBounds bounds = b.build();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels - getResources().getDisplayMetrics().heightPixels / 2;
        int padding = (int) (width * 0.10);
        //Change the padding as per needed
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        mMap.animateCamera(cu);
        service.changeApiBaseUrl("http://maps.googleapis.com");
        presenter.getDirection(startLat+","+startLong,endLat+","+endLng);
    }

    private void startAnim(ArrayList<LatLng> routelist){
        if(mMap != null) {
            moveToBounds(routelist);
            MapAnimator.getInstance().animateRoute(mMap, routelist);
        } else {
            Toast.makeText(getApplicationContext(), "Map not ready", Toast.LENGTH_LONG).show();
        }
    }

    private void moveToBounds(ArrayList<LatLng> routelist)
    {

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(int i = 0; i < routelist.size();i++){
            builder.include(routelist.get(i));
        }
        LatLngBounds bounds = builder.build();
        int padding = 150; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.animateCamera(cu);
    }

    protected void onDirectionResult(DirectionResults directionResults)
    {
        service.changeApiBaseUrl("https://roads.googleapis.com");
        ArrayList<LatLng> routelist = new ArrayList<LatLng>();
       // routelist.add(new LatLng(polyStartLat,polyStartLng));
        if(directionResults.getRoutes().size()>0){
            List<LatLng> decodelist;
            Route routeA = directionResults.getRoutes().get(0);
            Log.i("zacharia", "Legs length : "+routeA.getLegs().size());
            if(routeA.getLegs().size()>0){
                List<Steps> steps= routeA.getLegs().get(0).getSteps();
                Log.i("zacharia","Steps size :"+steps.size());
                Steps step;
                com.developers.uberanimation.models.Location location;
                String polyline;
                for(int i=0 ; i<steps.size();i++){
                    step = steps.get(i);
                    location =step.getStart_location();
                    routelist.add(new LatLng(location.getLat(), location.getLng()));
                    Log.i("zacharia", "Start Location :" + location.getLat() + ", " + location.getLng());
                    polyline = step.getPolyline().getPoints();
                    decodelist = PolyUtil.decode(polyline);
                    routelist.addAll(decodelist);
                    location =step.getEnd_location();
                    routelist.add(new LatLng(location.getLat() ,location.getLng()));
                    Log.i("zacharia","End Location :"+location.getLat() +", "+location.getLng());
                }
            }
        }

      //  routelist.add(new LatLng(polyEndLat,polyEndLng));
        startAnim(routelist);
    }

    protected LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if((locationResult!=null)&&(locationResult.getLocations().size()>0))
            {
                Location location=locationResult.getLocations().get(0);
                mCurrentLoc=location;
                initCamera( location );
                // It is possible that the user presses the button to get the address before the
                // GoogleApiClient object successfully connects. In such a case, mAddressRequested
                // is set to true, but no attempt is made to fetch the address (see
                // fetchAddressButtonHandler()) . Instead, we start the intent service here if the
                // user has requested an address, since we now have a connection to GoogleApiClient.
                startIntentService(null,location);

            }
        }


    };
    /**
     * Builds a GoogleApiClient. Uses {@code #addApi} to request the LocationServices API.
     */
    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(MapsActivity.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        if(mGoogleApiClient!=null)
            mGoogleApiClient.connect();

    }
    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        // Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                // Determine whether a Geocoder is available.
                if (!Geocoder.isPresent()) {
                    Toast.makeText(MapsActivity.this, R.string.no_geocoder_available, Toast.LENGTH_LONG).show();
                    return;
                }
                initCamera( mLastLocation );
                // It is possible that the user presses the button to get the address before the
                // GoogleApiClient object successfully connects. In such a case, mAddressRequested
                // is set to true, but no attempt is made to fetch the address (see
                // fetchAddressButtonHandler()) . Instead, we start the intent service here if the
                // user has requested an address, since we now have a connection to GoogleApiClient.
                startIntentService(null,mLastLocation);
                mCurrentLoc=mLastLocation;
            }
            else
            {
                if(!(PreferenceManager.getDefaultSharedPreferences(
                        MapsActivity.this).getString(resources.getString(R.string.loc_lat_lastsaved), "").equals("")))
                {
                    Double lat=Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(
                            MapsActivity.this).getString(resources.getString(R.string.loc_lat_lastsaved), ""));
                    Double lng=Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(
                            MapsActivity.this).getString(resources.getString(R.string.loc_lng_lastsaved), ""));
                    mCurrentLoc=new Location("");
                    mCurrentLoc.setLatitude(lat);
                    mCurrentLoc.setLongitude(lng);
                    initCamera( mCurrentLoc );
                    startIntentService(null,mCurrentLoc);
                }
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,mLocationCallback,null).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                    }
                });
            }
        }
        catch(SecurityException e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    /**
     * Initialize camera view
     * @param location Location to show on mapview
     */
    private void initCamera( Location location ) {
        try {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng( location.getLatitude(),
                    location.getLongitude()), zoomLevel));
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.getUiSettings().setMapToolbarEnabled(false);
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);


        }
        catch(SecurityException e)
        {
            e.printStackTrace();
        }


    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    protected void startIntentService(LatLng latlng, Location loc) {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(MapsActivity.this, FetchAddressIntentService.class);
        // Pass the result receiver as an extra to the service.
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        if(loc==null)
        {
            Location targetLocation = new Location("");//provider name is unecessary
            targetLocation.setLatitude(latlng.latitude);//your coords of course
            targetLocation.setLongitude(latlng.longitude);
            // Pass the location data as an extra to the service.
            intent.putExtra(Constants.LOCATION_DATA_EXTRA, targetLocation);
            mLastLocation=targetLocation;
        }
        else {
            // Pass the location data as an extra to the service.
            intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
        }


        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        startService(intent);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        final double latitude = 28.671246;
        double longitude = 77.317654;
        mMap.setTrafficEnabled(false);
        mMap.setIndoorEnabled(false);
        mMap.setBuildingsEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        // Add a marker in Home and move the camera
        sydney = new LatLng(49.03475205301813,-122.80113101005554);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);

        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                .target(sydney)
                .zoom(17)
                .build()));
        Location location1=new Location("a");
        location1.setLongitude(49.10449219998637);
        location1.setLatitude(-122.80433737625849);

        Location location2=new Location("b");
        location2.setLongitude(49.10668426706855);
        location2.setLatitude(-122.80515432357788);


        buildGoogleApiClient();


        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

            @Override
            public void onCameraChange(CameraPosition arg0) {
                if(APP_STATE==0)
                {
                    if (IS_ADDRESS_SEARCHED == false)
                        if(!IsStartSet)
                        {
                            startPostion=arg0.target;

                        }
                        else if(!IsEndSet)
                        {
                            endPosition=arg0.target;
                        }
                    startIntentService(arg0.target, null);

                    IS_ADDRESS_SEARCHED = false;
                }

            }
        });
    }

    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         *  Receives data sent from FetchAddressIntentService and updates the UI in WelcomeMainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string or an error message sent from the intent service.
            /*mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            displayAddressOutput();*/
            locationObj=resultData.getParcelable("address");
            addressText="";
            addressObj=resultData.getParcelable("addressText");
            if(addressObj!=null)
            {
               /* for (int i=0;i<3;i++)
                {
                    if(addressObj.getAddressLine(i)!=null)
                        addressText=addressText+addressObj.getAddressLine(i)+"\n";
                }*/
               addressText=addressObj.getFeatureName()+", "+addressObj.getThoroughfare()+", "+addressObj.getLocality();
                showMapData();
            }

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                //showToast(getString(R.string.address_found));
            }
            // Reset. Enable the Fetch Address button and stop showing the progress bar.
           /* mAddressRequested = false;
            updateUIWidgets();*/
        }
    }

    // Updates user interface for map
    private void showMapData()
    {
        try {
            Log.e("Add",addressText+"   "+IsStartSet);
            if(!IsStartSet)
            {
                startAdd=addressText;
                txtCurrentLocation.setText(addressText);
            }
            else
            {
                endAdd=addressText;
                txtLocDest.setText(addressText);
            }

            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.getUiSettings().setMapToolbarEnabled(false);
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
        catch(SecurityException e)
        {
            e.printStackTrace();
        }

    }

    private void saveLastLoc(Double lastLat,Double lastLng,Activity activity)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(resources.getString(R.string.loc_lat_lastsaved), String.valueOf(lastLat));
        editor.putString(resources.getString(R.string.loc_lng_lastsaved), String.valueOf(lastLng));
        editor.apply();
    }

    private void animationStart(){
        i=0;
        j=0;
        k=0;
        l=0;
        m=0;
        handler = new Handler();

        progressUpdater=new Runnable(){

            @Override
            public void run() {
                if(i<firstRouteList.size())
                {
                    //LatLng newPos = new LatLng(lat, lng);
                    // marker.setPosition(newPos);

                    //marker.setRotation(getBearing(startPosition, newPos));

/* rotateMarker(marker,Float.parseFloat(String.valueOf(getBearing(startPosition, loc.get(i)))));
                            animateUserMarkerToGB(marker,loc.get(i));*/


        // float rotation = (float) SphericalUtil.computeHeading(old, new);
        if(getBearing(startPosition1, firstRouteList.get(i))>0.0)
        rotateMarker(marker1, firstRouteList.get(i), Float.parseFloat(String.valueOf(getBearing(startPosition1, firstRouteList.get(i)))));
        // marker.setPosition(newPos);
        startPosition1=firstRouteList.get(i);
        i++;
        }
        else
        {
        i=0;
        }

        if(j<secondRouteList.size())
        {
        if(getBearing(startPosition2, secondRouteList.get(j))>0.0)
        rotateMarker(marker2, secondRouteList.get(j), Float.parseFloat(String.valueOf(getBearing(startPosition2, secondRouteList.get(j)))));
        // marker.setPosition(newPos);
        startPosition2=secondRouteList.get(j);
        j++;
        }
        else
        {
        j=0;
        }

        if(k<thirdRouteList.size())
        {
        if(getBearing(startPosition3, thirdRouteList.get(k))>0.0)
        rotateMarker(marker3, thirdRouteList.get(k), Float.parseFloat(String.valueOf(getBearing(startPosition3, thirdRouteList.get(k)))));
        // marker.setPosition(newPos);
        startPosition3=thirdRouteList.get(k);
        k++;
        }
        else
        {
        Collections.reverse(thirdRouteList);
        k=0;
        }

        if(l<fourthRouteList.size())
        {
        if(getBearing(startPosition4, fourthRouteList.get(l))>0.0)
        rotateMarker(marker4, fourthRouteList.get(l), Float.parseFloat(String.valueOf(getBearing(startPosition4, fourthRouteList.get(l)))));
        // marker.setPosition(newPos);
        startPosition4=fourthRouteList.get(l);
        l++;
        }
        else
        {
        Collections.reverse(fourthRouteList);
        l=0;
        }

        handler.postDelayed(this, 2500);
        }
        };
        progressUpdater.run();

        }

    protected void firstRoute(BeansMain beansMain)
    {
        firstRouteList=new ArrayList<>();
        for (int i=0;i<beansMain.getSnappedPoints().size();i++)
        {
            LatLng latLng=new LatLng(beansMain.getSnappedPoints().get(i).getLocation().getLatitude(),beansMain.getSnappedPoints().get(i).getLocation().getLongitude());
            firstRouteList.add(latLng);
        }
        startPosition1=firstRouteList.get(0);
        marker1 = mMap.addMarker(new MarkerOptions().position(startPosition1)
                .flat(true)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.uber)));
        marker1.setAnchor(0.5f, 0.5f);
        presenter.getRoute(url2,2);
    }

    protected void secondRoute(BeansMain beansMain)
    {
        secondRouteList=new ArrayList<>();
        for (int i=0;i<beansMain.getSnappedPoints().size();i++)
        {
            LatLng latLng=new LatLng(beansMain.getSnappedPoints().get(i).getLocation().getLatitude(),beansMain.getSnappedPoints().get(i).getLocation().getLongitude());
            secondRouteList.add(latLng);
        }
        startPosition2=secondRouteList.get(0);
        marker2 = mMap.addMarker(new MarkerOptions().position(startPosition2)
                .flat(true)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.uber)));
        marker2.setAnchor(0.5f, 0.5f);
        presenter.getRoute(url3,3);
    }

    protected void thirdRoute(BeansMain beansMain)
    {
        thirdRouteList=new ArrayList<>();
        for (int i=0;i<beansMain.getSnappedPoints().size();i++)
        {
            LatLng latLng=new LatLng(beansMain.getSnappedPoints().get(i).getLocation().getLatitude(),beansMain.getSnappedPoints().get(i).getLocation().getLongitude());
            thirdRouteList.add(latLng);
        }
        startPosition3=thirdRouteList.get(0);
        marker3 = mMap.addMarker(new MarkerOptions().position(startPosition3)
                .flat(true)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.taxi)));
        marker3.setAnchor(0.5f, 0.5f);
        presenter.getRoute(url4,4);
    }

    protected void fourthRoute(BeansMain beansMain)
    {
        fourthRouteList=new ArrayList<>();
        for (int i=0;i<beansMain.getSnappedPoints().size();i++)
        {
            LatLng latLng=new LatLng(beansMain.getSnappedPoints().get(i).getLocation().getLatitude(),beansMain.getSnappedPoints().get(i).getLocation().getLongitude());
            fourthRouteList.add(latLng);
        }
        startPosition4=fourthRouteList.get(0);
        marker4 = mMap.addMarker(new MarkerOptions().position(startPosition4)
                .flat(true)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.taxi)));
        marker4.setAnchor(0.5f, 0.5f);

        animationStart();
    }

    private void showCallDialog()
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle("Driver Number: 6044011476");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" +"6044011476"));
                startActivity(intent);
            }
        });

        dialog = builder.create();
        dialog.show();
    }

    private void showMessageDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle("Driver Number: 6044011476");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("smsto:" + Uri.encode("6044011476")));
                startActivity(intent);
            }
        });
        dialog = builder.create();
        dialog.show();
    }
}
