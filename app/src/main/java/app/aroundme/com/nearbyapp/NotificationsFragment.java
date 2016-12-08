package app.aroundme.com.nearbyapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import models.OfferResponse;
import models.Offers;
import rest.ApiClient;
import rest.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import service.GPSTracker;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationsFragment extends ListFragment implements SwipeRefreshLayout.OnRefreshListener {

    String seoStr;
    List<Offers> offers;
    List<Offers> offersList = new ArrayList<Offers>();

    ListView lv;
    UserOfferAdapter adapter;
    ProgressBar pBar;
    GPSTracker gps;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String lastOffer = "";

    public NotificationsFragment() {
        // Required empty public constructor
    }

    public static NotificationsFragment newInstance(String seo) {
        
        Bundle args = new Bundle();
        args.putString("seo_key", seo);

        NotificationsFragment fragment = new NotificationsFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        seoStr = getArguments().getString("seo_key");
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pBar = (ProgressBar) view.findViewById(R.id.pb_progess1);
        pBar.setVisibility(View.VISIBLE);
        gps = new GPSTracker(getActivity().getApplicationContext());
        lv = (ListView) view.findViewById(android.R.id.list);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                fetchOffersBySeo(seoStr, false);
            }
        });

    }

    private void fetchOffersBySeo(String seoStr, boolean isArchived) {

        // showing refresh animation before making http call
        swipeRefreshLayout.setRefreshing(true);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        //If show archived data last offer will be null
        lastOffer = (isArchived) ? "" : lastOffer;

        Call<OfferResponse> call = apiService.getOffersByLocation(seoStr, String.valueOf(gps.getLatitude()), String.valueOf(gps.getLongitude()), MainActivity.MAX_DISTANCE, lastOffer);

        call.enqueue(new Callback<OfferResponse>() {

            @Override
            public void onResponse(Call<OfferResponse> call, Response<OfferResponse> response) {

                offers = response.body().getData();

                if(offers.size() == 0){
                    Toast.makeText(getActivity().getApplicationContext(), "No offers found" , Toast.LENGTH_SHORT).show();
                } else {

                    for(Offers offer: offers) {
                        offersList.add(offer);
                    }

                    adapter = new UserOfferAdapter(getActivity(), offersList);
                    lv.setAdapter(adapter);

                    adapter.notifyDataSetChanged();
                    lastOffer = offers.get(0).getOfferId();
                }

                try {



                } catch (Exception e) {
                    Log.d("Exception for adapter", e.getMessage());
                }

                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);
                pBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(Call<OfferResponse> call, Throwable t) {

            }
        });

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        Fragment fragment = new OffersFragment(offers.get(position).getSubscriberId(), offers.get(position).getPlaceId());
        FragmentTransaction fragmentTransaction = getParentFragment().getFragmentManager().beginTransaction();

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onRefresh() {
        fetchOffersBySeo(seoStr, false);
    }

}
