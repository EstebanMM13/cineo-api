package com.estebanmm13.movies_service.services.review;

import com.estebanmm13.movies_service.dtoModels.request.ReviewRequestDTO;
import com.estebanmm13.movies_service.dtoModels.response.ReviewResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReviewService {

    List<ReviewResponseDTO> findAllReviews();

    ReviewResponseDTO findReviewById(Long id);

    ReviewResponseDTO createReview(Long userId, Long movieId, ReviewRequestDTO dto);

    ReviewResponseDTO updateReview(Long id, Long userId, ReviewRequestDTO dto);

    void deleteReview(Long reviewId,Long userId);

    Page<ReviewResponseDTO> findReviewsByMovieId(Long movieId, Pageable pageable);

    Page<ReviewResponseDTO> findReviewsByUserId(Long userId, Pageable pageable);

}
