package dev.sahan.movies;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "Movies")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Movie {

    @Id
    private ObjectId id;

    private String imdbId;

    private String title;

    private String releasDate;
    
    private String trilerLink;

    private String poster;

    private List<String> genres;

    private List<String> backdrops;

    @DocumentReference
    //means of the reference to another collection
    private List<Review> reviewIds;
    
}
