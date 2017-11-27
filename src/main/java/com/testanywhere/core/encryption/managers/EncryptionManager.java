package com.testanywhere.core.encryption.managers;

import com.testanywhere.core.utilities.class_support.Cast;
import com.testanywhere.core.classes.class_support.ParameterizedObject;
import com.testanywhere.core.encryption.utilities.EncryptionUtils;
import com.testanywhere.core.classes.support.map.DynamicMap;
import com.testanywhere.core.encryption.factories.EncryptionFactory;
import com.testanywhere.core.encryption.plugins.encryption.EncryptionStrategy;
import com.testanywhere.core.encryption.plugins.encryption.NullEncryptionStrategy;
import com.testanywhere.core.utilities.logging.TextManager;
import com.testanywhere.core.classes.managers.ABCManager;
import com.testanywhere.core.classes.managers.Registrar;

import java.util.Map;

@SuppressWarnings("ExternalizableWithoutPublicNoArgConstructor")
public class EncryptionManager extends ABCManager
{
	static
	{
		Registrar.getInstance().addManagerPath(EncryptionManager.class.getPackage().getName());
	}

	private static class EncryptionManagerHolder
	{
		public static final EncryptionManager INSTANCE = new EncryptionManager();
	}

	public static EncryptionManager getInstance()
	{
		return EncryptionManagerHolder.INSTANCE;
	}

	@Override
	public void buildObjectOutput( int numTabs )
	{
		if ( numTabs < 0 ) numTabs = 0;
		super.buildObjectOutput(numTabs);
	}

	// Get methods
	// --------------------------
	@Override
	public EncryptionFactory getFactory()
	{
		return (EncryptionFactory) super.getFactory();
	}

	public boolean removeEncryptionMethod( String name, String type, String padding ) {
		String cipherID = EncryptionUtils.buildCipher(name, type, padding);
		return this.getEncryptionMap().containsKey(cipherID) && (this.getEncryptionMap().remove(cipherID));
	}
	
	public EncryptionStrategy getEncryption(String name, String type, String padding )
	{
		String cipherID = EncryptionUtils.buildCipher(name, type, padding);
		
		if ( this.getEncryptionMap().containsKey(cipherID) )
			return this.getEncryptionMap().get(cipherID);
		
		ParameterizedObject po = new ParameterizedObject();
		if ( ! TextManager.validString(name) )
			name = "UNKNOWN";
		if ( TextManager.validString(type) )
			po.addPairing(type.getClass(), type);
		if ( TextManager.validString(padding) )
			po.addPairing(padding.getClass(), padding);

		EncryptionStrategy found = Cast.cast(this.getFactory().instantiate(EncryptionUtils.makeEncryptionMethodName(name), po.getParameterization()));
		
		if ( found == null )
			found = new NullEncryptionStrategy();

		this.getEncryptionMap().put(cipherID, found);
		return found;
	}

	public int getNumberCachedEncryptionMethods()
	{
		return this.getFactory().getFactoryMap().size();
	}

	// Mutable Methods
	// --------------------------
	@Override
	public void reset() 
	{
		this.getFactory().reset();
		this.setValid(true);
	}

	@Override
	public void cleanup()
	{}

	private EncryptionManager()
	{
		Class<?> clazz = EncryptionManager.class;

		super.setManagerType(clazz);
		super.setManagerName(clazz.getSimpleName());
		super.configure();

		this.__initialize();
		this.getFactory().findFactoryClasses();
	}

	private DynamicMap<String> getEncryptionMap()
	{
		return this.getFactory().getFactoryMap();
	}
	
	private Map<String, String> getEncryptionNameMap()
	{
		return this.getFactory().getFactoryNameMap();
	}

	/*private void findManagerClasses()
	{
		String locationEM = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
		String relativePath = ((EncryptionLoader) this.getFactory().getLoader()).getPackageID();

		Map<String, String> foundManagerClasses = ManagerUtils.findClassTypes(locationEM, relativePath, EncryptionUtils.STRATEGYNAME);
		if ( foundManagerClasses != null && foundManagerClasses.size() > 0 )
			for ( String s : foundManagerClasses.keySet() )
				this.encryptionNameMap.put(s, foundManagerClasses.get(s));
	}*/

	private void __initialize()
	{
		this.setFactory( new EncryptionFactory() );
	}
}
