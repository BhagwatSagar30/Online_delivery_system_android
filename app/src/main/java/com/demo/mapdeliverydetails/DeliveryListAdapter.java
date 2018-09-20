package com.demo.mapdeliverydetails;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Sagar Bhagwat on 9/19/18.
 */

class DeliveryListAdapter extends RecyclerView.Adapter {

    /**
     * JSON Array containing the data to be displayed on the UI
     */
    private JSONArray mViewItemList;

    /**
     * Fragment object to get the view element and navigate to other fragment for edit
     */
    private DeliveryListActivity mDeliveryActivity;

    public DeliveryListAdapter(DeliveryListActivity deliveryListActivity, JSONArray viewItemList) {
        mDeliveryActivity = deliveryListActivity;
        mViewItemList = viewItemList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mDeliveryActivity).inflate(R.layout.delivery_list_details_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        try {

            ViewHolder viewHolder = (ViewHolder) holder;

            final JSONObject viewDataJSON = mViewItemList.getJSONObject(position);

            final String imageUrl = viewDataJSON.getString("imageUrl");
            final String description = viewDataJSON.getString("description");
            final String address = new JSONObject(viewDataJSON.getString("location")).getString("address");
            final String lat = new JSONObject(viewDataJSON.getString("location")).getString("lat");
            final String lng = new JSONObject(viewDataJSON.getString("location")).getString("lng");

            Picasso.with(mDeliveryActivity).load(imageUrl).into(viewHolder.deliveryImage);

            viewHolder.deliveryNameTextView.setText(description);
            viewHolder.deliveryAddressTextView.setText(address);

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mDeliveryActivity.redirectToDeliveryDetailsFragment(imageUrl, description, address, lat, lng);

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {

        return mViewItemList.length();

    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public int getItemViewType(int position) {

        return position;

    }

    /**
     * Assign newly updated array list
     *
     * @param tableViewData list
     */
    public void setDeliveryViewData(JSONArray tableViewData, boolean clear) {
        try {

            if (!clear) {

                for (int index = 0; index < tableViewData.length(); index++) {

                    this.mViewItemList.put(tableViewData.get(index));

                }

            } else {

                this.mViewItemList = tableViewData;

            }

        } catch (JSONException e) {

            e.printStackTrace();

        }

    }

    /**
     * holder class
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        /**
         * Image view for displaying the image
         */
        private CircleImageView deliveryImage;

        /**
         * text view for displaying the delivery type
         */
        private TextView deliveryNameTextView;

        /**
         * text view for displaying the delivery address
         */
        private TextView deliveryAddressTextView;

        public ViewHolder(View view) {
            super(view);

            deliveryImage = view.findViewById(R.id.delivery_image);

            deliveryNameTextView = view.findViewById(R.id.delivery_name_text_view);

            deliveryAddressTextView = view.findViewById(R.id.delivery_address_text_view);

        }
    }

}
