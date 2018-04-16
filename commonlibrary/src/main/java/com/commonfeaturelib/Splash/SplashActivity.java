package com.commonfeaturelib.Splash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by janarthananr on 21/3/18.
 */

public class SplashActivity {
    Activity mActivity;
    LayoutInflater mInflater;
    View mView;
    int SPLASH_TIME_OUT = 2000;
    Class<?> TargetActivity = null;
    Bundle bundle = null;

    public SplashActivity (Activity activity,@LayoutRes int layoutId){
        this.mActivity = activity;
        this.mInflater = LayoutInflater.from(activity);
        this.mView = mInflater.inflate(layoutId, null);

    }
    public SplashActivity withFullScreen(){
        mActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return this;
    }
    public SplashActivity withTargetActivity( Class<?> tAct){
        this.TargetActivity = tAct;
        return this;
    }
    public SplashActivity withSplashTimeOut( int timout){
        this.SPLASH_TIME_OUT = timout;
        return this;
    }
    public SplashActivity withBundleExtras( Bundle bundle){
        this.bundle = bundle;
        return this;
    }
    public View create(){
        setUpHandler();
        return mView;
    }
    private void setUpHandler(){
        if (TargetActivity != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(mActivity, TargetActivity);
                    if (bundle != null) {
                        i.putExtras(bundle);
                    }
                    mActivity.startActivity(i);
                    mActivity.finish();
                }
            }, SPLASH_TIME_OUT);
        }
    }
}
