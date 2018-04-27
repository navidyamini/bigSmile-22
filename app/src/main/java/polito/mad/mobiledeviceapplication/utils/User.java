package polito.mad.mobiledeviceapplication.utils;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 22/04/2018.
 */

public class User {

    public String username;
    public String password;
    public String name;
    public String surname;
    public String email;
    public String phone;
    public String bio;
    public String address;
    public String ZIP;
    public String zone;


    public User() {
    }

    public User(String username, String password, String name, String surname, String email, String phone, String bio, String address, String ZIP, String zone) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phone = phone;
        this.bio = bio;
        this.address = address;
        this.ZIP = ZIP;
        this.zone = zone;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("username", this.username);
        result.put("password", this.password);
        result.put("name", this.name);
        result.put("surname", this.surname);
        result.put("email", this.email);
        result.put("phone", this.phone);
        result.put("bio", this.bio);
        result.put("address",this.address);
        result.put("ZIP",this.ZIP);
        result.put("zone",this.zone);

        return result;
    }
}
