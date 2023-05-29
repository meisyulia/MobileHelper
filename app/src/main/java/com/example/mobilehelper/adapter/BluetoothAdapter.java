package com.example.mobilehelper.adapter;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilehelper.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BluetoothAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private ArrayList<BluetoothDevice> mInfoArrayList;


    public BluetoothAdapter(Context context, ArrayList<BluetoothDevice> infoArrayList){
        mContext = context;
        mInfoArrayList = infoArrayList;
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_bluetooth, parent, false);
        return new ItemHolder(v);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        BluetoothDevice item = mInfoArrayList.get(position);
        ItemHolder itemHolder = (ItemHolder) holder;
        itemHolder.tv_blueName.setText(item.getName());
        itemHolder.tv_blueAddress.setText(item.getAddress());
    }

    @Override
    public int getItemCount() {
        return mInfoArrayList.size();
    }

    public void setDataList(ArrayList<BluetoothDevice> infoArrayList){
        mInfoArrayList = infoArrayList;
        notifyDataSetChanged();
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.rl_item)
        RelativeLayout rl_item;
        @BindView(R.id.tv_blueName)
        TextView tv_blueName;
        @BindView(R.id.tv_blueAddress)
        TextView tv_blueAddress;
        public ItemHolder(View v) {
            super(v);
            ButterKnife.bind(this,v);
        }
    }
}
