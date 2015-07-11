package demo.board.module;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import demo.board.Board;
import demo.board.Post;

@Service
public class BoardService {

	private Map<String, Board> boards = new LinkedHashMap<>();
	private List<Post> posts = new ArrayList<>();
	
	@PostConstruct
	public void init(){
		this.boards.put("notice", new Board("notice"));
		this.boards.put("random", new Board("random"));
		
		this.posts.add(new Post(0, "tester", "no title", "no content", boards.get("notice")));
	}

	public Board findBoard(String boardname) {
		Board board = boards.get(boardname);
		if(Objects.isNull(board)){
			throw new BoardNotFoundException(boardname);
		}
		return board;
	}

	public List<Post> findPosts(String boardname) {
		Board board = findBoard(boardname);
		
		return posts.stream() //POST 리스트를 스트림으로 변환한후 필터를 통해서 boardname이 일치하는 것들을 List로 만들어서 리턴
				.filter(post -> board.equals(post.getOwner()))
				.collect(Collectors.toList());
	}
	
}
