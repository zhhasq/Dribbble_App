package com.shengz.dribbbo.view.bucket_list;

import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.shengz.dribbbo.R;
import com.shengz.dribbbo.view.ShotDetail.ShotDetailFragment;
import com.shengz.dribbbo.view.Template.SingleFragmentActivity;

public class BucketListActivity extends SingleFragmentActivity {

    @NonNull
    @Override
    protected Fragment newFragment() {
        int mode = getIntent().getIntExtra(BucketListFragment.MODE, 1);
        String jsonString = getIntent().getStringExtra(ShotDetailFragment.SHOT_IN_BUCKETS);

        if(mode == 1){
            return BucketListFragment.newInstance(mode, jsonString);
        }
        return null;
    }

    @NonNull
    @Override
    protected String getActivityTitle() {
        return "choose buckets";
    }


}
