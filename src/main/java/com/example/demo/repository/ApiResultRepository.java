package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.ApiResult;

@Transactional
public interface ApiResultRepository extends JpaRepository<ApiResult, Integer> {

}
