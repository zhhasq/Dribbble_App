package com.shengz.dribbbo.view;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.JsonSyntaxException;
import com.shengz.dribbbo.R;
import com.shengz.dribbbo.model.Bucket;
import com.shengz.dribbbo.model.Shot;
import com.shengz.dribbbo.view.Template.Dribbble;
import com.shengz.dribbbo.view.bucket_list.BucketListFragment;
import com.shengz.dribbbo.view.shot_list.ShotListAdapter;
import com.shengz.dribbbo.view.shot_list.ShotListFragment;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private NavigationView nvDrawer;
    private TextView userName;
    private TextView logOutBtn;
    private SimpleDraweeView userHeadImage;
    public static List<Shot> allBucketsShots;
    public static List<Shot> allLikesShots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        nvDrawer = (NavigationView) findViewById(R.id.nv_drawer);

        View headerView = nvDrawer.getHeaderView(0);

        userName = (TextView) headerView.findViewById(R.id.nav_header_user_name);
        logOutBtn = (TextView) headerView.findViewById(R.id.nav_header_logout);
        userHeadImage = (SimpleDraweeView) headerView.findViewById(R.id.nav_header_user_picture);
        Log.i("userHead",Dribbble.getCurrentUser().avatar_url);
        userHeadImage.setImageURI(Uri.parse(Dribbble.getCurrentUser().avatar_url));

        userName.setText(Dribbble.getCurrentUser().name);
        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dribbble.logOut(MainActivity.this);
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
                finish();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        drawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,          /* DrawerLayout object */
                R.string.open_drawer,         /* "open drawer" description */
                R.string.close_drawer         /* "close drawer" description */
        );
        drawerLayout.setDrawerListener(drawerToggle);
        setupDrawerContent(nvDrawer);


        if(savedInstanceState == null){
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.fragment_container, ShotListFragment.newInstance(0));
            fragmentTransaction.commit();
            allBucketsShots = new ArrayList<>();
            allLikesShots = new ArrayList<>();
            new getAllBucketsShots().execute();
            new getAllLikesShots().execute();
        }


    }
    private class getAllLikesShots extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            List<Shot> onePageLikes = new ArrayList<>();

            int likePage = 1;

            boolean loop = true;

            try {
                onePageLikes = Dribbble.getLikes(1);
                while (loop) {
                    if (onePageLikes.size() < 12) {
                        loop = false;
                    } else if (onePageLikes.size() == 12) {
                        likePage++;
                    }
                    if(onePageLikes.size() != 0 ){
                        for(Shot shot : onePageLikes){
                            shot.liked = true;
                        }
                        allLikesShots.addAll(onePageLikes);
                    }

                    if(loop){
                        onePageLikes = Dribbble.getLikes(likePage);
                    }
                }
            } catch (IOException | JsonSyntaxException e) {
                e.printStackTrace();
                return null;
            }
            return null;
        }

    }

    private class getAllBucketsShots extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            List<Bucket> onePageBuckets = new ArrayList<>();
            List<Shot> onePageShots = new ArrayList<>();
            int bucketPage = 1;
            int shotPage = 1;
            boolean loop = true;
            boolean loop2 = true;

            try {
                onePageBuckets = Dribbble.getUserBuckets(1);
                while (loop) {
                    if (onePageBuckets.size() < 12) {
                        loop = false;
                    } else if (onePageBuckets.size() == 12) {
                        bucketPage++;
                    }
                    for (Bucket bucket : onePageBuckets) {
                        Log.d("bucket id is", bucket.id);
                        onePageShots = Dribbble.getBucketShots(shotPage, Integer.parseInt(bucket.id));
                        while (loop2) {
                            if (onePageShots.size() != 0) {
                                for(Shot singleShot : onePageShots){
                                    Log.d("belongs to bucket", bucket.id);
                                    singleShot.inBuckets = bucket.id;
                                }
                                allBucketsShots.addAll(onePageShots);
                                Log.d("allBucketsShots",""+allBucketsShots.size());

                            }
                            if (onePageShots.size() < 12) {
                                loop2 = false;
                            } else if (onePageShots.size() == 12) {
                                shotPage++;
                            }
                            if(loop2 != false){
                                onePageShots = Dribbble.getBucketShots(shotPage, Integer.parseInt(bucket.id));
                            }
                        }
                        shotPage = 1;
                        loop2 = true;
                    }
                    if(loop != false){
                        onePageBuckets = Dribbble.getUserBuckets(bucketPage);
                    }
                }
            } catch (IOException | JsonSyntaxException e) {
                e.printStackTrace();
                return null;
            }
            return null;
        }

    }



    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener(){

                    Fragment fragment = null;
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch(menuItem.getItemId()){
                            case R.id.drawer_item_home:
                                //Toast.makeText(MainActivity.this, "home clicked",Toast.LENGTH_LONG).show();
                                setTitle(R.string.title_home);
                                fragment = ShotListFragment.newInstance(0);
                                break;
                            case R.id.drawer_item_likes:
                                //Toast.makeText(MainActivity.this, "likes clicked",Toast.LENGTH_LONG).show();
                                setTitle(R.string.title_likes);
                                fragment = ShotListFragment.newInstance(1);
                                break;
                            case R.id.drawer_item_buckets:
                                //Toast.makeText(MainActivity.this, "buckets clicked",Toast.LENGTH_LONG).show();
                                setTitle(R.string.title_buckets);
                                fragment = BucketListFragment.newInstance(0,null);
                                break;

                        }
                        menuItem.setChecked(true);
                        drawerLayout.closeDrawers();

                        if (fragment != null){
                            FragmentManager fragmentManager = getFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.fragment_container, fragment);
                            fragmentTransaction.commit();
                        }
                        return true;
                    }
                }
        );
    }
}
