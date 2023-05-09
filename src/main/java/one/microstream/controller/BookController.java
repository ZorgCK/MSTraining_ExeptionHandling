package one.microstream.controller;

import java.util.List;
import java.util.stream.Collectors;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import one.microstream.domain.Book;
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
		List<Book> collect = DB.root.getBooks().stream().filter(b -> b.getName().startsWith("A")).collect(Collectors.toList());
		
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
		
		return HttpResponse.ok();
	}
	
	@Get("/updateNonStoreDeep")
	public HttpResponse<?> updateAuthorNonStore()
	{
		
		return HttpResponse.ok();
	}
	
	@Get("/rollbackFlat")
	public HttpResponse<?> rollbackBookFlat()
	{
		
		return HttpResponse.ok("Book successfully rollbacked!");
	}
	
	@Get("/rollbackDeep")
	public HttpResponse<?> rollbackBookDeep()
	{
		
		return HttpResponse.ok("Author successfully rollbacked!");
	}
}
