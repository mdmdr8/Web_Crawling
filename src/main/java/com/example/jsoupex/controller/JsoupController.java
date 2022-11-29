package com.example.jsoupex.controller;

import java.util.Objects;

import javax.imageio.ImageTranscoder;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JsoupController {
	@GetMapping("/thumnail")
	public String thumnail() {
		final String inflearnUrl = "https://www.inflearn.com/courses/it-programming";
		Connection conn = Jsoup.connect(inflearnUrl);
		StringBuilder sb = new StringBuilder(); //여러개의 문자를 가져올떄  builder가 유리 /buffer
		try {
			Document document = conn.get();
			Elements imgUrlElements = document.getElementsByClass("swiper-lazy");
			for(Element elemnet : imgUrlElements) {
				
				sb.append(elemnet.attr("abs:src")+"<br>"); //abs는 앱솔루트라는 기능인데, src라는 속성을 가져오는 기능/// "<br>"정렬해서 보기 위해
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	@GetMapping("/title")
	public String course_title() {
		final String titleUrl = "https://www.inflearn.com/courses/it-programming";
		Connection con = Jsoup.connect(titleUrl);
		StringBuilder ti = new StringBuilder(); //여러개의 문자를 가져올떄  builder가 유리 /buffer
		try {
			Document document = con.get();
			Elements titleElements = document.select("a.course_card_front");//> 자식이라는 표현 
			for(Element title : titleElements) {
				
				//abs는 앱솔루트라는 기능인데, src라는 속성을 가져오는 기능//ti.append(title) 이렇게만 쓰면 엘리먼트를 가져와서 태그도 같이 가져옴
				final String starUrlString = title.attr("abs:href");
				Connection conn = Jsoup.connect(starUrlString);
					Document innerDoc = conn.get();
					Element ratingElement = innerDoc.selectFirst("div.dashboard-star__num");
					double rating = Objects.isNull(ratingElement)? 0.0:Double.parseDouble(ratingElement.text());
					rating = Math.round(rating*10)/10.0;
					ti.append(starUrlString +", 평점: " +rating + "<br>");
					
				}
			}
		 catch(Exception e) {
			e.printStackTrace();
		}
		return ti.toString();
	}

	
	@GetMapping("/price")
	public String course_price() {
		final String priceUrl = "https://www.inflearn.com/courses/it-programming";
		Connection con = Jsoup.connect(priceUrl);
		StringBuilder pr = new StringBuilder(); //여러개의 문자를 가져올떄  builder가 유리 /buffer
		try {
			Document document = con.get();
			Elements priceElements = document.getElementsByClass("price");//> 자식이라는 표현 
			for(Element price : priceElements) {
				String pricee = price.text();
				String realPrice = getRealPrice(pricee);
				String salePrice = getSalePrice(pricee);
				
				int nrealPrice = toInt(realPrice);
				int nsalePrice = toInt(salePrice);
				pr.append("가격: " +nrealPrice);
				if(nrealPrice !=nsalePrice)
					pr.append("&nbsp;할인가격:"+nsalePrice);
				pr.append("<br>");
//				pr.append(price.text()+"<br>"); //abs는 앱솔루트라는 기능인데, src라는 속성을 가져오는 기능//ti.append(title) 이렇게만 쓰면 엘리먼트를 가져와서 태그도 같이 가져옴
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return pr.toString();
	}
	
	private String getRealPrice(String price) {
		return price.split(" ")[0];
		
	}
	private String getSalePrice(String price) {
		String[] prices = price.split(" ");
		return prices.length ==1? prices[0]:prices[1];
	}
	
	private int toInt(String str ) {
		str=str.replaceAll("₩", "");
		str = str.replaceAll(",", "");
		return Integer.parseInt(str);
	}
	
	//강의자, 강의 부가설명, 기술스택
	@GetMapping("/etc")
	public String etc() {
		final String inflearnUrl = "https://www.inflearn.com/courses/it-programming";
		Connection conn = Jsoup.connect(inflearnUrl);
		StringBuilder sb = new StringBuilder(); //여러개의 문자를 가져올떄  builder가 유리 /buffer
		try {
			Document document = conn.get();
			Elements instructors = document.getElementsByClass("instructor");
			Elements descriptions = document.select("p.course_description");
			Elements skills = document.select("div.course_skills>span");
			
			for(int i=0; i<instructors.size();i++) { 
				String instructor = instructors.get(i).text();
				String description = descriptions.get(i).text();
				String skill = skills.get(i).text().replace("\\s", "");
				
				sb.append("강의자: "+instructor+ "<br>");
				sb.append("강의 부가설명: "+ description+ "<br>");
				sb.append( "기술스택: "+skill+ "<br><br>");
				
			}
				
		} catch(Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}


}

////강의 제목
//@RestController
//public class JsoupController2{


//}