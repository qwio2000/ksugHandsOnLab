package demo.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import demo.board.Board;
import demo.board.Post;
import demo.board.module.BoardService;
import demo.board.module.PostForm;
import demo.board.module.ResourceNotFoundException;

@RestController
@RequestMapping("/board")
public class BoardController {
	
	private BoardService boardService;
	private MessageSource messageSource;
	
	
	@RequestMapping("/{boardname}/info")
	public ResponseEntity<Board> boardInfo(@PathVariable String boardname){
		Board board = boardService.findBoard(boardname);
		return ResponseEntity.ok(board);
	}
	
	@RequestMapping(value="/{boardname}", method={ RequestMethod.GET, RequestMethod.HEAD })
	public ResponseEntity<List<Post>> listPosts(@PathVariable String boardname){
		List<Post> posts = boardService.findPosts(boardname);
		
		return ResponseEntity.ok(posts);
	}
	
	@RequestMapping(value="/{boardname}",method={RequestMethod.POST})
	public ResponseEntity<Post> createPost(@PathVariable String boardname, @Valid PostForm postForm){
		Post post = boardService.writePost(boardname, postForm);
		return ResponseEntity.ok(post);
	}
	
	@RequestMapping(value="/{boardname}/{postId}",method={RequestMethod.PUT})
	public ResponseEntity<Post> updatePost(@PathVariable String boardname, @PathVariable Long postId, @Valid PostForm postForm){
		Post post = boardService.editPost(postId, postForm);
		return ResponseEntity.ok(post);
	}
	
	@RequestMapping(value="/{boardname}/{postId}",method={RequestMethod.DELETE})
	public ResponseEntity<Post> deletePost(@PathVariable String boardname, @PathVariable Long postId){
		Post post = boardService.erasePost(postId);
		return ResponseEntity.ok(post);
	}
	
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<Map<String, Object>> handleBoardNotFoundException(ResourceNotFoundException e, Locale locale){
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", new Date());
		body.put("status", e.getStatus());
		body.put("error", e.getError());
		body.put("message", messageSource.getMessage(e.getCode(), e.getArgs(), "No message available", locale));
		
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
	}
	@ExceptionHandler(BindException.class)
	public ResponseEntity<Map<String, Object>> handleBindException(BindException e, Locale locale){
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", new Date());
		body.put("status", HttpStatus.BAD_REQUEST);
		body.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
		body.put("message", messageSource.getMessage("error.BindException", null, "No message available", locale));
		
		List<String> errors = new ArrayList<>();
		e.getAllErrors().forEach(error -> {
			errors.add(messageSource.getMessage(error, locale));
		});;
		body.put("errors", errors);
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
	}
	
	@Autowired
	public void setBoardService(BoardService boardService) {
		this.boardService = boardService;
	}
	@Autowired
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
}
