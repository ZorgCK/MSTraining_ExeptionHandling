package one.microstream.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import one.microstream.domain.Book;
import one.microstream.persistence.types.Storer;
import one.microstream.persistence.util.Reloader;
import one.microstream.storage.DB;
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
	
	@Get("/updateMultiBooks")
	public HttpResponse<?> updateMultiBooks()
	{
		List<Book> collect =
			DB.root.getBooks().stream().filter(b -> b.getName().startsWith("A")).collect(Collectors.toList());
		
		Storer ls = DB.storageManager.createLazyStorer();
		
		try
		{
			collect.forEach(b ->
			{
				b.setPrice(b.getPrice().add(new BigDecimal(10)));
				ls.store(b);
			});
			
			ls.commit();
		}
		catch(Exception e)
		{
			Reloader reloader = Reloader.New(DB.storageManager.persistenceManager());
			collect.forEach(reloader::reloadFlat);
		}
		
		return HttpResponse.ok("Books successfully changed!");
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
		
		Reloader reloader = Reloader.New(DB.storageManager.persistenceManager());
		
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
		
		Reloader reloader = Reloader.New(DB.storageManager.persistenceManager());
		
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
	
	@Get("/rollbackImplExample")
	public HttpResponse<?> rollbackImplExample()
	{
		Storer ls = DB.storageManager.createLazyStorer();
		
		List<Book> filteredBooks =
			DB.root.getBooks().stream().filter(b -> b.getIsbn().startsWith("49")).collect(Collectors.toList());
		
		try
		{
			filteredBooks.forEach(b ->
			{
				b.setPrice(new BigDecimal(50.00));
				ls.store(b);
			});
			
			ls.commit();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
			Reloader reloader = Reloader.New(DB.storageManager.persistenceManager());
			
			filteredBooks.forEach(b -> reloader.reloadFlat(b));
			return HttpResponse.serverError("Update books failed - " + e.getMessage());
		}
		
		return HttpResponse.ok("Books successfully updated!");
	}
}
