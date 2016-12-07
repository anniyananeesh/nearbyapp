package app.aroundme.com.nearbyapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Required;

import models.User;
import models.UserResponse;
import rest.ApiClient;
import rest.ApiInterface;
import service.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Macbook on 16/09/16.
 */
public class WriteReviewFragment extends Fragment implements Validator.ValidationListener {

    View view;
    User user;
    TextView tvHead;
    SessionManager mSessionManager;

    @Required(order = 2, message = "Put your review message here")
    EditText reviewMessage;

    RatingBar rateReview;

    ProgressBar mProgressBar;

    LinearLayout lvWriteReview;

    Button btnAddReview;

    Validator validator;

    public WriteReviewFragment(User user) {
        this.user = user;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSessionManager = new SessionManager(getActivity().getApplicationContext());

        if(!mSessionManager.checkLogin()){

            //Log.d("Session manager", mSessionManager.getUserDetails().get(SessionManager.KEY_NAME));
            Toast.makeText(getActivity().getApplicationContext(), "Please login to write review", Toast.LENGTH_SHORT).show();

            //Send route to singin page
            Fragment fragment = new SigninFragment();

            // replace your custom fragment class
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.replace(R.id.content_frame, fragment);
            fragmentTransaction.commit();
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_write_review, container, false);

        ImageView iv = (ImageView) getActivity().findViewById(R.id.actionbar_logo);
        TextView tv = (TextView) getActivity().findViewById(R.id.actionbar_title);

        iv.setVisibility(View.GONE);
        tv.setVisibility(View.VISIBLE);
        tv.setText("Write review");

        validator = new Validator(this);
        validator.setValidationListener(this);

        reviewMessage = (EditText) view.findViewById(R.id.et_review_message);
        rateReview = (RatingBar) view.findViewById(R.id.ratingBar);
        mProgressBar = (ProgressBar) view.findViewById(R.id.pb_progess1);
        lvWriteReview = (LinearLayout) view.findViewById(R.id.lv_write_review);
        btnAddReview = (Button) view.findViewById(R.id.btn_send_request);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Super title for the UI
        tvHead = (TextView) view.findViewById(R.id.text_head);
        tvHead.setText("Write a review about " + user.getName());

        //Submit the form for review
        btnAddReview.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                validator.validate();
            }

        });
    }

    @Override
    public void onValidationSucceeded() {

        if(rateReview.getRating() == 0){
            Toast.makeText(getActivity().getApplicationContext(), "Please make your rating", Toast.LENGTH_LONG).show();
        } else {

            mProgressBar.setVisibility(View.VISIBLE);

            //Animation alpha 1
            final AlphaAnimation animation1 = new AlphaAnimation(1.0f, 0.5f);
            animation1.setDuration(500);

            //Animation alpha 2
            final AlphaAnimation animation2 = new AlphaAnimation(0.5f, 1.0f);
            animation2.setDuration(500);

            lvWriteReview.startAnimation(animation1);

            //save data to datbase
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

            Call<UserResponse> call = apiService.saveRating( user.getId(),
                    mSessionManager.getUserDetails().get(SessionManager.KEY_NAME),
                    mSessionManager.getUserDetails().get(SessionManager.KEY_EMAIL),
                    mSessionManager.getUserDetails().get(SessionManager.KEY_IMAGE),
                    String.valueOf(rateReview.getRating()),
                    reviewMessage.getText().toString(),
                    mSessionManager.getUserDetails().get(SessionManager.KEY_GENDER),
                    mSessionManager.getUserDetails().get(SessionManager.KEY_FBID));

            call.enqueue(new Callback<UserResponse>() {

                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {

                    int responseCode = response.body().getStatus();

                    if(responseCode == 200){
                        Toast.makeText(getActivity().getApplicationContext(), "Your review has been posted", Toast.LENGTH_SHORT).show();

                        //Reset form elements
                        rateReview.setRating(0.0f);
                        reviewMessage.setText("");

                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), "Something went wrong :(", Toast.LENGTH_SHORT).show();
                    }
                    lvWriteReview.startAnimation(animation2);
                    mProgressBar.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<UserResponse> call, Throwable t) {
                    lvWriteReview.startAnimation(animation2);
                    mProgressBar.setVisibility(View.GONE);
                }

            });
        }
    }

    @Override
    public void onValidationFailed(View failedView, Rule<?> failedRule) {
        final String failureMessage = failedRule.getFailureMessage();
        Toast.makeText(getActivity().getApplicationContext(), failureMessage, Toast.LENGTH_SHORT).show();
    }
}
