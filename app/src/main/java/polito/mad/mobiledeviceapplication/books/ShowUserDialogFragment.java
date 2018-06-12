package polito.mad.mobiledeviceapplication.books;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import polito.mad.mobiledeviceapplication.R;
import polito.mad.mobiledeviceapplication.utils.Comment;
import polito.mad.mobiledeviceapplication.utils.Constants;
import polito.mad.mobiledeviceapplication.utils.User;

/**
 * Created by user on 12/05/2018.
 */

public class ShowUserDialogFragment extends DialogFragment {

    private TextView username;
    private RatingBar user_rating;
    private ListView comment_list;
    private ImageView profile_image;

    private GoogleMap map;



    public interface FragUserObserver {

        void getUserInformation(Intent intent);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_users_comments, container, false);

        username = (TextView) v.findViewById(R.id.userNameText);
        user_rating = (RatingBar) v.findViewById(R.id.userRating);
        comment_list = (ListView) v.findViewById(R.id.commentsListView);
        profile_image = (ImageView) v.findViewById(R.id.userProfileImage);



        return v;

    }

    @Override
    public void onStart() {
        super.onStart();
        Activity a=getActivity();
        if (a instanceof ShowUserDialogFragment.FragUserObserver) {
            ShowUserDialogFragment.FragUserObserver observer = (ShowUserDialogFragment.FragUserObserver) a;
            Intent intent = new Intent(Constants.GET_USER_INFO);
            intent.putExtra("user_id",getArguments().getString("user_id",""));
            observer.getUserInformation(intent);
        }


    }

    public void retrieveUserInformation(User user, float rating, ArrayList<Comment> comments){

        username.setText(user.username);
        user_rating.setRating(rating);
        comment_list.setAdapter(new MyCommentAdapter(getContext(),comments));
        //retrieveLatLng(user.address);


    }



    class MyCommentAdapter extends BaseAdapter {

        private ArrayList<Comment> mDataset;
        private Context context;

        public MyCommentAdapter(Context context,ArrayList<Comment> mDataset) {
            this.mDataset = mDataset;
            this.context = context;
        }

        @Override
        public int getCount() {
            return mDataset.size();
        }

        @Override
        public Object getItem(int position) {
            return mDataset.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView==null)
            {
                convertView=LayoutInflater.from(context).inflate(R.layout.item_comments, null);
            }

            TextView userComment = (TextView) convertView.findViewById(R.id.userComment);
            RatingBar ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar);
            TextView username = (TextView) convertView.findViewById(R.id.userNameText);
            ImageView userImage = (ImageView) convertView.findViewById(R.id.userImage);

            ratingBar.setEnabled(false);
            userComment.setText(mDataset.get(position).message);
            ratingBar.setRating(mDataset.get(position).rate);
            username.setText("User " + position);


            return convertView;
        }
    }

    private void  retrieveLatLng(final String address){

        String url ="https://maps.googleapis.com/maps/api/geocode/json?address="+address+"&key="+ Constants.GEOCODER_API;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url.replace(" ","+"),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject res = null;

                        try {
                            res = new JSONObject(response);
                            if (res.optString("status").equals("OK")){

                                System.out.println(((JSONObject)((JSONObject)((JSONObject)((JSONArray)res.opt("results")).get(0)).opt("geometry")).opt("location")).opt("lat"));
                                System.out.println(((JSONObject)((JSONObject)((JSONObject)((JSONArray)res.opt("results")).get(0)).opt("geometry")).opt("location")).opt("lng"));

                                double lat = (double)((JSONObject)((JSONObject)((JSONObject)((JSONArray)res.opt("results")).get(0)).opt("geometry")).opt("location")).opt("lat");
                                double lng = (double)((JSONObject)((JSONObject)((JSONObject)((JSONArray)res.opt("results")).get(0)).opt("geometry")).opt("location")).opt("lng");

                                //BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.baseline_person_pin_24);

                                LatLng latLng = new LatLng(lat,lng);
                                Marker marker = map.addMarker(new MarkerOptions().position(latLng));

                                CameraUpdate center= CameraUpdateFactory.newLatLng(latLng);
                                CameraUpdate zoom=CameraUpdateFactory.zoomTo(5);

                                map.moveCamera(center);
                                map.animateCamera(zoom);


                            }



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }





                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        Volley.newRequestQueue(getContext()).add(stringRequest);


    }

}
