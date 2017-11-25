package com.testanywhere.core.utilities.classes;

import com.testanywhere.core.utilities.Constants;
import com.testanywhere.core.utilities.class_support.BaseClass;
import com.testanywhere.core.utilities.exceptions.ObjectCreationException;
import com.testanywhere.core.utilities.logging.*;

public class Range<T extends Comparable<T>> extends OutputDisplay
{
    private Pair<T,T> range;
    private boolean allowEndPoints;

    public Range()
    {
        super();
        this.initialize();
    }

    public Range( final T min, final T max )
    {
        this();
        this.range.set(min, max);
        this.check();
    }

    public Range ( Range other ) throws ObjectCreationException, CloneNotSupportedException
    {
        this();
        if ( other == null ) throw new ObjectCreationException(Range.class);
        this.range = other.getRange().copy();
    }

    @Override
    public String toString()
    {
        String leftSide  = TextManager.specializeName(Constants.nullRep);
        String rightSide = TextManager.specializeName(Constants.nullRep);

        if ( this.getRange().isNull() ) return "No Range defined";

        if ( this.getRange().getL() != null ) leftSide = this.getRange().getL().toString();
        if ( this.getRange().getR() != null ) rightSide = this.getRange().getR().toString();

        return "Minimum --> [ " + leftSide + " ] :: Maximum --> [ " + rightSide + " ]";
    }

    @Override
    public boolean isNull()
    {
        return this.getRange().isNull();
    }

    public Pair<T,T> getRange()
    {
        return this.range;
    }

    public void setRange( Pair<T,T> range )
    {
        if ( range == null ) return;
        this.range = range;
    }

    @Override
    public void buildObjectOutput(int numTabs)
    {
        if ( numTabs < 0 ) numTabs = 0;
        Tabbing tabEnvironment = new Tabbing(numTabs);
        DisplayManager dm = this.getDM();

        String outerSpacer = tabEnvironment.getSpacer();

        if ( this.getRange().isNull() )
            dm.append(outerSpacer + "Range [" + Object.class.getSimpleName() + "] :", DisplayType.TEXTTYPES.LABEL);
        else
            dm.append(outerSpacer + "Range [" + this.getRange().getL().getClass().getSimpleName() + "] :", DisplayType.TEXTTYPES.LABEL);

        tabEnvironment.increment();
        String innerSpacer = tabEnvironment.getSpacer();

        dm.append(innerSpacer + "Minimum : " + BaseClass.checkIsNull(this.getRange().getL()));
        dm.append(innerSpacer + "Maximum : " + BaseClass.checkIsNull(this.getRange().getR()));
    }

    public boolean includeEndPoints()
    {
        return this.allowEndPoints;
    }

    public void setEndPointInclusion( boolean allowEndPoints )
    {
        this.allowEndPoints = allowEndPoints;
    }

    public boolean outOfBounds( T input )
    {
        T min = this.getRange().first();
        T max = this.getRange().second();

        if ( this.includeEndPoints() )
        {
            if (input.compareTo(min) <= 0 || input.compareTo(max) >= 0) return true;
        }
        else
        {
            if (input.compareTo(min) < 0 || input.compareTo(max) > 0) return true;
        }
        return false;
    }

    public T bound(final T input )
    {
        T min = this.getRange().first();
        T max = this.getRange().second();

        if ( this.includeEndPoints() )
        {
            if (min.compareTo(input) >= 0) return min;
            if (max.compareTo(input) <= 0) return max;
        }
        else
        {
            if (min.compareTo(input) > 0) return min;
            if (max.compareTo(input) < 0) return max;
        }
        return input;
    }

    private void check()
    {
        T min = this.getRange().first();
        T max = this.getRange().second();

        if ( min.compareTo(max) > 0 )
        {
            T tmp = this.getRange().second();
            this.range.setR(this.getRange().getL());
            this.range.setL(tmp);
        }
    }

    private void initialize()
    {
        this.range = new Pair<>();
        this.allowEndPoints = false;
    }
}
