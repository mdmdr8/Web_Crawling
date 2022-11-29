package com.example.jsoupex.controller;

import java.util.Objects;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class inflearnController {
	private final int FIRST_PAGE_INDEX=1;
	private final int LAST_PAGE_INDEX=10;
	final String inflearnUrl = "https://www.inflearn.com/courses/it-programming?order=seq&page=";
	@GetMapping("/all")
	public String all() {
		int idx = 1;
		StringBuilder sb = new StringBuilder();
		try {
			for(int i=FIRST_PAGE_INDEX; i<=LAST_PAGE_INDEX; i++) {
				String url = inflearnUrl+i;
				System.out.println(url);
				Connection conn = Jsoup.connect(url);
				Document doc = conn.get();
				
				Elements imgUrlElems = doc.getElementsByClass("swiper-lazy");
				Elements titleElems = doc.select("div.card-content>div.course_title");
				Elements priceElems = doc.getElementsByClass("price");
				Elements instructorElems = doc.getElementsByClass("instructor");
				Elements linkElems = doc.select("a.course_card_front");
				Elements descriptionElems = doc.select("p.course_description");
				Elements skillElems = doc.select("div.course_skills>span");
				
				for(int j=0; j<imgUrlElems.size(); j++) {
					String imgUrl = imgUrlElems.get(j).attr("abs:src");
					String title = titleElems.get(j).text();
					String price = priceElems.get(j).text();
					int realPrice=0, salePrice=0; 
					if(price.equals("무료")==false) {
						realPrice = toInt(getRealPrice(price).replace("\\W", ""));
						salePrice = toInt(getSalePrice(price).replace("\\W", ""));
					}
					String instructor = instructorElems.get(j).text();
					String innerUrl = linkElems.get(j).attr("abs:href");
					String description = descriptionElems.get(j).text();
					String skills = skillElems.get(j).text().replace("\\s", "");
					
					System.out.println(innerUrl);
					Connection innerConn = Jsoup.connect(innerUrl);
					Document innerDoc = innerConn.get();
					Element ratingElem = innerDoc.selectFirst("div.dashboard-star__num");
					double rating = Objects.isNull(ratingElem)?
										0.0:
										Double.parseDouble(ratingElem.text());
					rating = Math.round(rating*10)/10.0;
					sb.append("순서: "+idx++ +"<br>");
					sb.append("썸네일: "+imgUrl+"<br>");
					sb.append("강의 제목: "+title+"<br>");
					sb.append("가격: "+realPrice+"<br>");
					if(realPrice!=salePrice) sb.append("할인 가격: "+salePrice+"<br>");
					sb.append("강의자: "+instructor+"<br>");
					sb.append("강의 링크: "+innerUrl+"<br>");
					sb.append("강의 설명: "+description+"<br>");
					sb.append("기술 스택: "+skills+"<br>");
					sb.append("평점: "+rating+"<br><br>");
				}
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	private String getRealPrice(String price) {
		return price.split(" ")[0];
	}

	private String getSalePrice(String price) {
		String[] prices = price.split(" ");
		return prices.length == 1 ? prices[0] : prices[1];
	}

	private int toInt(String str) {
		str = str.replaceAll("\\W", ""); 
		str = str.replaceAll(",", "");
		return Integer.parseInt(str);
	}	
}
