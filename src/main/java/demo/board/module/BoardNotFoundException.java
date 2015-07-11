package demo.board.module;

public class BoardNotFoundException extends RuntimeException {
	
	private final String boardname;

	public BoardNotFoundException(String boardname) {
		this.boardname = boardname;
	}
	
	public Object[] getArgs(){
		return new Object[]{ boardname };
	}
	
}
