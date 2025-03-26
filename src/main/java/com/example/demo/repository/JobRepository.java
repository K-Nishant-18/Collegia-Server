package com.example.demo.repository;

import com.example.demo.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {
    @Query("SELECT j FROM Job j WHERE " +
            "(:title IS NULL OR j.title LIKE %:title%) AND " +
            "(:company IS NULL OR j.company LIKE %:company%) AND " +
            "(:type IS NULL OR j.type = :type) AND " +
            "(:location IS NULL OR j.location LIKE %:location%)")
    List<Job> findByFilters(
            @Param("title") String title,
            @Param("company") String company,
            @Param("type") String type,
            @Param("location") String location);
}