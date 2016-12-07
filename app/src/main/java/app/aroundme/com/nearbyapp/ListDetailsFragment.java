package app.aroundme.com.nearbyapp;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.view.menu.ActionMenuItem;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.IOException;
import java.util.List;

import cache.DatabaseHandler;
import models.Favourite;
import models.PlaceDetails;
import models.PlaceDetailsResponse;
import models.User;
import models.UserResponse;
import rest.ApiClient;
import rest.ApiInterface;
import rest.GMapApiInterface;
import rest.GmapApiClient;
import service.GPSTracker;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListDetailsFragment extends Fragment implements OnMapReadyCallback {

    SupportMapFragment fragment = SupportMapFragment.newInstance();
    GoogleMap mGoogleMap;

    TextView title;
    TextView distance;
    TextView address1;
    TextView address2;
    PlaceDetails placedetails;
    User user;

    GPSTracker gps;
    String distanceTxt;
    String placeId;
    Boolean isFeatured;
    ImageView ivUser;
    Button btnUrl;
    RatingBar mRateBar;

    LinearLayout lvOffers;

    private ProgressBar spinner;
    LinearLayout lvDetails;
    RelativeLayout lvFeaturedRow;

    ImageLoader imageLoader = ImageLoader.getInstance();

    DatabaseHandler db;
    Favourite favourite;
    Menu mActionMenu;
    View rootView;

    public ListDetailsFragment(String placeId) {

        // Required empty public constructor
        this.placeId = placeId;
        this.isFeatured = false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_profile, menu);

        mActionMenu = menu;

        if (menu != null) {
            menu.findItem(R.id.action_settings).setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {

            case R.id.action_favourite:

                favourite = db.getFavourite(placedetails.getPlace_id());

                if(favourite.get_name() != null) {

                    db.deleteFavourite(placedetails.getPlace_id());
                    item.setIcon(getResources().getDrawable(R.drawable.ic_star_normal));
                    Toast.makeText(getActivity().getApplicationContext(), placedetails.getName() + " removed from favourites .", Toast.LENGTH_SHORT).show();
                } else {

                    Toast.makeText(getActivity().getApplicationContext(), placedetails.getName() + " added to favourites .", Toast.LENGTH_SHORT).show();
                    db.addFavourite(new Favourite(placedetails.getName(), placedetails.getFormatted_phone_number(), String.valueOf(placedetails.geometry.location.getLat()), String.valueOf(placedetails.geometry.location.getLng()), placedetails.getFormatted_address(), placedetails.getPlace_id()));
                    item.setIcon(getResources().getDrawable(R.drawable.ic_star_filled));
                }


                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void updateActionButtonFav() {

        favourite = db.getFavourite(placedetails.getPlace_id());

        if(favourite.get_name() == null) {
            mActionMenu.findItem(R.id.action_favourite).setIcon(getResources().getDrawable(R.drawable.ic_star_normal));
        } else {
            mActionMenu.findItem(R.id.action_favourite).setIcon(getResources().getDrawable(R.drawable.ic_star_filled));
        }

        return;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle bundle) {

        // creating GPS Class object
        gps = new GPSTracker(getActivity().getApplicationContext());
        db = new DatabaseHandler(getActivity().getApplicationContext());

        try {

            if(rootView == null) {
                rootView = inflater.inflate(R.layout.fragment_list_details, container, false);
            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spinner = (ProgressBar) view.findViewById(R.id.pb_progess1);
        spinner.setVisibility(View.VISIBLE);

        lvOffers = (LinearLayout) view.findViewById(R.id.offer_view_lv);
        title = (TextView) getView().findViewById(R.id.tv_list_title);
        address1 = (TextView) getView().findViewById(R.id.tv_list_address1);
        address2 = (TextView) getView().findViewById(R.id.tv_list_address2);
        distance = (TextView) getView().findViewById(R.id.tv_list_distance);
        ivUser = (ImageView) getView().findViewById(R.id.iv_user_profile_img);
        btnUrl = (Button) getView().findViewById(R.id.btn_link);
        lvDetails = (LinearLayout) getView().findViewById(R.id.lv_list_details);
        mRateBar = (RatingBar) getView().findViewById(R.id.rating_bar1);
        lvFeaturedRow = (RelativeLayout) getView().findViewById(R.id.lv_featured_item_row);

        fragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        GMapApiInterface apiService = GmapApiClient.getClient().create(GMapApiInterface.class);
        Call<PlaceDetailsResponse> call = apiService.getPlaceDetials(MainActivity.API_KEY, this.placeId);

        call.enqueue(new Callback<PlaceDetailsResponse>() {

            @Override
            public void onResponse(Call<PlaceDetailsResponse> call, Response<PlaceDetailsResponse> response) {

                placedetails = response.body().getResult();
                distanceTxt = gps.calcDistanceBnTwoGeoPoints(gps.getLatitude(), gps.getLongitude(), placedetails.geometry.location.getLat(), placedetails.geometry.location.getLng());

                //Put the callback data on to view
                title.setText(placedetails.getName());
                address1.setText(placedetails.getFormatted_address());
                distance.setText(distanceTxt);

                updateActionButtonFav();

                fragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap mMap) {

                        //Animate camera position for google map
                        CameraPosition position = CameraPosition.builder()
                                .target(new LatLng(placedetails.geometry.location.getLat(), placedetails.geometry.location.getLng()))
                                .zoom(16f)
                                .bearing(0.0f)
                                .tilt(0.0f)
                                .build();

                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), null);
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        mMap.setTrafficEnabled(false);
                        mMap.setMyLocationEnabled(true);

                        mMap.getUiSettings().setZoomControlsEnabled( false );
                        //Animate camera for google maps ends here

                        // create marker
                        MarkerOptions marker = new MarkerOptions().position(new LatLng(placedetails.geometry.location.getLat(), placedetails.geometry.location.getLng()));

                        // adding marker
                        mMap.addMarker(marker);

                    }

                });

                if(Geocoder.isPresent())
                {
                    Geocoder ge = new Geocoder(getActivity().getApplicationContext());
                    try {
                        List<Address> address = ge.getFromLocation(placedetails.geometry.location.getLat(), placedetails.geometry.location.getLng(), 1);
                        address2.setText(address.get(0).getSubLocality());
                    } catch (IOException e) {
                        e.printStackTrace();
                        address2.setVisibility(View.GONE);
                    }
                }else{
                    address2.setVisibility(View.GONE);
                }

                try{

                    if(placedetails.getWebsite() != null){
                        btnUrl.setVisibility(View.VISIBLE);
                    }

                }catch(Exception e){

                    btnUrl.setVisibility(View.GONE);
                    Log.d("Exception URL", e.getMessage());
                }

                lvDetails.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(Call<PlaceDetailsResponse> call, Throwable t) {

            }

        });

        //Make a parallel request for getting featuired user details from OUR OWN API
        ApiInterface nAPiService = ApiClient.getClient().create(ApiInterface.class);
        Call<UserResponse> mCall = nAPiService.getUser(this.placeId);

        mCall.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {

                user = response.body().getResult();

                try {

                    if(user.getName() != null) {

                        //Set user rating bar value
                        mRateBar.setRating(user.getRating());

                        if(user.getOffers().length > 0){
                            lvOffers.setVisibility(View.VISIBLE);
                        }

                        lvFeaturedRow.setVisibility(View.VISIBLE);

                    } else {

                        lvFeaturedRow.setVisibility(View.GONE);

                    }

                }catch (Exception e){

                    lvOffers.setVisibility(View.GONE);
                }

                try{

                    if(user.getLogo() != null){

                        // Load image, decode it to Bitmap and return Bitmap to callback
                        imageLoader.loadImage(user.getLogo(), new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                ivUser.setVisibility(View.VISIBLE);
                                ivUser.setImageBitmap(loadedImage);
                            }

                            @Override
                            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                                ivUser.setVisibility(View.GONE);
                            }
                        });

                    }

                }catch (Exception e){

                    ivUser.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    private void killOldMap() {
        SupportMapFragment mapFragment = ((SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map));

        if(mapFragment != null) {
            FragmentManager fM = getFragmentManager();
            fM.beginTransaction().remove(mapFragment).commit();
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        killOldMap();
        Log.d("on Detach", "DONE");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        killOldMap();
        Log.d("On Destroy", "DONE");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        killOldMap();
        Log.d("On Destroy view", "DONE");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Button btnCall = (Button) getView().findViewById(R.id.btn_call);
        Button btnReviewsUser = (Button) getView().findViewById(R.id.read_reviews1);
        Button btnNavigate = (Button) getView().findViewById(R.id.btn_navigate);
        Button btnOffers = (Button) getView().findViewById(R.id.btn_offers);
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

                Fragment fragment = new ReviewsListFragment(user.getId(), placeId);
                Bundle bundle = new Bundle();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

                fragmentTransaction.addToBackStack("ProfileScreen");
                fragmentTransaction.replace(R.id.content_frame, fragment);
                fragmentTransaction.commit();
            }
        });

        btnOffers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new OffersFragment(user.getId(), placeId);
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
                try {
                    getContext().startActivity(intent);
                } catch(ActivityNotFoundException ex) {
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

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

}
