package app.aroundme.com.nearbyapp;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

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
import service.ConnectivityReceiver;
import service.GPSTracker;
import service.MyApplication;
import service.SessionManager;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ConnectivityReceiver.ConnectivityReceiverListener {


    FragmentManager fm = getSupportFragmentManager();
    public final static String API_KEY = "AIzaSyCM5zwVWA-AUclmW-UZpI70ktPjHjuxr3g";
    public final static String API_URI = "http://192.168.56.1/nearby/";
    public final static String IMAGE_PATH = API_URI + "uploads/subscriber/";
    public final static String MAX_DISTANCE = "10";

    public Menu menu;
    public NavigationView navigationView;
    SessionManager mSession;
    private AdView mAdView;

    AppSetting appSetting;
    Config config;

    List<NotificationsCategory> mNotificationsCount;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initImageLoader(getApplicationContext());

        int SDK_INT = Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        appSetting = new AppSetting(this);
        config = appSetting.getConfig();

        if (config.get_distance_unit() == null) {
            appSetting.setConfig(new Config(1, "Km", "F", "Y", ""));
        }

        setContentView(R.layout.activity_main);

        fm.beginTransaction().replace(R.id.content_frame, new PlaceholderFragment()).commit();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //remove shadow from actionbar
        getSupportActionBar().setElevation(0);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar_home);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mSession = new SessionManager(this);
        updateDrawerMenu(this, mSession);

        //Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, "ca-app-pub-1417162167864676~4593161345");

        mAdView = (AdView) findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        // Start loading the ad in the background.
        mAdView.loadAd(adRequest);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    // Method to manually check connection status
    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        updateInternetConnectionToast(isConnected);
    }

    private void updateInternetConnectionToast(boolean isConnected) {

        String message;
        int color;

        if (isConnected) {
            message = "Good! Connected to Internet";
            color = Color.GREEN;
        } else {
            message = "Sorry! Not connected to internet";
            color = Color.RED;
        }

        updateSnackBar(message, color);
    }

    private void updateSnackBar(String message, int color) {

        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.coordinateLayout), message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(this);

        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }


    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        final String lastOfferID = (config.get_last_offerID() != null) ? config.get_last_offerID() : "";

        AsyncTask<Void, Void, Void> async = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {

                ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                Call<NotificationsCategoryResponse> call = apiService.getUnreadOffers(lastOfferID); // Change last offer id from disk cache

                call.enqueue(new Callback<NotificationsCategoryResponse>() {

                    @Override
                    public void onResponse(Call<NotificationsCategoryResponse> call, Response<NotificationsCategoryResponse> response) {

                        mNotificationsCount = response.body().getResults();

                        if(mNotificationsCount.get(0).getCount() > 0) {

                            ActionItemBadge.update(MainActivity.this, menu.findItem(R.id.action_alerts), getResources().getDrawable(R.drawable.ic_bell),ActionItemBadge.BadgeStyles.RED,mNotificationsCount.get(0).getCount());

                            try {
                                appSetting.setUpdateLastOfferIDConfig(mNotificationsCount.get(0).getLastOffer());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {
                            ActionItemBadge.hide(menu.findItem(R.id.action_alerts));
                        }
                    }

                    @Override
                    public void onFailure(Call<NotificationsCategoryResponse> call, Throwable t) {
                        t.printStackTrace();
                        ActionItemBadge.hide(menu.findItem(R.id.action_alerts));
                    }

                });

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }
        };

        async.execute();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            navigationView.getMenu().findItem(R.id.nav_settings).setChecked(true);
            fm.beginTransaction().replace(R.id.content_frame, new ConfigFragment()).commit();
            return true;
        }

        if (id == R.id.action_alerts) {
            fm.beginTransaction().replace(R.id.content_frame, new OfferSideFragment()).commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

            fm.beginTransaction().replace(R.id.content_frame, new PlaceholderFragment()).commit();

        } else if (id == R.id.nav_settings) {

            navigationView.getMenu().findItem(R.id.nav_settings).setChecked(true);
            fm.beginTransaction().replace(R.id.content_frame, new ConfigFragment()).commit();

        } else if (id == R.id.nav_add_business) {

            fm.beginTransaction().replace(R.id.content_frame, new AddProfileFragment()).commit();

        } else if (id == R.id.nav_offers) {

            fm.beginTransaction().replace(R.id.content_frame, new OfferSideFragment()).commit();

        } else if (id == R.id.nav_signin) {

            navigationView.getMenu().findItem(R.id.nav_signin).setChecked(true);
            fm.beginTransaction().replace(R.id.content_frame, new SigninFragment()).commit();

        } else if (id == R.id.nav_favourites) {

            navigationView.getMenu().findItem(R.id.nav_favourites).setChecked(true);
            fm.beginTransaction().replace(R.id.content_frame, new FavouritesFragment()).commit();
        } else if (id == R.id.nav_signout) {

            mSession.logoutUser();
            fm.beginTransaction().replace(R.id.content_frame, new PlaceholderFragment()).commit();

            updateDrawerMenu(this, mSession);

            //Logout from facebook
            FacebookSdk.sdkInitialize(this);
            LoginManager.getInstance().logOut();

            updateSnackBar("User logged out", Color.RED);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static void updateDrawerMenu(Activity activity, SessionManager mSession) {

        NavigationView navigationView = (NavigationView) activity.findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        MenuItem mnuSignin = menu.findItem(R.id.nav_signin);
        MenuItem mnuSignOut = menu.findItem(R.id.nav_signout);
        MenuItem mnuFavourites = menu.findItem(R.id.nav_favourites);

        MenuItem mnuAccountGroup = menu.findItem(R.id.account);
        SpannableString s = new SpannableString(mnuAccountGroup.getTitle());
        s.setSpan(new TextAppearanceSpan(activity, R.style.AppTheme_DrawerSecondaryText), 0, s.length(), 0);
        mnuAccountGroup.setTitle(s);

        if (mSession.checkLogin()) {
            mnuSignin.setVisible(false);
            mnuSignOut.setVisible(true);
            mnuFavourites.setVisible(true);
        } else {
            mnuSignin.setVisible(true);
            mnuSignOut.setVisible(false);
            mnuFavourites.setVisible(false);
        }

        return;
    }

    public static void initImageLoader(Context context) {

        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        updateInternetConnectionToast(isConnected);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://app.aroundme.com.nearbyapp/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://app.aroundme.com.nearbyapp/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    public static class PlaceholderFragment extends ListFragment implements AdapterView.OnItemClickListener {

        List<String> titles;
        String[] seo;
        ListView lv;
        CustomListAdapter adapter;
        EditText inputSearch;
        String[] titleArray;
        GPSTracker gps;

        private final String TAG = "mainactivity";

        int[] images = {R.drawable.ic_restaurant,
                R.drawable.ic_bar,
                R.drawable.ic_hotel,
                R.drawable.ic_bar,
                R.drawable.ic_bar,
                R.drawable.ic_bar,
                R.drawable.ic_bar,
                R.drawable.ic_bar,
                R.drawable.ic_restaurant,
                R.drawable.ic_bar,
                R.drawable.ic_hotel,
                R.drawable.ic_bar,
                R.drawable.ic_bar,
                R.drawable.ic_bar,
                R.drawable.ic_bar,
                R.drawable.ic_bar,
                R.drawable.ic_restaurant,
                R.drawable.ic_bar,
                R.drawable.ic_hotel,
                R.drawable.ic_bar,
                R.drawable.ic_bar};


        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_home, container, false);
            ImageView iv = (ImageView) getActivity().findViewById(R.id.actionbar_logo);
            TextView tv = (TextView) getActivity().findViewById(R.id.actionbar_title);

            iv.setVisibility(View.VISIBLE);
            tv.setVisibility(View.GONE);

            gps = new GPSTracker(getActivity().getApplicationContext());

            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            lv = (ListView) getView().findViewById(android.R.id.list);
            lv.setTextFilterEnabled(true);

            initList();

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    Fragment fragment = new ListsFragment();
                    Bundle bundle = new Bundle();

                    bundle.putString("seo", seo[position]); // use as per your need
                    bundle.putString("title", titles.get(position)); // use as per your need

                    fragment.setArguments(bundle);

                    getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment)
                            .addToBackStack("myfragment").commit();

                }

                @SuppressWarnings("unused")
                public void onClick(View v) {
                }


            });

            inputSearch = (EditText) getActivity().findViewById(R.id.search_text);

            inputSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    if (charSequence.toString().equals("")) {
                        initList();
                    } else {
                        searchItem(charSequence.toString());
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {


                }
            });

        }

        public void searchItem(String s) {
            for (String item : titleArray) {
                if (!item.contains(s.toString())) {
                    titles.remove(item);
                }
            }

            adapter.notifyDataSetChanged();
        }

        public void initList() {

            Resources res = getResources();

            titleArray = res.getStringArray(R.array.main_category_title);
            titles = new ArrayList<>(Arrays.asList(titleArray));
            seo = res.getStringArray(R.array.main_category_seo);

            adapter = new CustomListAdapter(getActivity(), titles, images);
            lv.setAdapter(adapter);
        }

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        }

    }
}

class CustomListAdapter extends ArrayAdapter<String>
{
    Context context;

    int image[];

    List<String> title;

    public CustomListAdapter(Context context, List<String> titles, int[] imgs) {
        super(context, R.layout.list_category, titles);
        this.context = context;
        this.title = titles;
        this.image = imgs;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.list_category, parent, false);

        ImageView im = (ImageView) row.findViewById(R.id.ic_category_iv);
        TextView tv = (TextView) row.findViewById(R.id.ic_category_txt_view);

        im.setImageResource(image[position]);
        tv.setText(title.get(position));

        return row;
    }

}
