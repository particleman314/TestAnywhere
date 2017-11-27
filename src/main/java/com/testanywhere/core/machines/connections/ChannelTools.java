package com.testanywhere.core.machines.connections;

import com.testanywhere.core.utilities.Constants;
import com.testanywhere.core.os.utilities.ProcessUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public final class ChannelTools 
{
    public static final int DEFAULT_BUFFER_SIZE = 16 * Constants.KILOBYTES; // Bytes
    
	public static void fastChannelCopy(final ReadableByteChannel src, final WritableByteChannel dest) throws IOException
	{
		ChannelTools.fastChannelCopy(src, dest, ChannelTools.DEFAULT_BUFFER_SIZE);
	}
	
	private static void fastChannelCopy(final ReadableByteChannel src, final WritableByteChannel dest, int bufferSize) throws IOException
	{
		if ( bufferSize < 0 ) bufferSize = ChannelTools.DEFAULT_BUFFER_SIZE;
		if ( bufferSize >= Integer.MAX_VALUE ) bufferSize = ChannelTools.DEFAULT_BUFFER_SIZE;
		
		final ByteBuffer buffer = ByteBuffer.allocateDirect(bufferSize);
		while (src.read(buffer) != -1) 
		{
			// prepare the buffer to be drained
			buffer.flip();
			// write to the channel, may block
			dest.write(buffer);
			// If partial transfer, shift remainder down
			// If buffer is empty, same as doing clear()
			buffer.compact();
		}
		// EOF will leave buffer in fill state
		buffer.flip();
		// make sure the buffer is fully drained.
		while (buffer.hasRemaining()) 
		{
			dest.write(buffer);
		}
	}
	
	public static void fastStreamCopy(final InputStream src, final OutputStream dest) throws IOException
	{
		ChannelTools.fastStreamCopy(src, dest, ChannelTools.DEFAULT_BUFFER_SIZE);
	}

	public static void fastStreamCopy(final InputStream src, final OutputStream dest, int bufferSize) throws IOException 
	{
		if ( bufferSize < 0 ) bufferSize = ChannelTools.DEFAULT_BUFFER_SIZE;
		if ( bufferSize >= Integer.MAX_VALUE ) bufferSize = ChannelTools.DEFAULT_BUFFER_SIZE;

		final ByteBuffer buffer = ByteBuffer.allocateDirect(bufferSize);
		while (src.read() != -1) 
		{
			// prepare the buffer to be drained
			buffer.flip();
			// write to the stream, may block
			dest.write(buffer.array());
			// If partial transfer, shift remainder down
			// If buffer is empty, same as doing clear()
			buffer.compact();
		}
		// EOF will leave buffer in fill state
		buffer.flip();
		// make sure the buffer is fully drained.
		while (buffer.hasRemaining()) 
		{
			dest.write(buffer.array());
		}
	}
	
	public static StringBuilder fastStreamReader( final InputStream src ) throws Exception
	{
		return ChannelTools.fastStreamReader(src, ChannelTools.DEFAULT_BUFFER_SIZE);
	}
	
	private static StringBuilder fastStreamReader(final InputStream src, int bufferSize) throws Exception
	{
		StringBuilder sb = new StringBuilder();
		if ( src == null ) return sb;
		
		if ( bufferSize < 0 ) bufferSize = ChannelTools.DEFAULT_BUFFER_SIZE;
		if ( bufferSize >= Integer.MAX_VALUE ) bufferSize = ChannelTools.DEFAULT_BUFFER_SIZE;
		
		//final ByteBuffer buffer = ByteBuffer.allocateDirect(bufferSize);
        //int length;
        
        sb = (StringBuilder) ProcessUtils.manageStream(src, sb);
        /*while ((length = src.read(buffer.array())) != -1) 
        {
            sb.append(new String(buffer.array(), 0, length));
        }*/
		
		return sb;
	}
}
