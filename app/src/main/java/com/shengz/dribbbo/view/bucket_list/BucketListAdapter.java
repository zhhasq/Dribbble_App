package com.shengz.dribbbo.view.bucket_list;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shengz.dribbbo.R;
import com.shengz.dribbbo.model.Bucket;
import com.shengz.dribbbo.view.shot_list.ShotListAdapter;
import com.shengz.dribbbo.view.shot_list.ShotListFragment;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shengzhong on 2017/11/22.
 */

public class BucketListAdapter extends RecyclerView.Adapter {
    public  List<Bucket> data;
    public int bucketMode;
    private static final int BUCKET_DATA = 0;
    private static final int BUCKET_LOADING = 1;
    private boolean showLoading;
    private ShotListAdapter.LoadMoreListener loadMoreListener;
    public ArrayList<String> choosenBucket;
    private final BucketListFragment bucketListFragment;

    public BucketListAdapter(BucketListFragment bucketListFragment, List<Bucket> data, Bundle args, ShotListAdapter.LoadMoreListener loadMoreListener){
        this.bucketListFragment = bucketListFragment;
        this.data = data;
        this.showLoading = true;
        this.loadMoreListener = loadMoreListener;
        this.bucketMode = args.getInt(BucketListFragment.MODE);
        choosenBucket = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == BUCKET_DATA){
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_bucket,parent,false);
            return new BucketListHolder(view);
        }else{
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_loading,parent,false);
            return new RecyclerView.ViewHolder(view){};
        }

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        int viewType = getItemViewType(position);
        if(viewType == BUCKET_DATA){
            final Bucket bucket = data.get(position);

            BucketListHolder bucketViewHolder = (BucketListHolder) holder;

            bucketViewHolder.bucketName.setText(bucket.name);
            bucketViewHolder.bucketShotCount.setText(bucket.shots_count);

            if(bucketMode == 0){
                bucketViewHolder.bucketChosen.setVisibility(View.GONE);
                bucketViewHolder.bucketLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String id = bucket.id;
                        String name = bucket.name;
                        //Intent intent = new Intent(holder.itemView.getContext(),BucketDetailActivity.class);
                        //intent.putExtra(ShotListFragment.BUCKET_ID,bucket.id);
                        //intent.putExtra(ShotListFragment.BUCKET_NAME,bucket.name);
                        //holder.itemView.getContext().startActivity(intent);
                        bucketListFragment.startBucketDetail(id,name);
                    }
                });

            }else {
                if(bucket.isChoosing == false){
                    bucketViewHolder.bucketChosen.setImageResource(R.drawable.ic_check_box_outline_blank_black_24px);
                }else if(bucket.isChoosing == true){
                    if(choosenBucket.contains(bucket.id) == false){
                        choosenBucket.add(bucket.id);
                    }
                    bucketViewHolder.bucketChosen.setImageResource(R.drawable.ic_check_box_black_24px);
                }
                bucketViewHolder.bucketLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        bucket.isChoosing = !bucket.isChoosing;
                        if(bucket.isChoosing == true){
                            if(choosenBucket.contains(bucket.id) == false){
                                choosenBucket.add(bucket.id);
                            }
                        }else{
                            if(choosenBucket.contains(bucket.id)){
                                choosenBucket.remove(bucket.id);
                            }
                        }
                        notifyItemChanged(position);
                    }
                });
            }

        }else {
            loadMoreListener.onLoadMore();
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (position < data.size()){
            return BUCKET_DATA;
        }else if(position == data.size() ){
            return BUCKET_LOADING;
        }
        return 0;
    }

    @Override
    public int getItemCount() {
        return showLoading ? data.size()+1 : data.size();
    }
    public void setLoading(boolean showLoading){
        this.showLoading = showLoading;
        notifyDataSetChanged();
    }
    public int getDataSize(){
        return data.size();
    }

    public void appendData(List<Bucket> moreData){
        data.addAll(moreData);
        notifyDataSetChanged();
    }
    public void prepend(@NonNull List<Bucket> data) {
        this.data.addAll(0, data);
        notifyDataSetChanged();
    }

    public void clear(){
        data.clear();
        notifyDataSetChanged();
    }

}
