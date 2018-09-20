package com.demo.mapdeliverydetails;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class DeliveryDetailsFragment extends Fragment implements OnMapReadyCallback {

    private String imageUrl;
    private String address;
    private String description;
    private String lat;
    private String lng;

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

    /**
     * Map view for displaying the delivery address into the map
     */
    private SupportMapFragment mapFragment;

    public DeliveryDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param imageUrl    image url
     * @param description what  type of delivery
     * @param address     address of  delivery
     * @param lat         latitude
     * @param lng         longitude
     * @return return new intsnace of Fragment;
     */
    public static DeliveryDetailsFragment newInstance(String imageUrl, String description, String address, String lat, String lng) {
        DeliveryDetailsFragment fragment = new DeliveryDetailsFragment();
        Bundle args = new Bundle();
        args.putString("ImageUrl", imageUrl);
        args.putString("Description", description);
        args.putString("Address", address);
        args.putString("Latitude", lat);
        args.putString("Longitude", lng);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View currentView = inflater.inflate(R.layout.fragment_delivery_details, container, false);

        initialiseUiComponent(currentView);

        setArgumentValue();

        assignDeliveryDetailsToUIComponent();

        return currentView;
    }

    /**
     * set arguments value to UI
     */
    private void assignDeliveryDetailsToUIComponent() {

        Picasso.with(getContext()).load(imageUrl).into(deliveryImage);
        deliveryNameTextView.setText(description);
        deliveryAddressTextView.setText(address);

    }

    /**
     * This method assign argument value to variable.
     */
    private void setArgumentValue() {

        if (getArguments() != null) {
            imageUrl = getArguments().getString("ImageUrl");
            description = getArguments().getString("Description");
            address = getArguments().getString("Address");
            lat = getArguments().getString("Latitude");
            lng = getArguments().getString("Longitude");

        }
    }

    /**
     * This Method initialise all UI component.
     *
     * @param currentView current view of fragment
     */
    private void initialiseUiComponent(View currentView) {

        deliveryImage = currentView.findViewById(R.id.delivery_image);
        deliveryNameTextView = currentView.findViewById(R.id.delivery_name_text_view);
        deliveryAddressTextView = currentView.findViewById(R.id.delivery_address_text_view);
        mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        Activity activity = getActivity();

        if (activity instanceof DeliveryListActivity) {

            ((DeliveryListActivity) activity).changeToolbar(true);
            ((DeliveryListActivity) activity).updateToolbarText(activity.getResources().getString(R.string.delivery_details));

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)))
                .title(address)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)), 10));

    }
}
