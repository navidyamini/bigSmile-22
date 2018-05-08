package polito.mad.mobiledeviceapplication.books;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import polito.mad.mobiledeviceapplication.MainActivity;
import polito.mad.mobiledeviceapplication.R;
import polito.mad.mobiledeviceapplication.utils.Book;
import polito.mad.mobiledeviceapplication.utils.Constants;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Asus on 4/24/2018.
 */

public class AllBooks extends Fragment {

    private RecyclerView book_list;
    private LinearLayoutManager mLayoutManager;
    private AllBooksAdapter mAdapter;
    private DatabaseReference myDatabase;
    private ArrayList<Book> books;
    private ImageView book_cover;
    private TextView title,author,ISBN,genre,publisher, published_date,book_conditions, extra_tags;
    private ValueEventListener postListener_nofirebase,postListener_firebase;
    private RelativeLayout field_lay, wait_lay;
    private ImageRequest request;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_all_books,container,false);

        book_cover = (ImageView) view.findViewById(R.id.book_image);
        title = (TextView) view.findViewById(R.id.title);
        author = (TextView) view.findViewById(R.id.author);
        ISBN = (TextView) view.findViewById(R.id.isbn);
        //genre = (TextView) view.findViewById(R.id.genre);
        publisher = (TextView) view.findViewById(R.id.publisher);
        published_date = (TextView) view.findViewById(R.id.publication_year);
        book_conditions = (TextView) view.findViewById(R.id.conditions);
        extra_tags = (TextView) view.findViewById(R.id.tags);

        field_lay= (RelativeLayout) view.findViewById(R.id.fields);
        wait_lay = (RelativeLayout) view.findViewById(R.id.wait_lay);



        book_list = (RecyclerView) view.findViewById(R.id.book_list);
        book_list.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        book_list.setLayoutManager(mLayoutManager);

        books = new ArrayList<>();
        mAdapter = new AllBooksAdapter(getContext(),books);
        book_list.setAdapter(mAdapter);

        book_list.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {

                if (book_list.getChildAdapterPosition(view)==0) {

                    wait_lay.setVisibility(View.INVISIBLE);
                    book_cover.setVisibility(View.VISIBLE);
                    field_lay.setVisibility(View.VISIBLE);

                   /* if (((ImageView)view.findViewById(R.id.cover)).getDrawable()!=null)
                        book_cover.setImageDrawable(((ImageView)view.findViewById(R.id.cover)).getDrawable());*/
/*                    else
                        book_cover.setImageResource(R.drawable.no_cover);*/

                    title.setText(mAdapter.mDataset.get(0).title);
                    author.setText(mAdapter.mDataset.get(0).author);
                    publisher.setText(mAdapter.mDataset.get(0).publisher);
                    published_date.setText("Published in: " + mAdapter.mDataset.get(0).edition_year);
                    book_conditions.setText("Book conditions\n" + mAdapter.mDataset.get(0).book_conditions);
                    extra_tags.setText("Extra tags\n"+ mAdapter.mDataset.get(0).extra_tags);
                    ISBN.setText("ISBN: " + mAdapter.mDataset.get(0).ISBN);



                }

            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {

            }
        });




        myDatabase = FirebaseDatabase.getInstance().getReference();

        if (((MainActivity)getActivity()).mAuth.getCurrentUser()==null) {
            postListener_nofirebase = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    books.clear();

                    if (dataSnapshot.hasChildren()) {
                        for (DataSnapshot child : dataSnapshot.getChildren().iterator().next().getChildren()) {
                            if (getActivity().getSharedPreferences(Constants.PREFERENCE_FILE, MODE_PRIVATE).getString("UID", "").equals(child.getKey())) {


                                for (DataSnapshot book : child.child("books").getChildren()) {

                                    books.add(book.getValue(Book.class));

                                    //wait_lay.setVisibility(View.INVISIBLE);
                                }

                                mAdapter.notifyDataSetChanged();





                            }


                        }

                    } else {

                        /*wait_lay.setVisibility(View.VISIBLE);
                        wait_message.setText(getString(R.string.try_again));
                        wait_progress.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), getString(R.string.user_not_found), Toast.LENGTH_SHORT).show();*/


                    }



                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                    Log.w("aaa", "loadPost:onCancelled", databaseError.toException());

                }
            };

            myDatabase.addValueEventListener(postListener_nofirebase);


        } else {

            postListener_firebase= new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {




                    books.clear();

                    if (dataSnapshot.hasChildren()) {
                        for (DataSnapshot child : dataSnapshot.getChildren().iterator().next().getChildren()) {
                            if (((MainActivity)getActivity()).mAuth.getCurrentUser().getUid().equals(child.getKey())) {

                                for (DataSnapshot book : child.child("books").getChildren()) {

                                    books.add(book.getValue(Book.class));

                                    //wait_lay.setVisibility(View.INVISIBLE);
                                }

                                mAdapter.notifyDataSetChanged();



                            }
                        }

                    } else {

  /*                      wait_lay.setVisibility(View.VISIBLE);
                        wait_message.setText(getString(R.string.try_again));
                        wait_progress.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), getString(R.string.user_not_found), Toast.LENGTH_SHORT).show();
*/
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                    Log.w("aaa", "loadPost:onCancelled", databaseError.toException());

                }
            };

            myDatabase.addValueEventListener(postListener_firebase);

        }





        return view;
    }

    class AllBooksAdapter extends RecyclerView.Adapter<AllBooksAdapter.ViewHolder> {

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




        public AllBooksAdapter(Context context, ArrayList<Book> myDataset) {
            mDataset = myDataset;
            this.context = context;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.books_entry,parent,false);
            TextView title = v.findViewById(R.id.title);
            TextView author = v.findViewById(R.id.author);
            ImageView cover = v.findViewById(R.id.cover);
            RelativeLayout no_info = v.findViewById(R.id.no_info_lay);


            AllBooksAdapter.ViewHolder vh = new AllBooksAdapter.ViewHolder(v,title,author,cover, no_info);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(final AllBooksAdapter.ViewHolder holder, final int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element


            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    book_cover.setVisibility(View.VISIBLE);
                    field_lay.setVisibility(View.VISIBLE);

                    wait_lay.setVisibility(View.INVISIBLE);


                    book_cover.setImageDrawable(holder.cover.getDrawable());
                    title.setText(mDataset.get(position).title);
                    author.setText(mDataset.get(position).author);
                    publisher.setText(mDataset.get(position).publisher);
                    published_date.setText("Published in: "+ mDataset.get(position).edition_year);
                    ISBN.setText("ISBN: " + mDataset.get(position).ISBN);
                    extra_tags.setText("Extra tags\n"+mDataset.get(position).extra_tags);
                    book_conditions.setText("Book conditions\n" + mDataset.get(position).book_conditions);



                }
            });

            holder.title.setText(mDataset.get(position).title);
            holder.author.setText(mDataset.get(position).author);

            request = new ImageRequest(mDataset.get(position).image_url,
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap bitmap) {
                            holder.cover.setImageBitmap(bitmap);
                            holder.no_info.setVisibility(View.INVISIBLE);




                        }
                    },0,0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565,
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






        }



        @Override
        public int getItemCount() {
            return mDataset.size();
        }


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (((MainActivity)getActivity()).mAuth.getCurrentUser()==null) {
            myDatabase.removeEventListener(postListener_nofirebase);
        } else {
            myDatabase.removeEventListener(postListener_firebase);
        }

        if (request!=null)
            request.cancel();



    }
}



