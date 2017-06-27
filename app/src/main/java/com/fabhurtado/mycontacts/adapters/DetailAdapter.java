package com.fabhurtado.mycontacts.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fabhurtado.mycontacts.R;
import com.fabhurtado.mycontacts.view.model.ItemDetail;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This adapter is used to manage DetailRecyclerView data loading.
 *
 * @author FabHurtado
 */

public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.DetailViewHolder> {

    private ArrayList<ItemDetail> mItemDetails;

    private DetailAdapterOnClickHandler mClickHandler;

    /**
     * This interface define the handler that will perform the
     * action when a row is selected.
     */
    public interface DetailAdapterOnClickHandler {
        void onClick(ItemDetail.DetailDataType type, String query);
    }

    public DetailAdapter(DetailAdapterOnClickHandler clickHandler) {
        mItemDetails = new ArrayList<>();
        mClickHandler = clickHandler;
    }

    @Override
    public DetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_view_detail, parent, false);
        return new DetailViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final DetailViewHolder holder, int position) {

        ItemDetail item = mItemDetails.get(position);

        holder.iconImageView.setImageResource(item.getImageResource());
        holder.valueTextView.setText(item.getValue());
        holder.typeTextView.setText(item.getValueType());
    }

    @Override
    public int getItemCount() {
        return mItemDetails.size();
    }

    public void updateDetails(ArrayList<ItemDetail> itemDetails){
        mItemDetails = itemDetails;
        notifyDataSetChanged();
    }

    class DetailViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.icon_image_view)
        ImageView iconImageView;

        @BindView(R.id.value_text_view)
        TextView valueTextView;

        @BindView(R.id.type_text_view)
        TextView typeTextView;

        DetailViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            ItemDetail itemDetail = mItemDetails.get(position);

            mClickHandler.onClick(itemDetail.getDataType(), itemDetail.getValue());
        }
    }
}
