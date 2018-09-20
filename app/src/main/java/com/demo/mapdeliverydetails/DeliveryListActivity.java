package com.demo.mapdeliverydetails;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

public class DeliveryListActivity extends AppCompatActivity {

    /**
     * This is used for checking the back press time count.
     */
    private long back_pressed;
    /**
     * used for displaying the list of delivery address
     */
    private RecyclerView mDeliveryListRecyclerView;

    /**
     * Adapter associated with recycle view to display the components
     */
    private DeliveryListAdapter mAdapter;

    /**
     * Layout to display if no element is visible
     */
    private LinearLayout mNoListViewLayout;

    /**
     * int containing total number of records downloaded
     */
    private int totalDownloadedRecords = 0;

    /**
     * Boolean to check whether download is in progress
     */
    private boolean loading = true;

    /**
     * Boolean to check whether next record should fetch or not
     */
    private boolean isFetchNextRecord = true;


    /**
     * Int containing the position of first visible item
     */
    private int firstVisibleItem;

    /**
     * Int containing the total first visible item on screen
     */
    private int visibleItemCount;

    /**
     * total number of item are display
     */
    private int totalItemCount;

    private int previousTotal = 0;

    /**
     * Offset to get the next list of items from the server
     */
    private int offset = 1;

    private int visibleThreshold = 5;

    private LinearLayoutManager mLayoutManager;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_list);

        initialiseUIComponent();


        if (isNetworkConnected()) {

            showProgressDialog(getString(R.string.please_wait));

            fetchDeliveryList(offset, Constants.LIMIT);

        }

        handleUIClickListeners();

    }

    /**
     * fetching the Table list
     *
     * @param offset max value to get
     */
    private void fetchDeliveryList(final int offset, final int limit) {

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                Constants.BASE_URL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        if (progressDialog != null)
                            progressDialog.dismiss();

                        if (response.length() == 0) {
                            isFetchNextRecord = false;
                        }

                        renderUI(response, offset == 1);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if (progressDialog != null)
                            progressDialog.dismiss();

                        isFetchNextRecord = false;

                        Message message = new Message();

                        message.obj = error.getMessage();

                        if (error != null && error.networkResponse != null) {

                            switch (error.networkResponse.statusCode) {
                                case 500:
                                    AlertDialog dialog = new AlertDialog.Builder(DeliveryListActivity.this)
                                            .setTitle(getString(R.string.txt_unable_to_connect))
                                            .setMessage(getString(R.string.request_could_not_be_completed))
                                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                }

                                            })
                                            .create();

                                    dialog.show();
                                    break;
                            }
                        }

                    }
                }
        ) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, Integer> params = new HashMap<String, Integer>();
                params.put("offset", offset);
                params.put("limit", limit);
                return super.getParams();
            }

            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
                return super.parseNetworkResponse(response);
            }
        };


        jsonArrayRequest.setShouldCache(false);
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 2, 2.0f));
        requestQueue.add(jsonArrayRequest);

    }

    /**
     * This Method initialise all UI component or variables.
     */
    private void initialiseUIComponent() {

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        updateToolbarText(getString(R.string.delivery_list_title));

        mNoListViewLayout = findViewById(R.id.no_list_view_layout);


        mDeliveryListRecyclerView = findViewById(R.id.delivery_list_recyclerView);
        mDeliveryListRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        offset = 0;

        visibleItemCount = 0;

        totalItemCount = 0;

        totalDownloadedRecords = 0;

    }

    /**
     * render the fragment table view
     *
     * @param viewItemList json array list of tables rows
     * @param clearList    check if update list or create new
     */
    private void renderUI(JSONArray viewItemList, boolean clearList) {

        if (viewItemList.length() == 0 && (offset > 0)) {
            return;
        }

        if (viewItemList.length() == 0) {

            mNoListViewLayout.setVisibility(View.VISIBLE);
            mDeliveryListRecyclerView.setVisibility(View.GONE);
        } else {

            mNoListViewLayout.setVisibility(View.GONE);
            mDeliveryListRecyclerView.setVisibility(View.VISIBLE);

            if (mAdapter == null) {

                mAdapter = new DeliveryListAdapter(this, viewItemList);
                mDeliveryListRecyclerView.setLayoutManager(mLayoutManager);
                mDeliveryListRecyclerView.setAdapter(mAdapter);

            } else {

                mAdapter.setDeliveryViewData(viewItemList, false);
                mAdapter.notifyDataSetChanged();
                mDeliveryListRecyclerView.invalidate();

            }

        }

    }

    /**
     * This method will update the tool bar title
     *
     * @param title name which is display as title to tool bar
     */
    public void updateToolbarText(String title) {

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {

            actionBar.setTitle(title);

        }

    }

    /**
     * this method set back arrow button enable or disable.
     *
     * @param showBackButtonFlag boolean value for changing back button according to requirement.
     */
    public void changeToolbar(boolean showBackButtonFlag) {

        try {
            if (getSupportActionBar() != null) {

                getSupportActionBar().setDisplayHomeAsUpEnabled(showBackButtonFlag);
                getSupportActionBar().setDisplayShowHomeEnabled(showBackButtonFlag);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager().getBackStackEntryCount() < 1) {

            if (back_pressed + 2000 > System.currentTimeMillis()) {

                finish();

            } else {

                Toast.makeText(getBaseContext(), getString(R.string.app_exit_double_click_message), Toast.LENGTH_SHORT).show();
                back_pressed = System.currentTimeMillis();

            }
        } else {

            super.onBackPressed();

            changeToolbar(false);

            updateToolbarText(getResources().getString(R.string.delivery_list_title));
        }
    }

    /**
     * This method check if network is connected or not
     */
    private boolean isNetworkConnected() {

        ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = null;

        if (mConnectivityManager != null) {

            mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        }

        if (mNetworkInfo == null || !mNetworkInfo.isConnectedOrConnecting()) {

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.connection_error))
                    .setMessage(getString(R.string.check_internet))
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            finish();

                        }

                    })
                    .create();

            dialog.show();

            return false;

        } else

            return true;
    }

    /**
     * handle all UI click listeners
     */
    private void handleUIClickListeners() {

        mDeliveryListRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //check for scroll down
                if (dy > 0) {

                    visibleItemCount = recyclerView.getChildCount();

                    totalItemCount = mLayoutManager.getItemCount();

                    firstVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                    if (loading) {

                        if (totalItemCount > previousTotal) {
                            loading = false;
                            previousTotal = totalItemCount;

                        }

                    }

                    if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {

                        if (isFetchNextRecord) {

                            if (isNetworkConnected()) {

                                showProgressDialog(getString(R.string.please_wait));

                                fetchDeliveryList(++offset, Constants.LIMIT);

                                loading = true;

                            }

                        }

                    }

                }

            }

        });

    }

    /**
     * Showing Progress Dialog while getting data
     */
    private void showProgressDialog(String message) {

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();

    }

    /**
     * This method is used for displaying the details of delivery with map.
     *
     * @param imageUrl    image url
     * @param description description of delivery
     * @param address     address of delivery
     * @param lat         latitude
     * @param lng         longitude
     */
    public void redirectToDeliveryDetailsFragment(String imageUrl, String description, String address, String lat, String lng) {

        Fragment fragment = DeliveryDetailsFragment.newInstance(imageUrl, description, address, lat, lng);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
