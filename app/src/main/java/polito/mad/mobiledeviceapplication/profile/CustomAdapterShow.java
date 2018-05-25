package polito.mad.mobiledeviceapplication.profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import polito.mad.mobiledeviceapplication.R;

/**
 * Created by user on 02/04/2018.
 */

class CustomAdapterShow extends BaseAdapter {

    private ArrayList<String[]> array;
    private Context context;
    private LayoutInflater inflater;


    public CustomAdapterShow(Context context, ArrayList<String[]> array) {

        this.array = array;
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }


    @Override
    public int getCount() {
        return this.array.size();
    }

    @Override
    public Object getItem(int i) {
        return this.array.get(i);
    }

    @Override
    public long getItemId(int i) {
        return this.array.indexOf(getItem(i));
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View v;
        if (view!=null)
            v = view;
        else
            v = (View) inflater.inflate(R.layout.listview_entry_show, viewGroup, false);

        TextView text = (TextView) v.findViewById(R.id.entry_value);
        TextView description = (TextView) v.findViewById(R.id.description_value);
        ImageView image = (ImageView) v.findViewById(R.id.icon_entry);

        text.setText(array.get(i)[0]);
        description.setText(array.get(i)[1]);

        if (array.get(i)[1].equals(context.getString(R.string.user_name)))
            image.setImageResource(R.drawable.ic_account_circle_white_24dp);
        if (array.get(i)[1].equals(context.getString(R.string.user_surname)))
            image.setImageResource(R.drawable.ic_account_circle_white_24dp);
        if (array.get(i)[1].equals(context.getString(R.string.user_email)))
            image.setImageResource(R.drawable.ic_email_white_24dp);
        if (array.get(i)[1].equals(context.getString(R.string.user_PhoneNumber)))
            image.setImageResource(R.drawable.ic_phone_white_24dp);
        if (array.get(i)[1].equals(context.getString(R.string.user_ShortBio)))
            image.setImageResource(R.drawable.ic_textsms_white_24dp);
        if (array.get(i)[1].equals(context.getString(R.string.user_address)))
            image.setImageResource(R.drawable.ic_place_white_24dp);
        if (array.get(i)[1].equals(context.getString(R.string.user_zipCode)))
            image.setImageResource(R.drawable.ic_location_city_white_24dp);
        if (array.get(i)[1].equals(context.getString(R.string.user_zone)))
            image.setImageResource(R.drawable.ic_streetview_white_24dp);
        if (array.get(i)[1].equals(context.getString(R.string.username)))
            image.setImageResource(R.drawable.ic_account_circle_white_24dp);


/*        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            image.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorPrimaryDark)));
        }*/


        return v;
    }

    @Override
    public CharSequence[] getAutofillOptions() {
        return new CharSequence[0];
    }
}

