package one.microstream;

import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.runtime.event.ApplicationShutdownEvent;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import one.microstream.storage.DB;

@Singleton
public class ApplicationHooks implements ApplicationEventListener<ApplicationShutdownEvent>
{
	@Inject private DB db;
	
	@Override
	public void onApplicationEvent(ApplicationShutdownEvent event)
	{
		System.out.println("Shutdown");
		db.getStorageManager().shutdown();
	}
}
