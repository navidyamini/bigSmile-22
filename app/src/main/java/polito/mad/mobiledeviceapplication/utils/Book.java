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
    public Book() {
    }

    public Book(String ISBN, String title, String author, String publisher, String edition_year, String book_conditions, String genre, String extra_tags, String image_url) {
        this.ISBN = ISBN;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.edition_year = edition_year;
        this.book_conditions = book_conditions;
        this.genre = genre;
        this.extra_tags = extra_tags;
        this.image_url = image_url;
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

        return result;
    }


}
