package com.example.demo.service.serviceImpl;

import com.example.demo.Util.Calculate;
import com.example.demo.Util.GetInfoUser;
import com.example.demo.constants.ConstantsReview;
import com.example.demo.model.Dto.CommentDto;
import com.example.demo.model.Dto.Messenger;
import com.example.demo.model.Dto.ReviewsDto;
import com.example.demo.model.Dto.ReviewsReturn;
import com.example.demo.model.entity.Reviews;
import com.example.demo.repositories.ProductRepository;
import com.example.demo.repositories.ReviewsRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.service.ReviewsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.demo.Util.Calculate.calculateAverage;
import static com.example.demo.Util.Calculate.roundToOneDecimal;

@Service
public class ReviewsImpl implements ReviewsService {

    private final ReviewsRepository reviewsRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final Messenger messenger;

    public ReviewsImpl(ReviewsRepository reviewsRepository, UserRepository userRepository, ProductRepository productRepository, Messenger messenger) {
        this.reviewsRepository = reviewsRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.messenger = messenger;
    }

    @Override
    public ResponseEntity<?> addReview(ReviewsDto reviewsDto) {

        try {
            if (reviewsDto.getRating() > 5 || reviewsDto.getRating() < 1) {
                messenger.setMessenger(ConstantsReview.RATING_RANGE);
                return new ResponseEntity<>(messenger, HttpStatus.BAD_REQUEST);
            }
            Reviews reviews = new Reviews();

            reviews.setRating(reviewsDto.getRating());
            reviews.setDateReview(new Date());
            reviews.setComment(reviewsDto.getComment());
            reviews.setUser(userRepository.findByUsername(GetInfoUser.getUsername()));
            reviews.setProduct(productRepository.findById(reviewsDto.getId_product()).orElse(null));

            reviewsRepository.save(reviews);
            messenger.setMessenger(ConstantsReview.ADD_SUCCESS);
            return new ResponseEntity<>(messenger, HttpStatus.OK);
        } catch (Exception e) {
            messenger.setMessenger(ConstantsReview.ADD_ERROR);
            return new ResponseEntity<>(messenger, HttpStatus.BAD_REQUEST);
        }

    }

    @Override
    public ResponseEntity<?> getReview(Long productID) {

        List<Reviews> reviewsList = reviewsRepository.findByProduct_Id(productID);
        ReviewsReturn reviewsReturn = new ReviewsReturn();

        List<Integer> Rating = new ArrayList<>();

        List<CommentDto> comments = new ArrayList<>();

        for (Reviews a : reviewsList) {
            //get list Rating
            Rating.add(a.getRating());
            //get cmt
            CommentDto commentDto = new CommentDto();
            commentDto.setName(a.getUser().getUsername());
            commentDto.setComment(a.getComment());
            commentDto.setRating(a.getRating());
            commentDto.setDateReview(a.getDateReview());
            //add  list
            comments.add(commentDto);
        }

        reviewsReturn.setAverageRating(roundToOneDecimal(calculateAverage(Rating)));
        reviewsReturn.setComments(comments);
        reviewsReturn.setQuantityReviews(reviewsList.size());

        return new ResponseEntity<>(reviewsReturn, HttpStatus.OK);
    }
}
