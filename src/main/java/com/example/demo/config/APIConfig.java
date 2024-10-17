package com.example.demo.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.demo.entity.ApiResult;
import com.example.demo.repository.ApiResultRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class APIConfig {
	
	@Autowired
	JobRepository jobRepository;
	
	@Autowired
	ApiResultRepository apiResultRepository;
	
	@Autowired
	PlatformTransactionManager manager;
	
	// JOB
	@Bean
	public Job simpleJob() {
		
		Job job = new JobBuilder("simpleJob", jobRepository)
						.start(step1())
						.next(step2())
						.next(step3())
						.build();
				
		return job;
		
	}
	
	@Bean
	public Step step1() {
	
		TaskletStep step = new StepBuilder("step1. ", jobRepository)
								.tasklet(testTasklet1(), manager)
								.build();
		
		return step;
		
	}
	
	@Bean
	public Step step2() {
	
		TaskletStep step = new StepBuilder("step2. ", jobRepository)
								.tasklet(testTasklet2(), manager)
								.build();
		
		return step;
		
	}
	
	@Bean
	public Step step3() {
	
		TaskletStep step = new StepBuilder("step3. ", jobRepository)
								.tasklet(testTasklet3(), manager)
								.build();
		
		return step;
		
	}
	
	@Bean
	public Tasklet testTasklet1() {
		
		Tasklet tasklet = (contribution, chunkContext) -> {
			
			System.out.println("Step1. API 호출하기.");
			
			StepContext context = chunkContext.getStepContext();
			
			ExecutionContext executionContext = context
												.getStepExecution()
												.getJobExecution()
												.getExecutionContext();
			
			executionContext.put("getWeather", getWeather());
			
			return RepeatStatus.FINISHED;
			
		};
		
		return tasklet;
		
	}
	
	@Bean
	public Tasklet testTasklet2() {
		
		Tasklet tasklet = (contribution, chunkContext) -> {
			
			System.out.println("Step2. 응답 데이터 파싱하기.");
			
			StepContext context = chunkContext.getStepContext();
			ExecutionContext executionContext = context
												.getStepExecution()
												.getJobExecution()
												.getExecutionContext();
			
			String getWeather = executionContext.get("getWeather").toString();
			
			ObjectMapper mapper = new ObjectMapper();
			
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			
			String weather = getWeather;
			
			Root root = null;
			
			root = mapper.readValue(weather, Root.class);
			
			String resultCode = root.response.header.resultCode; // 결과 코드
			String resultMsg = root.response.header.resultMsg; // 결과 메시지
			int totalCount = root.response.body.totalCount; // 전체 결과 수
			
			executionContext.put("resultCode", resultCode);
			executionContext.put("resultMsg", resultMsg);
			executionContext.put("totalCount", totalCount);
			
			System.out.println("결과 코드: " + resultCode);
			
			System.out.println("결과 메시지: " + resultMsg);
			
			System.out.println("전체 결과수: " + totalCount);
			
			return RepeatStatus.FINISHED;
			
		};
		
		return tasklet;
		
	}
	
	public Tasklet testTasklet3() {

		
		Tasklet tasklet = (contribution, chunkContext) -> {
	
			System.out.println("Step3. API 호출 결과를 테이블에 저장하기.");
			
			StepContext context = chunkContext.getStepContext();
			ExecutionContext executionContext = context
												.getStepExecution()
												.getJobExecution()
												.getExecutionContext();
			
			String resultCode = executionContext.get("resultCode").toString();
			String resultMsg = executionContext.get("resultMsg").toString();
			String totalCount = executionContext.get("totalCount").toString();
			
			int totalCountNum = Integer.parseInt(totalCount);
			
			ApiResult apiResult = ApiResult.builder()
											.resultCode(resultCode)
											.resultMsg(resultMsg)
											.totalCount(totalCountNum)
											.build();
			
			apiResultRepository.save(apiResult);
			
			return RepeatStatus.FINISHED;
			
		};
		
		return tasklet;
		
	}
	
	
	public String getWeather() throws IOException {
		
		String key = "fFHyCnW6Ajh7TVluu9LXHRRTmcd2MmOWhtntfo%2BOORbvL7F16U9Il%2Bp%2BSP1lOskAQr8mM0sINJsHan8083WTDA%3D%3D";
		String dataType = "JSON";
		String regID = "11B20201";
		
		StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/VilageFcstMsgService/getLandFcst"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=" + key); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("10", "UTF-8")); /*한 페이지 결과 수*/
        urlBuilder.append("&" + URLEncoder.encode("dataType","UTF-8") + "=" + URLEncoder.encode(dataType, "UTF-8")); /*요청자료형식(XML/JSON) Default: XML*/
        urlBuilder.append("&" + URLEncoder.encode("regId","UTF-8") + "=" + URLEncoder.encode(regID, "UTF-8")); /*11A00101(백령도), 11B10101 (서울), 11B20201(인천) 등... 별첨 엑셀자료 참조(‘육상’ 구분 값 참고)*/
        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        System.out.println("Response code: " + conn.getResponseCode());
        BufferedReader rd;
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();
        System.out.println(sb.toString());
        
        return sb.toString();
		
	}

}
