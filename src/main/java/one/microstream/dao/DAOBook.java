package one.microstream.dao;

import java.math.BigDecimal;
import java.util.List;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import one.microstream.domain.Book;
import one.microstream.persistence.types.Storer;
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
		}
	}
	
}
