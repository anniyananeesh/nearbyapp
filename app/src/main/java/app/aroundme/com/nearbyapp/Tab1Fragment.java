package app.aroundme.com.nearbyapp;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import models.PlaceDetails;
import models.PlaceDetailsResponse;
import models.Places;
import models.PlacesResponse;
import rest.GMapApiInterface;
import rest.GmapApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import service.GPSTracker;

/**
 * Created by Macbook on 16/09/16.
 */

public class Tab1Fragment extends ListFragment {

    ListView lv;

    UserListAdapter adapter;

    String seo;
    List<Places> placesList;
    GPSTracker gps;
    ArrayList<String> distances = new ArrayList<String>();
    ProgressBar pBar;

    public Tab1Fragment() {

    }

    public Tab1Fragment(String seo) {
        this.seo = seo;
    }

    public static Tab1Fragment newInstance() {
        
        Bundle args = new Bundle();
        
        Tab1Fragment fragment = new Tab1Fragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_tab1, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // creating GPS Class object
        gps = new GPSTracker(getActivity().getApplicationContext());
        pBar = (ProgressBar) view.findViewById(R.id.pb_progess1);

        pBar.setVisibility(View.VISIBLE);

        // check if GPS location can get
        if (!gps.canGetLocation()) {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }

        lv = (ListView) getView().findViewById(android.R.id.list);

        GMapApiInterface apiService = GmapApiClient.getClient().create(GMapApiInterface.class);

        Call<PlacesResponse> call = apiService.getNearbyUsers(MainActivity.API_KEY, "distance", gps.getLatitude() + "," + gps.getLongitude(), seo);

        call.enqueue(new Callback<PlacesResponse>() {

            @Override
            public void onResponse(Call<PlacesResponse> call, Response<PlacesResponse> response) {

                try {

                    placesList = response.body().getResults();

                    for (int i=0; i<placesList.size(); i++) {
                        distances.add(i, gps.calcDistanceBnTwoGeoPoints(gps.getLatitude(), gps.getLongitude(), placesList.get(i).geometry.location.getLat(), placesList.get(i).geometry.location.getLng()));
                    }

                    adapter = new UserListAdapter(getActivity(), placesList, distances, gps);
                    lv.setAdapter(adapter);

                    if(placesList.size() == 0){
                        Toast.makeText(getActivity().getApplicationContext(), "No items found", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {

                    Log.d("Exception for adapter", e.getMessage());
                }

                pBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(Call<PlacesResponse> call, Throwable t) {
                Log.d("Exception failure", t.getMessage());
            }

        });


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        Fragment fragment = new ListDetailsFragment(placesList.get(position).getPlace_id());
        // replace your custom fragment class
        FragmentTransaction fragmentTransaction = getParentFragment().getFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.commit();
    }
}

class UserListAdapter extends ArrayAdapter<Places>
{
    Context context;

    List<Places> placesList;

    ArrayList<String> distances;

    GPSTracker gps;

    PlaceDetails placedetails;

    public UserListAdapter(Context context, List<Places> objects, ArrayList<String> distances, GPSTracker gps) {
        super(context, R.layout.list_category, objects);
        this.context = context;
        this.placesList = objects;
        this.distances = distances;
        this.gps = gps;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.list_tab1_row, parent, false);

        TextView tvTitle = (TextView) row.findViewById(R.id.tv_list_title);
        TextView tvDistance = (TextView) row.findViewById(R.id.tv_list_distance);
        TextView tvAddress1 = (TextView) row.findViewById(R.id.tv_list_address1);
        TextView tvAddress2 = (TextView) row.findViewById(R.id.tv_list_address2);

        final Places place = placesList.get(position);

        tvTitle.setText(place.getName());
        tvDistance.setText(distances.get(position));
        tvAddress1.setText(place.getVicinity());

        if(Geocoder.isPresent())
        {
            Geocoder ge = new Geocoder(context);
            try {
                List<Address> address = ge.getFromLocation(place.geometry.location.getLat(), place.geometry.location.getLng(), 1);

                if(address.size() > 0){
                    tvAddress2.setText(address.get(0).getLocality());
                }else{
                    tvAddress2.setVisibility(View.GONE);
                }

            } catch (IOException e) {
                e.printStackTrace();
                tvAddress2.setVisibility(View.GONE);
            }
        }else{
            tvAddress2.setVisibility(View.GONE);
        }

        Button btnCall = (Button) row.findViewById(R.id.btn_call);
        Button btnNavigate = (Button) row.findViewById(R.id.btn_navigate);

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                GMapApiInterface apiService = GmapApiClient.getClient().create(GMapApiInterface.class);
                Call<PlaceDetailsResponse> call = apiService.getPlaceDetials(MainActivity.API_KEY, place.getPlace_id());

                call.enqueue(new Callback<PlaceDetailsResponse>() {

                    @Override
                    public void onResponse(Call<PlaceDetailsResponse> call, Response<PlaceDetailsResponse> response) {
                        placedetails = response.body().getResult();
                        if(placedetails.getFormatted_phone_number() == null){

                            Toast.makeText(getContext(), "Cannot make call to this user", Toast.LENGTH_LONG).show();
                        }else {
                            //Code to make phone call
                            Intent callIntent = new Intent(Intent.ACTION_DIAL);
                            callIntent.setData(Uri.parse("tel:"+Uri.encode(placedetails.getFormatted_phone_number())));
                            callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            getContext().startActivity(callIntent);
                        }
                    }

                    @Override
                    public void onFailure(Call<PlaceDetailsResponse> call, Throwable t) {

                    }

                });

            }
        });

        btnNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Please wait, we make navigation ...", Toast.LENGTH_LONG).show();

                //Code to make phone call
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr="+ gps.getLatitude()+"," + gps.getLongitude()+ "&daddr="+place.geometry.location.getLat()+","+ place.geometry.location.getLng()));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                try
                {
                    getContext().startActivity(intent);
                }
                catch(ActivityNotFoundException ex)
                {
                    try
                    {
                        Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr="+ gps.getLatitude()+"," + gps.getLongitude()+ "&daddr="+place.geometry.location.getLat()+","+ place.geometry.location.getLng()));
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