package polito.mad.mobiledeviceapplication;

import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by user on 02/04/2018.
 */

class CustomAdapterEdit extends BaseAdapter {

    private ArrayList<String[]> array;
    private Context context;
    private LayoutInflater inflater;


    public CustomAdapterEdit(Context context, ArrayList<String[]> array) {

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
            v = (View) inflater.inflate(R.layout.listview_entry_edit, viewGroup, false);

        TextInputLayout inputLayout = (TextInputLayout) v.findViewById(R.id.text_input_layout);
        TextInputEditText editText = (TextInputEditText) v.findViewById(R.id.edit_text);
        ImageView image = (ImageView) v.findViewById(R.id.icon_entry);

        editText.setText(array.get(i)[0]);
        inputLayout.setHint(array.get(i)[1]);

        editText.setSingleLine(true);

        if (array.get(i)[1].equals("Name")){

            editText.setInputType(EditorInfo.TYPE_TEXT_VARIATION_PERSON_NAME);
            image.setImageResource(R.drawable.ic_account_circle_white_24dp);
        }
        if (array.get(i)[1].equals("Surname")) {

            editText.setInputType(EditorInfo.TYPE_TEXT_VARIATION_PERSON_NAME);
            image.setImageResource(R.drawable.ic_account_circle_white_24dp);
        }
        if (array.get(i)[1].equals("Email")) {

            editText.setInputType(EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            image.setImageResource(R.drawable.ic_email_white_24dp);
        }
        if (array.get(i)[1].equals("Telephone number")){

            editText.setInputType(EditorInfo.TYPE_CLASS_PHONE);
            image.setImageResource(R.drawable.ic_phone_white_24dp);
        }

        if (array.get(i)[1].equals("Bio")) {

            editText.setRawInputType(InputType.TYPE_CLASS_TEXT);
            editText.setInputType(EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE);
            editText.setSingleLine(false);
            image.setImageResource(R.drawable.ic_textsms_white_24dp);
        }

        if (array.get(i)[1].equals("Address")) {

            editText.setInputType(EditorInfo.TYPE_TEXT_VARIATION_POSTAL_ADDRESS);
            image.setImageResource(R.drawable.ic_place_white_24dp);
        }
        if (array.get(i)[1].equals("ZIP")) {

            editText.setInputType(EditorInfo.TYPE_TEXT_VARIATION_POSTAL_ADDRESS);
            image.setImageResource(R.drawable.ic_location_city_white_24dp);
        }
        if (array.get(i)[1].equals("Zone")){

            editText.setInputType(EditorInfo.TYPE_TEXT_VARIATION_POSTAL_ADDRESS);
            image.setImageResource(R.drawable.ic_streetview_white_24dp);
        }



        if (i!=array.size()-1 && !array.get(i)[1].equals("Bio"))
            editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        else
            editText.setImeOptions(EditorInfo.IME_ACTION_DONE);



        return v;
    }

    @Override
    public CharSequence[] getAutofillOptions() {
        return new CharSequence[0];
    }
}

