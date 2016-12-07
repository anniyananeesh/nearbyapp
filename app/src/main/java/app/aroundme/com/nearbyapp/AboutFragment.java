package app.aroundme.com.nearbyapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class AboutFragment extends Fragment {


    public AboutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ImageView iv = (ImageView) getActivity().findViewById(R.id.actionbar_logo);
        TextView tv = (TextView) getActivity().findViewById(R.id.actionbar_title);

        iv.setVisibility(View.GONE);
        tv.setVisibility(View.VISIBLE);
        tv.setText("About Nearby App");

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

}
