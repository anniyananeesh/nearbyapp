package app.aroundme.com.nearbyapp;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Required;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import models.UserResponse;
import rest.ApiClient;
import rest.ApiInterface;
import service.GPSTracker;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class AddProfileFragment extends Fragment implements View.OnClickListener, Validator.ValidationListener{

    View view;
    Validator validator;
    Spinner spinner;
    String category = null;
    Double latitude = 0.0;
    Double longitude = 0.0;
    GPSTracker gps;

    @Required(order = 1, message = "Valid business name")
    EditText businessName;

    @Required(order = 2, message = "Enter a valid Email Address")
    @Email(order = 2, message = "Please Check and Enter a valid Email Address")
    EditText email;

    @Required(order = 3, message = "Enter a valid phone no.")
    EditText phone;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_add_profile, container, false);

        ImageView iv = (ImageView) getActivity().findViewById(R.id.actionbar_logo);
        TextView tv = (TextView) getActivity().findViewById(R.id.actionbar_title);

        iv.setVisibility(View.GONE);
        tv.setVisibility(View.VISIBLE);
        tv.setText("Add Profile");

        spinner = (Spinner) view.findViewById(R.id.spinner);
        List categories = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.main_category_title)));
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext().getApplicationContext(), R.layout.spinner_category, android.R.id.text1, categories);
        //dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner.setAdapter(dataAdapter);

        gps = new GPSTracker(getActivity().getApplicationContext());

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                category = adapterView.getItemAtPosition(i).toString().toLowerCase();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //User data
        businessName = (EditText) view.findViewById(R.id.et_business_name);
        email = (EditText) view.findViewById(R.id.et_email);
        phone = (EditText) view.findViewById(R.id.et_phone);

        validator = new Validator(this);
        validator.setValidationListener(this);

        Button btnSendReq = (Button) view.findViewById(R.id.btn_send_add_request);
        Button btnShareLocation = (Button) view.findViewById(R.id.btn_share_location);

        btnSendReq.setOnClickListener(this);
        btnShareLocation.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_send_add_request:

                validator.validate();
            break;

            case R.id.btn_share_location:

                if (gps.canGetLocation()) {
                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();

                    Toast.makeText(getActivity().getApplicationContext(), "Your location has been shared . Thanks", Toast.LENGTH_SHORT).show();

                } else {
                    gps.showSettingsAlert();
                }

            break;
        }
    }

    @Override
    public void onValidationSucceeded() {

        if(latitude == null && longitude == null)
        {
            Toast.makeText(getActivity().getApplicationContext(), "Please share your location", Toast.LENGTH_LONG).show();
        }else if (category == null){
            Toast.makeText(getActivity().getApplicationContext(), "Choose atleast one category please", Toast.LENGTH_LONG).show();
        }else{

            final ProgressDialog dialog = new ProgressDialog(getActivity());
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Sending your request...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

            //save data to datbase
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

            Call<UserResponse> call = apiService.saveUser(businessName.getText().toString(),
                                                        category,
                                                        email.getText().toString(),
                                                        phone.getText().toString(),
                                                        latitude.toString(),
                                                        longitude.toString());

            call.enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                    if(response.body().getStatus() == 200){
                        Toast.makeText(getActivity().getApplicationContext(), "Your profile request has been send.", Toast.LENGTH_LONG).show();

                    }else{
                        Toast.makeText(getActivity().getApplicationContext(), "Some problem has occured", Toast.LENGTH_LONG).show();
                    }

                    dialog.dismiss();

                    //Reset the form elements
                    businessName.setText("");
                    email.setText("");
                    phone.setText("");

                }

                @Override
                public void onFailure(Call<UserResponse> call, Throwable t) {

                }

            });
        }
    }

    @Override
    public void onValidationFailed(View failedView, Rule failedRule) {
        Toast.makeText(getActivity().getApplicationContext(), failedRule.getFailureMessage(), Toast.LENGTH_SHORT).show();
    }

}
