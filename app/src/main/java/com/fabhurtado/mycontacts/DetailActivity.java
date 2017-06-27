package com.fabhurtado.mycontacts;

import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fabhurtado.mycontacts.view.GlideApp;


import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * DetailActivity displays all contact data
 *
 * @author FabHurtado
 */
public class DetailActivity extends AppCompatActivity {

    public static final String PARAM_USR = "user_id";
    public static final String PARAM_NAME = "user_name";
    public static final String PARAM_URL = "user_photo";

    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    @BindView(R.id.detail_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.photo_image_view)
    ImageView mPhotoImageView;

    @BindView(R.id.share_fab)
    FloatingActionButton mShareFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        setupToolbar(getIntent().getStringExtra(PARAM_NAME),
                getIntent().getStringExtra(PARAM_URL));

        if(savedInstanceState == null){
            //Creates an instance of DetailFragment and display it in detail_container
            DetailFragment detailFragment = DetailFragment
                    .newInstance(getIntent().getStringExtra(PARAM_USR));

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_container_layout, detailFragment)
                    .commit();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            navigateUpTo(new Intent(this, MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Sets the contact name as title and loads the contact photo
     * at the collapsing toolbar.
     *
     * @param usrName  Contains contact first and last name
     * @param usrUrl   The contact's photo url
     */
    private void setupToolbar(String usrName, String usrUrl){
        mCollapsingToolbarLayout.setTitle(usrName);

        GlideApp.with(this)
                .load(usrUrl)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(withCrossFade(android.R.anim.fade_in, 3000))
                .into(mPhotoImageView);

        //Sets toolbar as action bar
        setSupportActionBar(mToolbar);

        //Display Home up enabled
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }
}
