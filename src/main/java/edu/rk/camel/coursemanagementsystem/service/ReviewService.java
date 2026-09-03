package edu.rk.camel.coursemanagementsystem.service;

import edu.rk.camel.coursemanagementsystem.model.dto.request.ReviewRequest;
import edu.rk.camel.coursemanagementsystem.model.dto.response.ReviewDto;

import java.util.List;

public interface ReviewService {
    List<ReviewDto> getCourseReviews(Integer courseId);
    ReviewDto submitReview(Integer courseId, ReviewRequest request);
    ReviewDto updateReview(Integer reviewId, ReviewRequest request);
    void deleteReview(Integer reviewId);
}
