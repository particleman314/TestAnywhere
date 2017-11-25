package com.testanywhere.core.utilities.logging;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class MultiOutputStream extends OutputStream
{
	private final Set<OutputStream> streams = Collections.synchronizedSet(new LinkedHashSet<OutputStream>());

	public MultiOutputStream()
	{
		this(null);
	}

	public MultiOutputStream( final Set<OutputStream> outStreams )
	{
		super();
		if ( outStreams != null )
		{
			for (OutputStream outputStream : outStreams) 
			{
				if (outputStream == null) continue;
				this.addStream(outputStream);
			}
		}
	}

	public int getNumStreams()
	{
		return this.streams.size();
	}
	
	public boolean addStream( final OutputStream outStream )
	{
		return outStream != null &&
				! this.streams.contains(outStream) &&
				this.streams.add(outStream);
	}

	public Set<OutputStream> getStreams()
	{
		return this.streams;
	}

	@Override
	public void write(int arg0) throws IOException
	{
		for (OutputStream os : this.streams) os.write(arg0);
	}

	@Override
	public void write(byte[] b) throws IOException
	{
		for (OutputStream os : this.streams) os.write(b);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException
	{
		for (OutputStream os : this.streams) os.write(b, off, len);
	}

	public void write( String s ) throws IOException
	{
		byte[] strBytes = s.getBytes();
		this.write(strBytes);
	}

	@Override
	public void close() throws IOException
	{
		for (OutputStream os : this.streams) os.close();
	}

	@Override
	public void flush() throws IOException
	{
		for (OutputStream os : this.streams) os.flush();
	}
}