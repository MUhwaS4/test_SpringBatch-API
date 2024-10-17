package com.example.demo.openapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
public class OpenAPITest {
	
	// 기상청 동네예보 서비스 API 호출
	// 응답 데이터 클래스 변환 후 "결과 코드/결과 메시지/전체 결과 수" 출력
	
	String key = "개인 인증키";
	String dataType = "JSON";
	String regID = "11B20201";
	
	@Test
	public String getWeather() throws IOException {
		
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
	
	@Test
	public void jsonToDto() throws IOException {
		
		ObjectMapper mapper = new ObjectMapper();
		
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		String weather = getWeather();
		
		Root root = null;
		
		root = mapper.readValue(weather, Root.class);
		
		System.out.println("문제 1. 공공데이터 포털에서 제공하는 “기상청 동네예보 서비스” API를 호출.");
		
		System.out.println("결과 코드: " + root.response.header.resultCode);
		
		System.out.println("결과 메시지: " + root.response.header.resultMsg);
		
		System.out.println("전체 결과수: " + root.response.body.totalCount);
		
	}
	

}
