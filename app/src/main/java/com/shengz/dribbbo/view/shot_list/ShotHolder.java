package com.shengz.dribbbo.view.shot_list;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.shengz.dribbbo.R;
import com.shengz.dribbbo.view.Template.BaseViewHolder;

import butterknife.BindView;

/**
 * Created by shengzhong on 2017/11/18.
 */

public class ShotHolder extends BaseViewHolder {
    @BindView(R.id.shot_clickable_cover) public View cover;
    @BindView(R.id.shot_like_count) public TextView likeCount;
    @BindView(R.id.shot_view_count) public TextView viewCount;
    @BindView(R.id.shot_bucket_count) public TextView bucketCount;
    @BindView(R.id.shot_image) public SimpleDraweeView image;
    public ShotHolder(View itemView) {
        super(itemView);
    }

}
