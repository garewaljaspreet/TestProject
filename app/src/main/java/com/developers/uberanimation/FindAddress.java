package com.developers.uberanimation;

/**
 * Created by navdeepg on 2017-08-29.
 */

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Iterator;

public class FindAddress extends AppCompatActivity implements ISelectAddress,GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks{

    RecyclerView recyclerView;
    EditText edCurrent,edDest;
    TextView txtSkip;
    GoogleApiClient mGoogleApiClient;
    Place startAdd,endAdd;
    boolean IS_Start=false;
    VehicleListAdapter mAdapter;
    ArrayList<BeansPrediction> resultList = new ArrayList<BeansPrediction>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_main);
        edCurrent=(EditText) findViewById(R.id.edCurrent);
        edDest=(EditText) findViewById(R.id.edDest);
        txtSkip=(TextView) findViewById(R.id.txtSkip);
        recyclerView=(RecyclerView) findViewById(R.id.recyclerView);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        if(mGoogleApiClient!=null)
            mGoogleApiClient.connect();

        setAdapter();
        edCurrent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>0)
                {
                    final PendingResult<AutocompletePredictionBuffer> result =
                            Places.GeoDataApi.getAutocompletePredictions(mGoogleApiClient, s.toString(), null, null);

                    result.setResultCallback(new ResultCallback<AutocompletePredictionBuffer>() {
                        @Override public void onResult(@NonNull AutocompletePredictionBuffer autocompletePredictions) {
                            // Iterate through results and display them in list or other UI

                            Iterator<AutocompletePrediction> iterator = autocompletePredictions.iterator();
                           resultList = new ArrayList<BeansPrediction>();
                            while (iterator.hasNext()) {
                                IS_Start=true;
                                AutocompletePrediction prediction = iterator.next();
                                BeansPrediction beansPrediction=new BeansPrediction();
                                beansPrediction.setStrFullTxt(prediction.getFullText(null).toString());
                                beansPrediction.setStrPlaceId(prediction.getPlaceId());
                                beansPrediction.setStrPrimaryTxt(prediction.getPrimaryText(null).toString());
                                resultList.add(beansPrediction);
                                // Only include U.S. cities
                                // States only have 1 comma (e.g. AK, United States). Don't include states.
                               Log.e("Result",prediction.getPrimaryText(null)+"  "+prediction.getFullText(null).toString());

                            }
                            mAdapter.notifyData(resultList);

                            autocompletePredictions.release();
                        }
                    });

                    /*final PendingResult<PlaceBuffer> placeResult =
                            Places.GeoDataApi.getPlaceById(mGoogleApiClient,"EiMxNjg4IDE1MiBTdHJlZXQsIFN1cnJleSwgQkMsIENhbmFkYQ");
                    placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                        @Override
                        public void onResult(PlaceBuffer places) {
                            if (places.getStatus().isSuccess()) {
                                Log.e("Success"," "+places.get(0).getLatLng().longitude+"    "+places.get(0).getLatLng().latitude+"   ");
                            } else {
                                Log.e("Error","Test  ");
                            }
                        }
                    });
*/

                }

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        edDest.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>0)
                {
                    final PendingResult<AutocompletePredictionBuffer> result =
                            Places.GeoDataApi.getAutocompletePredictions(mGoogleApiClient, s.toString(), null, null);

                    result.setResultCallback(new ResultCallback<AutocompletePredictionBuffer>() {
                        @Override public void onResult(@NonNull AutocompletePredictionBuffer autocompletePredictions) {
                            // Iterate through results and display them in list or other UI
                            IS_Start=false;
                            Iterator<AutocompletePrediction> iterator = autocompletePredictions.iterator();
                            resultList = new ArrayList<BeansPrediction>();
                            while (iterator.hasNext()) {
                                AutocompletePrediction prediction = iterator.next();
                                BeansPrediction beansPrediction=new BeansPrediction();
                                beansPrediction.setStrFullTxt(prediction.getFullText(null).toString());
                                beansPrediction.setStrPlaceId(prediction.getPlaceId());
                                beansPrediction.setStrPrimaryTxt(prediction.getPrimaryText(null).toString());
                                resultList.add(beansPrediction);
                                // Only include U.S. cities
                                // States only have 1 comma (e.g. AK, United States). Don't include states.
                            }
                            mAdapter.notifyData(resultList);
                            autocompletePredictions.release();
                        }
                    });

                    /*final PendingResult<PlaceBuffer> placeResult =
                            Places.GeoDataApi.getPlaceById(mGoogleApiClient,"EiMxNjg4IDE1MiBTdHJlZXQsIFN1cnJleSwgQkMsIENhbmFkYQ");
                    placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                        @Override
                        public void onResult(PlaceBuffer places) {
                            if (places.getStatus().isSuccess()) {
                                Log.e("Success"," "+places.get(0).getLatLng().longitude+"    "+places.get(0).getLatLng().latitude+"   ");
                            } else {
                                Log.e("Error","Test  ");
                            }
                        }
                    });
*/

                }

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        txtSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Click",startAdd.getLatLng().latitude+"Find"+startAdd.getLatLng().longitude);
                BeansAPNS beans=new BeansAPNS();
                beans.setStartLatitude(startAdd.getLatLng().latitude);
                beans.setStartLongitude(startAdd.getLatLng().longitude);
                beans.setStrStartAdd(startAdd.getAddress().toString());
                beans.setStrEndAdd(endAdd.getAddress().toString());
                beans.setEndLongitude(endAdd.getLatLng().longitude);
                beans.setEndLatitude(endAdd.getLatLng().latitude);
                Intent intent=new Intent();
                intent.putExtra("addressInfo",beans);
                setResult(1,intent);
                finish();
            }
        });
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void setAdapter() {

        // Setting the LayoutManager.
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(FindAddress.this);
        recyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new VehicleListAdapter(this, FindAddress.this, resultList,false);
        recyclerView.setAdapter(mAdapter);
        // Setting the adapter.
        //Your RecyclerView.Adapter
        /*mAdapter = new VehicleListAdapter(getActivity().getBaseContext(), getActivity(), VehicleListFragment.this, resultVehicle,IS_INIT_SETTINGS);
        rvVehicleList.setAdapter(mAdapter);*/
    }

    @Override
    public void itemSelected(int position) {
        final PendingResult<PlaceBuffer> placeResult =
                Places.GeoDataApi.getPlaceById(mGoogleApiClient,resultList.get(position).getStrPlaceId());
        placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
            @Override
            public void onResult(PlaceBuffer places) {
                if (places.getStatus().isSuccess()) {
                    Place place=places.get(0);
                    if(IS_Start) {
                        startAdd = place;
                        edCurrent.setText(places.get(0).getAddress().toString());
                    }
                    else {
                        endAdd = place;
                        edDest.setText(places.get(0).getAddress().toString());
                    }

                    Log.e("Success"," "+places.get(0).getLatLng().longitude+"    "+places.get(0).getLatLng().latitude+"   ");
                } else {
                    Log.e("Error","Test  ");
                }

            }
        });

    }


}
