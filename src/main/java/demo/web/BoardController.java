package demo.web;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import demo.board.Board;
import demo.board.Post;
import demo.board.module.BoardNotFoundException;
import demo.board.module.BoardService;

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
	
	@RequestMapping("/{boardname}")
	public ResponseEntity<List<Post>> listPosts(@PathVariable String boardname){
		List<Post> posts = boardService.findPosts(boardname);
		
		return ResponseEntity.ok(posts);
	}
	
	@ExceptionHandler(BoardNotFoundException.class)
	public ResponseEntity<Map<String, Object>> handleBoardNotFoundException(BoardNotFoundException e, Locale locale){
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", new Date());
		body.put("status", HttpStatus.NOT_FOUND.value());
		body.put("error", HttpStatus.NOT_FOUND.getReasonPhrase());
		body.put("message", messageSource.getMessage("error.BoardNotFoundException", e.getArgs(), "No message available", locale));
		
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
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
