package com.testanywhere.core.machines.connections;

import com.testanywhere.core.classes.support.RollBack;
import com.testanywhere.core.classes.support.Tristate;
import com.testanywhere.core.machines.access.SingleLogin;
import com.testanywhere.core.utilities.classes.Pair;
import com.testanywhere.core.utilities.logging.DisplayManager;
import com.testanywhere.core.utilities.logging.OutputDisplay;
import com.testanywhere.core.utilities.logging.Tabbing;
import com.testanywhere.core.utilities.logging.TextManager;

import java.util.*;

@SuppressWarnings("ExternalizableWithoutPublicNoArgConstructor")
public class ConnectionCollector extends OutputDisplay
{
	public static Integer currentCID = 0;

	private static final Integer NO_AVAILABLE_CIDS = -1;
	private static final String DEFAULT_ALIAS = "alias";
	private static final int MAX_LOOPS_TO_FIND_CONNECTION = 3;
	
	private RollBack<ConnectionClient> latestConnection;
	private Map<UUID, ConnectionClient> openSessions;
	private Map<Integer, UUID> openSessionsMap;
	
	// This is a temporary holding site for login as part of generating a brand new connection
	// If you are "cloning" a connection, then the copy will contain the login and there is
	// no requirement to set this temporary field.
	private volatile SingleLogin seedSingleLogin;

	static
	{
		ConnectionConstants.getInstance();
	}

	private static class ConnectionCollectorHolder
	{
		public static final ConnectionCollector INSTANCE = new ConnectionCollector();
	}

	public static ConnectionCollector getInstance()
	{
		return ConnectionCollectorHolder.INSTANCE;
	}

	private ConnectionCollector()
	{
		super();
		this.initialize();
	}

	@Override
	public boolean isNull() {
		return false;
	}

	@Override
	public void buildObjectOutput(int numTabs)
	{
		if ( numTabs < 0 ) numTabs = 0;
		Tabbing tabEnvironment = new Tabbing(numTabs);
		DisplayManager dm = this.getDM();

		String outerSpacer = tabEnvironment.getSpacer();
		dm.append(outerSpacer + "Number Connections : " + this.size());
		
		tabEnvironment.increment();
		String innerSpacer = tabEnvironment.getSpacer();
		for ( Integer i : this.getConnectionMap().keySet() )
		{
			UUID uuid = this.getConnectionMap().get(i);
			dm.append(innerSpacer + "Connection " + i + " : ");
			ConnectionClient cc = this.getConnections().get(uuid);
			if ( cc == null ) continue;

			cc.buildObjectOutput(tabEnvironment.numberTabs());
			dm.addFormatLines(cc.getDM().getLines());
		}
	}

	public ConnectionClient getConnection(final UUID key )
	{
		return this.__getConnection(key);
	}
	
	public ConnectionClient getConnection(final Integer cid )
	{
		return this.__getConnection(cid);
	}
	
	public ConnectionClient getConnectionByAlias(final String alias )
	{
		for ( UUID entry : this.openSessions.keySet() )
		{
			ConnectionClient cc = this.openSessions.get(entry);
			if ( cc.getAlias().equals(alias) )
				return cc;
		}
		return null;
	}
	
	public ConnectionClient getAvailableConnection()
	{
		int count = 0;

		while ( count < ConnectionCollector.MAX_LOOPS_TO_FIND_CONNECTION )
		{
			for ( UUID key : this.openSessions.keySet() )
			{
				ConnectionClient cc = this.openSessions.get(key);
				if ( ! cc.isBusy() ) {
					cc.setBusy(true);
					return cc;
				}
			}
			
			try 
			{
				Thread.sleep(5 * ConnectionConstants.DEFAULT_BUSY_SLEEP);
			} 
			catch (InterruptedException ignored)
			{}
			++count;
		}
		return null;
	}
	
	public int size()
	{
		return this.openSessions.size();
	}
	
	public List<ConnectionClient> getAllConnections()
	{
		List<ConnectionClient> availableConnections = new ArrayList<>();
		availableConnections.addAll(this.openSessions.values());
		return availableConnections;
	}
	
	public boolean hasConnection( final UUID key )
	{
		return ( this.getConnection(key) != null);
	}
	
	public boolean hasConnection( final Integer cid )
	{
		return ( this.getConnection(cid) != null);
	}
	
	public void setLoginSeed( final SingleLogin lr )
	{
		this.seedSingleLogin = lr;
	}
	
	// This is a rollback object and as such the references it keeps are from the "past"
	public ConnectionClient getLatestAddConnection()
	{
		if ( this.latestConnection.getNumberItems() < 1 ) return null;

		ConnectionClient cc = null;
		int count = this.latestConnection.getNumberItems();

		while ( count > 0  )
		{
			cc = this.latestConnection.latest();
			if ( cc == null ) this.latestConnection.unroll();
			count = this.latestConnection.getNumberItems();
		}
		
		return cc;
	}
	
	// Adds a new connection from a previous defined one by cloning it
	// This can fail if cloning fails or it cannot connect.
	public boolean addConnection( final ConnectionClient ccTemplate )
	{
		if ( ccTemplate == null ) return false;
		ConnectionClient ccDuplicate;
		try 
		{
			ccDuplicate = ccTemplate.copy();
			if ( ccDuplicate != null )
				this.__makeAlias(ccTemplate.getAlias(), ccDuplicate);
		} 
		catch (CloneNotSupportedException e) 
		{
			return false;
		}
		
		return this.makeConnection(ccDuplicate, false);
	}
	
	// Takes a connection and pushes it into the list of available connections.
	// If it is a duplicate, it will NOT be pushed.
	public boolean pushConnection( final ConnectionClient cc ) {
		return cc.isValid() && this.__addConnectionToSessionMap(cc);
	}
	
	public Map<UUID, String> getConnectionAliases()
	{
		Map<UUID, String> aliasMap = new TreeMap<>();
		
		for ( UUID uuid : this.getConnections().keySet() )
		{
			if ( uuid == null ) this.cleanUp();
			String alias = this.openSessions.get(uuid).getAlias();
			if ( TextManager.validString(alias) )
				aliasMap.put(uuid, alias);
		}
		
		return aliasMap;
	}
	
	// Take a connection client and attempt to connect using credentials and
	// information stored within it.
	public boolean makeConnection(ConnectionClient cc, boolean needAlias )
	{
		if ( cc == null ) return false;
		for ( UUID key : this.openSessions.keySet() )
		{
			if ( key.equals(cc.getUUIDKey()) ) return false;
		}
		
		boolean result = false;
		if ( ! cc.hasConnection() ) 
			result = cc.connect();
		
		if ( result )
		{
			if ( needAlias ) this.__makeAlias(cc.getAlias(), cc);
			return this.__addConnectionToSessionMap(cc);
		}
		return result;
	}
	
	public boolean removeConnection( final UUID key ) {
		Map.Entry<Integer, UUID> entry = this.findEntry(key);
		return entry != null && this.__removeConnection(key);
	}
	
	public boolean removeConnection( final String alias )
	{
		for ( UUID entry : this.openSessions.keySet() )
		{
			ConnectionClient cc = this.openSessions.get(entry);
			if ( cc.getAlias().equals(alias) )
			{
				this.disconnectConnection(entry);
				this.openSessions.remove(entry);
				if ( this.openSessionsMap.containsValue(entry) )
				{
					this.removeUsingValue(this.openSessionsMap, entry);
					return true;
				}
				this.cleanUp();
			}
		}
		return false;
	}
	
	public boolean removeConnection( final int cid ) {
		Map.Entry<Integer, UUID> entry = this.findEntry(cid);
		return entry != null && this.__removeConnection(entry.getValue());
	}
	
	public Pair<Boolean, Set<UUID>> removeAllConnections()
	{
		Set<UUID> keys = new TreeSet<>(this.openSessions.keySet());
		Tristate result = new Tristate();
		
		for ( UUID key : keys )
		{
			result.and(this.removeConnection(key));
		}
		return new Pair<>(result.getBoolean(), keys);
	}
	
	private void disconnectConnection( final UUID key )
	{
		ConnectionClient cc = this.openSessions.get(key);
		if ( cc == null ) return;
		
		cc.disconnect();
	}
	
	private<T> Map.Entry<Integer, UUID> findEntry(final T key)
	{
		Class<?> keyClazz = key.getClass();
		for (Map.Entry<Integer, UUID> entry : this.openSessionsMap.entrySet()) {
			if ( key instanceof UUID ) {
				if (entry.getValue().equals(key))
					return entry;
			}
			if ( key instanceof Integer ) {
				if ( entry.getKey().equals(key) )
					return entry;
			}
		}
		
		return null;
	}
	
	private Integer getAvailableCID()
	{
		if ( ConnectionCollector.currentCID == (Integer.MAX_VALUE - 1) )
			ConnectionCollector.currentCID = 0;
		
		Integer savedLocation = ConnectionCollector.currentCID;
		
		while ( this.openSessionsMap.containsKey(++ConnectionCollector.currentCID) && ! ConnectionCollector.currentCID.equals(savedLocation) )
		{
			if ( ConnectionCollector.currentCID < (Integer.MAX_VALUE - 1) )
				ConnectionCollector.currentCID = 0;
		}
		
		if ( ConnectionCollector.currentCID.equals(savedLocation) ) return ConnectionCollector.NO_AVAILABLE_CIDS;
		return ConnectionCollector.currentCID;
	}
	
	private<T> boolean removeUsingValue( Map<?,T> map, final T match )
	{
		return map.values().removeAll(Collections.singleton(match));
	}
	
	@SuppressWarnings("unchecked")
	private<T> boolean removeUsingKey( Map<T,?> map, final T match )
	{
		T deletedKey = (T) map.remove(match);
		return deletedKey != null;
	}
	
	private Map<UUID, ConnectionClient> getConnections()
	{
		return this.openSessions;
	}
	
	private Map<Integer, UUID> getConnectionMap()
	{
		return this.openSessionsMap;
	}
	
	private void cleanUp()
	{
		// Check to see that entries are consistent in the two maps...
		// 1) number of entries match...
		if ( this.openSessions.size() != this.openSessionsMap.size() )
		{
			for ( UUID uuid : this.openSessions.keySet() )
			{
				if ( this.findEntry(uuid) == null ) 
				{
					if ( uuid.equals(this.latestConnection.latest().getUUIDKey()) )
						this.latestConnection.unroll();
					this.removeConnection(uuid);
				}
			}
		}
		
		// 2) UUIDs are consistent in both maps...
		for ( UUID uuid : this.openSessions.keySet() )
		{
			if ( ! this.openSessionsMap.containsValue(uuid) )
			{
				if ( this.findEntry(uuid) == null ) 
				{
					if ( uuid.equals(this.latestConnection.latest().getUUIDKey()) )
						this.latestConnection.unroll();
					this.removeConnection(uuid);
				}
			}
		}
	}
	
	private ConnectionClient getEntry(final Map.Entry<Integer, UUID> entry )
	{
		if ( entry == null ) return null;
		
		return this.openSessions.get(entry.getValue());		
	}
	
	private<T> ConnectionClient __getConnection(final T input)
	{
		Map.Entry<Integer, UUID> entry = this.findEntry(input);
		return this.getEntry(entry);		
	}
	
	private<T> boolean __removeConnection(UUID entry)
	{
		if ( entry == null ) return false;
		
		this.disconnectConnection(entry);
		
		Tristate result = new Tristate(Tristate.PASS);
		result.and(this.removeUsingKey(this.openSessions, entry));
		result.and(this.removeUsingValue(this.openSessionsMap, entry));
		
		return result.getBoolean();		
	}
	
	private void __makeAlias( String previousAlias, ConnectionClient cc )
	{
		if ( ! TextManager.validString(previousAlias) ) previousAlias = ConnectionCollector.DEFAULT_ALIAS;
		int addendum = this.size();
		String possibleAlias = previousAlias + "_" + addendum;
		ConnectionClient ccAliasDuplicate = this.getConnectionByAlias(possibleAlias);
		while ( ccAliasDuplicate != null )
		{
			if ( addendum == Integer.MAX_VALUE - 1 ) addendum = -1;
			++addendum;
			possibleAlias = previousAlias + "_" + addendum;
			ccAliasDuplicate = this.getConnectionByAlias(possibleAlias);
		}
		cc.setAlias(possibleAlias);		
	}
	
	private boolean __addConnectionToSessionMap( ConnectionClient cc )
	{
		UUID newUUID = cc.getUUIDKey();
		if ( newUUID == null )
			newUUID = UUID.randomUUID();
		
		ConnectionClient ccClash = this.getConnection(newUUID);
		boolean repurposedUUID = false;
		while ( ccClash != null )
		{
			newUUID = UUID.randomUUID();
			ccClash = this.getConnection(newUUID);
			repurposedUUID = true;
		}
		
		if ( repurposedUUID )
			cc.setUUIDKey(newUUID);
		
		this.__makeAlias(cc.getAlias(), cc);
		this.openSessionsMap.put(this.getAvailableCID(), newUUID);
		this.openSessions.put(newUUID, cc);
		this.latestConnection.push(cc);
		this.cleanUp();
		return true;
	}
	
	private void initialize()
	{
		this.openSessions = new TreeMap<>();
		this.openSessionsMap = new TreeMap<>();
		this.latestConnection = new RollBack<>();
	}
}