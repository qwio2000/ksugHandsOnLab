package demo.board.module;

public class PostNotFoundException extends ResourceNotFoundException {

	private final long postId;

	public PostNotFoundException(long postId) {
		this.postId = postId;
	}
	
	@Override
	public Object[] getArgs() {
		return new Object[]{ postId };
	}
	
	@Override
	public String getCode(){
		return "error.PostNotFoundException";
	}
}
