package polito.mad.mobiledeviceapplication.search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import polito.mad.mobiledeviceapplication.R;
import polito.mad.mobiledeviceapplication.books.AddBookDialogFragment;
import polito.mad.mobiledeviceapplication.books.AllBooks;
import polito.mad.mobiledeviceapplication.books.ShowBookDialogFragment;
import polito.mad.mobiledeviceapplication.loginsignin.SignupFragment;
import polito.mad.mobiledeviceapplication.utils.Book;
import polito.mad.mobiledeviceapplication.utils.Constants;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by user on 08/05/2018.
 */

public class SearchMap extends Fragment {

    private Bundle b;
    private HashMap<String,Bundle> entries;
    private GoogleMap map;
    private DatabaseReference myDatabase;
    private ArrayList<Book> book_list;
    private MarkerAdapter myAdapter;
    private RecyclerView book_view;
    private LinearLayoutManager mLayoutManager;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_search_result, container,false);
        SupportMapFragment mapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView));

        b = getArguments();
        if (b!=null)
              entries = (HashMap<String,Bundle>) b.getSerializable("arg");




        book_view = (RecyclerView) v.findViewById(R.id.book_list);

        book_list = new ArrayList<>();
        mLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        book_view.setLayoutManager(mLayoutManager);

        myAdapter = new MarkerAdapter(getContext(),book_list);
        book_view.setAdapter(myAdapter);


        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                map = googleMap;

                for (String key : entries.keySet()){
                    retrieveLatLng(entries.get(key).getString("address"), entries.get(key).getStringArrayList("book_list"));


                }


                map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {


                        book_list.clear();

                        final ArrayList<String> books = (ArrayList<String>) marker.getTag();
                        System.out.println(books);

                        myDatabase = FirebaseDatabase.getInstance().getReference();

                        ValueEventListener eventListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (dataSnapshot.hasChildren()) {
                                    for (DataSnapshot child : dataSnapshot.getChildren().iterator().next().getChildren()) {

                                        for (DataSnapshot book : child.child("books").getChildren()) {

                                            if (books.contains(book.getKey()))
                                                book_list.add(book.getValue(Book.class));

                                        }



                                    }

                                    myAdapter.notifyDataSetChanged();

                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                                Log.w("aaa", "loadPost:onCancelled", databaseError.toException());

                            }
                        };

                        myDatabase.addListenerForSingleValueEvent(eventListener);



                        return false;
                    }
                });

                map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {

                        book_list.clear();
                        myAdapter.notifyDataSetChanged();

                    }
                });



            }
        });
        return v;
    }

    private void retrieveLatLng(String address, final List<String> books){

        System.out.println(address);
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

                                LatLng latLng = new LatLng(lat,lng);
                                Marker marker = map.addMarker(new MarkerOptions().position(latLng));
                                marker.setTag(books);



                                CameraUpdate center= CameraUpdateFactory.newLatLng(latLng);
                                CameraUpdate zoom=CameraUpdateFactory.zoomTo(6);

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



    class MarkerAdapter extends RecyclerView.Adapter<MarkerAdapter.ViewHolder> {

        private ArrayList<Book> mDataset;
        private Context context;

        public class ViewHolder extends RecyclerView.ViewHolder {

            public View mView;
            public TextView title,author;
            public ImageView cover;
            public RelativeLayout no_info;
            public ViewHolder(View v, TextView title, TextView author, ImageView cover, RelativeLayout no_info) {
                super(v);
                mView = v;
                this.title = title;
                this.author = author;
                this.cover = cover;
                this.no_info = no_info;
            }


        }




        public MarkerAdapter(Context context, ArrayList<Book> myDataset) {
            mDataset = myDataset;
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.books_entry,parent,false);
            TextView title = v.findViewById(R.id.title);
            TextView author = v.findViewById(R.id.author);
            ImageView cover = v.findViewById(R.id.cover);
            RelativeLayout no_info = v.findViewById(R.id.no_info_lay);


            ViewHolder vh = new ViewHolder(v,title,author,cover, no_info);
            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {


            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Bundle b = new Bundle();
                    b.putString("title",mDataset.get(position).title);
                    b.putString("author",mDataset.get(position).author);
                    b.putString("edition_year",mDataset.get(position).edition_year);
                    b.putString("book_conditions",mDataset.get(position).book_conditions);
                    b.putString("publisher",mDataset.get(position).publisher);
                    b.putString("isbn",mDataset.get(position).ISBN);
                    b.putString("genre",mDataset.get(position).genre);
                    b.putString("extra_tags",mDataset.get(position).extra_tags);
                    b.putString("image_url",mDataset.get(position).image_url);


                    ShowBookDialogFragment showBookDialogFragment = new ShowBookDialogFragment();
                    showBookDialogFragment.setArguments(b);
                    showBookDialogFragment.show(getChildFragmentManager(), "ShowBookDialog");




                }
            });


            holder.title.setText(mDataset.get(position).title);
            holder.author.setText(mDataset.get(position).author);

            if (mDataset.get(position).image_url!=null) {
                ImageRequest request = new ImageRequest(mDataset.get(position).image_url,
                        new Response.Listener<Bitmap>() {
                            @Override
                            public void onResponse(Bitmap bitmap) {
                                holder.cover.setImageBitmap(bitmap);
                                holder.no_info.setVisibility(View.INVISIBLE);


                            }
                        }, 0, 0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565,
                        new Response.ErrorListener() {
                            public void onErrorResponse(VolleyError error) {
                                holder.cover.setImageResource(R.drawable.blank);
                                holder.no_info.setVisibility(View.VISIBLE);
                                holder.title.setText(mDataset.get(position).title);
                                holder.author.setText(mDataset.get(position).author);
                            }
                        });

                request.setRetryPolicy(new DefaultRetryPolicy(4 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                Volley.newRequestQueue(context).add(request);
            }else {

                holder.cover.setImageResource(R.drawable.blank);
                holder.no_info.setVisibility(View.VISIBLE);
                holder.title.setText(mDataset.get(position).title);
                holder.author.setText(mDataset.get(position).author);


            }

        }


        @Override
        public int getItemCount() {
            return mDataset.size();
        }


    }


}
