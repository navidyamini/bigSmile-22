package polito.mad.mobiledeviceapplication.requests;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;

import polito.mad.mobiledeviceapplication.MainActivity;
import polito.mad.mobiledeviceapplication.R;
import polito.mad.mobiledeviceapplication.utils.Constants;

/**
 * Created by user on 06/06/2018.
 */

public class RequestsFragment extends Fragment {

    private ViewPager pager;
    private TabLayout tabLayout;
    private OutgoingRequests outgoingRequestsFragment;
    private IncomingRequests incomingRequestsFragment;


    public interface RequestObserver{

        void searchRequests(Intent intent);
        void deleteRequest(Intent intent);
        void acceptRequest(Intent intent);
        void startLending(Intent intent);
        void endRequest(Intent intent);
        void contactBorrower(Intent intent);
        void receivedRequest(Intent intent);
        void sentbackRequest(Intent intent);
        void rateRequest(Intent intent);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_requests, container, false);
        ((MainActivity)getActivity()).toolbar.setTitle(R.string.requests_text);

        pager = (ViewPager) rootView.findViewById(R.id.container);
        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);

        tabLayout.setupWithViewPager(pager);
        pager.setAdapter(new FragAdapter(getChildFragmentManager(), 2, getContext()));

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                switch (position){

                    case 0:
                        Activity a = getActivity();

                        if (a instanceof RequestsFragment.RequestObserver) {
                            RequestsFragment.RequestObserver observer = (RequestsFragment.RequestObserver) a;
                            Intent intent = new Intent(Constants.SEARCH_OUTGOING_REQUESTS);
                            observer.searchRequests(intent);
                        }
                        break;

                    case 1:

                        a = getActivity();

                        if (a instanceof RequestsFragment.RequestObserver) {
                            RequestsFragment.RequestObserver observer = (RequestsFragment.RequestObserver) a;
                            Intent intent = new Intent(Constants.SEARCH_INCOMING_REQUESTS);
                            observer.searchRequests(intent);
                        }

                        break;

                    default:

                        break;

                }


            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return rootView;

    }



    class FragAdapter extends FragmentStatePagerAdapter {

        int tabCount;
        private Context context;
        private String[] tabTitles;


        public FragAdapter(FragmentManager fm, int tabCount, Context context) {
            super(fm);
            this.context = context;
            this.tabCount = tabCount;
            tabTitles = new String[]{context.getResources().getString(R.string.outgoing_requests),
                    context.getResources().getString(R.string.incoming_requests)};

        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    OutgoingRequests outgoingRequests = new OutgoingRequests();
                    return outgoingRequests;

                case 1:
                    IncomingRequests incomingRequests = new IncomingRequests();
                    return incomingRequests;



                default:
                    return null;
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
            // save the appropriate reference depending on position
            switch (position) {
                case 0:
                    outgoingRequestsFragment = (OutgoingRequests) createdFragment;
                    break;
                case 1:
                    incomingRequestsFragment = (IncomingRequests) createdFragment;
                    break;
            }
            return createdFragment;
        }

        @Override
        public int getCount() {
            return tabCount;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {

            return tabTitles[position];

        }


    }


    public void sendOutgoingData(ArrayList<HashMap> list){

        if (pager.getCurrentItem()==0 && outgoingRequestsFragment!=null)
            outgoingRequestsFragment.initializeList(list);


    }

    public void setIncomingData(ArrayList<HashMap> list){

        if (pager.getCurrentItem()==1 && incomingRequestsFragment!=null)
            incomingRequestsFragment.initializeList(list);

    }

}
