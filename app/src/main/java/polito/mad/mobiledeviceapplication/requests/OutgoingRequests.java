package polito.mad.mobiledeviceapplication.requests;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import polito.mad.mobiledeviceapplication.R;
import polito.mad.mobiledeviceapplication.utils.Constants;
import polito.mad.mobiledeviceapplication.utils.MyRequest;

/**
 * Created by user on 07/06/2018.
 */

public class OutgoingRequests extends Fragment {
    private RecyclerView requests_rv;
    private LinearLayoutManager linearLayoutManager;
    private MyRequestsAdapter myRequestsAdapter;
    private ArrayList<HashMap> requests;
    private TextView empty_txt;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_outgoing_requests, container, false);

        requests_rv = (RecyclerView) rootView.findViewById(R.id.requests_rv);
        linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);

        requests = new ArrayList<>();
        myRequestsAdapter = new MyRequestsAdapter(getContext(),requests);

        requests_rv.setLayoutManager(linearLayoutManager);
        requests_rv.setAdapter(myRequestsAdapter);

        empty_txt = (TextView) rootView.findViewById(R.id.empty_txt);



        return rootView;
    }

    @Override
    public void onStart() {

        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    class MyRequestsAdapter extends RecyclerView.Adapter<MyRequestsAdapter.ViewHolder> {

        private ArrayList<HashMap> mDataset;
        private Context context;

        public class ViewHolder extends RecyclerView.ViewHolder {

            public View mView;
            public TextView owner_name,book_name;
            public Button operation_btn, cancel_btn;
            public ImageButton info_btn;
            public TextView start_date,end_date;
            public TextView status;
            public ViewHolder(View v, TextView owner_name, TextView book_name, Button operation_btn, Button cancel_btn, ImageButton info_btn, TextView start_date, TextView end_date, TextView status) {
                super(v);
                mView = v;
                this.owner_name = owner_name;
                this.book_name = book_name;
                this.operation_btn = operation_btn;
                this.cancel_btn = cancel_btn;
                this.info_btn = info_btn;
                this.start_date = start_date;
                this.end_date = end_date;
                this.status = status;
            }


        }




        public MyRequestsAdapter(Context context, ArrayList<HashMap> myDataset) {
            mDataset = myDataset;
            this.context = context;
        }

        @Override
        public MyRequestsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.outgoing_request_entry,parent,false);
            TextView owner_name = v.findViewById(R.id.owner_name);
            TextView book_name = v.findViewById(R.id.book_name);
            TextView start_date = v.findViewById(R.id.start_date);
            TextView end_date = v.findViewById(R.id.end_date);
            Button operation_btn = v.findViewById(R.id.operation_btn);
            Button cancel_btn = v.findViewById(R.id.cancel_btn);
            ImageButton info_btn = v.findViewById(R.id.info_btn);
            TextView status = v.findViewById(R.id.status);


            MyRequestsAdapter.ViewHolder vh = new MyRequestsAdapter.ViewHolder(v,owner_name,book_name,operation_btn, cancel_btn, info_btn, start_date, end_date,status);
            return vh;
        }

        @Override
        public void onBindViewHolder(final MyRequestsAdapter.ViewHolder holder, final int position) {


            holder.owner_name.setText(mDataset.get(position).get("username").toString());
            holder.book_name.setText(mDataset.get(position).get("book_name").toString());
            holder.start_date.setText(((MyRequest) mDataset.get(position).get("request")).start_date);
            holder.end_date.setText(((MyRequest) mDataset.get(position).get("request")).end_date);
            holder.status.setText(((MyRequest) mDataset.get(position).get("request")).toMap().get("status").toString());
            holder.info_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            if (((MyRequest) mDataset.get(position).get("request")).toMap().get("status").toString().equals(MyRequest.STATUS.SENT.toString())) {


                holder.cancel_btn.setText("Contact the borrower");
                holder.cancel_btn.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                holder.cancel_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Activity a = getActivity();

                        if (a instanceof RequestsFragment.RequestObserver) {
                            RequestsFragment.RequestObserver observer = (RequestsFragment.RequestObserver) a;
                            Intent intent = new Intent(Constants.CHAT_REQUEST);
                            intent.putExtra("user_id_r", mDataset.get(position).get("owner_id").toString());
                            observer.contactBorrower(intent);
                        }


                    }
                });


                holder.operation_btn.setText("Confirm the reception of the book");
                holder.operation_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String req_id = mDataset.get(position).get("request_id").toString();

                        Activity a = getActivity();

                        if (a instanceof RequestsFragment.RequestObserver) {
                            RequestsFragment.RequestObserver observer = (RequestsFragment.RequestObserver) a;
                            Intent intent = new Intent(Constants.RECEIVED_REQUEST);
                            intent.putExtra("request_id", req_id);
                            observer.receivedRequest(intent);
                        }


                    }
                });

            } else if (((MyRequest) mDataset.get(position).get("request")).toMap().get("status").toString().equals(MyRequest.STATUS.RECEIVED.toString())) {


                holder.operation_btn.setText("Finalize the lending");
                holder.operation_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.DialogTheme);
                        builder.setCancelable(false);
                        builder.setTitle("Finalize borrowing");
                        builder.setMessage("Have you sent the book back to the owner?");
                        builder.setPositiveButton("Yes, I have", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                String req_id = mDataset.get(position).get("request_id").toString();

                                Activity a = getActivity();

                                if (a instanceof RequestsFragment.RequestObserver) {
                                    RequestsFragment.RequestObserver observer = (RequestsFragment.RequestObserver) a;
                                    Intent intent = new Intent(Constants.SENTBACK_REQUEST);
                                    intent.putExtra("request_id", req_id);
                                    observer.sentbackRequest(intent);
                                }

                                AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext(), R.style.DialogTheme);
                                View v = getLayoutInflater().inflate(R.layout.rating, null);
                                builder1.setView(v);
                                builder1.setCancelable(false);
                                builder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });

                                builder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        dialog.dismiss();
                                    }
                                });

                                builder1.create().show();

                            }
                        });
                        builder.setNegativeButton("No, not yet", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        builder.create().show();


                    }
                });

                holder.cancel_btn.setText("Contact the borrower");
                holder.cancel_btn.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                holder.cancel_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Activity a = getActivity();

                        if (a instanceof RequestsFragment.RequestObserver) {
                            RequestsFragment.RequestObserver observer = (RequestsFragment.RequestObserver) a;
                            Intent intent = new Intent(Constants.CHAT_REQUEST);
                            intent.putExtra("user_id_r", mDataset.get(position).get("owner_id").toString());
                            observer.contactBorrower(intent);
                        }


                    }
                });


            } else if (((MyRequest) mDataset.get(position).get("request")).toMap().get("status").toString().equals(MyRequest.STATUS.ACCEPTED.toString())) {

                holder.operation_btn.setText("More info about the book");
                holder.operation_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.DialogTheme);
                        builder.setCancelable(false);
                        builder.setTitle("Book status: ACCEPTED");
                        builder.setMessage("Your request has been accepted by the owner and he will soon send the book to you");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.create().show();

                    }


                });


                holder.cancel_btn.setText("REFUSE REQUEST");
                holder.cancel_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.DialogTheme);
                        builder.setCancelable(true);
                        builder.setTitle("Delete book request");
                        builder.setMessage("Are you sure you want to cancel this reservation? The owner will be notified about your decision");
                        builder.setPositiveButton("OK, delete it", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                String req_id = mDataset.get(position).get("request_id").toString();

                                Activity a = getActivity();

                                if (a instanceof RequestsFragment.RequestObserver) {
                                    RequestsFragment.RequestObserver observer = (RequestsFragment.RequestObserver) a;
                                    Intent intent = new Intent(Constants.REJECT_REQUEST);
                                    intent.putExtra("request_id", req_id);
                                    observer.deleteRequest(intent);
                                }


                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                            }
                        });

                        builder.create().show();


                    }
                });

            } else if (((MyRequest) mDataset.get(position).get("request")).toMap().get("status").toString().equals(MyRequest.STATUS.WAIT.toString())) {



                holder.operation_btn.setText("More info about the book");
                holder.operation_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.DialogTheme);
                        builder.setCancelable(false);
                        builder.setTitle("Book status: WAIT");
                        builder.setMessage("Your request has been taken into account by the owner. You will be notified about his/her decision.");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.create().show();

                    }


                });


                holder.cancel_btn.setText("REFUSE REQUEST");
                holder.cancel_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.DialogTheme);
                        builder.setCancelable(true);
                        builder.setTitle("Delete book request");
                        builder.setMessage("Are you sure you want to cancel this reservation? The owner will be notified about your decision");
                        builder.setPositiveButton("OK, delete it", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                String req_id = mDataset.get(position).get("request_id").toString();

                                Activity a = getActivity();

                                if (a instanceof RequestsFragment.RequestObserver) {
                                    RequestsFragment.RequestObserver observer = (RequestsFragment.RequestObserver) a;
                                    Intent intent = new Intent(Constants.REJECT_REQUEST);
                                    intent.putExtra("request_id", req_id);
                                    observer.deleteRequest(intent);
                                }


                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                            }
                        });

                        builder.create().show();


                    }
                });



            } else {

                holder.operation_btn.setText("More info about the book");
                holder.operation_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (((MyRequest) mDataset.get(position).get("request")).toMap().get("status").toString().equals(MyRequest.STATUS.ACCEPTED.toString())) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.DialogTheme);
                            builder.setCancelable(false);
                            builder.setTitle("Book status: ACCEPTED");
                            builder.setMessage("Your request has been accepted by the owner and he will soon send the book to you");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.create().show();

                        } else if (((MyRequest) mDataset.get(position).get("request")).toMap().get("status").toString().equals(MyRequest.STATUS.SENT_BACK.toString())) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.DialogTheme);
                            builder.setCancelable(false);
                            builder.setTitle("Book status: SENTBACK");
                            builder.setMessage("The book you have sent hasn't been received by the owner yet");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.create().show();


                        }

                        }
                });

                holder.cancel_btn.setText("Contact the borrower");
                holder.cancel_btn.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                holder.cancel_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Activity a = getActivity();

                        if (a instanceof RequestsFragment.RequestObserver) {
                            RequestsFragment.RequestObserver observer = (RequestsFragment.RequestObserver) a;
                            Intent intent = new Intent(Constants.CHAT_REQUEST);
                            intent.putExtra("user_id_r", mDataset.get(position).get("owner_id").toString());
                            observer.contactBorrower(intent);
                        }


                    }
                });


            }
        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }


    }

    public void initializeList(ArrayList<HashMap> list){

        this.requests.clear();

        for (HashMap elem : list)
            this.requests.add(elem);

        myRequestsAdapter.notifyDataSetChanged();

        if (this.requests.size()==0)
            empty_txt.setVisibility(View.VISIBLE);
        else
            empty_txt.setVisibility(View.INVISIBLE);


    }


}
