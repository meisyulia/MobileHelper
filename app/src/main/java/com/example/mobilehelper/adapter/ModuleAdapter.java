package com.example.mobilehelper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilehelper.MainActivity;
import com.example.mobilehelper.R;
import com.example.mobilehelper.bean.ModuleInfo;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ModuleAdapter extends RecyclerView.Adapter {

    private final Context mContext;
    private final ArrayList<ModuleInfo> mListArray;
    private MainActivity mMainActivity;

    public ModuleAdapter(Context context, ArrayList<ModuleInfo> listArray){
        mContext = context;
        mListArray = listArray;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_title, parent, false);
        return new ItemHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ModuleInfo item = mListArray.get(position);
        ItemHolder itemHolder = (ItemHolder) holder;
        itemHolder.ll_item.setTag(item.getTag());
        itemHolder.iv_icon.setImageResource(item.getIc_id());
        itemHolder.tv_title.setText(item.getTitle());
        itemHolder.ll_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainActivity.switchPage((Integer) v.getTag());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mListArray.size();
    }

    private class ItemHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ll_item)
        LinearLayout ll_item;

        @BindView(R.id.iv_icon)
        ImageView iv_icon;

        @BindView(R.id.tv_title)
        TextView tv_title;

        public ItemHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }


}
