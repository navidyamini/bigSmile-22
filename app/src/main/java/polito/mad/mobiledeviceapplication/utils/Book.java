package polito.mad.mobiledeviceapplication.utils;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 26/04/2018.
 */

public class Book {

    public String ISBN;
    public String title;
    public String author;
    public String publisher;
    public String edition_year;
    public String book_conditions;
    public String genre;
    public String extra_tags;
    public String image_url;
    public String insert_time;
    public Book() {
    }

    public Book(String ISBN, String title, String author, String publisher, String edition_year, String book_conditions, String genre, String extra_tags, String image_url, String insert_time) {
        this.ISBN = ISBN;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.edition_year = edition_year;
        this.book_conditions = book_conditions;
        this.genre = genre;
        this.extra_tags = extra_tags;
        this.image_url = image_url;
        this.insert_time = insert_time;
    }
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("isbn", this.ISBN);
        result.put("author", this.author);
        result.put("title", this.title);
        result.put("publisher", this.publisher);
        result.put("edition_year", this.edition_year);
        result.put("book_conditions", this.book_conditions);
        result.put("genre", this.genre);
        result.put("extra_tags",this.extra_tags);
        result.put("image_url",this.image_url);
        result.put("insert_time",this.insert_time);

        return result;
    }


}
