package com.shengz.dribbbo.view.ShotDetail;

import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.shengz.dribbbo.R;
import com.shengz.dribbbo.view.Template.SingleFragmentActivity;

public class ShotDetailActivity extends SingleFragmentActivity {

    public static final String KEY_SHOT_TITLE = "shot_title";

    @NonNull
    @Override
    protected Fragment newFragment() {
        Bundle gString = getIntent().getExtras();
        return ShotDetailFragment.newInstance(gString);
    }

    @NonNull
    @Override
    protected String getActivityTitle() {
        return getIntent().getStringExtra(KEY_SHOT_TITLE);
    }


}
