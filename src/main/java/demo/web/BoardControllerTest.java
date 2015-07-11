package demo.web;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.MatcherAssertionErrors;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import demo.KsugHandsOnLabApplication;
import demo.board.Post;
import demo.board.module.BoardNotFoundException;
import demo.board.module.BoardService;
import demo.board.module.PostForm;
import demo.board.module.PostNotFoundException;
import demo.board.module.ResourceNotFoundException;
 
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = KsugHandsOnLabApplication.class)
@WebIntegrationTest(randomPort = true)
public class BoardControllerTest {
	
	private static final String PATH_BOARDINFO = "/board/{boardname}/info";
	private static final String PATH_POST = "/board/{boardname}";
	private static final String PATH_UPDATE_POST = "/board/{boardname}/{postId}";
	
	private static final String TEST_BOARDNAME = "notice";
 
	
	@Value("${local.server.port}")
	private int serverPort;
	
	@Autowired
	private BoardService boardService;
	
	private TestRestTemplate restTemplate;
	
	@Before
	public void setUp() {
		this.restTemplate = new TestRestTemplate();
		this.restTemplate.getInterceptors().add((request, body, execution) -> {
			request.getHeaders().setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			
			return execution.execute(request, body);
		});
	}
	
	@Test
	public void 공지사항_게시판_정보_확인() {
		UriComponentsBuilder uriBuilder = uriBuilderFromPath(PATH_BOARDINFO);
 
		ResponseEntity<JsonNode> resposne = restTemplate.getForEntity(uriBuilder.buildAndExpand(TEST_BOARDNAME).toUri(), JsonNode.class);
		MatcherAssertionErrors.assertThat(resposne.getStatusCode(), Matchers.is(HttpStatus.OK));
		MatcherAssertionErrors.assertThat(resposne.getBody().path("name").asText(), Matchers.equalTo(TEST_BOARDNAME));
	}
	
	@Test
	public void 게시판이_없으면_BoardNotFoundException_발생() {
		UriComponentsBuilder uriBuilder = uriBuilderFromPath(PATH_BOARDINFO);
		
		ResponseEntity<JsonNode> response = restTemplate.getForEntity(uriBuilder.buildAndExpand("error").toUri(), JsonNode.class);
		System.out.println("status code : "+response.getStatusCode());
		MatcherAssertionErrors.assertThat(response.getStatusCode(), Matchers.equalTo(HttpStatus.NOT_FOUND));
		
		assertNotFound(response.getBody(), new BoardNotFoundException(TEST_BOARDNAME));
	}
	
	@Test
	public void 게시물_목록_요청() {
		boardService.writePost(TEST_BOARDNAME, new PostForm("tester", "Hi~ 1", "No Content"));
		boardService.writePost(TEST_BOARDNAME, new PostForm("tester", "Hi~ 2", "No Content"));
		
		List<Post> posts = boardService.findPosts(TEST_BOARDNAME);
		
		
		UriComponentsBuilder uriBuilder = uriBuilderFromPath(PATH_POST);
		
		ResponseEntity<ArrayNode> resposne = restTemplate.getForEntity(uriBuilder.buildAndExpand(TEST_BOARDNAME).toUri(), ArrayNode.class);
		MatcherAssertionErrors.assertThat(resposne.getStatusCode(), Matchers.is(HttpStatus.OK));
		MatcherAssertionErrors.assertThat(resposne.getBody().size(), Matchers.is(posts.size()));
	}
	
	@Test
	public void 게시물_쓰기_요청() {
		URI uriCreatePost = uriBuilderFromPath(PATH_POST).buildAndExpand(TEST_BOARDNAME).toUri();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		
		MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
		form.add("author", "tester");
		form.add("title", UUID.randomUUID().toString());
		form.add("content", UUID.randomUUID().toString());
		
		ResponseEntity<JsonNode> resposne = restTemplate.exchange(new RequestEntity<>(form, headers, HttpMethod.POST, uriCreatePost), JsonNode.class);
		MatcherAssertionErrors.assertThat(resposne.getStatusCode(), Matchers.is(HttpStatus.OK));
		
		assertPost(form, resposne.getBody());
	}
	
	@Test
	public void 게시물_수정_요청() {
		Post origin = boardService.writePost(TEST_BOARDNAME, new PostForm("tester", "Hi~", "No Content"));
		
		
		URI uriUpdatePost = uriBuilderFromPath(PATH_UPDATE_POST).buildAndExpand(TEST_BOARDNAME, origin.getId()).toUri();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		
		MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
		form.add("author", "tester");
		form.add("title", UUID.randomUUID().toString());
		form.add("content", UUID.randomUUID().toString());
		
		ResponseEntity<JsonNode> resposne = restTemplate.exchange(new RequestEntity<>(form, headers, HttpMethod.PUT, uriUpdatePost), JsonNode.class);
		MatcherAssertionErrors.assertThat(resposne.getStatusCode(), Matchers.is(HttpStatus.OK));
		
		assertPost(form, resposne.getBody());
	}
 
	private void assertPost(MultiValueMap<String, Object> form, JsonNode body) {
		MatcherAssertionErrors.assertThat(body.path("author").asText(), Matchers.equalTo(form.get("author").get(0)));
		MatcherAssertionErrors.assertThat(body.path("title").asText(), Matchers.equalTo(form.get("title").get(0)));
		MatcherAssertionErrors.assertThat(body.path("content").asText(), Matchers.equalTo(form.get("content").get(0)));
	}
	
	@Test
	public void 수정할_게시물이_없으면_PostNotFoundException_발생() {
		URI uriUpdatePost = uriBuilderFromPath(PATH_UPDATE_POST).buildAndExpand(TEST_BOARDNAME, Integer.MAX_VALUE).toUri();
		
		ResponseEntity<JsonNode> resposne = restTemplate.exchange(new RequestEntity<>(HttpMethod.PUT, uriUpdatePost), JsonNode.class);
		MatcherAssertionErrors.assertThat(resposne.getStatusCode(), Matchers.equalTo(HttpStatus.NOT_FOUND));
		
		assertNotFound(resposne.getBody(), new PostNotFoundException(Integer.MAX_VALUE));
	}
 
	private void assertNotFound(JsonNode body, ResourceNotFoundException resourceNotFoundException) {
		MatcherAssertionErrors.assertThat(body.path("status").asInt(), Matchers.is(resourceNotFoundException.getStatus().value()));
		MatcherAssertionErrors.assertThat(body.path("error").asText(), Matchers.equalTo(resourceNotFoundException.getError()));
		MatcherAssertionErrors.assertThat(body.has("message"), Matchers.is(true));
	}
	
	private UriComponentsBuilder uriBuilderFromPath(String path) {
		return UriComponentsBuilder.fromPath(path).scheme("http").host("localhost").port(serverPort);
	}
 
}

