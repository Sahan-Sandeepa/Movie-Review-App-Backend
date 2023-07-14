package dev.sahan.movies;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {
    
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<Review> getMovieReviews(String imdbId) {
        return reviewRepository.findByImdbId(imdbId);
    }

    public Review createReview(String reviewBody, String movieId) {
        Review review = reviewRepository.insert(new Review(reviewBody, movieId));

        Optional<Movie> optionalMovie = movieRepository.findMovieByImdbId(movieId);
        if (optionalMovie.isPresent()) {
            Movie movie = optionalMovie.get();
            String imdbId = movie.getImdbId();
            review.setImdbId(imdbId);
            reviewRepository.save(review);
        }

        return review;
    }
}
