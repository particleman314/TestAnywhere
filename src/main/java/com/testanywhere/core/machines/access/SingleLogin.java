package com.testanywhere.core.machines.access;

import com.testanywhere.core.utilities.class_support.Cast;
import com.testanywhere.core.machines.exceptions.LoginException;
import com.testanywhere.core.encryption.utilities.EncryptionUtils;
import com.testanywhere.core.encryption.credentials.Credentials;
import com.testanywhere.core.encryption.credentials.Password;
import com.testanywhere.core.utilities.exceptions.ObjectCreationException;
import com.testanywhere.core.utilities.logging.DisplayManager;
import com.testanywhere.core.utilities.logging.DisplayType;
import com.testanywhere.core.utilities.logging.Tabbing;
import com.testanywhere.core.utilities.logging.TextManager;
import com.testanywhere.core.network.classes.IP;

@SuppressWarnings("ExternalizableWithoutPublicNoArgConstructor")
public class SingleLogin extends ABCLogin
{
    private IP ip;
    private Credentials credentials;

    public SingleLogin(String ip ) throws LoginException
    {
        this();
        if ( ! TextManager.validString(ip) ) throw new LoginException("Bad IP specified for login");
        this.ip = new IP(ip);
    }

    public SingleLogin(String ip, String user, Password passwd ) throws LoginException
    {
        this();
        if ( ! TextManager.validString(ip) ) throw new LoginException("Bad IP specified for login");
        if ( ! TextManager.validString(user) || passwd == null ) throw new LoginException("Credentials are null or empty");
        this.ip = new IP(ip);
        this.credentials = EncryptionUtils.makeCredentials(user, passwd);
    }

    public SingleLogin(String ip, Credentials creds ) throws LoginException
    {
        this();
        if ( ! TextManager.validString(ip) ) throw new LoginException("Bad IP specified for login");
        if ( creds == null || creds.isEmpty() ) throw new LoginException("Credentials are null or empty");
        this.ip = new IP(ip);
        this.credentials = creds;
    }

    public SingleLogin( IP ip, Credentials creds ) throws LoginException
    {
        this();
        if ( ! ip.isValid() ) throw new LoginException("Bad IP specified for login");
        if ( creds == null || creds.isEmpty() ) throw new LoginException("Credentials are null or empty");
        this.ip = ip;
        this.credentials = creds;
    }

    public SingleLogin( final SingleLogin sl ) throws ObjectCreationException
    {
        this();
        if ( sl == null ) throw new ObjectCreationException(SingleLogin.class);

        try {
            this.ip = sl.getIP().copy();
            this.credentials = sl.getCredentials().copy();
        } catch (CloneNotSupportedException e) {
            throw new ObjectCreationException(SingleLogin.class);
        }
    }

    @Override
    public void buildObjectOutput(int numTabs)
    {
        if ( numTabs < 0 ) numTabs = 0;
        Tabbing tabEnvironment = new Tabbing(numTabs);
        DisplayManager dm = this.getDM();

        String outerSpacer = tabEnvironment.getSpacer();
        dm.append(outerSpacer + "Login :", DisplayType.TEXTTYPES.LABEL);

        tabEnvironment.increment();
        String innerSpacer = tabEnvironment.getSpacer();

        dm.append(innerSpacer + "Login IP : " + this.getIP().toString());
        this.getCredentials().buildObjectOutput(tabEnvironment.numberTabs());
        dm.addFormatLines(this.getCredentials().getDM().getLines());
    }

    @Override
    public boolean equals( Object other ) {
        if (other == null) return false;

        SingleLogin otherSingleLogin = Cast.cast(other);
        if (otherSingleLogin == null) return false;

        IP otherIP = otherSingleLogin.getIP();
        Credentials otherCreds = otherSingleLogin.getCredentials();

        return this.getIP().equals(otherIP) && this.getCredentials().equals(otherCreds);
    }

    @Override
    public String toString()
    {
        return getIP().toString() + TextManager.STR_OUTPUTSEPARATOR + this.getCredentials().toString();
    }

    @Override
    public boolean isNull()
    {
        return false;
    }

    public IP getIP()
    {
        return this.ip;
    }

    public String getIPasString()
    {
        return this.getIP().getIP();
    }

    public Credentials getCredentials()
    {
        return this.credentials;
    }

    public String getUserID()
    {
        return this.getCredentials().first();
    }

    public Password getPassword()
    {
        return this.getCredentials().second();
    }

    public void setIP(IP ip)
    {
        this.ip = ip;
    }

    public void setCredentials( Credentials creds )
    {
        this.credentials = creds;
    }

    public boolean isPrepared()
    {
        IP myIP = this.getIP();

        return myIP != null && myIP.isValid();

    }

    private SingleLogin()
    {
        super();
        this.initialize();
    }

    private void initialize()
    {
        this.ip = null;
        this.credentials = null;
    }
}
