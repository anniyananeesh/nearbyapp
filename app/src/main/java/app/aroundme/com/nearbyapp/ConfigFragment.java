package app.aroundme.com.nearbyapp;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.onesignal.OneSignal;

import models.Config;
import service.AppSetting;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConfigFragment extends Fragment {

    View view;
    AppSetting appSetting;

    RelativeLayout rvDistKm;
    RelativeLayout rvDistMi;

    RelativeLayout rvTempF;
    RelativeLayout rvTempC;

    RelativeLayout rvTerms;
    RelativeLayout rvPrivacy;
    RelativeLayout rvAboutUs;

    Config config;

    SwitchCompat swEnableNotifications;

    public ConfigFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appSetting = new AppSetting(getActivity().getApplicationContext());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ImageView iv = (ImageView) getActivity().findViewById(R.id.actionbar_logo);
        TextView tv = (TextView) getActivity().findViewById(R.id.actionbar_title);

        iv.setVisibility(View.GONE);
        tv.setVisibility(View.VISIBLE);
        tv.setText("Settings");

        return inflater.inflate(R.layout.fragment_config, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        config = appSetting.getConfig();

        updateView(config);

        final FragmentManager fm = getFragmentManager();

        rvDistKm = (RelativeLayout) view.findViewById(R.id.rl_distance_km);
        rvDistMi = (RelativeLayout) view.findViewById(R.id.rl_distance_mi);

        rvTempC = (RelativeLayout) view.findViewById(R.id.rl_temperature_c);
        rvTempF = (RelativeLayout) view.findViewById(R.id.rl_temperature_f);

        rvTerms = (RelativeLayout) view.findViewById(R.id.lv_terms);
        rvPrivacy = (RelativeLayout) view.findViewById(R.id.lv_privacy);
        rvAboutUs = (RelativeLayout) view.findViewById(R.id.lv_about_us);

        swEnableNotifications = (SwitchCompat) view.findViewById(R.id.enable_notifications);

        try {

            if(config.get_enable_notifications().replace(" ", "").toLowerCase().equals("y")) {
                swEnableNotifications.setChecked(true);
            } else {
                swEnableNotifications.setChecked(false);
            }

        } catch (Exception e) {

            appSetting.setNotificationsConfig("Y");
            swEnableNotifications.setChecked(true);
        }

        swEnableNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if(isChecked){
                    appSetting.setNotificationsConfig("Y");
                    OneSignal.setSubscription(true);
                }else{
                    appSetting.setNotificationsConfig("N");
                    OneSignal.setSubscription(false);
                }
            }
        });

        rvDistKm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appSetting.setDistanceConfig("Km");
                updateView(appSetting.getConfig());
            }
        });

        rvDistMi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appSetting.setDistanceConfig("Mi");
                updateView(appSetting.getConfig());
            }
        });

        rvTempC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appSetting.setTemperatureConfig("C");
                updateView(appSetting.getConfig());
            }
        });

        rvTempF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appSetting.setTemperatureConfig("F");
                updateView(appSetting.getConfig());
            }
        });

        rvTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fm.beginTransaction().replace(R.id.content_frame, new TermsFragment()).commit();
            }

        });

        rvPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fm.beginTransaction().replace(R.id.content_frame, new PrivacyFragment()).commit();
            }
        });

        rvAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fm.beginTransaction().replace(R.id.content_frame, new AboutFragment()).commit();
            }
        });
    }

    private void updateView(Config config) {

        ImageView ivDistKm = (ImageView) getActivity().findViewById(R.id.iv_distance_km);
        ImageView ivDistMi = (ImageView) getActivity().findViewById(R.id.iv_distance_mi);

        ImageView ivTempC = (ImageView) getActivity().findViewById(R.id.iv_temperature_c);
        ImageView ivTempF = (ImageView) getActivity().findViewById(R.id.iv_temperature_f);

        if(config.get_distance_unit().replace(" ", "").toLowerCase().toString().equals("km")) {

            ivDistKm.setVisibility(View.VISIBLE);
            ivDistMi.setVisibility(View.GONE);
        } else {
            ivDistKm.setVisibility(View.GONE);
            ivDistMi.setVisibility(View.VISIBLE);
        }

        //For temperature
        if(config.get_temperature_unit().replace(" ", "").toLowerCase().toString().equals("f")) {

            ivTempF.setVisibility(View.VISIBLE);
            ivTempC.setVisibility(View.GONE);

        } else {

            ivTempF.setVisibility(View.GONE);
            ivTempC.setVisibility(View.VISIBLE);

        }

    }

}
