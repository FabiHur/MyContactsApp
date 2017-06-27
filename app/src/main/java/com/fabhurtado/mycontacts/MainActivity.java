package com.fabhurtado.mycontacts;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.fabhurtado.mycontacts.adapters.ContactAdapter;
import com.fabhurtado.mycontacts.sync.RetrofitClient;
import com.fabhurtado.mycontacts.sync.model.Contact;
import com.fabhurtado.mycontacts.sync.service.ContactService;
import com.fabhurtado.mycontacts.utils.NetworkDetector;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * This class will load the contact list and perform
 * the contact search.
 *
 * @author FabHurtado
 */
public class MainActivity extends AppCompatActivity implements ContactAdapter.ContactAdapterOnClickHandler {

    private static final String SEARCH_STATE = "search_query";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.contact_recycler_view)
    RecyclerView mContactRecyclerView;

    @BindView(R.id.error_text_view)
    TextView mErrorTextView;

    private ArrayList<Contact> mContacts;
    private ContactAdapter mContactAdapter;
    private SearchView mSearchView;
    private String mQuery;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ButterKnife will perform all findViewById for us
        ButterKnife.bind(this);

        //Set the toolbar
        setSupportActionBar(mToolbar);

        setupView();

        //Check twoPane
        if(findViewById(R.id.detail_container_layout) != null){
            mTwoPane = true;
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(SEARCH_STATE, mSearchView.getQuery().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mQuery = savedInstanceState.getString(SEARCH_STATE);
    }

    /**
     * Setup the menu. The search item menu allow the user to
     * seach a contact by writing part of the first or last name.
     *
     * @param menu the activity menu
     * @return true when menu options are created
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        //Set up the search menu option.
        MenuItem searchItem = menu.findItem(R.id.search);

        //Set search action to search item menu
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        if(mQuery != null && !mQuery.isEmpty()){
            searchItem.expandActionView();
            mSearchView.setQuery(mQuery, true);
            mSearchView.clearFocus();
        }

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performFilter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                performFilter(newText);
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_refresh:
                loadContactsWithRefreshing();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void performFilter(String query){
        if(mContactAdapter != null){
            mContactAdapter.getFilter().filter(query);
        }
    }

    /**
     * Setup contact list view
     */
    private void setupView(){
        //Set the action to perform each time the user swipe to refresh the screen
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadContacts();
            }
        });

        //Create an instance of ContactAdapter to set to Contact RecyclerView
        mContactAdapter = new ContactAdapter(this, this);

        //Setup Contact RecyclerView
        mContactRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mContactRecyclerView.setAdapter(mContactAdapter);
        mContactRecyclerView.setHasFixedSize(true);

        //First time refreshing indicator is shown while loading contacts in the background
        loadContactsWithRefreshing();
    }

    /**
     * This method activate the refreshing indicator
     * manually before contact loading.
     */
    private void loadContactsWithRefreshing(){
        mSwipeRefreshLayout.setRefreshing(true);
        loadContacts();
    }

    /**
     * Loads contacts in the background
     */
    private void loadContacts(){
        //First verify if the device is connected to internet
        if(NetworkDetector.isConnectedToInternet(this)){
            //If so, let's show the RecyclerView
            displayError(false);

            //Get the Retrofit service
            ContactService contactService = RetrofitClient.getContactService();

            //Look for contact list
            Call<ArrayList<Contact>> call = contactService.getContacts();
            call.enqueue(new Callback<ArrayList<Contact>>() {
                @Override
                public void onResponse(@NonNull Call<ArrayList<Contact>> call, @NonNull Response<ArrayList<Contact>> response) {
                    mContacts = response.body();

                    if(mContacts != null && !mContacts.isEmpty()){
                        //Update the Adapter contact list and notify the data change
                        mContactAdapter.updateContacts(mContacts);
                        onLoadContactsComplete();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ArrayList<Contact>> call, @NonNull Throwable t) {
                    Log.e("RETROFIT", t.toString());

                    if(!NetworkDetector.isConnectedToInternet(getApplicationContext())){
                        //As is not connected to internet. Let's show the connection error message
                        setErrorMessage(R.string.error_connection_main);
                    }else {
                        setErrorMessage(R.string.error_unexpected_main);
                    }

                    displayError(true);
                    onLoadContactsComplete();
                }
            });

        } else {
            //As is not connected to internet. Let's show the connection error message
            setErrorMessage(R.string.error_connection_main);
            displayError(true);
            onLoadContactsComplete();
        }

    }

    /**
     * Stop showing the refreshing indicator
     */
    private void onLoadContactsComplete(){
        mSwipeRefreshLayout.setRefreshing(false);
    }

    /**
     * Show/Hide Error message and Contact List
     * @param show true to enable error message
     */
    private void displayError(boolean show){
        if(show) {
            mContactRecyclerView.setVisibility(View.GONE);
            mErrorTextView.setVisibility(View.VISIBLE);
        }else {
            mContactRecyclerView.setVisibility(View.VISIBLE);
            mErrorTextView.setVisibility(View.GONE);
        }
    }

    private void setErrorMessage(int errorID){
        mErrorTextView.setText(getString(errorID));
    }

    /**
     * This method is perform each time the user touch
     * a ContactRecyclerView row.
     *
     * @param id        contact identifier
     * @param name      contact display name
     * @param photoUrl  contact photo url
     */
    @Override
    public void onClick(String id, String name, String photoUrl) {

        if(mTwoPane){
            DetailFragment detailFragment = DetailFragment.newInstance(id);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.detail_container_layout, detailFragment)
                    .commit();
        }else {

            //Create an intent to load DetailActivity
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            intent.putExtra(DetailActivity.PARAM_USR, id);
            intent.putExtra(DetailActivity.PARAM_NAME, name);
            intent.putExtra(DetailActivity.PARAM_URL, photoUrl);
            startActivity(intent);
        }
    }
}
