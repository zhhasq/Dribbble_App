package com.shengz.dribbbo.view.shot_list;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.JsonSyntaxException;
import com.shengz.dribbbo.R;
import com.shengz.dribbbo.model.Shot;
import com.shengz.dribbbo.model.User;
import com.shengz.dribbbo.view.MainActivity;
import com.shengz.dribbbo.view.ShotDetail.ShotDetailActivity;
import com.shengz.dribbbo.view.ShotDetail.ShotDetailFragment;
import com.shengz.dribbbo.view.Template.CardSpace;
import com.shengz.dribbbo.view.Template.Dribbble;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

/**
 * Created by shengzhong on 2017/11/18.
 */

public class ShotListFragment extends Fragment
{
    public static final String LIST_TYPE = "List_Type";
    public static final String BUCKET_ID = "Bucket_ID";
    public static final String BUCKET_NAME = "Bucket_name";
    public static final int UPDATE_SHOT = 103;

    private String shotID;
    private int adapterPosition;

    private ShotListAdapter adapter;
    private static final int COUNT_PER_PAGE =20;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    private int listType;

    public static ShotListFragment newInstance(int fragmentType){
        ShotListFragment shotListFragment = new ShotListFragment();
        Bundle args = new Bundle();
        args.putInt(LIST_TYPE,fragmentType);
        shotListFragment.setArguments(args);

        return shotListFragment;
    }

    public static Fragment newBucketInstance(int fragmentType, int id) {
        ShotListFragment shotListFragment = new ShotListFragment();
        Bundle args = new Bundle();
        args.putInt(LIST_TYPE,fragmentType);
        args.putInt(BUCKET_ID, id);
        shotListFragment.setArguments(args);

        return shotListFragment;

    }

    public int getIndex() {
        return getArguments().getInt(LIST_TYPE, 0);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);
        ButterKnife.bind(this,view);
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        final Handler handler = new Handler();
        //adapter = new ShotListAdapter(fakeData(0), new ShotListAdapter.LoadMoreListener() {
        this.adapter = new ShotListAdapter(this,new ArrayList<Shot>(), new ShotListAdapter.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                    Load();
            }
        });
        recyclerView.addItemDecoration(new CardSpace(getResources().getDimensionPixelSize(R.dimen.spacing_medium)));
        recyclerView.setAdapter(this.adapter);
        super.onViewCreated(view, savedInstanceState);
    }

    private void Load(){
        if(getIndex()==0){
            new LoadShotTask(this.adapter.getDataCount()/Dribbble.COUNT_PER_PAGE + 1).execute();
        }
        if(getIndex()==1){
            new LoadLikeTask(this.adapter.getDataCount()/Dribbble.COUNT_PER_PAGE + 1).execute();
        }
        if(getIndex() ==2){
            new LoadBucketShots(
                    this.adapter.getDataCount()/Dribbble.COUNT_PER_PAGE + 1,
                    getArguments().getInt(BUCKET_ID)).execute();
        }
    }

    public void startDetailAcitivity(Bundle args, String shotTitle, String shotID, int adapterPosition) {
        this.shotID = shotID;
        this.adapterPosition = adapterPosition;
        String jsonShotString = args.getString(ShotDetailFragment.KEY_SHOT);
        Intent intent = new Intent(getActivity(), ShotDetailActivity.class);
        intent.putExtra(ShotDetailFragment.KEY_SHOT, jsonShotString);
        intent.putExtra (ShotDetailActivity.KEY_SHOT_TITLE,shotTitle);

        startActivityForResult(intent,UPDATE_SHOT);
        //Toast.makeText(getActivity(),"aa", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Toast.makeText(getActivity(),"bb", Toast.LENGTH_LONG).show();
        if(getIndex() == 1){
            adapter.clear();
            new LoadLikeTask(this.adapter.getDataCount()/Dribbble.COUNT_PER_PAGE + 1).execute();
        }else {
            new LoadSingleShot(shotID).execute();
        }
        super.onActivityResult(requestCode, resultCode, data);
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
            adapter.data.set(adapterPosition, shot);
            adapter.notifyItemChanged(adapterPosition);
        }
    }

    private class LoadBucketShots extends AsyncTask<Void, Void, List<Shot> >{
        int page;
        int id;
        public LoadBucketShots(int page, int id){
            this.page = page;
            this.id = id;
        }

        @Override
        //get Shot from API request
        protected List<Shot> doInBackground(Void... voids) {
            try{
                return Dribbble.getBucketShots(page,id);
            }catch (IOException | JsonSyntaxException e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        //add shot to the adapter.data
        protected void onPostExecute(List<Shot> shots) {
            if(shots != null){
                adapter.append(shots);
                adapter.setShowLoading(shots.size() == Dribbble.COUNT_PER_PAGE);
            }else{
                Snackbar.make(getView(), "Error!",Snackbar.LENGTH_LONG).show();
            }
        }
    }

    private class LoadLikeTask extends AsyncTask<Void, Void, List<Shot> >{
        int page;
        public LoadLikeTask(int page){
            this.page = page;
        }

        @Override
        //get Shot from API request
        protected List<Shot> doInBackground(Void... voids) {
            try{
                return Dribbble.getLikes(page);
            }catch (IOException | JsonSyntaxException e){
                e.printStackTrace();
                return null;
            }
        }
        @Override
        //add shot to the adapter.data
        protected void onPostExecute(List<Shot> shots) {
            if(shots != null){
                adapter.append(shots);
                adapter.setShowLoading(shots.size() == Dribbble.COUNT_PER_PAGE);
            }else{
                Snackbar.make(getView(), "Error!",Snackbar.LENGTH_LONG).show();
            }
        }
    }


    private class LoadShotTask extends AsyncTask<Void, Void, List<Shot> >{
        int page;
        public LoadShotTask(int page){
            this.page = page;
        }

        @Override
        //get Shot from API request
        protected List<Shot> doInBackground(Void... voids) {
            try{
                return Dribbble.getShot(page);
            }catch (IOException | JsonSyntaxException e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        //add shot to the adapter.data
        protected void onPostExecute(List<Shot> shots) {
            if(shots != null){
                adapter.append(shots);
                adapter.setShowLoading(shots.size() == Dribbble.COUNT_PER_PAGE);
            }else{
                Snackbar.make(getView(), "Error!",Snackbar.LENGTH_LONG).show();
            }
        }
    }
}
