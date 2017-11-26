package com.testanywhere.core.classes.support.map;

import com.testanywhere.core.utilities.class_support.Cast;
import com.testanywhere.core.classes.class_support.CompartmentObject;
import com.testanywhere.core.utilities.classes.NullObject;
import com.testanywhere.core.utilities.exceptions.ObjectCreationException;
import com.testanywhere.core.utilities.logging.*;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

public class DynamicMap<T> extends OutputDisplay
{
	private Map<T, CompartmentObject<?>> dynamicData;

	private boolean allowKeyRemoval;
	private boolean allowNull;
	private boolean allowNullTranslation;
	private boolean isActive;

	public DynamicMap()
	{
		super();
		this.initialize();
	}

	public DynamicMap( final DynamicMap otherMap ) throws ObjectCreationException
	{
		this();
		if ( otherMap == null ) throw new ObjectCreationException(DynamicMap.class);

		this.allowKeyRemoval = otherMap.isAllowKeyRemoval();
		this.allowNull = otherMap.isAllowNull();
		this.allowNullTranslation = otherMap.allowNullTranslation;
		this.isActive = otherMap.isActive;

		this.dynamicData = copy(this.getMap());
	}

	@Override
	public String toString()
	{
		return this.dynamicData.toString();
	}

	@Override
	public boolean isNull() { return false; }

	@Override
	public void buildObjectOutput( int numTabs )
	{
		if ( numTabs < 0 ) numTabs = 0;
		Tabbing tabEnvironment = new Tabbing(numTabs);
		DisplayManager dm = this.getDM();

		String outerSpacer = tabEnvironment.getSpacer();
		dm.append(outerSpacer + "Dynamic Map : ", DisplayType.TEXTTYPES.LABEL);

		tabEnvironment.increment();
		String innerSpacer = tabEnvironment.getSpacer();

		dm.append(innerSpacer + "Allow Key Removal     : " + TextManager.StringRepOfBool(this.isAllowKeyRemoval(), "yn"));
		dm.append(innerSpacer + "Allow Null            : " + TextManager.StringRepOfBool(this.isAllowNull(), "yn"));
		dm.append(innerSpacer + "Allow NullTranslation : " + TextManager.StringRepOfBool(this.allowNullTranslation, "yn"));
		dm.append(innerSpacer + "Active                : " + TextManager.StringRepOfBool(this.isActive, "yn"));

		dm.append(innerSpacer + "Map Data Size : " + this.size());

		if ( ! this.getMap().isEmpty() ) {
			for (T key : this.getMap().keySet()) {
				CompartmentObject<?> objAtKey = this.getMap().get(key);
				if (!CompartmentObject.isNull(objAtKey))
					dm.append(innerSpacer + "Key : " + TextManager.specializeName(key.toString()) + TextManager.EOL + objAtKey.getPrettyPrint(tabEnvironment.numberTabs() + 1), DisplayType.TEXTTYPES.LABEL);
				else {
					NullObject no = new NullObject();
					dm.append(innerSpacer + "Key : " + TextManager.specializeName(key.toString()) + TextManager.EOL + no.getPrettyPrint(tabEnvironment.numberTabs() + 1), DisplayType.TEXTTYPES.LABEL);
				}
			}
		}
	}

	public Map<T, CompartmentObject<?>> getMap()
	{
		return this.dynamicData;
	}

	public<S> boolean put( final T key, final S data, final boolean allowRemoval )
	{
		return this.setData(key, new CompartmentObject<>(data), allowRemoval);
	}

	public<S> boolean put( final T key, final S data )
	{
		return this.setData(key, new CompartmentObject<>(data), this.isAllowKeyRemoval());
	}

	public<S> boolean setData( final T key, final S data )
	{
		return this.setData(key, new CompartmentObject<>(data), this.isAllowKeyRemoval());
	}

	public boolean setData( final T key, final CompartmentObject<?> data )
	{
		return this.setData(key, data, this.isAllowKeyRemoval());
	}

	public boolean setData( final T key, final CompartmentObject<?> data, final boolean allowRemoval )
	{
		if ( ! this.isValidKey(key) ) return false;

		// allow Element Nullification with Removal
		// internal	external	result
		// yes		no			no
		// yes		yes			yes
		// no		no			no
		// no		yes			yes
		if ( allowRemoval )
			if ( data == null || CompartmentObject.isNull(data.getObject()) ) return this.remove(key);
		else
			if ( data == null ) {
				if ( this.isAllowNull() )
					if (this.allowNullTranslation) this.getMap().put(key, new CompartmentObject<>(new NullObject()));
			}

		this.getMap().put(key, data);

		return true;
	}

	public boolean setData( final T key, final Class<?> valClazz, final Object val )
	{
		return this.setData(key, valClazz, val, this.isAllowKeyRemoval());
	}

	public boolean setData( final T key, final Class<?> valClazz, final Object val, final boolean allowRemoval )
	{
		if ( ! this.isValidKey(key) ) return false;

		if ( allowRemoval )
			if ( val == null || valClazz == null ) return this.remove(key);
		else
			if ( val == null || valClazz == null ) {
				if ( this.isAllowNull() )
					if (this.allowNullTranslation) this.getMap().put(key, new CompartmentObject<>(new NullObject()));
			}

		this.getMap().put(key, new CompartmentObject<>(valClazz, val));

		return true;
	}

	public boolean remove( final T key )
	{
		return this.removeData(key);
	}

	public boolean removeData( final T key ) {
		return this.isValidKey(key) && this.getMap().containsKey(key) && (this.getMap().remove(key) != null);
	}

	public int size() { return this.getMap().size(); }

	public int count()
	{
		int total = 0;

		Map<T, CompartmentObject<?>> map = this.getMap();
		for ( T key : map.keySet() )
		{
			try
			{
				Collection<CompartmentObject<?>> list = Cast.cast(map.get(key).getObject());
				if ( list == null ) continue;
				total += list.size();
			}
			catch ( ClassCastException e )
			{
				++total;
			}
		}
		return total;
	}

	public void clear() { this.getMap().clear(); }

	public boolean isEmpty()
	{
		return this.getMap().isEmpty();
	}
	
	public boolean containsKey(final T key)
	{
		return this.getMap().containsKey(key);
	}

	public boolean merge( final DynamicMap<T> otherData )
	{
		return this.merge(otherData, false);
	}

	public boolean merge( final DynamicMap<T> otherData, boolean allowReplace )
	{
		if ( otherData == null ) return false;
		Map<T, CompartmentObject<?>> map = this.getMap();

		for ( T key : otherData.getMap().keySet() )
		{
			if ( map.containsKey(key) )
			{
				if ( allowReplace )
				{
					CompartmentObject<?> tempExtract = otherData.getMap().get(key);
					if (tempExtract == null)
						map.put(key, new CompartmentObject<>(new NullObject()));
					else
						map.put(key, otherData.getMap().get(key));
				}
				else
				{
					Collection<CompartmentObject<?>> mergedSubTree = new LinkedList<>();
					mergedSubTree.add(map.get(key));
					mergedSubTree.add(otherData.getMap().get(key));
					map.put(key, new CompartmentObject<>(mergedSubTree));
				}
			}
			else
			{
				map.put(key, otherData.getMap().get(key));
			}
		}

		return true;
	}

	public CompartmentObject<?> getDataPair( final T key )
	{
		if ( ! this.isValidKey(key) ) return null;
		if ( this.getMap().containsKey(key) )
			return this.getMap().get(key);
		return null;
	}

	public<S> S getData( final T key, final S defVal )
	{
		if ( ! this.isValidKey(key) ) return defVal;
		if ( this.getMap() == null || this.getMap().isEmpty()) return defVal;
		if ( !this.containsKey(key) ) return defVal;

		try
		{
			if ( this.getDataPair(key) == null ) return defVal;
			if ( CompartmentObject.isNull(this.getDataPair(key).getObject()) ) return defVal;
			return Cast.cast(this.getDataPair(key).getObject());
		}
		catch (ClassCastException e)
		{
			return defVal;
		}
	}

	public<S> S getData( final T key )
	{
		return this.getData(key, null);
	}

	public<S> S get( final T key )
	{
		return this.getData(key, null);
	}

	public DynamicMap getSection( final T subsection )
	{
		Object o = Cast.safeCast(this.getData(subsection), DynamicMap.class);
		if ( o instanceof DynamicMap ) return (DynamicMap) o;
		return null;
	}

	public boolean isAllowKeyRemoval() { return this.allowKeyRemoval; }
	public boolean isAllowNull() { return this.allowNull; }

	public void allowKeyRemoval( final boolean allowRemoval ) { this.allowKeyRemoval = allowRemoval; }
	public void allowNullValues( final boolean allowNull ) { this.allowNull = allowNull; }
	public void translateNull( final boolean translate ) { this.allowNullTranslation = translate; }

	public void enable() { this.isActive = true; }
	public void disable() { this.isActive = false; }

	private boolean isValidKey( final T key )
	{
		if ( key == null ) return false;
		//String keyStr = Cast.safeCast(key, String.class);
		//if ( keyStr == null || ! TextManager.validString(keyStr) ) return false;
		return true;
	}

	private void initialize()
	{
		this.dynamicData = new TreeMap<>();
		this.allowKeyRemoval = true;
		this.allowNull = true;
		this.allowNullTranslation = true;
		this.isActive = true;
	}
}
