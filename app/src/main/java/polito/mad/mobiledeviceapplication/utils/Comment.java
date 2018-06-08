package polito.mad.mobiledeviceapplication.utils;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 08/06/2018.
 */

public class Comment {

    public String message;
    public float rate;
    public String writer_id;

    public Comment() {
    }

    public Comment(String message, float rate, String borrower_id) {
        this.message = message;
        this.rate = rate;
        this.writer_id = writer_id;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("message", this.message);
        result.put("rate", this.rate);
        result.put("borrower_id",this.writer_id);


        return result;
    }


}
