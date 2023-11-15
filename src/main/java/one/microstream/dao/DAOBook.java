package one.microstream.dao;

import java.math.BigDecimal;
import java.util.List;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import one.microstream.domain.Book;
import one.microstream.persistence.types.Storer;
import one.microstream.persistence.util.Reloader;
import one.microstream.storage.DB;


@Singleton
public class DAOBook
{
	@Inject DB db;
	
	public void addBooks(List<Book> books)
	{
		db.getRoot().getBooks().addAll(books);
		db.store(db.getRoot().getBooks());
	}
	
	public void clearBooks()
	{
		db.getRoot().getBooks().clear();
		db.store(db.getRoot().getBooks());
	}
	
	public List<Book> books()
	{
		return db.getRoot().getBooks();
	}
	
	public void updateMultiBooks(List<Book> books)
	{
		Storer ls = db.getStorageManager().createLazyStorer();
		
		try
		{
			books.forEach(b ->
			{
				b.setPrice(new BigDecimal(50.00));
				ls.store(b);
			});
			
			ls.commit();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
			Reloader reloader = Reloader.New(db.getStorageManager().persistenceManager());
			
			books.forEach(b -> reloader.reloadFlat(b));
		}
	}
	
	public String updateBookNonStore()
	{
		Book book =
			db.getRoot().getBooks().stream().filter(b -> b.getIsbn().equalsIgnoreCase("498123138-5")).findFirst().get();
		String oldname = book.getName();
		book.setName("Java, The Good Parts");
		
		return "Name of book successfully changed from " + oldname + " to " + book.getName();
	}
	
	public String updateAuthorNonStore()
	{
		Book book =
			db.getRoot().getBooks().stream().filter(b -> b.getIsbn().equalsIgnoreCase("498123138-5")).findFirst().get();
		String oldname = book.getAuthor().getLastname();
		book.getAuthor().setLastname("Travolta");
		
		return "Name of author successfully changed from " + oldname + " to " + book.getAuthor().getLastname();
	}
	
	public String rollbackBookFlat()
	{
		Book book =
			db.getRoot().getBooks().stream().filter(b -> b.getIsbn().equalsIgnoreCase("498123138-5")).findFirst().get();
		
		Reloader reloader = Reloader.New(db.getStorageManager().persistenceManager());
		
		reloader.reloadFlat(book);
				
		return "Book successfully rollbacked!";
		
	}
	
	public String rollbackBookDeep()
	{
		Book book =
			db.getRoot().getBooks().stream().filter(b -> b.getIsbn().equalsIgnoreCase("498123138-5")).findFirst().get();
				
		Reloader reloader = Reloader.New(db.getStorageManager().persistenceManager());
		
		reloader.reloadDeep(book);
		
		return "Author successfully rollbacked!";
	}
}
