package dev.sahan.movies;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;


@RestController
@RequestMapping("/api/v1/reviews")
@CrossOrigin(origins = "http://localhost:3000")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @PostMapping
    public ResponseEntity<Review> createReview(@RequestBody Map<String, String> payload) {
        return new ResponseEntity<Review>(reviewService.createReview(payload.get("reviewBody"), payload.get("imdbId")), HttpStatus.CREATED);
    }

    @GetMapping("/movies/{imdbId}")
    public ResponseEntity<List<Review>> getMovieReviews(@PathVariable("imdbId") String imdbId) {
        List<Review> reviews = reviewService.getMovieReviews(imdbId);
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReview(@PathVariable("id") String reviewId) {
        ObjectId objectId;
        try {
            objectId = new ObjectId(reviewId);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Invalid review ID", HttpStatus.BAD_REQUEST);
        }

        Optional<Review> optionalReview = reviewRepository.findById(objectId);
        if (optionalReview.isPresent()) {
            Review review = optionalReview.get();

            // Delete the review from the movie's reviewIds array
            String imdbId = review.getImdbId();
            Query query = new Query(Criteria.where("imdbId").is(imdbId));
            Update update = new Update().pull("reviewIds", review);
            mongoTemplate.updateFirst(query, update, Movie.class);

            // Delete the review from the review collection
            reviewRepository.deleteById(objectId);

            return new ResponseEntity<>("Review deleted", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Review not found", HttpStatus.NOT_FOUND);
        }
    }
}
