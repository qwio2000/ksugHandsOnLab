package demo.web;

import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.HttpPutFormContentFilter;

@Configuration
public class WebConfig {
	
	
	/*
	 * ������ ������ �����־�� �ϴ� ��쿡��
	 * FilterRegistrationBean �� �̿��Ͽ� Order�� �������ָ� �ȴ�
	 */
	
/*	@Bean
	public HttpPutFormContentFilter httpPutFormContentFilter(){//���� ���� ���� �ڵ����� �������Ϳ� ��ϵ�
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
