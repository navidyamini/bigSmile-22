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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
import polito.mad.mobiledeviceapplication.utils.User;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by user on 08/05/2018.
 */

public class SearchMap extends Fragment {

    private Bundle b;
    private HashMap<String,Bundle> entries;
    private GoogleMap map;
    private DatabaseReference myDatabase;
    private ArrayList<Bundle> book_list;
    private MarkerAdapter myAdapter;
    private RecyclerView book_view;
    private LinearLayoutManager mLayoutManager;
    private TextView explaination;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_search_result, container,false);
        SupportMapFragment mapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView));

        b = getArguments();
        explaination = (TextView) v.findViewById(R.id.explaination);

        if (b!=null) {
            entries = (HashMap<String, Bundle>) b.getSerializable("arg");
            if (entries.size() == 0)
                explaination.setText("Non sono stati trovati risultati legati alla tua ricerca.");
        }


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




                for (String key : entries.keySet()){// key is user_id

                    System.out.println("ADDRESS " + ((HashMap)entries.get(key).get("user")).get("address").toString());
                    System.out.println("BOOK " + ((ArrayList)entries.get(key).get("book_list")));
                    retrieveLatLng(entries.get(key),key);
                }

                map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {

                        explaination.setVisibility(View.INVISIBLE);

                        book_list.clear();

                        final Bundle b = ((Bundle)marker.getTag());
                        final ArrayList<HashMap<String,Object>> books = (ArrayList) b.getSerializable("book_list");
                        final String user_id = b.getString("user_id");
                        final HashMap<String,Object> user_info = (HashMap) b.getSerializable("user");


                        for (HashMap<String,Object> book : books){

                            Bundle b1 = new Bundle();

                            b1.putString("user_id",user_id);
                            b1.putString("title",book.get("title").toString());
                            b1.putString("author",book.get("author").toString());
                            b1.putString("publisher",book.get("publisher").toString());
                            b1.putString("edition_year",book.get("edition_year").toString());
                            b1.putString("genre",book.get("genre").toString());
                            b1.putString("book_conditions",book.get("book_conditions").toString());
                            b1.putString("extra_tags",book.get("extra_tags").toString());
                            b1.putString("isbn",book.get("isbn").toString());
                            b1.putString("book_id",book.get("book_id").toString());

                            if (book.get("image_url")!=null)
                                b1.putString("image_url",book.get("image_url").toString());
                            b1.putString("name",user_info.get("name").toString());
                            b1.putString("surname",user_info.get("surname").toString());

                            book_list.add(b1);
                            b1 = null;

                        }



                        myAdapter.notifyDataSetChanged();




                        return false;
                    }
                });

                map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {

                        book_list.clear();
                        myAdapter.notifyDataSetChanged();

                        explaination.setVisibility(View.VISIBLE);

                    }
                });



            }
        });
        return v;
    }

    private void  retrieveLatLng(final Bundle entry, final String user_id){

        String url ="https://maps.googleapis.com/maps/api/geocode/json?address="+((HashMap)entry.get("user")).get("address").toString()+"&key="+ Constants.GEOCODER_API;

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
                                Bundle b = new Bundle();
                                b.putSerializable("user",(HashMap)entry.get("user"));
                                b.putStringArrayList("book_list",((ArrayList)entry.get("book_list")));
                                b.putString("user_id",user_id);
                                marker.setTag((Bundle)b.clone());



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



    class MarkerAdapter extends RecyclerView.Adapter<MarkerAdapter.ViewHolder> {

        private ArrayList<Bundle> mDataset;
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




        public MarkerAdapter(Context context, ArrayList<Bundle> myDataset) {
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


                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference().child("images").child("books").child(mDataset.get(position).getString("book_id") + ".png");


                    final long ONE_MEGABYTE = 1024 * 1024;
                    storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {


                            //ASK FIREBASE TO RETRIEVE USER INFO AND BOOK ADDITIONAL INFO
                            Bundle b = new Bundle();
                            b.putString("title",mDataset.get(position).getString("title"));
                            b.putString("author",mDataset.get(position).getString("author"));
                            b.putString("edition_year",mDataset.get(position).getString("edition_year"));
                            b.putString("book_conditions",mDataset.get(position).getString("book_conditions"));
                            b.putString("publisher",mDataset.get(position).getString("publisher"));
                            b.putString("isbn",mDataset.get(position).getString("isbn"));
                            b.putString("genre",mDataset.get(position).getString("genre"));
                            b.putString("extra_tags",mDataset.get(position).getString("extra_tags"));
                            b.putString("image_url",mDataset.get(position).getString("image_url"));
                            b.putString("name",mDataset.get(position).getString("name"));
                            b.putString("surname",mDataset.get(position).getString("surname"));

                            b.putByteArray("book_conditions_image",bytes);

                            ShowBookDialogFragment showBookDialogFragment = new ShowBookDialogFragment();
                            showBookDialogFragment.setArguments(b);
                            showBookDialogFragment.show(getChildFragmentManager(), "ShowBookDialog");



                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors

                            //ASK FIREBASE TO RETRIEVE USER INFO AND BOOK ADDITIONAL INFO
                            Bundle b = new Bundle();
                            b.putString("title",mDataset.get(position).getString("title"));
                            b.putString("author",mDataset.get(position).getString("author"));
                            b.putString("edition_year",mDataset.get(position).getString("edition_year"));
                            b.putString("book_conditions",mDataset.get(position).getString("book_conditions"));
                            b.putString("publisher",mDataset.get(position).getString("publisher"));
                            b.putString("isbn",mDataset.get(position).getString("isbn"));
                            b.putString("genre",mDataset.get(position).getString("genre"));
                            b.putString("extra_tags",mDataset.get(position).getString("extra_tags"));
                            b.putString("image_url",mDataset.get(position).getString("image_url"));
                            b.putString("name",mDataset.get(position).getString("name"));
                            b.putString("surname",mDataset.get(position).getString("surname"));


                            ShowBookDialogFragment showBookDialogFragment = new ShowBookDialogFragment();
                            showBookDialogFragment.setArguments(b);
                            showBookDialogFragment.show(getChildFragmentManager(), "ShowBookDialog");
                        }
                    });


                }
            });


            holder.title.setText(mDataset.get(position).getString("title"));
            holder.author.setText(mDataset.get(position).getString("author"));

            if (mDataset.get(position).getString("image_url")!=null) {
                ImageRequest request = new ImageRequest(mDataset.get(position).getString("image_url"),
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
                                holder.title.setText(mDataset.get(position).getString("title"));
                                holder.author.setText(mDataset.get(position).getString("author"));
                            }
                        });

                request.setRetryPolicy(new DefaultRetryPolicy(4 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                Volley.newRequestQueue(context).add(request);


            }else {

                holder.cover.setImageResource(R.drawable.blank);
                holder.no_info.setVisibility(View.VISIBLE);
                holder.title.setText(mDataset.get(position).getString("title"));
                holder.author.setText(mDataset.get(position).getString("author"));


            }




            FirebaseStorage storage = FirebaseStorage.getInstance();

            StorageReference storageRef = storage.getReference().child("images").child("books").child(mDataset.get(position).getString("book_id") + ".png");






        }


        @Override
        public int getItemCount() {
            return mDataset.size();
        }


    }


}
