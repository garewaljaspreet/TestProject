package com.developers.uberanimation;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.developers.uberanimation.network.NetworkService;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.ls.LSException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static com.google.android.gms.maps.model.JointType.ROUND;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = MapsActivity.class.getSimpleName();
    SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private List<LatLng> polyLineList;
    private Marker marker1,marker2,marker3,marker4,marker5;
    private float v;
    private double lat, lng;
    private Handler handler;
    private Runnable progressUpdater;
    private LatLng startPosition1,startPosition2, startPosition3,startPosition4,startPosition5;
    private int index, next;
    private LatLng sydney;
    private Button button;
    String url1="49.03475205301813,-122.80113101005554|49.03475205301813,-122.80068039894104|49.03444959709725,-122.80039072036743|49.03401349462516,-122.80037999153137|49.03384467973859,-122.8006911277771|49.03408383399211,-122.80110955238342|49.03458324063788,-122.80110955238342";
    String url2="49.03506857468707,-122.80115246772766|49.03587745424663,-122.80116319656372|49.03655971634473,-122.80116319656372|49.03655971634473,-122.80261158943176|49.03638387649291,-122.80413508415222|49.03574381418296,-122.80401706695557|49.03488569574625,-122.80338406562805|49.034745019180384,-122.80247211456299|49.03475205301813,-122.80115246772766";
    String url3="49.031129494946306,-122.80116319656372|49.03219870457269,-122.8011417388916|49.03331009548515,-122.80120611190796|49.034322987095955,-122.80120611190796|49.03475908685489,-122.80118465423584";
    String url4="49.032944324001825,-122.80037999153137|49.03359145633461,-122.80036926269531|49.034365190465515,-122.80039072036743|49.03475205301813,-122.80043363571167|49.03476612069063,-122.80100226402283|49.034745019180384,-122.80256867408752|49.03433705488978,-122.80373811721802|49.03369696624291,-122.8043282032013";
    ArrayList<LatLng> firstRouteList,secondRouteList,thirdRouteList,fourthRouteList;
    private EditText destinationEditText;
    private String destination;
    private PolylineOptions polylineOptions, blackPolylineOptions;
    private Polyline blackPolyline, greyPolyLine;
    RoadsPresenterInteractor presenter;
    int i=0,j=0,k=0,l=0,m=0;
    boolean isMarkerRotating=false;
    NetworkService service;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        service = ApplicationData.getInstance().getNetworkService();
        presenter=new RoadsPresenter(this,service);
        presenter.getRoute(url1,1);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        polyLineList = new ArrayList<>();
        button = (Button) findViewById(R.id.destination_button);
        destinationEditText = (EditText) findViewById(R.id.edittext_place);
        mapFragment.getMapAsync(MapsActivity.this);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        });

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
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        final double latitude = 28.671246;
        double longitude = 77.317654;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setTrafficEnabled(false);
        mMap.setIndoorEnabled(false);
        mMap.setBuildingsEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        // Add a marker in Home and move the camera
        sydney = new LatLng(49.03475205301813,-122.80113101005554);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
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

      /*  String requestUrl = null;
        try {
            requestUrl = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "mode=driving&"
                    + "transit_routing_preference=less_driving&"
                    + "origin=" + latitude + "," + longitude + "&"
                    + "destination=" + destination + "&"
                    + "key=" + "AIzaSyDIaU9M7cN8z7LG2zJjWmfPWebtSClJSRQ";
            Log.d(TAG, requestUrl);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                    requestUrl, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, response + "");
                            try {
                                JSONArray jsonArray = response.getJSONArray("routes");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject route = jsonArray.getJSONObject(i);
                                    JSONObject poly = route.getJSONObject("overview_polyline");
                                    String polyline = poly.getString("points");
                                    polyLineList = decodePoly(polyline);
                                    Log.d(TAG, polyLineList + "");
                                }
                                //Adjusting bounds
                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                for (LatLng latLng : polyLineList) {
                                    builder.include(latLng);
                                }
                                LatLngBounds bounds = builder.build();
                                CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 2);
                                mMap.animateCamera(mCameraUpdate);

                                polylineOptions = new PolylineOptions();
                                polylineOptions.color(Color.GRAY);
                                polylineOptions.width(5);
                                polylineOptions.startCap(new SquareCap());
                                polylineOptions.endCap(new SquareCap());
                                polylineOptions.jointType(ROUND);
                                polylineOptions.addAll(polyLineList);
                                greyPolyLine = mMap.addPolyline(polylineOptions);

                                blackPolylineOptions = new PolylineOptions();
                                blackPolylineOptions.width(5);
                                blackPolylineOptions.color(Color.BLACK);
                                blackPolylineOptions.startCap(new SquareCap());
                                blackPolylineOptions.endCap(new SquareCap());
                                blackPolylineOptions.jointType(ROUND);
                                blackPolyline = mMap.addPolyline(blackPolylineOptions);

                                mMap.addMarker(new MarkerOptions()
                                        .position(polyLineList.get(polyLineList.size() - 1)).flat(true));

                                ValueAnimator polylineAnimator = ValueAnimator.ofInt(0, 100);
                                polylineAnimator.setDuration(2000);
                                polylineAnimator.setInterpolator(new LinearInterpolator());
                                polylineAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                        List<LatLng> points = greyPolyLine.getPoints();
                                        int percentValue = (int) valueAnimator.getAnimatedValue();
                                        int size = points.size();
                                        int newPoints = (int) (size * (percentValue / 100.0f));
                                        List<LatLng> p = points.subList(0, newPoints);
                                        blackPolyline.setPoints(p);
                                    }
                                });
                                polylineAnimator.start();
                                marker = mMap.addMarker(new MarkerOptions().position(sydney)
                                        .flat(true)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car)));
                                handler = new Handler();
                                index = -1;
                                next = 1;
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (index < polyLineList.size() - 1) {
                                            index++;
                                            next = index + 1;
                                        }
                                        if (index < polyLineList.size() - 1) {
                                            startPosition = polyLineList.get(index);
                                            endPosition = polyLineList.get(next);
                                        }
                                        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
                                        valueAnimator.setDuration(3000);
                                        valueAnimator.setInterpolator(new LinearInterpolator());
                                        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                            @Override
                                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                                v = valueAnimator.getAnimatedFraction();
                                                lng = v * endPosition.longitude + (1 - v)
                                                        * startPosition.longitude;
                                                lat = v * endPosition.latitude + (1 - v)
                                                        * startPosition.latitude;
                                                LatLng newPos = new LatLng(lat, lng);
                                                marker.setPosition(newPos);
                                                marker.setAnchor(0.5f, 0.5f);
                                                //marker.setRotation(getBearing(startPosition, newPos));
                                                rotateMarker(marker,Float.parseFloat(String.valueOf(getBearing(startPosition, newPos))));
                                                marker.setPosition(newPos);
                                                *//*mMap.moveCamera(CameraUpdateFactory
                                                        .newCameraPosition
                                                                (new CameraPosition.Builder()
                                                                        .target(newPos)
                                                                        .zoom(15.5f)
                                                                        .build()));*//*
                                            }
                                        });
                                        valueAnimator.start();
                                        handler.postDelayed(this, 3000);
                                    }
                                }, 3000);


                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, error + "");
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

    }


    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    /*private float getBearing(LatLng begin, LatLng end) {
        double lat = Math.abs(begin.latitude - end.latitude);
        double lng = Math.abs(begin.longitude - end.longitude);

        if (begin.latitude < end.latitude && begin.longitude < end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if (begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if (begin.latitude < end.latitude && begin.longitude >= end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        return -1;
    }
*/

    private void rotateMarker(final Marker marker, final float toRotation) {
        if(!isMarkerRotating) {
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            final float startRotation = marker.getRotation();
            final long duration = 1000;

            final Interpolator interpolator = new LinearInterpolator();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    isMarkerRotating = true;

                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed / duration);

                    float rot = t * toRotation + (1 - t) * startRotation;

                    marker.setRotation(-rot > 180 ? rot / 2 : rot);
                    if (t < 1.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16);
                    } else {
                        isMarkerRotating = false;
                    }
                }
            });
        }
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

    static void animateUserMarkerToGB(final Marker marker, final LatLng finalPosition) {
        final LatLng startPosition = marker.getPosition();
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();

        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final float durationInMs = 2500;

        handler.post(new Runnable() {
            long elapsed;
            float t;
            float v;

            @Override
            public void run() {
                // Calculate progress using interpolator
                elapsed = SystemClock.uptimeMillis() - start;
                t = elapsed / durationInMs;
                v = interpolator.getInterpolation(t);
                marker.setPosition(new LatLngInterpolator.Spherical().interpolate(v, startPosition, finalPosition));
                //rotateMarker(marker,Float.parseFloat(String.valueOf(getBearing(startPosition, finalPosition))),v,startPosition,finalPosition);
                // Repeat till progress is complete.
                if (t < 1) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    // fragment.IS_USER_ANIM=false;
                }
            }
        });
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

}
