
package one.microstream.utils;

import java.util.function.Consumer;

import one.microstream.X;
import one.microstream.persistence.binary.types.Binary;
import one.microstream.persistence.types.PersistenceLoader;
import one.microstream.persistence.types.PersistenceManager;
import one.microstream.reference.Swizzling;
import one.microstream.util.traversing.ObjectGraphTraverser;


public interface BinaryPersistenceReloader
{
	public Object reloadFlat(Object instance);
	
	public Object reloadDeep(Object instance);
	
	public static BinaryPersistenceReloader New(final PersistenceManager<Binary> persistenceManager)
	{
		return new BinaryPersistenceReloader.Default(
			X.notNull(persistenceManager));
	}
	
	public static class Default implements BinaryPersistenceReloader
	{
		private final PersistenceManager<Binary> persistenceManager;
		
		Default(
			final PersistenceManager<Binary> persistenceManager)
		{
			super();
			this.persistenceManager = persistenceManager;
		}
		
		private Object reloadObject(
			final Object instance,
			final PersistenceLoader loader)
		{
			final long oid;
			return Swizzling.isFoundId(oid = this.persistenceManager.lookupObjectId(instance))
				? loader.getObject(oid)
				: null;
		}
		
		@Override
		public Object reloadFlat(final Object instance)
		{
			X.notNull(instance);
			
			return this.reloadObject(
				instance,
				this.persistenceManager.createLoader());
		}
		
		@Override
		public Object reloadDeep(final Object instance)
		{
			X.notNull(instance);
			
			final long oid;
			if(Swizzling.isNotFoundId(oid = this.persistenceManager.lookupObjectId(instance)))
			{
				return null;
			}
			
			final PersistenceLoader loader = this.persistenceManager.createLoader();
			final Consumer<Object> logic = object -> this.reloadObject(object, loader);
			
			// reload references
			ObjectGraphTraverser.Builder().modeFull().acceptorLogic(logic).buildObjectGraphTraverser().traverse(
				instance);
			
			// reload instance
			return loader.getObject(oid);
		}
		
	}
	
}
