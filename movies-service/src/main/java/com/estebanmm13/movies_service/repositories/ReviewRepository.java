package com.estebanmm13.movies_service.repositories;


import com.estebanmm13.movies_service.models.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review,Long> {

    Page<Review> findReviewsByMovieId(Long movieId, Pageable pageable);
    Page<Review> findReviewsByUserId(Long userId, Pageable pageable);

    boolean existsByUserIdAndMovieId(Long userId, Long movieId);

}
