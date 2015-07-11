package demo.board.module;

public class BoardNotFoundException extends ResourceNotFoundException {
	
	private final String boardname;

	public BoardNotFoundException(String boardname) {
		this.boardname = boardname;
	}
	
	@Override
	public Object[] getArgs(){
		return new Object[]{ boardname };
	}
	
	@Override
	public String getCode(){
		return "error.BoardNotFoundException";
	}
}
