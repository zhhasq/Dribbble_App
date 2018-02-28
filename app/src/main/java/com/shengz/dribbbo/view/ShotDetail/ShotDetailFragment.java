package com.shengz.dribbbo.view.ShotDetail;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.shengz.dribbbo.R;
import com.shengz.dribbbo.model.Bucket;
import com.shengz.dribbbo.model.Like;
import com.shengz.dribbbo.model.Shot;
import com.shengz.dribbbo.utils.utils;
import com.shengz.dribbbo.view.MainActivity;
import com.shengz.dribbbo.view.Template.Dribbble;
import com.shengz.dribbbo.view.bucket_list.BucketListActivity;
import com.shengz.dribbbo.view.bucket_list.BucketListFragment;
import com.shengz.dribbbo.view.shot_list.ShotListFragment;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.POWER_SERVICE;

/**
 * Created by shengzhong on 2017/11/19.
 */

public class ShotDetailFragment extends Fragment{

    public static final String KEY_SHOT = "shot";
    public static final String SHOT_IN_BUCKETS = "shot_in_bucket";
    private static final int REQ_CODE_BUCKET = 100;
    public static final int REQ_CODE_SAVE_SHOT_TO_BUCKETS = 101;
    private Shot shot;
    public ShotDetailAdapter shotDetailAdapter;
    private  ArrayList<String> allBuckets;
    private  ArrayList<String> bucketsChoosen;

    @BindView(R.id.recycler_view) RecyclerView recyclerView;

    public static  ShotDetailFragment newInstance(@NonNull Bundle args){
        ShotDetailFragment shotDetailFragment = new ShotDetailFragment();
        shotDetailFragment.setArguments(args);
        return shotDetailFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);
        setHasOptionsMenu(true);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        int ifLiked;
        int ifInBucket;
        shot = utils.toObject(getArguments().getString(KEY_SHOT), new TypeToken<Shot>(){});
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        shotDetailAdapter = new ShotDetailAdapter(shot, this);
        recyclerView.setAdapter(shotDetailAdapter);
    }
    public void addtoFavorite(String id) {
        new addFavorite(id).execute();
        MainActivity.allLikesShots.add(shot);
        new LoadSingleShot(shot.id).execute();
    }
    private class addFavorite extends AsyncTask<Void, Void, Void>{
        String shotID;
        public addFavorite (String shotID){
            this.shotID = shotID;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            try{
                return Dribbble.addShotToFavorite(shotID);
            }catch (IOException | JsonSyntaxException e){
                e.printStackTrace();
                return null;
            }
        }
    }

    public void deleteFromFavorite(String id) {
        new deleteFavorite(id).execute();
        for(Shot aShot : MainActivity.allLikesShots){
            if(aShot.id.equals(shot.id)){
                MainActivity.allLikesShots.remove(aShot);
                break;
            }
        }
        new LoadSingleShot(shot.id).execute();
    }
    private class deleteFavorite extends AsyncTask<Void, Void, Void>{
        String shotID;
        public deleteFavorite (String shotID){
            this.shotID = shotID;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            try{
                return Dribbble.deleteShotFavorite(shotID);
            }catch (IOException | JsonSyntaxException e){
                e.printStackTrace();
                return null;
            }
        }
    }

    public void addToBucket(String shotID) {
        allBuckets = new ArrayList<>();
        bucketsChoosen = new ArrayList<>();
        for(Shot singleShot : MainActivity.allBucketsShots){
            if (shotID.equals(singleShot.id)){
                allBuckets.add(singleShot.inBuckets);
            }
        }
        Gson gson = new Gson();
        String JsonAllBuckets = gson.toJson(allBuckets);
        Log.d("old buckets is", JsonAllBuckets);

        Intent intent = new Intent(getActivity(), BucketListActivity.class);
        intent.putExtra(BucketListFragment.MODE, 1);
        intent.putExtra(SHOT_IN_BUCKETS,JsonAllBuckets);
        startActivityForResult(intent,REQ_CODE_SAVE_SHOT_TO_BUCKETS);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQ_CODE_SAVE_SHOT_TO_BUCKETS && resultCode == RESULT_OK){

            String savedBucketsID =  data.getStringExtra(BucketListFragment.CHOSEN_BUCKET_IDS);
            bucketsChoosen = utils.toObject(savedBucketsID, new TypeToken<ArrayList<String>>(){});
            Log.d("return buckets is",savedBucketsID );

            //returned is bucketsChoosen, old is allBuckets
            Log.d("allBucketShots1",""+MainActivity.allBucketsShots.size());
            if(bucketsChoosen.size() != 0) {
                for (String singleBucketID : bucketsChoosen) {
                    if (!allBuckets.contains(singleBucketID)) {
                        new addShotToBucket(shot.id, singleBucketID).execute();
                        Shot newshot = new Shot();
                        newshot.id = shot.id;
                        newshot.inBuckets = singleBucketID;
                        MainActivity.allBucketsShots.add(newshot);
                        Log.d("allBucketShots2", "" + MainActivity.allBucketsShots.size());
                    }
                }
                for (String singleOldBucketID : allBuckets){
                    //old bucket is not included in the new selected bucket
                    //delete.
                    if(!bucketsChoosen.contains(singleOldBucketID)){
                        new deleteShotFromBucket(shot.id,singleOldBucketID).execute();
                        for(Shot checkAllshots : MainActivity.allBucketsShots){
                            if(checkAllshots.id.equals(shot.id) && checkAllshots.inBuckets.equals(singleOldBucketID)){
                                Log.d("enter the loop1","aa");
                                MainActivity.allBucketsShots.remove(checkAllshots);
                                Log.d("allBucketShots3", "" + MainActivity.allBucketsShots.size());
                                break;
                            }

                        }
//                        for(Shot checkAllshots : MainActivity.allBucketsShots){
//                            if(checkAllshots.id.equals(shot.id) && checkAllshots.inBuckets.equals(singleOldBucketID)){
//                                    //int oldindex = allBuckets.indexOf(singleOldBucketID);
//                                    // allBuckets.remove(oldindex);
//                                    int index =  MainActivity.allBucketsShots.indexOf(checkAllshots);
//                                    MainActivity.allBucketsShots.remove(index);
//                            }
//                        }
                    }
                }
            } else{
                for (String singleOldBucketID : allBuckets){
                    new deleteShotFromBucket(shot.id,singleOldBucketID).execute();
                    for(Shot checkAllshots : MainActivity.allBucketsShots){
                        if(checkAllshots.id.equals(shot.id) && checkAllshots.inBuckets.equals(singleOldBucketID)){
                            //int oldindex = allBuckets.indexOf(singleOldBucketID);
                            // allBuckets.remove(oldindex);
                            int index =  MainActivity.allBucketsShots.indexOf(checkAllshots);
                            MainActivity.allBucketsShots.remove(index);
                            Log.d("allBucketShots4", "" + MainActivity.allBucketsShots.size());
                            break;
                        }
                    }
                }
            }
            new LoadSingleShot(shot.id).execute();

        }

    }




    private class LoadSingleShot extends AsyncTask<Void, Void, Shot >{
        String shotID;
        public LoadSingleShot(String shotID){
            this.shotID = shotID;
        }
        @Override
        //get Shot from API request
        protected Shot doInBackground(Void... voids) {
            try{
                return Dribbble.LoadSingleShot(shotID);
            }catch (IOException | JsonSyntaxException e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Shot shot) {
            shot.bucketed = false;
            for(Shot singleShot : MainActivity.allBucketsShots){
                if(shot.id.equals(singleShot.id)){
                    shot.bucketed = true;
                    break;
                }
            }
            for(Shot singleShot : MainActivity.allLikesShots){
                if(shot.id.equals(singleShot.id)){
                    shot.liked = true;
                    break;
                }
            }
            shotDetailAdapter.shot = shot;
            shotDetailAdapter.notifyDataSetChanged();
        }
    }

    private class deleteShotFromBucket extends AsyncTask<Void, Void, Void>{
        String shotID;
        String bucketID;
        public deleteShotFromBucket (String shotID, String bucketID){
            this.shotID = shotID;
            this.bucketID = bucketID;
        }
        @Override
        protected Void doInBackground(Void... voids) {

            try{
                return Dribbble.deleteShotFromBucket(shotID, bucketID);
            }catch (IOException | JsonSyntaxException e){
                e.printStackTrace();
                return null;
            }
        }
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        if(item.getItemId() == android.R.id.home){
//            Intent intent = new Intent();
//           // intent.putExtra(CHOSEN_BUCKET_IDS, utils.toString(adapter.choosenBucket,new TypeToken<ArrayList<String>>(){}));
//            getActivity().setResult(Activity.RESULT_OK, intent);
//            Log.d("fragment back "," clicked");
//            getActivity().finish();
//        }
//        return super.onOptionsItemSelected(item);
//    }

    private class addShotToBucket extends AsyncTask<Void, Void, Void>{
        String shotID;
        String bucketID;
        public addShotToBucket (String shotID, String bucketID){
            this.shotID = shotID;
            this.bucketID = bucketID;
        }
        @Override
        protected Void doInBackground(Void... voids) {

            try{
                return Dribbble.addShotToBucket(shotID, bucketID);
            }catch (IOException | JsonSyntaxException e){
                e.printStackTrace();
                return null;
            }
        }
        //@Override
//        protected void onPreExecute() {
//           shot.bucketed =true;
//           shot.likes_count++;
//           shotDetailAdapter.notifyDataSetChanged();
//        }


    }
}
