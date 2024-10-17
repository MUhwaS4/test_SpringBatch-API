package com.example.demo.apiresult;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.entity.ApiResult;
import com.example.demo.repository.ApiResultRepository;

@SpringBootTest
public class ApiResultRepositoryTest {
	
	@Autowired
	ApiResultRepository apiResultRepository;
	
	@Test
	public void 빈주입() {
		System.out.println("API Result: " + apiResultRepository);
	}
	
	@Test
	public void 데이터등록() {
		
		ApiResult apiResult1 = ApiResult.builder()
										.resultCode("01")
										.resultMsg("OK")
										.totalCount(10)
										.build();
		
		apiResultRepository.save(apiResult1);
		
		ApiResult apiResult2 = ApiResult.builder()
										.resultCode("02")
										.resultMsg("OK")
										.totalCount(10)
										.build();
		
		apiResultRepository.save(apiResult2);
		
	}
	
	@Test
	public void 데이터조회() {
		
		Optional<ApiResult> optional = apiResultRepository.findById(1);
		
		if (optional.isPresent()) {
			System.out.println(optional);
		}
		
	}
	
	@Test
	public void 데이터수정() {
		
		Optional<ApiResult> optional = apiResultRepository.findById(1);
		
		if (optional.isPresent()) {
			ApiResult apiResult = optional.get();
			apiResult.setResultMsg("수정 OK");
			
			apiResultRepository.save(apiResult);
		}
		
	}
	
	@Test
	public void 데이터삭제() {
		
		Optional<ApiResult> optional = apiResultRepository.findById(2);
		
		if (optional.isPresent()) {
			apiResultRepository.deleteById(2);
		}
		
	}

}
