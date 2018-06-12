package polito.mad.mobiledeviceapplication.books;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

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
    private ArrayList<HashMap> books;
    private ImageView book_cover, book_conditions_image;
    private TextView title,author,ISBN,genre,publisher, published_date,book_conditions, extra_tags;
    private ValueEventListener postListener_nofirebase,postListener_firebase;
    private RelativeLayout field_lay, wait_lay;
    private ImageRequest request;
    public String currentBookId;


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

        book_conditions_image = (ImageView) view.findViewById(R.id.book_conditions_image);

        field_lay= (RelativeLayout) view.findViewById(R.id.fields);
        wait_lay = (RelativeLayout) view.findViewById(R.id.wait_lay);




        book_list = (RecyclerView) view.findViewById(R.id.book_list);
        book_list.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        book_list.setLayoutManager(mLayoutManager);

        books = new ArrayList<>();
        mAdapter = new AllBooksAdapter(getContext(),books);
        book_list.setAdapter(mAdapter);


        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();

                if (mAdapter.mDataset.size()>0) {
                    //currentBookId= mAdapter.mDataset.get(0).get("book_id").toString();
                    //System.out.println(currentBookId);
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference().child("images").child("books").child(mAdapter.mDataset.get(0).get("book_id").toString() + ".png");


                    final long ONE_MEGABYTE = 1024 * 1024;
                    storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {


                            book_cover.setVisibility(View.VISIBLE);
                            field_lay.setVisibility(View.VISIBLE);

                            wait_lay.setVisibility(View.INVISIBLE);

                            book_cover.setImageDrawable(((ImageView) book_list.findViewHolderForAdapterPosition(0).itemView.findViewById(R.id.cover)).getDrawable());
                            title.setText(mAdapter.mDataset.get(0).get("title").toString());
                            author.setText(mAdapter.mDataset.get(0).get("author").toString());
                            publisher.setText(mAdapter.mDataset.get(0).get("publisher").toString());
                            published_date.setText("Published in: " + mAdapter.mDataset.get(0).get("edition_year").toString());
                            ISBN.setText("ISBN: " + mAdapter.mDataset.get(0).get("ISBN").toString());
                            extra_tags.setText("Extra tags\n" + mAdapter.mDataset.get(0).get("extra_tags").toString());
                            book_conditions.setText("Book conditions\n" + mAdapter.mDataset.get(0).get("book_conditions").toString());

                            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            book_conditions_image.setImageBitmap(bmp);
                            book_conditions_image.setVisibility(View.VISIBLE);


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors

                            //ASK FIREBASE TO RETRIEVE USER INFO AND BOOK ADDITIONAL INFO

                            book_cover.setVisibility(View.VISIBLE);
                            field_lay.setVisibility(View.VISIBLE);

                            wait_lay.setVisibility(View.INVISIBLE);

                            if ((book_list.findViewHolderForAdapterPosition(0) != null))
                                book_cover.setImageDrawable(((ImageView) book_list.findViewHolderForAdapterPosition(0).itemView.findViewById(R.id.cover)).getDrawable());

                            title.setText(mAdapter.mDataset.get(0).get("title").toString());
                            author.setText(mAdapter.mDataset.get(0).get("author").toString());
                            publisher.setText(mAdapter.mDataset.get(0).get("publisher").toString());
                            published_date.setText("Published in: " + mAdapter.mDataset.get(0).get("edition_year").toString());
                            ISBN.setText("ISBN: " + mAdapter.mDataset.get(0).get("ISBN").toString());
                            extra_tags.setText("Extra tags\n" + mAdapter.mDataset.get(0).get("extra_tags").toString());
                            book_conditions.setText("Book conditions\n" + mAdapter.mDataset.get(0).get("book_conditions").toString());

                            book_conditions_image.setVisibility(View.GONE);
                        }
                    });


                    wait_lay.setVisibility(View.INVISIBLE);
                    book_cover.setVisibility(View.VISIBLE);
                    field_lay.setVisibility(View.VISIBLE);
                }
            }
        });


        book_list.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {



            @Override
            public void onChildViewAttachedToWindow(final View view) {

               /* if (book_list.getChildAdapterPosition(view)==0) {

                    currentBookId= mAdapter.mDataset.get(0).get("book_id").toString();
                    System.out.println(currentBookId);
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference().child("images").child("books").child(mAdapter.mDataset.get(0).get("book_id").toString() + ".png");


                    final long ONE_MEGABYTE = 1024 * 1024;
                    storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {



                            book_cover.setVisibility(View.VISIBLE);
                            field_lay.setVisibility(View.VISIBLE);

                            wait_lay.setVisibility(View.INVISIBLE);

                            book_cover.setImageDrawable(((ImageView)view.findViewById(R.id.cover)).getDrawable());
                            title.setText(mAdapter.mDataset.get(0).get("title").toString());
                            author.setText(mAdapter.mDataset.get(0).get("author").toString());
                            publisher.setText(mAdapter.mDataset.get(0).get("publisher").toString());
                            published_date.setText("Published in: "+ mAdapter.mDataset.get(0).get("edition_year").toString());
                            ISBN.setText("ISBN: " + mAdapter.mDataset.get(0).get("ISBN").toString());
                            extra_tags.setText("Extra tags\n"+mAdapter.mDataset.get(0).get("extra_tags").toString());
                            book_conditions.setText("Book conditions\n" + mAdapter.mDataset.get(0).get("book_conditions").toString());

                            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            book_conditions_image.setImageBitmap(bmp);
                            book_conditions_image.setVisibility(View.VISIBLE);





                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors

                            //ASK FIREBASE TO RETRIEVE USER INFO AND BOOK ADDITIONAL INFO

                            book_cover.setVisibility(View.VISIBLE);
                            field_lay.setVisibility(View.VISIBLE);

                            wait_lay.setVisibility(View.INVISIBLE);

                            book_cover.setImageDrawable(((ImageView)view.findViewById(R.id.cover)).getDrawable());
                            title.setText(mAdapter.mDataset.get(0).get("title").toString());
                            author.setText(mAdapter.mDataset.get(0).get("author").toString());
                            publisher.setText(mAdapter.mDataset.get(0).get("publisher").toString());
                            published_date.setText("Published in: "+ mAdapter.mDataset.get(0).get("edition_year").toString());
                            ISBN.setText("ISBN: " + mAdapter.mDataset.get(0).get("ISBN").toString());
                            extra_tags.setText("Extra tags\n"+mAdapter.mDataset.get(0).get("extra_tags").toString());
                            book_conditions.setText("Book conditions\n" + mAdapter.mDataset.get(0).get("book_conditions").toString());

                            book_conditions_image.setVisibility(View.GONE);
                        }
                    });


                    wait_lay.setVisibility(View.INVISIBLE);
                    book_cover.setVisibility(View.VISIBLE);
                    field_lay.setVisibility(View.VISIBLE);

//                    if (((ImageView)view.findViewById(R.id.cover)).getDrawable()!=null)
//                              book_cover.setImageDrawable(((ImageView)view.findViewById(R.id.cover)).getDrawable());
//
//                    //System.out.println("AAA " + mAdapter.mDataset.get(mAdapter.mDataset.size()-1).get("title").toString());
//                    title.setText(mAdapter.mDataset.get(0).get("title").toString());
//                    author.setText(mAdapter.mDataset.get(0).get("author").toString());
//                    publisher.setText(mAdapter.mDataset.get(0).get("publisher").toString());
//                    published_date.setText("Published in: " + mAdapter.mDataset.get(0).get("edition_year").toString());
//                    book_conditions.setText("Book conditions\n" + mAdapter.mDataset.get(0).get("book_conditions").toString());
//                    extra_tags.setText("Extra tags\n"+ mAdapter.mDataset.get(0).get("extra_tags").toString());
//                    ISBN.setText("ISBN: " + mAdapter.mDataset.get(0).get("ISBN").toString());


                }
*/
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {

            }
        });




        myDatabase = FirebaseDatabase.getInstance().getReference();


            postListener_firebase= new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    books.clear();

                    if (dataSnapshot.hasChildren()) {
                        for (DataSnapshot child : dataSnapshot.child("users").getChildren()) {
                            if (((MainActivity)getActivity()).mAuth.getCurrentUser().getUid().equals(child.getKey())) {

                                for (DataSnapshot book : child.child("books").getChildren()) {

                                    HashMap<String,Object> book_map = (HashMap<String, Object>) book.getValue(Book.class).toMap();
                                    book_map.put("book_id",book.getKey());
                                    books.add(book_map);

                                    //wait_lay.setVisibility(View.INVISIBLE);
                                }

                                mAdapter.notifyDataSetChanged();
                                wait_lay.setVisibility(View.INVISIBLE);



                            }
                        }

                    } else {

                        wait_lay.setVisibility(View.INVISIBLE);

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                    Log.w("aaa", "loadPost:onCancelled", databaseError.toException());

                }
            };

            myDatabase.addValueEventListener(postListener_firebase);







        return view;
    }

    class AllBooksAdapter extends RecyclerView.Adapter<AllBooksAdapter.ViewHolder> {

        private ArrayList<HashMap> mDataset;
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


        public AllBooksAdapter(Context context, ArrayList<HashMap> myDataset) {
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

                    wait_lay.setVisibility(View.VISIBLE);

                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference().child("images").child("books").child(mDataset.get(position).get("book_id").toString() + ".png");

                    currentBookId = mDataset.get(position).get("book_id").toString();
                    System.out.println(currentBookId);

                    final long ONE_MEGABYTE = 1024 * 1024;
                    storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {




                            wait_lay.setVisibility(View.INVISIBLE);

                            book_cover.setVisibility(View.VISIBLE);
                            field_lay.setVisibility(View.VISIBLE);

                            wait_lay.setVisibility(View.INVISIBLE);


                            book_cover.setImageDrawable(((ImageView)holder.mView.findViewById(R.id.cover)).getDrawable());
                            title.setText(mDataset.get(position).get("title").toString());
                            author.setText(mDataset.get(position).get("author").toString());
                            publisher.setText(mDataset.get(position).get("publisher").toString());
                            published_date.setText("Published in: "+ mDataset.get(position).get("edition_year").toString());
                            ISBN.setText("ISBN: " + mDataset.get(position).get("ISBN").toString());
                            extra_tags.setText("Extra tags\n"+mDataset.get(position).get("extra_tags").toString());
                            book_conditions.setText("Book conditions\n" + mDataset.get(position).get("book_conditions").toString());

                            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            book_conditions_image.setImageBitmap(bmp);
                            book_conditions_image.setVisibility(View.VISIBLE);





                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {


                            wait_lay.setVisibility(View.INVISIBLE);

                            book_cover.setVisibility(View.VISIBLE);
                            field_lay.setVisibility(View.VISIBLE);

                            wait_lay.setVisibility(View.INVISIBLE);

                            book_cover.setImageDrawable(((ImageView)holder.mView.findViewById(R.id.cover)).getDrawable());
                            title.setText(mDataset.get(position).get("title").toString());
                            author.setText(mDataset.get(position).get("author").toString());
                            publisher.setText(mDataset.get(position).get("publisher").toString());
                            published_date.setText("Published in: "+ mDataset.get(position).get("edition_year").toString());
                            ISBN.setText("ISBN: " + mDataset.get(position).get("ISBN").toString());
                            extra_tags.setText("Extra tags\n"+mDataset.get(position).get("extra_tags").toString());
                            book_conditions.setText("Book conditions\n" + mDataset.get(position).get("book_conditions").toString());

                            book_conditions_image.setVisibility(View.GONE);
                        }
                    });

                }
            });

            holder.title.setText(mDataset.get(position).get("title").toString());
            holder.author.setText(mDataset.get(position).get("author").toString());


            if (mDataset.get(position).get("image_url")!=null) {
                request = new ImageRequest(mDataset.get(position).get("image_url").toString(),
                        new Response.Listener<Bitmap>() {
                            @Override
                            public void onResponse(Bitmap bitmap) {
                                holder.cover.setImageBitmap(bitmap);
                                holder.no_info.setVisibility(View.INVISIBLE);
                                //book_cover.setImageBitmap(bitmap);


                            }
                        }, 0, 0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565,
                        new Response.ErrorListener() {
                            public void onErrorResponse(VolleyError error) {
                                holder.cover.setImageResource(R.drawable.blank);
                                holder.no_info.setVisibility(View.VISIBLE);
                                holder.title.setText(mDataset.get(position).get("title").toString());
                                holder.author.setText(mDataset.get(position).get("author").toString());
                            }
                        });

                request.setRetryPolicy(new DefaultRetryPolicy(4 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                Volley.newRequestQueue(context).add(request);

            } else {

                holder.cover.setImageResource(R.drawable.blank);
                holder.no_info.setVisibility(View.VISIBLE);
                holder.title.setText(mDataset.get(position).get("title").toString());
                holder.author.setText(mDataset.get(position).get("author").toString());


            }

        }



        @Override
        public int getItemCount() {
            return mDataset.size();
        }


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

            if (((MainActivity) getActivity()).mAuth.getCurrentUser() == null) {
                if (postListener_nofirebase!=null)
                    myDatabase.removeEventListener(postListener_nofirebase);
            } else {
                if (postListener_firebase!=null)
                    myDatabase.removeEventListener(postListener_firebase);
            }

            if (request != null)
                request.cancel();



    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onDetach() {
        super.onDetach();


    }
}



