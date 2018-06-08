package polito.mad.mobiledeviceapplication.home;

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
import android.util.ArraySet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

import polito.mad.mobiledeviceapplication.MainActivity;
import polito.mad.mobiledeviceapplication.R;
import polito.mad.mobiledeviceapplication.books.AllBooks;
import polito.mad.mobiledeviceapplication.books.ShowBookDialogFragment;
import polito.mad.mobiledeviceapplication.search.SearchForm;
import polito.mad.mobiledeviceapplication.search.SearchMap;
import polito.mad.mobiledeviceapplication.utils.Book;
import polito.mad.mobiledeviceapplication.utils.Constants;

/**
 * Created by user on 05/05/2018.
 */

public class HomeFragment extends Fragment {

    private RecyclerView book_list_prefs,book_list_genre,book_list_news;
    private LinearLayoutManager mLayoutManagerPrefs,mLayoutManagerGenre,mLayoutManagerNews;
    private ArrayList<Bundle> books_prefs,books_genre,books_news;
    private HomeBookAdapter myAdapterPref, myAdapterNews, myAdapterGenre;

    public interface HomeObserver{

        void searchBooks(Intent intent);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_home, container, false);


        book_list_prefs = (RecyclerView) rootView.findViewById(R.id.book_list_prefs);
        book_list_prefs.setHasFixedSize(true);

        book_list_genre = (RecyclerView) rootView.findViewById(R.id.book_list_genre);
        book_list_genre.setHasFixedSize(true);

        book_list_news = (RecyclerView) rootView.findViewById(R.id.book_list_news);
        book_list_news.setHasFixedSize(true);


        mLayoutManagerPrefs = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        mLayoutManagerGenre = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        mLayoutManagerNews = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);

        book_list_prefs.setLayoutManager(mLayoutManagerPrefs);
        book_list_genre.setLayoutManager(mLayoutManagerGenre);
        book_list_news.setLayoutManager(mLayoutManagerNews);


        initializeLists();

        Activity a=getActivity();
        if (a instanceof HomeFragment.HomeObserver) {
            HomeFragment.HomeObserver observer = (HomeFragment.HomeObserver) a;
            Intent intent = new Intent(Constants.SEARCH_PREFS);
            observer.searchBooks(intent);
        }

        if (a instanceof HomeFragment.HomeObserver) {
            HomeFragment.HomeObserver observer = (HomeFragment.HomeObserver) a;
            Intent intent = new Intent(Constants.SEARCH_NEWS);
            observer.searchBooks(intent);
        }

        if (a instanceof HomeFragment.HomeObserver) {
            HomeFragment.HomeObserver observer = (HomeFragment.HomeObserver) a;
            Intent intent = new Intent(Constants.SEARCH_GENRE);
            observer.searchBooks(intent);
        }

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((MainActivity)getActivity()).toolbar.setTitle(R.string.home);

            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }



    class HomeBookAdapter extends RecyclerView.Adapter<HomeBookAdapter.ViewHolder> {

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




        public HomeBookAdapter(Context context, ArrayList<Bundle> myDataset) {
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

                    //ASK FIREBASE TO RETRIEVE USER INFO AND BOOK ADDITIONAL INFO
                    FirebaseStorage storage = FirebaseStorage.getInstance();

                    StorageReference storageRef = storage.getReference().child("images").child("books").child(mDataset.get(position).getString("book_id") + ".png");


                    final long ONE_MEGABYTE = 1024 * 1024;
                    storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {


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
                            b.putString("user_id",mDataset.get(position).getString("user_id"));
                            b.putString("book_id",mDataset.get(position).getString("book_id"));

                            b.putByteArray("book_conditions_image",bytes);

                            ShowBookDialogFragment showBookDialogFragment = new ShowBookDialogFragment();
                            showBookDialogFragment.setArguments(b);
                            showBookDialogFragment.show(getChildFragmentManager(), "ShowBookDialog");

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors

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
                            b.putString("user_id",mDataset.get(position).getString("user_id"));
                            b.putString("book_id",mDataset.get(position).getString("book_id"));


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

        }


        @Override
        public int getItemCount() {
            return mDataset.size();
        }


    }

    public void updatePrefs(Bundle b){

        HashMap<String,Bundle> entries = (HashMap<String,Bundle>) b.getSerializable("arg");


        for (String key : entries.keySet()) {// key is user_id

            System.out.println("ADDRESS " + ((HashMap) entries.get(key).get("user")).get("address").toString());
            System.out.println("BOOK " + ((ArrayList) entries.get(key).get("book_list")));

            for (HashMap<String, Object> entry : ((ArrayList<HashMap>) entries.get(key).get("book_list"))) {

                Bundle b1 = new Bundle();

                b1.putString("user_id", key);
                b1.putString("title", entry.get("title").toString());
                b1.putString("author", entry.get("author").toString());
                b1.putString("publisher", entry.get("publisher").toString());
                b1.putString("edition_year", entry.get("edition_year").toString());
                b1.putString("genre", entry.get("genre").toString());
                b1.putString("book_conditions", entry.get("book_conditions").toString());
                b1.putString("extra_tags", entry.get("extra_tags").toString());
                b1.putString("isbn", entry.get("isbn").toString());
                if (entry.get("image_url") != null)
                    b1.putString("image_url", entry.get("image_url").toString());
                b1.putString("book_id",entry.get("book_id").toString());

                b1.putString("name",((HashMap) entries.get(key).get("user")).get("name").toString());
                b1.putString("surname",((HashMap) entries.get(key).get("user")).get("surname").toString());

                books_prefs.add(b1);
                b1 = null;
            }
        }





        myAdapterPref.notifyDataSetChanged();




    }

    public void updateNews(Bundle b){

        books_news.add(b);
        myAdapterNews.notifyDataSetChanged();



    }

    public void updateGenre(Bundle b){

        books_genre.add(b);
        myAdapterGenre.notifyDataSetChanged();



    }

    private void initializeLists(){

        books_prefs = new ArrayList<>();
        myAdapterPref = new HomeBookAdapter(getContext(),books_prefs);
        book_list_prefs.setAdapter(myAdapterPref);

        books_news = new ArrayList<>();
        myAdapterNews = new HomeBookAdapter(getContext(),books_news);
        book_list_news.setAdapter(myAdapterNews);

        books_genre = new ArrayList<>();
        myAdapterGenre = new HomeBookAdapter(getContext(),books_genre);
        book_list_genre.setAdapter(myAdapterGenre);




    }


}

