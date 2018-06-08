package polito.mad.mobiledeviceapplication.utils;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 06/06/2018.
 */

public class MyRequest {

    public String start_date;
    public String end_date;
    public String comments;
    public String requester_id;
    public String status;

    public MyRequest() {
    }

    public MyRequest(String start_date, String end_date, String comments, String requester_id, String status) {
        this.start_date= start_date;
        this.end_date = end_date;
        this.comments = comments;
        this.requester_id = requester_id;
        this.status = status;

    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("start_date", this.start_date);
        result.put("end_date", this.end_date);
        result.put("comments", this.comments);
        result.put("requester_id", this.requester_id);
        result.put("status",this.status);
        return result;
    }

    public enum STATUS{

        WAIT, //1
        SENT, //3
        ACCEPTED, //2
        REJECTED,
        ENDED, //6
        RECEIVED, //4
        SENT_BACK //5


    }


}
