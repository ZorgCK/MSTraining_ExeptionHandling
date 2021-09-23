package one.microstream.controller;

import java.util.List;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import one.microstream.domain.Book;
import one.microstream.storage.DB;
import one.microstream.utils.BinaryPersistenceReloader;
import one.microstream.utils.MockupUtils;


@Controller("/books")
public class BookController
{
	@Get("/create")
	public HttpResponse<?> createBooks()
	{
		List<Book> allCreatedBooks = MockupUtils.loadMockupData();
		
		DB.root.getBooks().addAll(allCreatedBooks);
		DB.storageManager.store(DB.root.getBooks());
		
		return HttpResponse.ok("Books successfully created!");
	}
	
	@Get
	public List<Book> getBook()
	{
		return DB.root.getBooks();
	}
	
	@Get("/clear")
	public HttpResponse<?> clearBooks()
	{
		DB.root.getBooks().clear();
		DB.storageManager.store(DB.root.getBooks());
		
		return HttpResponse.ok("Books successfully cleared!");
	}
	
	@Get("/updateNonStore")
	public HttpResponse<?> updateBookNonStore()
	{
		Book book =
			DB.root.getBooks().stream().filter(b -> b.getIsbn().equalsIgnoreCase("498123138-5")).findFirst().get();
		String oldname = book.getName();
		book.setName("Java, The Good Parts");
		
		return HttpResponse.ok("Name of book successfully changed from " + oldname + " to " + book.getName());
	}
	
	@Get("/updateNonStoreDeep")
	public HttpResponse<?> updateAuthorNonStore()
	{
		Book book =
			DB.root.getBooks().stream().filter(b -> b.getIsbn().equalsIgnoreCase("498123138-5")).findFirst().get();
		String oldname = book.getAuthor().getLastname();
		book.getAuthor().setLastname("John Travolta");
		
		return HttpResponse.ok(
			"Name of author successfully changed from " + oldname + " to " + book.getAuthor().getLastname());
	}
	
	@Get("/rollbackFlat")
	public HttpResponse<?> rollbackBookFlat()
	{
		Book book =
			DB.root.getBooks().stream().filter(b -> b.getIsbn().equalsIgnoreCase("498123138-5")).findFirst().get();
		System.out.println(book.getName());
		
		final BinaryPersistenceReloader reloader =
			BinaryPersistenceReloader.New(DB.storageManager.persistenceManager());
		
		reloader.reloadFlat(book);
		System.out.println(book.getName());
		
		return HttpResponse.ok("Book successfully rollbacked!");
	}
	
	@Get("/rollbackDeep")
	public HttpResponse<?> rollbackBookDeep()
	{
		Book book =
			DB.root.getBooks().stream().filter(b -> b.getIsbn().equalsIgnoreCase("498123138-5")).findFirst().get();
		System.out.println(book.getAuthor().getLastname());
		
		final BinaryPersistenceReloader reloader =
			BinaryPersistenceReloader.New(DB.storageManager.persistenceManager());
		
		try
		{
			reloader.reloadDeep(book);
		}
		catch(Exception e)
		{
			System.out.println(book.getAuthor().getLastname());
		}
		
		return HttpResponse.ok("Author successfully rollbacked!");
	}
}