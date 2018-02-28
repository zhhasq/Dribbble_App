package com.shengz.dribbbo.view.bucket_list;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.shengz.dribbbo.R;
import com.shengz.dribbbo.view.Template.BaseViewHolder;

import butterknife.BindView;

/**
 * Created by shengzhong on 2017/11/22.
 */

public class BucketListHolder extends BaseViewHolder{
    @BindView(R.id.bucket_layout) public View bucketLayout;
    @BindView(R.id.bucket_name) public TextView bucketName;
    @BindView(R.id.bucket_shot_count) public TextView bucketShotCount;
    @BindView(R.id.bucket_shot_chosen) public ImageView bucketChosen;


    public BucketListHolder(View itemView) {
        super(itemView);
    }
}
