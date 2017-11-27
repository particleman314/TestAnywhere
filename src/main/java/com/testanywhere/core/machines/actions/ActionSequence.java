package com.testanywhere.core.machines.actions;

import com.testanywhere.core.classes.support.ReturnInfo;
import com.testanywhere.core.classes.utilities.ErrorUtils;
import com.testanywhere.core.machines.connections.ConnectionClient;
import com.testanywhere.core.machines.exceptions.NoActionsInListException;
import com.testanywhere.core.utilities.class_support.Cast;
import com.testanywhere.core.utilities.exceptions.ObjectCreationException;
import com.testanywhere.core.utilities.logging.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ListIterator;

public class ActionSequence extends OutputDisplay
{
    private static long ASCounter = 0;

    private String seqLabel;
    private ArrayList<Action> sequence;
    private ListIterator<Action> iterator;
    private boolean breakOnFailure;
    private int currentIteratorID;

    public ActionSequence()
    {
        super();
        this.initialize();
    }

    public ActionSequence( String ASLabel )
    {
        this();
        this.seqLabel = ASLabel;
    }

    public ActionSequence( Collection<Action> replayActions ) throws NoActionsInListException
    {
        this();
        if ( replayActions == null ) throw new NoActionsInListException(ActionSequence.class);
        this.sequence.addAll(replayActions);
        this.breakOnFailure = true;
    }

    public ActionSequence( Collection<Action> replayActions, boolean stopOnFail ) throws NoActionsInListException
    {
        this(replayActions);
        this.breakOnFailure = stopOnFail;
    }

    public ActionSequence( ActionSequence otherAS ) throws ObjectCreationException
    {
        this();
        if ( otherAS == null ) throw new ObjectCreationException(ActionSequence.class);

        this.sequence = this.copy(otherAS.sequence);
        this.iterator = this.copy(otherAS.iterator);
        this.breakOnFailure = otherAS.breakOnFailure;
        this.currentIteratorID = otherAS.currentIteratorID;
    }

    @Override
    public void buildObjectOutput(int numTabs)
    {
        if ( numTabs < 0 ) numTabs = 0;
        Tabbing tabEnvironment = new Tabbing(numTabs);
        DisplayManager dm = this.getDM();

        dm.append("Action Sequence : " + this.seqLabel, DisplayType.TEXTTYPES.LABEL);
        tabEnvironment.increment();
        String innerSpacer = tabEnvironment.getSpacer();
        dm.append(innerSpacer + "Number of Actions : " + this.getSequence().size());
        dm.append(innerSpacer + "Break on Failure : " + TextManager.StringRepOfBool(this.breakOnFailure, "yn"));
    }

    @Override
    public String toString() { return null; }

    @Override
    public boolean equals( Object other )
    {
        if ( other == null ) return false;
        ActionSequence otherAS = Cast.cast(other);

        if ( otherAS == null ) return false;

        if ( this.breakOnFailure != otherAS.breakOnFailure ) return false;
        if ( this.getSequence().size() != otherAS.getSequence().size() ) return false;

        for ( int loop = 0; loop < this.getSequence().size(); loop++ )
        {
            if ( ! ((ArrayList<?>) this.getSequence()).get(loop).equals( ((ArrayList<?>) otherAS.getSequence()).get(loop)) ) return false;
        }
        return true;
    }

    @Override
    public boolean isNull()
    {
        return false;
    }

    public String getLabel() { return this.seqLabel; }

    public void setLabel( String ASLabel ) { this.seqLabel = ASLabel; }

    public Collection<Action> getSequence()
    {
        return this.sequence;
    }

    public boolean add( Action a )
    {
        return this.sequence.add(a);
    }

    public int size()
    {
        return this.getSequence().size();
    }

    public void clear()
    {
        this.currentIteratorID = 0;

        if ( this.getSequence() != null )
            this.sequence.clear();

        this.iterator = null;
    }

    public Action getNext()
    {
        if ( this.getIteratorPosition() == null )
            this.iterator = this.getIterator();
        if ( this.getIteratorPosition().hasNext())
        {
            ++this.currentIteratorID;
            return this.getIteratorPosition().next();
        }
        return null;
    }

    public Action getPrevious()
    {
        if ( this.getIteratorPosition() == null )
            this.iterator = this.getIterator();
        if ( this.getIteratorPosition().hasPrevious())
        {
            --this.currentIteratorID;
            return this.getIteratorPosition().previous();
        }
        return null;
    }

    public Action getCurrent()
    {
        if ( this.getIteratorPosition() == null || this.getSequence().isEmpty()) return null;

        if ( this.getIteratorPosition().hasPrevious() )
        {
            this.getIteratorPosition().previous();
            return this.getIteratorPosition().next();
        }

        if ( this.getIteratorPosition().hasNext() )
        {
            this.getIteratorPosition().next();
            return this.getIteratorPosition().previous();
        }

        return null;
    }

    public ReturnInfo play(ConnectionClient cc)
    {
        if ( this.getIteratorPosition() == null ) return ReturnInfo.NO_RESPONSE_NO_ERROR;
        return this.getCurrent().execute(cc);
    }

    public ReturnInfo replayAll(ConnectionClient cc)
    {
        if ( this.getSequence().isEmpty() ) return ReturnInfo.NO_RESPONSE_NO_ERROR;
        ReturnInfo overallResult = new ReturnInfo();

        int actionCount = 1;
        for ( Action a : this.getSequence() )
        {
            ReturnInfo actionResult = a.execute(cc);
            overallResult.setOutput( overallResult.getOutput() + TextManager.EOL + actionResult.getOutput() );
            overallResult.setReturnCode( overallResult.getReturnCode() + actionResult.getReturnCode() );
            if ( this.breakOnFailure && overallResult.getReturnCode() != ErrorUtils.SUCCESS_ID )
                return overallResult;

            this.debug("Completed replaying action #" + actionCount);
            ++actionCount;
        }

        return overallResult;
    }

    private ListIterator<Action> getIteratorPosition()
    {
        return this.iterator;
    }

    private ListIterator<Action> getIterator()
    {
        return this.sequence.listIterator();
    }

    private void initialize()
    {
        this.breakOnFailure    = true;
        this.currentIteratorID = 0;
        this.sequence          = new ArrayList<>();
        this.iterator          = null;
        this.seqLabel          = "Action_Sequence_" + ActionSequence.ASCounter;
        ActionSequence.ASCounter++;
    }
}
