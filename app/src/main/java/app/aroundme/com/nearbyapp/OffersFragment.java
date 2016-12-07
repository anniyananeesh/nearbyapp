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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import models.OfferResponse;
import models.Offers;
import models.PlaceDetails;
import models.User;
import models.UserResponse;
import rest.ApiClient;
import rest.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import service.GPSTracker;

/**
 * Created by Macbook on 16/09/16.
 */
public class OffersFragment extends Fragment {

    ListView lv;
    String userId;
    List<Offers> offers;
    OffersAdapter adapter;
    String placeId;

    TextView title;
    TextView distance;
    TextView address1;
    TextView address2;
    PlaceDetails placedetails;
    User user;

    GPSTracker gps;
    String distanceTxt;

    ProgressBar mProgressBar;
    RelativeLayout mShowLoader;
    LinearLayout mLvShowContent;

    RatingBar userRating;

    public OffersFragment(String userId, String placeId){
        this.userId = userId;
        this.placeId = placeId;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ImageView iv = (ImageView) getActivity().findViewById(R.id.actionbar_logo);
        TextView tv = (TextView) getActivity().findViewById(R.id.actionbar_title);

        iv.setVisibility(View.GONE);
        tv.setVisibility(View.VISIBLE);
        tv.setText("Offers");

        gps = new GPSTracker(getActivity().getApplicationContext());

        return inflater.inflate(R.layout.fragment_offers, null, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lv = (ListView) view.findViewById(android.R.id.list);
        title = (TextView) view.findViewById(R.id.tv_list_title);
        address1 = (TextView) view.findViewById(R.id.tv_list_address1);
        address2 = (TextView) view.findViewById(R.id.tv_list_address2);
        distance = (TextView) view.findViewById(R.id.tv_list_distance);

        mProgressBar = (ProgressBar) view.findViewById(R.id.pb_progess1);
        mShowLoader = (RelativeLayout) view.findViewById(R.id.rl_show_loader);
        mLvShowContent = (LinearLayout) view.findViewById(R.id.lv_show_content);

        userRating = (RatingBar) view.findViewById(R.id.rating_bar1);

        lv.setEmptyView(view.findViewById(android.R.id.empty));

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<OfferResponse> call = apiService.getOffersByUser(this.userId);

        Call<UserResponse> uCall = apiService.getUser(this.placeId);

        uCall.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                user = response.body().getResult();

                distanceTxt = gps.calcDistanceBnTwoGeoPoints(gps.getLatitude(), gps.getLongitude(), user.getCordinates()[1], user.getCordinates()[0]);

                //Put the callback data on to view
                title.setText(user.getName());
                address1.setText(user.getAddress());
                distance.setText(distanceTxt);
                userRating.setRating(user.getRating());

                if(Geocoder.isPresent())
                {
                    Geocoder ge = new Geocoder(getActivity().getApplicationContext());
                    try {
                        List<Address> address = ge.getFromLocation(user.getCordinates()[1], user.getCordinates()[0], 1);
                        address2.setText(address.get(0).getSubLocality());

                        if(address.get(0).getSubLocality() == null) {
                            address2.setVisibility(View.GONE);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        address2.setVisibility(View.GONE);

                    }
                }else{
                    address2.setVisibility(View.GONE);

                }

                mProgressBar.setVisibility(View.GONE);
                mShowLoader.setVisibility(View.GONE);
                mLvShowContent.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {

                mProgressBar.setVisibility(View.GONE);
                mShowLoader.setVisibility(View.VISIBLE);
                mLvShowContent.setVisibility(View.GONE);
                Toast.makeText(getActivity().getApplicationContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });

        call.enqueue(new Callback<OfferResponse>() {

            @Override
            public void onResponse(Call<OfferResponse> call, Response<OfferResponse> response) {
                Log.d("Callback offers", call.request().url().toString());
                try{

                    offers = response.body().getData();

                    adapter = new OffersAdapter(getActivity().getApplicationContext(), offers);
                    lv.setAdapter(adapter);

                } catch (Exception e) {
                    Log.v("Exception", e.getMessage());
                }

                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {

                        Fragment fragment = new OfferDetailsFragment(user, offers.get(position).getOfferId());
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

                        fragmentTransaction.addToBackStack("ProfileScreen");
                        fragmentTransaction.replace(R.id.content_frame, fragment);
                        fragmentTransaction.commit();

                    }

                    @SuppressWarnings("unused")
                    public void onClick(View v) {
                    }

                });

            }

            @Override
            public void onFailure(Call<OfferResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Button btnCall = (Button) getView().findViewById(R.id.btn_call);
        Button btnReviewsUser = (Button) getView().findViewById(R.id.read_reviews1);
        Button btnNavigate = (Button) getView().findViewById(R.id.btn_navigate);
        Button btnUrl = (Button) getView().findViewById(R.id.btn_link);

        btnUrl.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String url = placedetails.getWebsite();
                Log.d("URL", url);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);

            }

        });

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
        });

        btnReviewsUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment fragment = new ReviewsListFragment(userId, placeId);
                Bundle bundle = new Bundle();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

                fragmentTransaction.addToBackStack("ProfileScreen");
                fragmentTransaction.replace(R.id.content_frame, fragment);
                fragmentTransaction.commit();
            }
        });

        btnNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Please wait, we make navigation ...", Toast.LENGTH_LONG).show();

                //Code to make phone call
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr="+ gps.getLatitude()+"," + gps.getLongitude()+ "&daddr="+placedetails.geometry.location.getLat()+","+ placedetails.geometry.location.getLng()));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                try
                {
                    getContext().startActivity(intent);
                }
                catch(ActivityNotFoundException ex)
                {
                    try
                    {
                        Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr="+ gps.getLatitude()+"," + gps.getLongitude()+ "&daddr="+placedetails.geometry.location.getLat()+","+ placedetails.geometry.location.getLng()));
                        getContext().startActivity(unrestrictedIntent);
                    }
                    catch(ActivityNotFoundException innerEx)
                    {
                        Toast.makeText(getContext(), "Please install a maps application", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

    }

}

class OffersAdapter extends ArrayAdapter<Offers> {
    Context context;
    List<Offers> offers;
    SimpleDateFormat formatDate = new SimpleDateFormat("y M d");

    public OffersAdapter(Context context, List<Offers> offers) {
        super(context, R.layout.fragment_offer_list_row, offers);
        this.context = context;
        this.offers = offers;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.fragment_offer_list_row, parent, false);

        TextView tvTitle = (TextView) row.findViewById(R.id.tv_list_title);
        TextView tvValidity = (TextView) row.findViewById(R.id.tv_list_validity);

        final Offers offer = offers.get(position);

        tvTitle.setText(offer.getOfferTitle());
        tvValidity.setText("Available till " + formatDate.format(offer.getAvailDate()).toString());

        return row;
    }

}