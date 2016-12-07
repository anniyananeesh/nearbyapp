package app.aroundme.com.nearbyapp;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import models.Favourite;
import models.Offer;
import models.OfferResponseJson;
import models.User;
import models.UserResponse;
import rest.ApiClient;
import rest.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import service.GPSTracker;
import service.SessionManager;

public class OfferDetailsFragment extends Fragment{

    View view;
    String userTitle;
    String offerID;
    Offer offer;

    TextView offerTitle;
    TextView offerDescription;
    ImageView offerImage;

    ImageLoader imageLoader = ImageLoader.getInstance();

    Button btnCall;
    Button btnNavigate;
    Button btnGetOffer;

    GPSTracker gps;
    SessionManager mSessionManager;
    User user;
    ShareActionProvider mShareActionProvider;

    public OfferDetailsFragment(User user, String offerID) {
        this.user = user;
        this.offerID = offerID;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_share, menu);

        if (menu != null) {
            menu.findItem(R.id.action_settings).setVisible(false);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // handle item selection
        switch (item.getItemId()) {

            case R.id.action_share:

                try {

                    URL url = new URL(offer.getOfferImage());
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();

                    InputStream input = connection.getInputStream();
                    Bitmap immutableBpm = BitmapFactory.decodeStream(input);
                    Bitmap mutableBitmap = immutableBpm.copy(Bitmap.Config.ARGB_8888, true);

                    View view  = new View(getActivity());
                    view.draw(new Canvas(mutableBitmap));

                    String path = MediaStore.Images.Media.insertImage(getActivity().getApplicationContext().getContentResolver(), mutableBitmap, "Nur", null);

                    Uri uri = Uri.parse(path);

                    Intent emailIntent = new Intent(Intent.ACTION_SEND);

                    emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    emailIntent.putExtra(Intent.EXTRA_TEXT, offer.getOfferTitle());
                    emailIntent.setType("image/png");
                    startActivity(Intent.createChooser(emailIntent, "Send mail..."));

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ImageView iv = (ImageView) getActivity().findViewById(R.id.actionbar_logo);
        TextView tv = (TextView) getActivity().findViewById(R.id.actionbar_title);

        iv.setVisibility(View.GONE);
        tv.setVisibility(View.VISIBLE);
        tv.setText(user.getName());

        gps = new GPSTracker(getActivity().getApplicationContext());
        mSessionManager = new SessionManager(getActivity().getApplicationContext());

        return inflater.inflate(R.layout.fragment_offer_details, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ProgressBar mProgressBar = (ProgressBar) view.findViewById(R.id.pb_progress1);
        final LinearLayout lvOfferDetails = (LinearLayout) view.findViewById(R.id.lv_view_offer_details);
        final LinearLayout lvOfferButtonGrp = (LinearLayout) view.findViewById(R.id.lv_footer_button);

        offerTitle = (TextView) view.findViewById(R.id.tv_offer_title);
        offerDescription = (TextView) view.findViewById(R.id.tv_offer_description);
        offerImage = (ImageView) view.findViewById(R.id.iv_offer_image);

        btnCall = (Button) view.findViewById(R.id.btn_offer_call);
        btnNavigate = (Button) view.findViewById(R.id.btn_navigate);
        btnGetOffer = (Button) view.findViewById(R.id.btn_get_offer);

        final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<OfferResponseJson> mCall = apiService.getOffersById(offerID);

        mCall.enqueue(new Callback<OfferResponseJson>() {

            @Override
            public void onResponse(Call<OfferResponseJson> call, Response<OfferResponseJson> response) {

                offer = response.body().getResult();
                offerTitle.setText(offer.getOfferTitle());
                offerDescription.setText(offer.getOfferDescription());

                mProgressBar.setVisibility(View.GONE);
                lvOfferButtonGrp.setVisibility(View.VISIBLE);
                lvOfferDetails.setVisibility(View.VISIBLE);

                try {

                    imageLoader.loadImage(offer.getOfferImage(), new SimpleImageLoadingListener(){

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            Log.d("Image loading failed", failReason.toString());
                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, final Bitmap loadedImage) {
                            offerImage.setImageBitmap(loadedImage);

                            offerImage.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View view) {

                                    final Dialog nagDialog = new Dialog(getActivity(),android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
                                    nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    nagDialog.setCancelable(false);
                                    nagDialog.setContentView(R.layout.full_image);
                                    Button btnClose = (Button)nagDialog.findViewById(R.id.btnIvClose);
                                    ImageView ivPreview = (ImageView)nagDialog.findViewById(R.id.iv_preview_image);
                                    ivPreview.setImageBitmap(loadedImage);

                                    btnClose.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            nagDialog.dismiss();
                                        }
                                    });
                                    nagDialog.show();
                                }

                            });
                        }

                    });

                } catch (Exception e) {
                    offerImage.setVisibility(View.GONE);
                    Log.d("Exception generated ", e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<OfferResponseJson> call, Throwable t) {

                mProgressBar.setVisibility(View.GONE);
                lvOfferButtonGrp.setVisibility(View.GONE);
                lvOfferDetails.setVisibility(View.GONE);

            }

        });

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(user.getContactNo() == null){

                    Toast.makeText(getContext(), "Cannot make call to this user", Toast.LENGTH_LONG).show();
                }else {
                    //Code to make phone call
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:"+Uri.encode(user.getContactNo())));
                    callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getContext().startActivity(callIntent);
                }
            }
        });

        btnNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Please wait, we make navigation ...", Toast.LENGTH_LONG).show();

                //Code to make phone call
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr="+ gps.getLatitude()+"," + gps.getLongitude()+ "&daddr="+user.getCordinates()[1]+","+ user.getCordinates()[0]));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                try
                {
                    getContext().startActivity(intent);
                }
                catch(ActivityNotFoundException ex)
                {
                    try
                    {
                        Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr="+ gps.getLatitude()+"," + gps.getLongitude()+ "&daddr="+user.getCordinates()[1]+","+ user.getCordinates()[0]));
                        getContext().startActivity(unrestrictedIntent);
                    }
                    catch(ActivityNotFoundException innerEx)
                    {
                        Toast.makeText(getContext(), "Please install a maps application", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        btnGetOffer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Call<UserResponse> mOfferReqCall = apiService.sendGetOffer(offer.getSubscriber(), offerID, offer.getOfferTitle(), mSessionManager.getUserDetails().get(mSessionManager.KEY_NAME), mSessionManager.getUserDetails().get(mSessionManager.KEY_EMAIL), offer.getOfferEmail());

                mOfferReqCall.enqueue(new Callback<UserResponse>() {

                    @Override
                    public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                        Toast.makeText(getActivity().getApplicationContext(), "Your request has been send", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<UserResponse> call, Throwable t) {
                        Log.d("Exception", t.getMessage());
                        Toast.makeText(getActivity().getApplicationContext(), "Something went wrong : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                });

            }

        });

    }

}