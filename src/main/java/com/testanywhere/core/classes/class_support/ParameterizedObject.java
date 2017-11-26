package com.testanywhere.core.classes.class_support;

import com.testanywhere.core.utilities.class_support.BaseClass;
import com.testanywhere.core.utilities.class_support.Cast;
import com.testanywhere.core.utilities.class_support.ClassWrapper;
import com.testanywhere.core.utilities.class_support.functional_support.ListFunctions;
import com.testanywhere.core.utilities.classes.Pair;
import com.testanywhere.core.utilities.exceptions.ObjectCreationException;
import com.testanywhere.core.utilities.logging.DisplayManager;
import com.testanywhere.core.utilities.logging.DisplayType;
import com.testanywhere.core.utilities.logging.OutputDisplay;
import com.testanywhere.core.utilities.logging.Tabbing;

import java.io.Externalizable;
import java.lang.reflect.Type;
import java.util.*;

public class ParameterizedObject extends OutputDisplay implements Externalizable
{
	private Collection<CompartmentObject<?>> constructorInputs;

	public ParameterizedObject()
	{
		super();
		this.initialize();
	}

	public ParameterizedObject( final Collection<CompartmentObject<?>> a )
	{
		this();
		this.setParameter(a);
	}

	public ParameterizedObject( final ParameterizedObject po )
	{
		this();
		if ( po == null ) throw new ObjectCreationException(ParameterizedObject.class);
		this.constructorInputs = copy(po.getParameterization());
	}

	@Override
	public String toString()
	{
		if ( this.getParameterization() == null ) return null;	
		return this.getParameterization().toString();
	}
	
	@Override
	public boolean equals( final Object other )
	{
		if (other == null) return false;

		ParameterizedObject otherpo = Cast.cast(other);
		if ( otherpo == null ) return false;
		
		if ( this.getParameterization() == null && otherpo.getParameterization() == null ) return true;
		if ( this.getParameterization().size() != otherpo.getParameterization().size() ) return false;
		for ( int i = 0; i < this.getParameterization().size(); ++i )
		{
			CompartmentObject<?> a = ((ArrayList<CompartmentObject<?>>) this.getParameterization()).get(i);
			CompartmentObject<?> b = ((ArrayList<CompartmentObject<?>>) otherpo.getParameterization()).get(i);

			if ( ! a.getClassType().equals(b.getClassType()) ) return false;
			if ( (a.getObject() != null && b.getObject() == null) || (a.getObject() == null && b.getObject() != null) ) return false;
			if ( a.getObject() != null )
				if ( b.getObject() != null && ! a.getObject().equals(b.getObject()) ) return false;
		}
		return true;
	}

	@Override
	public boolean isNull()
	{
		return (this.getNumberElements() == 0);
	}

	@Override
	public void buildObjectOutput(int numTabs)
	{
		if ( numTabs < 0 ) numTabs = 0;
		Tabbing tabEnvironment = new Tabbing(numTabs);
		DisplayManager dm = this.getDM();

		String outerSpacer = tabEnvironment.getSpacer();
		dm.append(outerSpacer + "Parameterized Object :", DisplayType.TEXTTYPES.LABEL);

		tabEnvironment.increment();
		String innerSpacer = tabEnvironment.getSpacer();
		int size = this.getNumberElements();

		dm.append(innerSpacer + "Signature : " + BaseClass.checkIsNull(this.getSignature()));
		dm.append(innerSpacer + "Size : " + size);

		ArrayList<Class<?>> ci = this.getClassTypes();

		if ( ci != null )
		{
			dm.append("");
			dm.append(innerSpacer + "Class Definitions :", DisplayType.TEXTTYPES.LABEL);
			tabEnvironment.increment();
			dm.addFormatLines(ListFunctions.asNumberedList( ci, tabEnvironment ));
			tabEnvironment.decrement();
		}

		ArrayList<Object> oi = this.getObjectTypes();

		if ( oi != null )
		{
			dm.append("");
			dm.append(innerSpacer + "Object Definitions :", DisplayType.TEXTTYPES.LABEL);
			tabEnvironment.increment();
			dm.addFormatLines(ListFunctions.asNumberedList( oi, tabEnvironment ));
			tabEnvironment.decrement();
		}
	}

	public int getNumberElements()
	{
		if ( this.getParameterization() == null ) return 0;
		if ( this.getClassTypes() == null ) return 0;
		return this.getClassTypes().size();
	}

	public Collection<CompartmentObject<?>> getParameterization()
	{
		return this.constructorInputs;
	}

	public ArrayList<Class<?>> getClassTypes()
	{
		if ( this.getParameterization() == null ) return null;
		ArrayList<Class<?>> clazzs = new ArrayList<>();
		for ( CompartmentObject<?> co : this.getParameterization() )
		{
			clazzs.add(co.getClassType());
		}
		return clazzs;
	}
	
	public ArrayList<Object> getObjectTypes() 
	{
		if ( this.getParameterization() == null ) return null;
		ArrayList<Object> objs = new ArrayList<>();
		for ( CompartmentObject<?> co : this.getParameterization() )
		{
			objs.add(co.getObject());
		}
		return objs;
	}
	
	public int getSignature() 
	{
		if ( this.isEmpty() ) return GeneralizedConstructor.NO_SIGNATURE;
		
		StringBuilder sb = new StringBuilder();
		for ( Class<?> cl : this.getClassTypes() )
		{
			sb.append(cl.getName());
		}
		this.debug("Signature to build : " + sb.toString());
		return sb.toString().hashCode();
	}
	
	public int getSignature( final List<Boolean> modifications )
	{
		if ( modifications == null ) return this.getSignature();
		
		List<Class<?>> cl = this.getClassTypes();
		
		StringBuilder sb = new StringBuilder();
		for ( Integer i = 0; i < cl.size(); ++i )
		{
			if ( ! modifications.get(i) )
				sb.append(cl.get(i).getName());
			else
				sb.append(ClassWrapper.convertFromWrapperType(cl.get(i)));
		}
		this.debug("Signature to build (after modifications applied : " + sb.toString());
		return sb.toString().hashCode();
	}

	public CompartmentObject<?> getPairing( final int id )
	{
		if ( id < 1 || id > this.getNumberElements() ) return null;
		return ((ArrayList<CompartmentObject<?>>) this.getParameterization()).get(id - 1);
	}

	public void addPairing( final Type type, final Object o )
	{
		if ( this.getParameterization() == null )
			this.constructorInputs = new ArrayList<>();

		Collection<CompartmentObject<?>> po_old = copy(this.getParameterization());

		if ( type == null ) return;

		this.getParameterization().add(new CompartmentObject<>((Class<?>) type, o));
		if ( ! this.validate() )
		{
			this.error("Unable to add latest contents since there was an problem");
			this.constructorInputs = po_old;
		}
	}

	public void addPairing( final CompartmentObject<?> o )
	{
		if ( this.getParameterization() == null )
			this.constructorInputs = new ArrayList<>();

		if ( o != null )
		this.getParameterization().add(o);
	}

	public void addPairings( final Collection<CompartmentObject<?>> addons )
	{
		this.getParameterization().addAll(addons);
	}

	public void removePairing( final Integer ... indices )
	{
		int numElements = this.getNumberElements();

		if ( indices.length < 1 || numElements < 1 ) return;

		Arrays.sort(indices);

		for ( int removeId : indices )
		{
			if ( removeId < 0 ) continue;
			if ( removeId > numElements ) break;

			((ArrayList<CompartmentObject<?>>) this.constructorInputs).set(removeId - 1, null);
		}

		Iterator<CompartmentObject<?>> iter = this.getParameterization().iterator();
		while ( iter.hasNext() )
		{
			if ( iter.next() == null ) iter.remove();
		}
	}

	public void setParameter( final Collection<CompartmentObject<?>> a )
	{
		this.constructorInputs = new ArrayList<>(a);
	}

	public void clear()
	{
		this.getParameterization().clear();
	}

	public boolean isEmpty()
	{
		return (this.getParameterization() == null || this.getParameterization().isEmpty());
	}
	
	public boolean validate() 
	{
		// There is nothing defined, so it is technically valid
		if ( this.getParameterization() == null || this.getParameterization().isEmpty() ) return true;
		
		// Access the array lists and validate each corresponding one.
		ArrayList<Class<?>> cal = this.getClassTypes();
		ArrayList<Object> oal   = this.getObjectTypes();
		
		// If the arrays are null, then this is still a qualifying condition 
		if ( cal == null && oal == null ) return true;
		
		// Loop over each corresponding element and validate the class type
		// with the object.  They should be synced in size since that is the ONLY
		// way they can be set.
		for ( int idx = 0; idx < oal.size(); idx++ ) 
		{
			if ( oal.get(idx) == null ) continue;
			Class<?> oalClazz = oal.get(idx).getClass();
			this.debug("Object class representation for idx [" + idx + "] = " + oalClazz.toString());
			this.debug("Stored class representation for idx [" + idx + "] = " + (cal != null ? cal.get(idx).toString() : null));
			if ( ! oalClazz.toString().equals(cal.get(idx).toString()) ) return false;
		}
		return true;
	}

	public static Collection<Object> getParameterizedObjects( final ParameterizedObject po )
	{
		if ( po == null ) return null;
		return po.getObjectTypes();
	}

	public static Collection<Object> getParameterizedObjects( final Collection<CompartmentObject<?>> alo )
	{
		if ( alo == null ) return null;

		Collection<Object> allObjects = new ArrayList<>();
		for ( CompartmentObject<?> co : alo )
		{
			if ( co != null ) allObjects.add(co.getObject());
		}
		return allObjects;
	}

	public static Object[] getParameterizedObjectsAsArray( final ParameterizedObject po )
	{
		if ( po == null ) return null;
		return po.getObjectTypes().toArray();
	}

	public static Object[] getParameterizedObjectsAsArray( final Collection<CompartmentObject<?>> alo )
	{
		if (alo == null) return null;
		return ParameterizedObject.getParameterizedObjects(alo).toArray();
	}

	public static Collection<Class<?>> getParameterizedClasses( final ParameterizedObject po )
	{
		if ( po == null ) return null;
		return po.getClassTypes();
	}

	public static Collection<Class<?>> getParameterizedClasses( final Collection<CompartmentObject<?>> alo )
	{
		if ( alo == null ) return null;

		Collection<Class<?>> allClasses = new ArrayList<>();
		for ( CompartmentObject<?> co : alo )
		{
			if ( co != null ) allClasses.add(co.getClassType());
		}
		return allClasses;
	}

	public static Class<?>[] getParameterizedClassesAsArray( final ParameterizedObject po )
	{
		if ( po == null ) return null;
		Class<?>[] classArray = new Class<?>[ po.getNumberElements() ];
		return po.getClassTypes().toArray(classArray);
	}

	public static Class<?>[] getParameterizedClassesAsArray( final Collection<CompartmentObject<?>> alo )
	{
		if (alo == null) return null;
		Class<?>[] classArray = new Class<?>[ alo.size() ];
		return ParameterizedObject.getParameterizedClasses(alo).toArray(classArray);
	}

	public static Pair<Integer, Integer> buildSignature( final Collection<CompartmentObject<?>> alo )
	{
		Pair<Integer, Integer> result = new Pair<>(0,0);

		if ( alo != null && ! alo.isEmpty() )
		{
			StringBuilder sb = new StringBuilder();

			int numberEntries = alo.size();
			result.setL(numberEntries);
			for ( CompartmentObject<?> o : alo )
			{
				if ( o != null ) sb.append(o.getClassType().getName());
			}
			result.setR(sb.toString().hashCode());
		}
		return result;
	}

	public static Pair<Integer, Integer> buildSignature( final ParameterizedObject po )
	{
		Pair<Integer, Integer> result = new Pair<>(0,0);
		if ( po != null )
		{
			result.setL(po.getNumberElements());
			result.setR(po.getSignature());
		}
		return result;
	}

	private void initialize()
	{
		this.constructorInputs = null;
	}
}