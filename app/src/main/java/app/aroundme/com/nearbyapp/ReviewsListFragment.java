package app.aroundme.com.nearbyapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

import models.Reviews;
import models.ReviewsResponse;
import models.User;
import models.UserResponse;
import rest.ApiClient;
import rest.ApiInterface;
import service.GPSTracker;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Macbook on 16/09/16.
 */
public class ReviewsListFragment extends Fragment {

    View view;
    ListView lv;

    Button writeReview;
    RatingBar rateBar;
    TextView tvAddress2;

    String userID;
    String placeID;

    TextView title;
    TextView distance;
    TextView address1;
    ImageView ivUser;
    GPSTracker gps;
    User user;
    String distanceTxt;

    ProgressBar pBar;
    LinearLayout lvTop;
    LinearLayout lvBottom;
    List<Reviews> reviews;
    ReviewsListAdapter adapter;

    public ReviewsListFragment(String userID, String placeID) {

        // Required empty public constructor
        this.userID = userID;
        this.placeID = placeID;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ImageView iv = (ImageView) getActivity().findViewById(R.id.actionbar_logo);
        TextView tv = (TextView) getActivity().findViewById(R.id.actionbar_title);

        iv.setVisibility(View.GONE);
        tv.setVisibility(View.VISIBLE);
        tv.setText("Reviews");

        // creating GPS Class object
        gps = new GPSTracker(getActivity().getApplicationContext());

        return inflater.inflate(R.layout.fragment_reviews_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lv = (ListView) view.findViewById(android.R.id.list);
        writeReview = (Button) view.findViewById(R.id.read_reviews1);
        tvAddress2 = (TextView) view.findViewById(R.id.tv_list_address2);

        title = (TextView) view.findViewById(R.id.tv_list_title);
        address1 = (TextView) view.findViewById(R.id.tv_list_address1);
        distance = (TextView) view.findViewById(R.id.tv_list_distance);
        ivUser = (ImageView) view.findViewById(R.id.iv_user_profile_img);
        pBar = (ProgressBar) view.findViewById(R.id.pb_progess1);
        lvTop = (LinearLayout) view.findViewById(R.id.lv_top1);
        lvBottom = (LinearLayout) view.findViewById(R.id.lv_bottom1);

        //Show progress loader
        pBar.setVisibility(View.VISIBLE);

        //Set address2 TV ti hidden
        tvAddress2.setVisibility(View.GONE);

        //Set review button text to "Write review"
        writeReview.setText("Write Review");

        //Rating bar view hidden
        rateBar = (RatingBar) view.findViewById(R.id.rating_bar1);
        rateBar.setVisibility(View.GONE);

        //Make a parallel request for getting featuired user details from OUR OWN API
        ApiInterface nAPiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ReviewsResponse> mCall = nAPiService.getReviewsByUser(this.userID);

        mCall.enqueue(new Callback<ReviewsResponse>() {
            @Override
            public void onResponse(Call<ReviewsResponse> call, Response<ReviewsResponse> response) {
                reviews = response.body().getResult();

                adapter = new ReviewsListAdapter(getActivity(), reviews);
                lv.setAdapter(adapter);

                if(reviews.size() > 0){
                    lvBottom.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getContext(), "No reviews has been found", Toast.LENGTH_LONG).show();
                    writeReview.setText("Write the first review");
                }

            }

            @Override
            public void onFailure(Call<ReviewsResponse> call, Throwable t) {

            }
        });

        //EOR :- User api request ends up here ...

        //Get user details fomr API
        Call<UserResponse> mCallUser = nAPiService.getUser(this.placeID);

        mCallUser.enqueue(new Callback<UserResponse>() {

            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                user = response.body().getResult();

                distanceTxt = gps.calcDistanceBnTwoGeoPoints(gps.getLatitude(), gps.getLongitude(), user.getCordinates()[1], user.getCordinates()[0]);

                //Put the callback data on to view
                title.setText(user.getName());
                address1.setText(user.getAddress());
                distance.setText(distanceTxt);

                pBar.setVisibility(View.GONE);
                lvTop.setVisibility(View.VISIBLE);

            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {

            }
        });
        //EOR: API request for get user

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Toast.makeText(getContext(), "Review from " + reviews.get(position).getName() , Toast.LENGTH_LONG).show();

            }

            @SuppressWarnings("unused")
            public void onClick(View v) {
            }


        });

        writeReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment fragment = new WriteReviewFragment(user);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.content_frame, fragment);
                fragmentTransaction.commit();
            }
        });
    }
}

class ReviewsListAdapter extends ArrayAdapter<Reviews>
{
    Context context;
    List<Reviews> reviews;

    TextView tvName;
    TextView tvEmail;
    TextView tvComment;
    RatingBar rateBar;
    ImageView ivReview;
    ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options;

    public ReviewsListAdapter(Context context, List<Reviews> objects) {
        super(context, R.layout.fragment_review_row, objects);
        this.context = context;
        this.reviews = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.fragment_review_row, parent, false);

        tvName = (TextView) row.findViewById(R.id.tv_review_name);
        tvEmail = (TextView) row.findViewById(R.id.tv_review_email);
        tvComment = (TextView) row.findViewById(R.id.tv_review_text);
        rateBar = (RatingBar) row.findViewById(R.id.rb_review);
        ivReview = (ImageView) row.findViewById(R.id.review_profile_image);

        final Reviews review = reviews.get(position);

        rateBar.setRating(new Float(review.getRate()));
        tvName.setText(review.getName());
        tvEmail.setText(review.getEmail());
        tvComment.setText(review.getMessage());

        try{

            // Load image, decode it to Bitmap and return Bitmap to callback
            imageLoader.loadImage(review.getProfileImage(), new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    // Do whatever you want with Bitmap
                    ivReview.setImageBitmap(loadedImage);
                }
            });

        }catch (Exception e){
            ivReview.setVisibility(View.GONE);
            //Log.d("Exception Image", e.getMessage());
        }

        return row;
    }
}
