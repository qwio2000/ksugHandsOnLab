package demo.board;

import java.util.Date;

public class Post {
	
	private Long id;
 
	private String author;
	private String title;
	private String content;
	private Date createdAt;
	
	private Board owner;
 
	
	public Post(long id, String author, String title, String content, Board owner) {
		this.id = id;
		this.author = author;
		this.title = title;
		this.content = content;
		this.createdAt = new Date();
		this.owner = owner;
	}
 
	
	public Long getId() {
		return id;
	}
 
	public String getAuthor() {
		return author;
	}
 
	public String getTitle() {
		return title;
	}
 
	public String getContent() {
		return content;
	}
 
	public Date getCreatedAt() {
		return createdAt;
	}
 
	public Board getOwner() {
		return owner;
	}
	
	
	public Post update(String author, String title, String content) {
		this.author = author;
		this.title = title;
		this.content = content;
		
		return this;
	}
	
}
