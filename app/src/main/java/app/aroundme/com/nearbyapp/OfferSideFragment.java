package app.aroundme.com.nearbyapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import models.Config;
import models.NotificationsCategory;
import models.NotificationsCategoryResponse;
import rest.ApiClient;
import rest.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import service.AppSetting;

public class OfferSideFragment extends Fragment {

    //This is our tablayout
    private TabLayout tabLayout;

    //This is our viewPager
    private ViewPager viewPager;

    Tab1Fragment mFragment;

    List<NotificationsCategory> mNotificationsCategory;

    LinearLayout lvOfferNotificationsContents;

    ProgressBar mProgressBar;

    AppSetting appSetting;

    Config config;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_offer_side, container, false);

        appSetting = new AppSetting(getActivity().getApplicationContext());
        config = appSetting.getConfig();

        //Initializing viewPager
        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        //viewPager.setOffscreenPageLimit(3);
        setupViewPager(viewPager);

        //Initializing the tablayout
        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        lvOfferNotificationsContents = (LinearLayout) rootView.findViewById(R.id.lv_offer_side_contents);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.pb_progress1);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                viewPager.setCurrentItem(position,false);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });

        return rootView;
    }

    private void setupViewPager(final ViewPager viewPager) {

        final ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());

        //Get all categories with count that has offers .
        // JSON DAta should be like {subsc_category: "Atm", subsc_seo: "atm", offer_count: "20"}

        final String lastOfferID = (config.get_last_offerID() != null) ? config.get_last_offerID() : "";

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<NotificationsCategoryResponse> call = apiService.getAllOffers("");

        call.enqueue(new Callback<NotificationsCategoryResponse>() {

            @Override
            public void onResponse(Call<NotificationsCategoryResponse> call, Response<NotificationsCategoryResponse> response) {

                mNotificationsCategory = response.body().getResults();

                for (NotificationsCategory category: mNotificationsCategory) {

                    //If category has any new notifications
                    if(category.getCount() > 0) {
                        adapter.addFragment(NotificationsFragment.newInstance(category.getType()), getCategoryTitle(category.getType()));
                    }
                }

                viewPager.setAdapter(adapter);

                try {
                    setupTabIcons(mNotificationsCategory);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                lvOfferNotificationsContents.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(Call<NotificationsCategoryResponse> call, Throwable t) {
                t.printStackTrace();
            }

        });

    }

    private String getCategoryTitle(String seo) {

        String[] titlesFull = getResources().getStringArray(R.array.main_category_title);
        String[] seoFull = getResources().getStringArray(R.array.main_category_seo);

        int index = getIndexOfItemInArray(seoFull, seo);

        return (index != -1) ? titlesFull[index] : "Others";
    }

    public static int getIndexOfItemInArray(String[] stringArray, String name) {

        if (stringArray != null && stringArray.length > 0) {

            ArrayList<String> list = new ArrayList<String>(Arrays.asList(stringArray));
            int index = list.indexOf(name);
            list.clear();
            return index;
        }

        return -1;
    }

    private View prepareTabView(NotificationsCategory mCategory) {

        View view = getActivity().getLayoutInflater().inflate(R.layout.custom_tab,null);
        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        TextView tv_count = (TextView) view.findViewById(R.id.tv_count);
        tv_title.setText(getCategoryTitle(mCategory.getType()));
        tv_count.setVisibility(View.VISIBLE);
        tv_count.setText("" + mCategory.getCount());

        return view;
    }

    private void setupTabIcons(List<NotificationsCategory> mCategory) {

        for(int i=0; i < mCategory.size(); i++) {
            tabLayout.getTabAt(i).setCustomView(prepareTabView(mCategory.get(i)));
        }

    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        @Override
        public int getItemPosition(Object object){
            return super.getItemPosition(object);

        }

    }

}