package app.aroundme.com.nearbyapp;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import models.OfferResponse;
import models.Offers;
import rest.ApiClient;
import rest.ApiInterface;
import service.GPSTracker;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Macbook on 16/09/16.
 */
public class Tab2Fragment extends ListFragment {

    List<Offers> offers;
    ListView lv;
    UserOfferAdapter adapter;
    String seo;
    ProgressBar pBar;
    GPSTracker gps;

    public Tab2Fragment(String seo) {
        this.seo = seo;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_tab2, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        pBar = (ProgressBar) getView().findViewById(R.id.pb_progess1);
        pBar.setVisibility(View.VISIBLE);

        gps = new GPSTracker(getActivity().getApplicationContext());

        lv = (ListView) getView().findViewById(android.R.id.list);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<OfferResponse> call = apiService.getOffersByLocation(this.seo, String.valueOf(gps.getLatitude()), String.valueOf(gps.getLongitude()), "10", "");

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

class UserOfferAdapter extends ArrayAdapter<Offers>
{
    Context context;
    List<Offers> offers;
    GPSTracker gps;

    public UserOfferAdapter(Context context, List<Offers> offers) {
        super(context, R.layout.list_category, offers);
        this.context = context;
        this.offers = offers;
        gps = new GPSTracker(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.list_tab2_row, parent, false);

        TextView tvTitle = (TextView) row.findViewById(R.id.tv_list_title);
        TextView tvAddress1 = (TextView) row.findViewById(R.id.tv_list_address1);

        final Offers offer = offers.get(position);

        tvTitle.setText(offer.getOfferTitle());
        tvAddress1.setText(offer.getAddress());

        ImageButton btnCall = (ImageButton) row.findViewById(R.id.btn_offer_call);
        ImageButton btnNavigate = (ImageButton) row.findViewById(R.id.btn_offer_nav);

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            //Code to make phone call
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:"+Uri.encode(offer.getPhone())));
            callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(callIntent);

            }
        });

        btnNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Please wait, we make navigation ...", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr="+ gps.getLatitude()+"," + gps.getLongitude()+ "&daddr="+offer.getLatitude()+","+ offer.getLongitude()));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                try
                {
                    getContext().startActivity(intent);
                }
                catch(ActivityNotFoundException ex)
                {
                    try
                    {
                        Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr="+ gps.getLatitude()+"," + gps.getLongitude()+ "&daddr="+offer.getLatitude()+","+ offer.getLongitude()));
                        getContext().startActivity(unrestrictedIntent);
                    }
                    catch(ActivityNotFoundException innerEx)
                    {
                        Toast.makeText(getContext(), "Please install a maps application", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        return row;
    }

}