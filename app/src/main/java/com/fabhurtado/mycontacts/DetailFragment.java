package com.fabhurtado.mycontacts;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fabhurtado.mycontacts.adapters.DetailAdapter;
import com.fabhurtado.mycontacts.sync.RetrofitClient;
import com.fabhurtado.mycontacts.sync.model.Contact;
import com.fabhurtado.mycontacts.sync.service.ContactService;
import com.fabhurtado.mycontacts.utils.MyContactsUtils;
import com.fabhurtado.mycontacts.utils.NetworkDetector;
import com.fabhurtado.mycontacts.view.model.ItemDetail;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * DetailFragment display the Contact detail information and
 * allow the user to interact with other apps.
 *
 * @author FabHurtado
 */
public class DetailFragment extends Fragment implements DetailAdapter.DetailAdapterOnClickHandler {

    private static final String ARG_USER_ID = "user_id";

    private String mUserIdParam;

    private Contact mContact;

    private DetailAdapter mDetailAdapter;

    @BindView(R.id.detail_recycler_view)
    RecyclerView mDetailRecyclerView;

    @BindView(R.id.error_text_view)
    TextView mErrorTextView;



    public DetailFragment() {
        // Required empty public constructor
    }

    /**
     * Factory method to create a new instance of
     * DetailFragment using the provided parameters.
     *
     * @param userId selected user identifier
     * @return A new instance of fragment DetailFragment.
     */
    public static DetailFragment newInstance(String userId) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Get user identifier from arguments
        if (getArguments() != null) {
            mUserIdParam = getArguments().getString(ARG_USER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_detail, container, false);

        //Bind views
        ButterKnife.bind(this, view);

        setupView();

        //Load contact details
        loadDetails();

        return view;
    }

    /**
     * Loads contact details in the background
     */
    private void loadDetails(){

        //Checks internet connection
        if(NetworkDetector.isConnectedToInternet(getActivity())) {
            //Let's hide error message
            displayError(false);

            //Get Retrofit service
            ContactService contactService = RetrofitClient.getContactService();

            //Looks for contact details
            Call<Contact> call = contactService.getContactBy(mUserIdParam);
            call.enqueue(new Callback<Contact>() {
                @Override
                public void onResponse(@NonNull Call<Contact> call, @NonNull Response<Contact> response) {
                    //Get contact details
                    mContact = response.body();
                    mDetailAdapter.updateDetails(
                            MyContactsUtils.prepareDetailData(mContact, getActivity()));
                }

                @Override
                public void onFailure(@NonNull Call<Contact> call, @NonNull Throwable t) {
                    Log.e("DETAIL", t.toString());
                    if(!NetworkDetector.isConnectedToInternet(getActivity())){
                        setErrorMessage(R.string.error_connection_detail);
                    }else{
                        setErrorMessage(R.string.error_unexpected_detail);
                    }

                    displayError(true);
                }
            });
        } else {
            //As is not connected to internet. Let's show the connection error message
            setErrorMessage(R.string.error_connection_detail);
            displayError(true);
        }


    }

    /**
     * Show/Hide Error message and Contact List
     * @param show true to enable error message
     */
    private void displayError(boolean show){
        if(show) {
            mDetailRecyclerView.setVisibility(View.GONE);
            mErrorTextView.setVisibility(View.VISIBLE);
        }else {
            mDetailRecyclerView.setVisibility(View.VISIBLE);
            mErrorTextView.setVisibility(View.GONE);
        }
    }

    private void setErrorMessage(int errorID){
        mErrorTextView.setText(getString(errorID));
    }

    /**
     * Setting up the view
     */
    private void setupView(){

        mDetailAdapter = new DetailAdapter(this);
        mDetailRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mDetailRecyclerView.setAdapter(mDetailAdapter);
        mDetailRecyclerView.setHasFixedSize(true);

        Activity activity = getActivity();

        if(activity instanceof DetailActivity){
            //When share FAB is click a perform this action
            ((DetailActivity)activity).mShareFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareContact();
                }
            });

        }

    }

    /**
     * This method is called when the user touch a row in DetailRecyclerView.
     *
     * @param type  Data type (Phone, Address, Birth date)
     * @param query the data (phone number, address, birth date)
     */
    @Override
    public void onClick(ItemDetail.DetailDataType type, String query) {
        String q;
        Uri uri;
        Intent intent;

        switch (type){
            case PHONE:
                q= "tel:" + query.replaceAll("-","");
                uri = Uri.parse(q);
                intent = new Intent(Intent.ACTION_DIAL, uri);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                }
                break;
            case ADDRESS:
                q =  "geo:0,0?q=" + query;
                uri = Uri.parse(q);
                intent = new Intent(Intent.ACTION_VIEW, uri);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                }
                break;
            case BIRTH_DATE:
                intent = new Intent(Intent.ACTION_INSERT)
                        .setData(CalendarContract.Events.CONTENT_URI)
                        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                                MyContactsUtils.calculateBirthdayInMillis(query));
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                }
                break;
        }
    }

    /**
     * Format contact data to share it with other apps
     */
    private void shareContact(){

        File vcfFile = MyContactsUtils.prepareVcf(mContact, getActivity());
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/x-vcard");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(vcfFile));
        startActivity(intent);
    }
}
