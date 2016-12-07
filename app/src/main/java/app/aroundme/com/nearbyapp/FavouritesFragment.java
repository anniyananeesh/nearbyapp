package app.aroundme.com.nearbyapp;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cache.DatabaseHandler;
import models.Favourite;
import models.Places;
import service.GPSTracker;

/**
 * Created by Macbook on 16/09/16.
 */
public class FavouritesFragment extends Fragment {

    View view;
    ListView lv;
    List<Favourite> favourites;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_favourites, container, false);

        ImageView iv = (ImageView) getActivity().findViewById(R.id.actionbar_logo);
        TextView tv = (TextView) getActivity().findViewById(R.id.actionbar_title);

        iv.setVisibility(View.GONE);
        tv.setVisibility(View.VISIBLE);
        tv.setText("My Favourites");

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        lv = (ListView) getView().findViewById(android.R.id.list);

        DatabaseHandler  db = new DatabaseHandler(getActivity().getApplicationContext());
        favourites = db.getAllFavourites();

        FavouritesAdapter adapter = new FavouritesAdapter(getActivity(), favourites);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(getContext(), favourites.get(position).get_name(), Toast.LENGTH_LONG).show();

                Fragment fragment = new ListDetailsFragment(favourites.get(position).get_placeId());
                // replace your custom fragment class
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.content_frame, fragment);
                fragmentTransaction.commit();

            }

            @SuppressWarnings("unused")
            public void onClick(View v) {
            }


        });
    }

}

class FavouritesAdapter extends ArrayAdapter<Favourite>
{
    Context context;
    List<Favourite> favourites;
    DatabaseHandler db;
    GPSTracker gps;

    public FavouritesAdapter(Context context, List<Favourite> objects) {
        super(context, R.layout.fragment_favourite_row, objects);
        this.context = context;
        this.favourites = objects;
        this.gps = new GPSTracker(context);
        this.db = new DatabaseHandler(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.fragment_favourite_row, parent, false);

        TextView tvTitle = (TextView) row.findViewById(R.id.tv_list_title);
        TextView tvAddress1 = (TextView) row.findViewById(R.id.tv_list_address1);

        Button btnCall = (Button) row.findViewById(R.id.btn_call);
        Button btnNavigate = (Button) row.findViewById(R.id.btn_navigate);
        Button btnDelete = (Button) row.findViewById(R.id.btn_delete_fav);

        final Favourite favourite = favourites.get(position);

        tvTitle.setText(favourite.get_name());
        tvAddress1.setText(favourite.get_address());

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(favourite.get_phone_number() == null){

                    Toast.makeText(getContext(), "Cannot make call to this user", Toast.LENGTH_LONG).show();
                }else {
                    //Code to make phone call
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:"+Uri.encode(favourite.get_phone_number())));
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
                        Uri.parse("http://maps.google.com/maps?saddr="+ gps.getLatitude()+"," + gps.getLongitude()+ "&daddr="+favourite.get_latitude()+","+ favourite.get_longitude()));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                try {
                    getContext().startActivity(intent);
                } catch(ActivityNotFoundException ex) {
                    try
                    {
                        Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr="+ gps.getLatitude()+"," + gps.getLongitude()+ "&daddr="+favourite.get_latitude()+","+ favourite.get_longitude()));
                        getContext().startActivity(unrestrictedIntent);
                    }
                    catch(ActivityNotFoundException innerEx)
                    {
                        Toast.makeText(getContext(), "Please install a maps application", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog dialogBox =new AlertDialog.Builder(context)
                        //set message, title, and icon
                        .setTitle("Remove")
                        .setMessage("Do you want to remove " + favourite.get_name() + " from favourites ?" )
                        .setIcon(R.drawable.ic_delete)

                        .setPositiveButton("Remove", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {

                                db.deleteFavourite(favourite.get_placeId());
                                favourites.remove(position);
                                notifyDataSetChanged();

                                Toast.makeText(context, "Removed from favourites", Toast.LENGTH_SHORT).show();

                                if(favourites.size() == 0) {
                                    Toast.makeText(context, "No items in favourites", Toast.LENGTH_SHORT).show();
                                }
                                //your deleting code
                                dialog.dismiss();
                            }

                        })

                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                            }
                        })
                        .create();

                dialogBox.show();

            }
        });

        return row;
    }

}
