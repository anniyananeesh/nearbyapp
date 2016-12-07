package app.aroundme.com.nearbyapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;

import models.Config;
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
public class NotificationsFragment extends ListFragment {

    String seoStr;
    List<Offers> offers;
    ListView lv;
    Config config;
    UserOfferAdapter adapter;
    ProgressBar pBar;
    GPSTracker gps;

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

        pBar = (ProgressBar) getView().findViewById(R.id.pb_progess1);
        pBar.setVisibility(View.VISIBLE);
        gps = new GPSTracker(getActivity().getApplicationContext());
        lv = (ListView) getView().findViewById(android.R.id.list);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<OfferResponse> call = apiService.getOffersByLocation(seoStr, String.valueOf(gps.getLatitude()), String.valueOf(gps.getLongitude()), MainActivity.MAX_DISTANCE);

        call.enqueue(new Callback<OfferResponse>() {

            @Override
            public void onResponse(Call<OfferResponse> call, Response<OfferResponse> response) {

                try {

                    offers = response.body().getData();

                    adapter = new UserOfferAdapter(getActivity(), offers);
                    lv.setAdapter(adapter);

                    if(offers.size() == 0){
                        Toast.makeText(getActivity().getApplicationContext(), "No offers found" , Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.d("Exception for adapter", e.getMessage());
                }

                pBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(Call<OfferResponse> call, Throwable t) {

            }
        });

        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        Fragment fragment = new OffersFragment(offers.get(position).getSubscriberId(), offers.get(position).getPlaceId());
        FragmentTransaction fragmentTransaction = getParentFragment().getFragmentManager().beginTransaction();

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.commit();
    }

}
