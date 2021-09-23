package one.microstream.domain;

import java.math.BigDecimal;
import java.time.LocalDate;


public class Book
{
	private String		isbn;
	private String		name;
	private LocalDate	release;
	private BigDecimal	price;
	private Author		author;
	
	public Book()
	{
		super();
	}
	
	public Book(String isbn, String name, LocalDate release, BigDecimal price, Author author)
	{
		super();
		this.isbn = isbn;
		this.name = name;
		this.price = price;
		this.author = author;
	}
	
	public String getIsbn()
	{
		return isbn;
	}
	
	public void setIsbn(String isbn)
	{
		this.isbn = isbn;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public BigDecimal getPrice()
	{
		return price;
	}
	
	public void setPrice(BigDecimal price)
	{
		this.price = price;
	}
	
	public Author getAuthor()
	{
		return author;
	}
	
	public void setAuthor(Author author)
	{
		this.author = author;
	}
	
	public LocalDate getRelease()
	{
		return release;
	}
	
	public void setRelease(LocalDate release)
	{
		this.release = release;
	}
	
}
