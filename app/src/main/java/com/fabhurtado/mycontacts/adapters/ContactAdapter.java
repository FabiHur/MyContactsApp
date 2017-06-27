package com.fabhurtado.mycontacts.adapters;

import android.content.Context;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestBuilder;
import com.fabhurtado.mycontacts.R;
import com.fabhurtado.mycontacts.sync.model.Contact;
import com.fabhurtado.mycontacts.view.GlideApp;
import com.fabhurtado.mycontacts.view.svg.SvgSoftwareLayerSetter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * This adapter is used to load ContactRecyclerView data. It also
 * implements Filterable to let the user looks for a contact by writing
 * the name.
 *
 * @author FabHurtado
 */

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> implements Filterable {

    //Complete contact list
    private ArrayList<Contact> mContacts;

    //Filtered contact list
    private ArrayList<Contact> mFilteredContacts;

    private Context mContext;

    private ContactAdapterOnClickHandler mClickHandler;

    /**
     * This interface define the handler that will perform the
     * action when a row is selected.
     */
    public interface ContactAdapterOnClickHandler{
        void onClick(String id, String name, String photoUrl);
    }

    public ContactAdapter(Context context,
                          ContactAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;

        mFilteredContacts = new ArrayList<>();
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_view_contact,parent,false);

        return new ContactViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {

        //Get the contact at position
        Contact contact = mFilteredContacts.get(position);

        //Set the contact name
        String name = contact.getFirstName() + " " + contact.getLastName();
        holder.mNameTextView.setText(name);

        //Load the thumb image
        Uri thumbUri = Uri.parse(contact.getThumb());
        String lastPathSegment = thumbUri.getLastPathSegment();
        String thumbExt = lastPathSegment.substring(lastPathSegment.length()-3);

        if("svg".equals(thumbExt.toLowerCase())){
            RequestBuilder<PictureDrawable> requestBuilder =
                    GlideApp.with(mContext)
                            .as(PictureDrawable.class)
                            .transition(withCrossFade())
                            .listener(new SvgSoftwareLayerSetter());
            requestBuilder
                    .load(thumbUri)
                    .into(holder.mThumbImageView);
        }else {
            GlideApp.with(mContext)
                    .load(contact.getThumb())
                    .into(holder.mThumbImageView);
        }
    }

    @Override
    public int getItemCount() {
        return mFilteredContacts.size();
    }

    /**
     * Updates the contact list and notify data set changed
     * @param contacts the new contact list
     */
    public void updateContacts(ArrayList<Contact> contacts){
        mContacts = contacts;
        mFilteredContacts = contacts;
        notifyDataSetChanged();
    }

    /**
     * Define the filter that will perform the search
     * @return filter result
     */
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String searchString = constraint.toString();

                if(searchString.isEmpty()){
                    mFilteredContacts = mContacts;
                } else {
                    ArrayList<Contact> filteredList = new ArrayList<>();

                    for(Contact contact : mContacts){

                        if(contact.getFirstName().toLowerCase().contains(searchString.toLowerCase()) ||
                                contact.getLastName().toLowerCase().contains(searchString.toLowerCase())){

                            filteredList.add(contact);
                        }
                    }
                    mFilteredContacts = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredContacts;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mFilteredContacts = (ArrayList<Contact>)results.values;
                notifyDataSetChanged();
            }
        };
    }

    /**
     * Contact ViewHolder Class
     */
    class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.thumb_image_view)
        ImageView mThumbImageView;

        @BindView(R.id.name_text_view)
        TextView mNameTextView;

        ContactViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            //Set on click action
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Contact contact = mFilteredContacts.get(position);

            mClickHandler.onClick(contact.getUserId(),
                    contact.getFirstName() + " " + contact.getLastName(),
                    contact.getPhoto());
        }
    }
}
