package com.shengz.dribbbo.view.ShotDetail;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by shengzhong on 2017/11/19.
 */

public class ShotDetailViewHolderImage extends RecyclerView.ViewHolder{
    SimpleDraweeView image;
    public ShotDetailViewHolderImage(View itemView) {
        super(itemView);
        image = (SimpleDraweeView) itemView;
    }
}
