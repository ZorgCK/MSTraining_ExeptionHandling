package one.microstream.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.Context;
import jakarta.inject.Inject;
import one.microstream.storage.embedded.configuration.types.EmbeddedStorageConfiguration;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;


@Context
public class DB
{
	 @Inject ApplicationContext context;
	
	private final Logger					LOG		= LoggerFactory.getLogger(DB.class);
	private final EmbeddedStorageManager	storageManager;
	private final DataRoot					root	= new DataRoot();
	
	public DB()
	{
		super();
		
		System.out.println("init");
		
		storageManager = EmbeddedStorageConfiguration.Builder().setChannelCount(2).setStorageDirectory(
			"data").createEmbeddedStorageFoundation().createEmbeddedStorageManager(root).start();
	}
	
	public EmbeddedStorageManager getStorageManager()
	{
		return storageManager;
	}
	
	public DataRoot getRoot()
	{
		return root;
	}
	
	public void store(final Object object)
	{
		try
		{
			storageManager.store(object);
		}
		catch(final Throwable t)
		{
			onStorageFailure(t);
		}
	}
	
	public void storeAll(final Object... objects)
	{
		try
		{
			storageManager.storeAll(objects);
		}
		catch(final Throwable t)
		{
			onStorageFailure(t);
		}
	}
	
	public void storeAll(Iterable<?> iterable)
	{
		try
		{
			storageManager.storeAll(iterable);
		}
		catch(final Throwable t)
		{
			onStorageFailure(t);
		}
	}
	
	private void onStorageFailure(final Throwable t)
	{
		if(storageManager != null && storageManager.isRunning())
		{
			try
			{
				LOG.error("Storage error! Shutting down storage...", t);
				context.stop();
			}
			catch(final Throwable tt)
			{
				tt.printStackTrace();
			}
		}
		
		root.clear();
	}	
}
