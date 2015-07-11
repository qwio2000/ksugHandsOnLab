package demo.web;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ConfigurationProperties("service.info")//application.properties 파일에 존재하는 데이터를 가져오기 위함
public class ServiceInfoController {
	
	private String name;
	private String version;

	@RequestMapping("/")
	public ResponseEntity<Map<String, String>> info(){
		Map<String, String> info = new LinkedHashMap<>();
		info.put("name", name);
		info.put("version", version);
		
		return ResponseEntity.status(HttpStatus.OK).body(info);
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
	
}
