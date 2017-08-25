package com.developers.uberanimation;

import android.util.Log;

import com.developers.uberanimation.network.NetworkService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by navdeepg on 2017-08-24.
 */

public class RoadsPresenter implements RoadsPresenterInteractor{
    MapsActivity view;
    private NetworkService service;
    public RoadsPresenter(MapsActivity view, NetworkService service){
        this.view = view;
        this.service = service;
    }

    @Override
    public void getRoute(String URL, final int route) {
        Call<BeansMain> call = service.getAPI().getPath(URL,
                true,"AIzaSyAiZW5l6Si2-SZ_L_bfk_4o6sJqXaQuH6o");
        call.enqueue(new Callback<BeansMain>() {
            @Override
            public void onResponse(Call<BeansMain> call, Response<BeansMain> response) {
                Log.e("Success","oyeeee"+response.body().getSnappedPoints().size());
                if(route==1)
                    view.firstRoute(response.body());
                else if(route==2)
                    view.secondRoute(response.body());
                else if(route==3)
                    view.thirdRoute(response.body());
                else if(route==4)
                    view.fourthRoute(response.body());
            }

            @Override
            public void onFailure(Call<BeansMain> call, Throwable t) {
                Log.e("Error","error");
            }
        });
    }
}