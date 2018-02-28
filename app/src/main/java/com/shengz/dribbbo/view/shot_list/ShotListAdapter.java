package com.shengz.dribbbo.view.shot_list;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.animated.base.AbstractAnimatedDrawable;
import com.facebook.imagepipeline.image.ImageInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shengz.dribbbo.R;
import com.shengz.dribbbo.model.Shot;
import com.shengz.dribbbo.utils.utils;
import com.shengz.dribbbo.view.MainActivity;
import com.shengz.dribbbo.view.ShotDetail.ShotDetailActivity;
import com.shengz.dribbbo.view.ShotDetail.ShotDetailFragment;
import com.shengz.dribbbo.view.Template.BaseViewHolder;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shengzhong on 2017/11/18.
 */

public class ShotListAdapter extends RecyclerView.Adapter {
    public List<Shot> data;
    private static int TYPE_SHOT = 0;
    private  static int TYPE_LOAD = 1;
    private boolean showLoading;
    private LoadMoreListener loadMoreListener;
    private final ShotListFragment shotListFragment;

    public ShotListAdapter(@NonNull ShotListFragment shotListFragment, @NonNull List<Shot> data, @NonNull LoadMoreListener loadMoreListener)
    {
        this.shotListFragment = shotListFragment;
        this.data = data;
        this.loadMoreListener = loadMoreListener;
        this.showLoading = true;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_SHOT){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_shot, parent,false);
            return new ShotHolder(view);
        }else if(viewType == TYPE_LOAD){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_loading, parent,false);
            return new RecyclerView.ViewHolder(view){};
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if(getItemViewType(position) == TYPE_SHOT){
            final Shot shot = data.get(position);

            ShotHolder shotHolder = (ShotHolder) holder;

            shotHolder.likeCount.setText(String.valueOf(shot.likes_count));
            shotHolder.viewCount.setText(String.valueOf(shot.views_count));
            shotHolder.bucketCount.setText(String.valueOf(shot.buckets_count));
            for(Shot singleShot : MainActivity.allBucketsShots){
                        if(shot.id.equals(singleShot.id)){
                            shot.bucketed = true;
                            shotHolder.bucketCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_inbox_white_24px,0,0,0);
                            break;
                        }else {
                            shotHolder.bucketCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_inbox_black_24px,0,0,0);
                }
            }

            for(Shot singleShot : MainActivity.allLikesShots){
                if(shot.id.equals(singleShot.id)){
                    shot.liked = true;
                    shotHolder.likeCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite_red_24px,0,0,0);
                    break;
                }else {
                    shotHolder.likeCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite_black_24px,0,0,0);

                }
            }
            //shotHolder.image.setImageResource(R.drawable.shot_placeholder);
            // play gif automatically
            String imageUrl = shot.getImageUrl();
            if (!TextUtils.isEmpty(imageUrl)) {
                Uri imageUri = Uri.parse(imageUrl);
                if(shot.animated){
                    DraweeController controller = Fresco.newDraweeControllerBuilder()
                            .setUri(imageUri)
                            .setAutoPlayAnimations(true)
                            .build();

                    shotHolder.image.setController(controller);
                }else {
                    shotHolder.image.setImageURI(imageUri);
                }
            }

            shotHolder.cover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String singleShotJson = utils.toString(shot,new TypeToken<Shot>(){});
                    Bundle args = new Bundle();
                    args.putString(ShotDetailFragment.KEY_SHOT, singleShotJson);
                    shotListFragment.startDetailAcitivity(args,shot.title,shot.id,position);

//                    Context context = holder.itemView.getContext();
//                    Intent intent = new Intent(context, ShotDetailActivity.class);
//                    intent.putExtra(ShotDetailFragment.KEY_SHOT,
//                            utils.toString(shot,new TypeToken<Shot>(){}));
//                    intent.putExtra (ShotDetailActivity.KEY_SHOT_TITLE,shot.title);
                    //context.startActivity(intent);


                }
            });
        }else if(getItemViewType(position) == TYPE_LOAD){
            loadMoreListener.onLoadMore();
        }

    }

    @Override
    public int getItemViewType(int position) {
        if(position < data.size()){
            return TYPE_SHOT;
        }else {
            return TYPE_LOAD;
        }


        }

    public interface LoadMoreListener{
        void onLoadMore();
    }

    public void append(@NonNull List<Shot> moreShots){
        data.addAll(moreShots);
        notifyDataSetChanged();//will call getItemCount
    }
    public void clear(){
        data.clear();
        notifyDataSetChanged();
    }

    public void setShowLoading(boolean showLoading){
        this.showLoading = showLoading;
        notifyDataSetChanged();
    }
    @Override
    //all the elements in the list
    public int getItemCount() {

        return showLoading ? data.size()+1 : data.size();
    }
    public int getDataCount(){
        return data.size();
    }
}
