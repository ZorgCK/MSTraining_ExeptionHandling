package one.microstream.controller;

import java.util.List;
import java.util.stream.Collectors;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import jakarta.inject.Inject;
import one.microstream.dao.DAOBook;
import one.microstream.domain.Book;
import one.microstream.utils.MockupUtils;


@Controller("/books")
public class BookController
{
	@Inject private DAOBook dao;
	
	@Get("/create")
	public HttpResponse<?> createBooks()
	{
		List<Book> allCreatedBooks = MockupUtils.loadMockupData();
		
		dao.addBooks(allCreatedBooks);
		
		return HttpResponse.ok("Books successfully created!");
	}
	
	@Get
	public List<Book> getBooks()
	{
		return dao.books();
	}
	
	@Get("/clear")
	public HttpResponse<?> clearBooks()
	{
		dao.clearBooks();
		
		return HttpResponse.ok("Books successfully cleared!");
	}
	
	@Get("/updateMulti")
	public HttpResponse<?> rollbackImplExample()
	{
		List<Book> filteredBooks =
			dao.books().stream().filter(b -> b.getIsbn().startsWith("49")).collect(Collectors.toList());
		
		dao.updateMultiBooks(filteredBooks);
		
		return HttpResponse.ok("Books successfully updated!");
	}
	
	@Get("/updateBookNonStore")
	public HttpResponse<?> updateBookNonStore()
	{
		String result = dao.updateBookNonStore();
		
		return HttpResponse.ok(result);
	}
	
	@Get("/updateAuthorNonStore")
	public HttpResponse<?> updateAuthorNonStore()
	{
		String result = dao.updateAuthorNonStore();
		
		return HttpResponse.ok(result);
	}
	
	@Get("/rollbackFlat")
	public HttpResponse<?> rollbackBookFlat()
	{
		String result = dao.rollbackBookFlat();
		
		return HttpResponse.ok(result);
	}
	
	@Get("/rollbackDeep")
	public HttpResponse<?> rollbackBookDeep()
	{
		String result = dao.rollbackBookDeep();
		
		return HttpResponse.ok(result);
	}
}
