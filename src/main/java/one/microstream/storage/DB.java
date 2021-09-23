package one.microstream.storage;

import java.net.URL;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micronaut.core.io.ResourceResolver;
import io.micronaut.core.io.scan.ClassPathResourceLoader;
import one.microstream.storage.embedded.configuration.types.EmbeddedStorageConfiguration;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;


public class DB
{
	private static final Logger				LOG		= LoggerFactory.getLogger(DB.class);
	
	public static EmbeddedStorageManager	storageManager;
	public final static DataRoot			root	= new DataRoot();
	
	static
	{
		ClassPathResourceLoader loader = new ResourceResolver().getLoader(ClassPathResourceLoader.class).get();
		Optional<URL> resource = loader.getResource("microstream.xml");
		
		storageManager = EmbeddedStorageConfiguration.load(
			resource.get().getPath()).createEmbeddedStorageFoundation().createEmbeddedStorageManager(root).start();
	}
	
	public static synchronized void store(final Object object)
	{
		try
		{
			DB.storageManager.store(object);
		}
		catch(final Throwable t)
		{
			onStorageFailure(t);
		}
	}
	
	public static synchronized void storeAll(final Object... objects)
	{
		try
		{
			DB.storageManager.storeAll(objects);
		}
		catch(final Throwable t)
		{
			onStorageFailure(t);
		}
	}
	
	private static void onStorageFailure(final Throwable t)
	{
		if(DB.storageManager != null && DB.storageManager.isRunning())
		{
			try
			{
				DB.LOG.error("Storage error! Shutting down storage...", t);
				DB.storageManager.shutdown();
			}
			catch(final Throwable tt)
			{
				tt.printStackTrace();
			}
		}
		
		root.clear();
	}
}
