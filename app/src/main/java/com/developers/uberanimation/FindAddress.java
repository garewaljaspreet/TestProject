package com.developers.uberanimation;

/**
 * Created by navdeepg on 2017-08-29.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
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
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.Iterator;

public class FindAddress extends AppCompatActivity implements View.OnClickListener,ISelectAddress,GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks{
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(49.041638, -122.703781), new LatLng(49.051089, -122.885742));
    RecyclerView recyclerView;
    EditText edCurrent,edDest;
    RelativeLayout rlPick,rlPickDest,rlNoData;
    TextView txtSkip,txtCancel;
    GoogleApiClient mGoogleApiClient;
    Place startAdd,endAdd;
    boolean IS_Start=false;
    BeansAPNS beansData;
    VehicleListAdapter mAdapter;
    TextWatcher txtWatchCurrent,txtWatchDest;
    ArrayList<BeansPrediction> resultList = new ArrayList<BeansPrediction>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_main);
        AndroidBug5497Workaround.assistActivity(this);

        edCurrent=(EditText) findViewById(R.id.edCurrent);
        edDest=(EditText) findViewById(R.id.edDest);
        txtSkip=(TextView) findViewById(R.id.txtSkip);
        txtCancel=(TextView) findViewById(R.id.txtCancel);
        txtCancel.setOnClickListener(this);
        rlPick=(RelativeLayout) findViewById(R.id.rlPick);
        rlPickDest=(RelativeLayout) findViewById(R.id.rlPickDest);
        rlNoData=(RelativeLayout) findViewById(R.id.rlNoData);
        rlPick.setOnClickListener(this);
        rlPickDest.setOnClickListener(this);
        recyclerView=(RecyclerView) findViewById(R.id.recyclerView);
        txtSkip.setVisibility(View.GONE);
        beansData=getIntent().getParcelableExtra("StartLocInfo");
        edCurrent.setCursorVisible(false);
        if(beansData!=null)
        {
            edCurrent.setText(beansData.getStrStartAdd());

        }
        else
        {
            beansData=null;
        }
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        if(mGoogleApiClient!=null)
            mGoogleApiClient.connect();


        edCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edCurrent.setCursorVisible(true);
                edCurrent.setSelection(edCurrent.getText().length());
            }
        });
        edDest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edDest.setCursorVisible(true);
                edDest.setSelection(edDest.getText().length());
            }
        });

        setAdapter();
        txtWatchCurrent=new TextWatcher() {
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
                            rlNoData.setVisibility(View.GONE);
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
        };
        edCurrent.addTextChangedListener(txtWatchCurrent);

        txtWatchDest=new TextWatcher() {
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
                            rlNoData.setVisibility(View.GONE);
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
        };
        edDest.addTextChangedListener(txtWatchDest);

        txtSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BeansAPNS beans=new BeansAPNS();
                if(beansData==null)
                {
                    beans.setStartLatitude(startAdd.getLatLng().latitude);
                    beans.setStartLongitude(startAdd.getLatLng().longitude);
                    beans.setStrStartAdd(startAdd.getAddress().toString());
                }
                else
                {
                    beans.setStartLatitude(beansData.getStartLatitude());
                    beans.setStartLongitude(beansData.getStartLongitude());
                    beans.setStrStartAdd(beansData.getStrStartAdd());
                }
                beans.setStrEndAdd(endAdd.getAddress().toString());
                beans.setEndLongitude(endAdd.getLatLng().longitude);
                beans.setEndLatitude(endAdd.getLatLng().latitude);
                Intent intent=new Intent();
                intent.putExtra("addressInfo",beans);
                setResult(2,intent);
                finish();
            }
        });
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e("Connected","here");
        /*AutocompleteFilter filter = new AutocompleteFilter.Builder().setTypeFilter(AutocompleteFilter.TYPE_FILTER_ESTABLISHMENT).setCountry("CA").build();
        final PendingResult<AutocompletePredictionBuffer> result =
                Places.GeoDataApi.getAutocompletePredictions(mGoogleApiClient, "", BOUNDS_MOUNTAIN_VIEW, filter);

        result.setResultCallback(new ResultCallback<AutocompletePredictionBuffer>() {
            @Override public void onResult(@NonNull AutocompletePredictionBuffer autocompletePredictions) {
                // Iterate through results and display them in list or other UI
                Log.e("Connected","hereInside");
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
*/
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
                        beansData=null;
                        hideSoftKeyboard(FindAddress.this,edCurrent);
                        edCurrent.setCursorVisible(false);
                        edCurrent.removeTextChangedListener(txtWatchCurrent);
                        startAdd = place;
                        edCurrent.setText(places.get(0).getAddress().toString());
                        edCurrent.setSelection(edCurrent.getText().length());
                        mAdapter.clear();
                        edCurrent.addTextChangedListener(txtWatchCurrent);

                    }
                    else {
                        hideSoftKeyboard(FindAddress.this,edDest);
                        edDest.setCursorVisible(false);
                        edDest.removeTextChangedListener(txtWatchDest);
                        endAdd = place;
                        edDest.setText(places.get(0).getAddress().toString());
                        mAdapter.clear();
                        edDest.setSelection(edDest.getText().length());
                        edDest.addTextChangedListener(txtWatchDest);
                    }
                    if(!edCurrent.getText().toString().trim().equals("")&&!edDest.getText().toString().trim().equals(""))
                    {
                        txtSkip.setVisibility(View.VISIBLE);
                    }
                    else {
                        txtSkip.setVisibility(View.GONE);
                    }
                } else {
                    Log.e("Error","Test  ");
                }

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlPick:
                rlNoData.setVisibility(View.VISIBLE);
                edCurrent.setText("");
                txtSkip.setVisibility(View.GONE);
                break;

            case R.id.rlPickDest:
                rlNoData.setVisibility(View.VISIBLE);
                edDest.setText("");
                txtSkip.setVisibility(View.GONE);
                break;

            case R.id.txtCancel:
                finish();
                break;
        }
    }

    public static void hideSoftKeyboard (Activity activity, View view)
    {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }
}
