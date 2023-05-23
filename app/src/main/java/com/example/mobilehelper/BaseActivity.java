package com.example.mobilehelper;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
    }

    protected abstract void initData();
    protected abstract void initView();

    public void setTheView(@LayoutRes int layoutId){
        LayoutInflater.from(this).inflate(layoutId,this.findViewById(R.id.baseContentView));
    }

    protected void releaseRes(){

    }

    @Override
    protected void onDestroy() {
        releaseRes();
        super.onDestroy();
    }
}