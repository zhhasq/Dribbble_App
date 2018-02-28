package com.shengz.dribbbo.view.ShotDetail;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.JsonSyntaxException;
import com.shengz.dribbbo.R;
import com.shengz.dribbbo.model.Bucket;
import com.shengz.dribbbo.model.Shot;
import com.shengz.dribbbo.view.MainActivity;
import com.shengz.dribbbo.view.Template.Dribbble;
import com.shengz.dribbbo.view.bucket_list.BucketListFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by shengzhong on 2017/11/19.
 */

public class ShotDetailAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_SHOT_IMAGE = 0;
    private static final int VIEW_TYPE_SHOT_INFO = 1;

    public  Shot shot;
    private final ShotDetailFragment shotDetailFragment;

    public ShotDetailAdapter(Shot shot, ShotDetailFragment shotDetailFragment){
        this.shot=shot;
        this.shotDetailFragment = shotDetailFragment;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch(viewType){
            case VIEW_TYPE_SHOT_IMAGE:
                Log.i("image","image1");
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shot_detail_image, parent,false);
                return new ShotDetailViewHolderImage(view);
            case VIEW_TYPE_SHOT_INFO:
                Log.i("info","info1");
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shot_detail, parent,false);
                return new ShotDetailViewHolder(view);
            default:
                return null;
        }

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final int viewType = getItemViewType(position);
        switch(viewType){
            case VIEW_TYPE_SHOT_IMAGE:
                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setUri(Uri.parse(shot.getImageUrl()))
                        .setAutoPlayAnimations(true)
                        .build();
                ((ShotDetailViewHolderImage) holder).image.setController(controller);
                break;
            case VIEW_TYPE_SHOT_INFO:
                final ShotDetailViewHolder shotDetailViewHolder = (ShotDetailViewHolder) holder;
                shotDetailViewHolder.title.setText(shot.title);
                shotDetailViewHolder.authorName.setText(shot.user.name);
                shotDetailViewHolder.description.setText(Html.fromHtml(
                        shot.description == null ? "" : shot.description));
                shotDetailViewHolder.likeCount.setText(String.valueOf(shot.likes_count));
                shotDetailViewHolder.bucketCount.setText(String.valueOf(shot.buckets_count));
                shotDetailViewHolder.viewCount.setText(String.valueOf(shot.views_count));
                Uri uri = Uri.parse(shot.user.avatar_url);
                shotDetailViewHolder.authorPicture.setImageURI(uri);
                shotDetailViewHolder.bucketButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shotDetailFragment.addToBucket(shot.id);
                    }
                });
                shotDetailViewHolder.likeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shot.liked = !shot.liked;
                        if(shot.liked == true){
                            shotDetailFragment.addtoFavorite(shot.id);
                        }else {
                            shotDetailFragment.deleteFromFavorite(shot.id);
                        }

                        //shotDetailFragment.addToFavorite(shot.id);
                    }
                });
                if(shot.bucketed == true){
                    shotDetailViewHolder.bucketButton.setImageResource(R.drawable.ic_inbox_white_24px);
                }else {
                    shotDetailViewHolder.bucketButton.setImageResource(R.drawable.ic_inbox_black_24px);
                }
                if(shot.liked == true){
                    shotDetailViewHolder.likeButton.setImageResource(R.drawable.ic_favorite_red_24px);
                }else {
                    shotDetailViewHolder.likeButton.setImageResource(R.drawable.ic_favorite_black_24px);
                }

        }
    }



//    private class CreateBucket extends AsyncTask<Void, Void, Integer> {
//        String targetShotID;
//        public CreateBucket (String id){
//            targetShotID = id;
//            Log.i("target shot id is " , id);
//        }
//
//        @Override
//        protected Integer doInBackground(Void... voids) {
//            List<Bucket> onePageBuckets = new ArrayList<>();
//            List<Shot> onePageShots = new ArrayList<>();
//            int result=0;
//            int bucketPage =1;
//            int shotPage =1;
//            boolean loop= true;
//            boolean loop2= true;
//
//            try {
//                onePageBuckets = Dribbble.getUserBuckets(1);
//                while (loop) {
//                    if(onePageBuckets.size() < 12){
//                        loop = false;
//                    }else if (onePageBuckets.size() == 12){
//                        bucketPage++;
//                    }
//                    for (Bucket bucket : onePageBuckets){
//                        onePageShots = Dribbble.getBucketShots(shotPage, Integer.parseInt(bucket.id));
//                        while(loop2){
//                            if(onePageShots.size() <12){
//                                loop2 = false;
//                            }else if (onePageShots.size() == 12){
//                                shotPage++;
//                            }
//                            for(Shot shot : onePageShots){
//                                Log.i("shot id is " ,shot.id);
//                                if (targetShotID.equals(shot.id) ){
//                                    result = 1;
//                                    return result;
//                                }
//                            }
//                            onePageShots = Dribbble.getBucketShots(shotPage, Integer.parseInt(bucket.id));
//                        }
//                        shotPage =1;
//                        loop2 = true;
//                    }
//                    onePageBuckets = Dribbble.getUserBuckets(bucketPage);
//                }
//                return result;
//
//            }catch (IOException | JsonSyntaxException e){
//                e.printStackTrace();
//                return null;
//            }
//        }
//
//        @Override
//        protected void onPostExecute(Integer integer) {
//            Log.i("result is ",""+integer);
//            if(integer == 1){
//                Log.i("in the bucket", "this shot in the bucket");
//            }
//        }
//    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_SHOT_IMAGE;
        }else{
            return VIEW_TYPE_SHOT_INFO;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }


}
