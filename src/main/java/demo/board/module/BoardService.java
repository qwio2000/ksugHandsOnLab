package demo.board.module;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import demo.board.Board;
import demo.board.Post;

@Service
public class BoardService {

	private Map<String, Board> boards = new LinkedHashMap<>();
	private List<Post> posts = new ArrayList<Post>();

	private AtomicLong postNumberGenerator = new AtomicLong(0); // AtomicLong : Thread safe 하게 1씩 증가시켜주는 클래스

	@PostConstruct
	public void init() {
		this.boards.put("notice", new Board("notice"));
		this.boards.put("random", new Board("random"));

//		this.posts.add(new Post(0, "tester", "no title,", "no content", boards.get("notice")));
	}

	public Board findBoard(String boardname) {
		Board board = boards.get(boardname);
		if (Objects.isNull(board)) {
			throw new BoardNotFoundException(boardname);
		}

		return board;
	}

	public List<Post> findPosts(String boardname) {
		Board board = findBoard(boardname);

		return posts.stream()
				.filter(post -> board.equals(post.getOwner()))
				.collect(Collectors.toList());
	}

	public Post writePost(String boardname, PostForm postForm) {
		Board board = findBoard(boardname);
		Post post = new Post(postNumberGenerator.incrementAndGet(), postForm.getAuthor(), postForm.getTitle(), postForm.getContent(), board);

		posts.add(post);

		return post;
	}

	public Post editPost(long postId, PostForm postForm) {
		return posts.stream().filter(_post -> postId == _post.getId()).findAny()//findAny() : 찾아라
				.orElseThrow(() -> new PostNotFoundException(postId)).update(postForm.getAuthor(), postForm.getTitle(), postForm.getContent());//없으면 PostNotFoundException 있으면 update
	}

	public Post erasePost(long postId) {
		Post post = posts.stream().filter(_post -> postId == _post.getId()).findAny()
				.orElseThrow(() -> new PostNotFoundException(postId));

		posts.remove(post);

		return post;
	}

}