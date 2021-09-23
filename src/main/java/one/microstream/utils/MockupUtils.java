package one.microstream.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import io.micronaut.core.io.ResourceResolver;
import io.micronaut.core.io.scan.ClassPathResourceLoader;
import one.microstream.domain.Author;
import one.microstream.domain.Book;


public class MockupUtils
{
	public static List<Book> loadMockupData()
	{
		List<Book> books = new ArrayList<Book>();
		List<Author> authors = new ArrayList<Author>();
		
		ClassPathResourceLoader loader = new ResourceResolver().getLoader(ClassPathResourceLoader.class).get();
		Optional<URL> authorsURL = loader.getResource("mockup/Authors.json");
		Optional<URL> booksURL = loader.getResource("mockup/Books.json");
		
		JSONParser bookParser = new JSONParser();
		JSONParser authorParser = new JSONParser();
		
		try
		{
			FileReader authorReader = new FileReader(new File(authorsURL.get().getFile()));
			FileReader bookReader = new FileReader(new File(booksURL.get().getFile()));
			// Read JSON file
			Object authorJSON = authorParser.parse(authorReader);
			JSONArray authorList = (JSONArray)authorJSON;
			// Iterate over employee array
			authorList.forEach(emp ->
			{
				Author a = parseAuthorObject((JSONObject)emp);
				authors.add(a);
			});
			
			// Read JSON file
			Object bookJSON = bookParser.parse(bookReader);
			JSONArray bookList = (JSONArray)bookJSON;
			// Iterate over employee array
			bookList.forEach(emp ->
			{
				Book book = parseBookObject((JSONObject)emp, authors);
				books.add(book);
			});
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		catch(ParseException e)
		{
			e.printStackTrace();
		}
		
		return books;
	}
	
	private static Book parseBookObject(JSONObject book, List<Author> authors)
	{
		Book b = new Book();
		
		b.setIsbn((String)book.get("isbn"));
		b.setName((String)book.get("name"));
		
		String release = (String)book.get("release");
		LocalDate date = LocalDate.parse(release, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
		b.setRelease(date);
		
		Double price = (Double)book.get("price");
		b.setPrice(new BigDecimal(price));
		
		Long authorID = (Long)book.get("author");
		b.setAuthor(authors.stream().filter(a -> a.getId().equals(authorID.toString())).findFirst().get());
		
		return b;
	}
	
	private static Author parseAuthorObject(JSONObject author)
	{
		Author a = new Author();
		
		a.setId(((Long)author.get("id")).toString());
		a.setLastname((String)author.get("last_name"));
		a.setFirstname((String)author.get("first_name"));
		a.setEmail((String)author.get("email"));
		a.setGender((String)author.get("gender"));
		
		return a;
	}
}
