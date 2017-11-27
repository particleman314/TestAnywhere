package com.testanywhere.core.machines.access;

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

import java.util.*;

public class ParallelLogin extends ABCLogin
{
    private Map<Credentials, Set<IP>> loginSites;

    public ParallelLogin( String username, String password, Collection<IP> requestedIPs ) throws LoginException
    {
        super();
        this.initialize();

        if ( TextManager.validString(username) && TextManager.validString(password) )
        {
            Credentials creds = EncryptionUtils.makeCredentials(username, password);
            if ( ! this.assignIPtoCredentials(creds, requestedIPs) )
                throw new LoginException("No IPs found for setting up logi credentials!");
        }
        else
        {
            throw new LoginException("Invalid username or password");
        }
    }

    public ParallelLogin( String username, Password password, Collection<IP> requestedIPs)
    {
        super();
        this.initialize();

        if ( TextManager.validString(username) && password != null )
        {
            Credentials creds = EncryptionUtils.makeCredentials(username, password);
            if ( ! this.assignIPtoCredentials(creds, requestedIPs) )
                throw new LoginException("No IPs found for setting up logi credentials!");
        }
        else
        {
            throw new LoginException("Invalid username or password object");
        }
    }

    public ParallelLogin( Credentials creds, Collection<IP> requestedIPs) throws LoginException
    {
        if ( ! this.assignIPtoCredentials(creds, requestedIPs) )
            throw new LoginException("No IPs found for setting up logi credentials!");
    }

    public ParallelLogin( Credentials creds, String ... IPs )
    {
        List<IP> listIPs = new ArrayList<>();
        for ( String ip : IPs ) {
            if ( ! TextManager.validString(ip) ) continue;
            IP convertedIP = new IP(ip);
            if (convertedIP.getInetAddress() != null) listIPs.add(convertedIP);
        }
        if ( ! this.assignIPtoCredentials(creds, listIPs) )
            throw new LoginException("No IPs found for setting up logi credentials!");
    }

    public ParallelLogin( ParallelLogin pl ) throws ObjectCreationException
    {
        super();
        this.initialize();

        if ( pl == null ) throw new ObjectCreationException(ParallelLogin.class);
        this.loginSites = this.copy(pl.getLoginSites());
    }

    @Override
    public void buildObjectOutput( int numTabs )
    {
        if ( numTabs < 0 ) numTabs = 0;
        Tabbing tabEnvironment = new Tabbing(numTabs);
        DisplayManager dm = this.getDM();

        String outerSpacer = tabEnvironment.getSpacer();
        dm.append(outerSpacer + "Parallel Login :", DisplayType.TEXTTYPES.LABEL);

        int currentDepth = tabEnvironment.numberTabs();

        for ( Credentials creds : this.getKnownCredentials() ) {
            creds.buildObjectOutput(currentDepth + 1);
            dm.addFormatLines(creds.getDM().getLines());

            for ( IP ip : this.getEntryByCredentials(creds).getValue() )
            {
                ip.buildObjectOutput(currentDepth + 2);
                dm.addFormatLines(ip.getDM().getLines());
            }
        }
    }

    @Override
    public boolean isNull()
    {
        return false;
    }

    public boolean addLogin( IP newIP, Credentials creds )
    {
        if ( creds == null ) return false;
        Map.Entry<Credentials, Set<IP>> entry = this.getEntryByCredentials(creds);

        if ( entry == null )
        {
            Set<IP> ipList = new HashSet<>();
            ipList.add(newIP);
            return this.assignIPtoCredentials(creds, ipList);
        }

        this.removeLogin(newIP);
        return entry.getValue().add(newIP);
    }

    public boolean removeLogin( IP oldIP ) {
        if (!oldIP.isValid()) return false;
        Map.Entry<Credentials, Set<IP>> entry = this.getEntryByIP(oldIP);

        return entry != null && entry.getValue().remove(oldIP);
    }

    public Map.Entry<Credentials, Set<IP>> getEntryByIP( IP ip )
    {
        for ( Map.Entry<Credentials, Set<IP>> stc : this.getLoginSites().entrySet() )
        {
            for ( IP stIP : stc.getValue() )
                if ( stIP.equals(ip) ) return stc;
        }

        return null;
    }

    public Map.Entry<Credentials, Set<IP>> getEntryByCredentials( Credentials creds )
    {
        for ( Map.Entry<Credentials, Set<IP>> stc : this.getLoginSites().entrySet() )
            if ( creds.equals(stc.getKey()) ) return stc;

        return null;
    }

    public Map<Credentials, Set<IP>> getLoginSites() { return this.loginSites; }

    public boolean isPrepared( Credentials creds )
    {
        Map.Entry<Credentials, Set<IP>> entry = this.getEntryByCredentials(creds);
        if ( entry != null )
        {
            Set<IP> myIPs = entry.getValue();
            Set<IP> badIPs = new HashSet<>();

            for (IP ip : myIPs) {
                if (ip == null) {
                    this.warn("Null IP discovered.  Removing this IP from the set tied to credentials...");
                    badIPs.add(ip);
                }
                else if (! ip.isValid()) {
                    this.warn("IP " + ip.toString() + " is invalid.  Removing this IP from the set tied to credentials...");
                    badIPs.add(ip);
                }
            }

            if (!badIPs.isEmpty())
                for (IP ip : badIPs) {
                    this.removeLogin(ip);
                }
            return true;
        }
        else
            return false;
    }

    private Collection<Credentials> getKnownCredentials()
    {
        return this.getLoginSites().keySet();
    }

    private boolean assignIPtoCredentials( Credentials creds, Collection<IP> requestedIPs )
    {
        if ( ! creds.isEmpty() && requestedIPs.size() > 0 ) {
            Map.Entry<Credentials, Set<IP>> entry = this.getEntryByCredentials(creds);
            if ( entry == null )
                entry.getValue().addAll(requestedIPs);
            else
                this.getLoginSites().put(creds, new HashSet<>(requestedIPs));
            return true;
        }
        return false;
    }

    private void initialize()
    {
        this.loginSites = new HashMap<>();
    }
}
