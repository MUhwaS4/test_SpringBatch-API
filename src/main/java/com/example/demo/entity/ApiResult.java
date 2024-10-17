package com.example.demo.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="tbl_api") // 실제 테이블 이름
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(value = {AuditingEntityListener.class})
public class ApiResult {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Comment("번호")
	int no;
	
	@CreatedDate
	@Comment("API 호출 시간")
	LocalDateTime adiCallTime;
	
	@Column(length = 10)
	@Comment("결과 코드")
	String resultCode; 
	
	@Column(length = 20)
	@Comment("결과 메시지")
	String resultMsg; 
	
	@Column(nullable = false)
	@Comment("전체 결과 수")
	int totalCount;

}
