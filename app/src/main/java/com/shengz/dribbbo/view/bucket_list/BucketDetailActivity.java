package com.shengz.dribbbo.view.bucket_list;

import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.shengz.dribbbo.R;
import com.shengz.dribbbo.view.Template.SingleFragmentActivity;
import com.shengz.dribbbo.view.shot_list.ShotListFragment;

public class BucketDetailActivity extends SingleFragmentActivity {


    @NonNull
    @Override
    protected Fragment newFragment() {
        String idString = getIntent().getStringExtra(ShotListFragment.BUCKET_ID);
        int id = Integer.parseInt(idString);

       return ShotListFragment.newBucketInstance(2, id);
    }

    @NonNull
    @Override
    protected String getActivityTitle() {
        return getIntent().getStringExtra(ShotListFragment.BUCKET_NAME);
    }

}
