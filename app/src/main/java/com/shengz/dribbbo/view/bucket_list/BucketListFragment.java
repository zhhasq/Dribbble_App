package com.shengz.dribbbo.view.bucket_list;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.shengz.dribbbo.R;
import com.shengz.dribbbo.model.Bucket;
import com.shengz.dribbbo.utils.utils;
import com.shengz.dribbbo.view.Template.CardSpace;
import com.shengz.dribbbo.view.Template.Dribbble;
import com.shengz.dribbbo.view.shot_list.ShotListAdapter;
import com.shengz.dribbbo.view.shot_list.ShotListFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by shengzhong on 2017/11/22.
 */

public class BucketListFragment extends Fragment {
    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.fab) FloatingActionButton fab;
    public BucketListAdapter adapter;
    public static final int REQ_CODE_NEW_BUCKET = 100;

    public static final String MODE = "Bucket_Mode";
    public static final String CHOSEN_BUCKET_IDS = "choosenBucketIDS";

    public static ArrayList<String> shotInBuckets;
    public static final int CHECK_BUCKET = 104;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getMode() == 1){
            setHasOptionsMenu(true);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bucket_view, container, false);

        ButterKnife.bind(this,view);
        return view;
    }
    //mode 0: show bucket list, mode 1: edit bucket list
    public static BucketListFragment newInstance(int mode, String listBuckets){
        BucketListFragment bucketListFragment = new BucketListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(MODE, mode);
        bucketListFragment.setArguments(bundle);

        shotInBuckets = utils.toObject(listBuckets, new TypeToken<ArrayList<String>>(){});
        return  bucketListFragment;
    }

    private int getMode(){
        return getArguments().getInt(MODE);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Bundle bundle = new Bundle();
        bundle.putInt(MODE, getMode());

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new CardSpace(
                getResources().getDimensionPixelSize(R.dimen.spacing_medium)
        ));
        adapter = new BucketListAdapter(this,new ArrayList<Bucket>(), bundle, new ShotListAdapter.LoadMoreListener() {
            @Override
            public void onLoadMore() {

                new LoadBucketTask(adapter.getDataSize()/Dribbble.COUNT_PER_PAGE + 1).execute();
            }
        });
        recyclerView.setAdapter(adapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BucketDialogFragment bucketDialogFragment = new BucketDialogFragment();
                bucketDialogFragment.setTargetFragment(BucketListFragment.this, REQ_CODE_NEW_BUCKET);
                bucketDialogFragment.show(getFragmentManager(),BucketDialogFragment.TAG);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

            inflater.inflate(R.menu.save_bucket_menu,menu);


    }
    //user click menu save button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() ==R.id.save_bucket_button){
            Log.d("save","save clicked");
            Intent intent = new Intent();
            intent.putExtra(CHOSEN_BUCKET_IDS, utils.toString(adapter.choosenBucket,new TypeToken<ArrayList<String>>(){}));
            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQ_CODE_NEW_BUCKET && resultCode == Activity.RESULT_OK){
            String bucketName = data.getStringExtra(BucketDialogFragment.KEY_BUCKET_NAME);
            String bucketDescription = data.getStringExtra(BucketDialogFragment.KEY_BUCKET_DESCRIPTION);
            if(!TextUtils.isEmpty(bucketName)){
                Log.i("get new bucket name",bucketName);
                new CreateBucket(bucketName, bucketDescription).execute();
            }
        }
        if(requestCode == CHECK_BUCKET){
            //Log.i("get new bucket name","lalala");
            adapter.clear();
            new LoadBucketTask(adapter.getDataSize()/Dribbble.COUNT_PER_PAGE + 1).execute();
        }
    }

    public void startBucketDetail(String id, String name) {
        Intent intent = new Intent(getActivity(),BucketDetailActivity.class);
        intent.putExtra(ShotListFragment.BUCKET_ID,id);
        intent.putExtra(ShotListFragment.BUCKET_NAME,name);
        startActivityForResult(intent,CHECK_BUCKET);
    }


    private class LoadBucketTask extends AsyncTask<Void, Void, List<Bucket>>{
        int page;

        public LoadBucketTask(int page){
            this.page = page;
        }
        @Override
        protected List<Bucket> doInBackground(Void... voids) {

            try{
                return Dribbble.getUserBuckets(page);
            }catch (IOException | JsonSyntaxException e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Bucket> buckets) {
            if(buckets != null){
                if(shotInBuckets != null){
                    for(Bucket singleBucket : buckets){
                        for(String checkBucket : shotInBuckets){
                            if(singleBucket.id.equals(checkBucket)){
                                singleBucket.isChoosing = true;
                            }

                        }
                    }
                }


                adapter.appendData(buckets);
                adapter.setLoading(buckets.size() == Dribbble.COUNT_PER_PAGE);

            }else {
                Snackbar.make(getView(), "Error!", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    private class CreateBucket extends AsyncTask<Void, Void, Bucket>{
        String bucketName;
        String buckDescription;
        public CreateBucket (String name, String description){
            this.bucketName = name;
            this.buckDescription = description;
        }
        @Override
        protected Bucket doInBackground(Void... voids) {

            try{
                return Dribbble.createBucket(bucketName, buckDescription);
            }catch (IOException | JsonSyntaxException e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bucket newBucket) {
            if(newBucket != null){
                adapter.prepend(Collections.singletonList(newBucket));
            }else {
                Snackbar.make(getView(), "Error!", Snackbar.LENGTH_LONG).show();
            }
        }
    }

}
