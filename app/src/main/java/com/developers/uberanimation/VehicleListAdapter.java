package com.developers.uberanimation;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.location.places.AutocompletePrediction;

import java.util.ArrayList;


/**
 * Created by navdeepg on 11/10/2015.
 */
public class VehicleListAdapter extends RecyclerView.Adapter<VehicleListAdapter.SimpleViewHolder> {

    private final Context mContext;
    ISelectAddress mActivity;
    ArrayList<BeansPrediction> responseVehicle;
    public void notifyData(ArrayList<BeansPrediction> responseVehicle){
        this.responseVehicle=responseVehicle;
        notifyDataSetChanged();
    }

    public class SimpleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final TextView txtCarName,txtNumberPlate;
        public final ImageView imgVehicle;
        public final RelativeLayout rlMain,rlOption;
        public SimpleViewHolder(View view) {
            super(view);
            txtCarName = (TextView) view.findViewById(R.id.txtCarName);
            txtNumberPlate = (TextView) view.findViewById(R.id.txtNumberPlate);
            imgVehicle = (ImageView) view.findViewById(R.id.imgVehicle);
            rlMain = (RelativeLayout) view.findViewById(R.id.rlMain);
            rlOption = (RelativeLayout) view.findViewById(R.id.rlOption);

            rlMain.setOnClickListener(this);
            rlOption.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mActivity.itemSelected(getLayoutPosition());
        }
    }

    public VehicleListAdapter(Context context, ISelectAddress activity, ArrayList<BeansPrediction> responseVehicle, boolean IS_INIT_SETTINGS) {
        mContext = context;
        mActivity=activity;
        this.responseVehicle=responseVehicle;
    }

    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.vehicle_list_item, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, final int position) {

        holder.txtCarName.setText(responseVehicle.get(position).getStrFullTxt());

        holder.txtNumberPlate.setText(responseVehicle.get(position).getStrPrimaryTxt());
    }

    @Override
    public int getItemCount() {
        return responseVehicle.size();
    }


}
