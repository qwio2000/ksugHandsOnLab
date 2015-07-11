package demo.web;

import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.HttpPutFormContentFilter;

@Configuration
public class WebConfig {
	
	
	/*
	 * 필터의 순서도 정해주어야 하는 경우에는
	 * FilterRegistrationBean 을 이용하여 Order를 설정해주면 된다
	 */
	
/*	@Bean
	public HttpPutFormContentFilter httpPutFormContentFilter(){//별도 설정 없이 자동으로 서블릿필터에 등록됨
		return new HttpPutFormContentFilter();
	}*/
	
	@Bean
	public FilterRegistrationBean filterRegistrationBean(){
		FilterRegistrationBean bean = new FilterRegistrationBean();
		bean.setFilter(new HttpPutFormContentFilter());
		bean.setOrder(10);
		return bean;
	}
}
