package com.testanywhere.core.machines.managers;

import com.testanywhere.core.machines.connections.ConnectionClient;
import com.testanywhere.core.machines.connections.ConnectionConstants;
import com.testanywhere.core.machines.factories.ConnectionFactory;
import com.testanywhere.core.utilities.logging.TextManager;

import com.testanywhere.core.classes.managers.ABCManager;
import com.testanywhere.core.classes.managers.Registrar;

@SuppressWarnings("ExternalizableWithoutPublicNoArgConstructor")
public class ConnectionManager extends ABCManager
{
    static
    {
        ConnectionConstants.getInstance();
        Registrar.getInstance().addManagerPath(ConnectionManager.class.getPackage().getName());
    }

    private static class ConnectionTypeManagerHolder
    {
        public static final ConnectionManager INSTANCE = new ConnectionManager();
    }

    public static ConnectionManager getInstance()
    {
        return ConnectionTypeManagerHolder.INSTANCE;
    }

    private ConnectionManager()
    {
        Class<?> clazz = ConnectionManager.class;

        super.setManagerType(clazz);
        super.setManagerName(clazz.getName());
        super.configure();

        this.setFactory( new ConnectionFactory() );
    }

    @Override
    public void reset()
    {
        this.getFactory().reset();
        this.setValid(true);
    }

    @Override
    public void cleanup()
    {}

    // Standard API Methods
    public ConnectionClient getType(String type )
    {
        if ( ! TextManager.validString(type) ) return null;
        return this.getFactory().instantiate(type, null);
    }

    @Override
    public ConnectionFactory getFactory()
    {
        return (ConnectionFactory) super.getFactory();
    }
}
