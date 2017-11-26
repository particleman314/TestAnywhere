package com.testanywhere.core.classes.utilities;

import com.testanywhere.core.utilities.logging.LogConfiguration;
import com.testanywhere.core.utilities.logging.TextManager;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import sun.misc.Launcher;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class FileDirUtils
{
	public static Logger logger;

	static
	{
		FileDirUtils.logger = Logger.getLogger("FileDirUtils");
		LogConfiguration.configure();
	}
	// Configuration settings ///////////////////////////////////////////////////

	/**
	 * Default 10 digit file storage distribution array. This means that if I 
	 * want to name file as 10 digit number e.g. number 123 as 0000000123 or 
	 * number 123456789 as 01234567890. Then the path constructed from number 
	 * 1234567890 using distribution 2/2/2/4 would be 12/34/56/0123456789 
	 */
	private static final int DEFAULT_BUFFER_SIZE = 65536;

	public static final int[] DEFAULT_STRORAGE_TREE_DISTRIBUTION = {2, 2, 2, 4};
	/**
	 * How big buffer to use to process files.
	 */
	private static final int BUFFER_SIZE = DEFAULT_BUFFER_SIZE;

	// Cached values ////////////////////////////////////////////////////////////


	/**
	 * Temporary directory to use. It is guarantee that it ends with \ (or /)
	 */
	public static final String TEMP_DIRECTORY = org.apache.commons.io.FileUtils.getTempDirectoryPath();

	// Constructors /////////////////////////////////////////////////////////////

	public static String getCurrentDirectory( final boolean trailingSlash )
	{
		String path;
		try
		{
			path = new File(".").getCanonicalPath();
		}
		catch (IOException e)
		{
			path = ".";
		}
		if ( trailingSlash )
			return path + File.separator;
		else
			return path;
	}

	public static String getCurrentDirectory()
	{
		return FileDirUtils.getCurrentDirectory(true);
	}

	public static boolean fileExists ( final File f )
	{
		if ( f == null ) {
			FileDirUtils.logger.error("Unable to use NULL File object.  Not processed.");
			return false;
		}

		if ( ! f.exists() || ! f.isFile() || ! f.canRead() )
		{
			FileDirUtils.logger.error("Unable to access requested file " + TextManager.specializeName(f.getAbsolutePath()));
			return false;
		}

		return true;
	}

	public static boolean fileExists( final String file )
	{
		if ( ! TextManager.validString(file) )
		{
			FileDirUtils.logger.error("Improper input for " + TextManager.specializeName(file) + " found.  Not processed.");
			return false;
		}

		File f = new File(file);
		return FileDirUtils.fileExists(f);
	}

	public static boolean directoryExists( final String dir )
	{
		if ( ! TextManager.validString(dir) )
		{
			FileDirUtils.logger.error("Improper input for " + TextManager.specializeName(dir) + " found.  Not processed.");
			return false;
		}

		File d = new File(dir);
		return FileDirUtils.directoryExists(d);

	}

	public static boolean directoryExists( final File d )
	{
		if ( d == null ) {
			FileDirUtils.logger.error("Unable to use NULL File object.  Not processed.");
			return false;
		}

		if ( ! d.exists() || ! d.isDirectory() || ! d.canRead() || ! d.canExecute() )
		{
			FileDirUtils.logger.error("Unable to access requested directory " + TextManager.specializeName(d.getAbsolutePath()));
			return false;
		}

		return true;
	}

	public static boolean writeFile( final File file, final String data ) throws IOException
	{
		if (file == null) throw new IOException("Unable to use NULL File object for destination");

		File tempFile = null;
		if ( FileDirUtils.fileExists( file ) ) tempFile = FileDirUtils.renameToTemporaryName(file, "TEMP", null);

		FileUtils.write(file, data);
		if ( tempFile != null )
			FileUtils.forceDelete(tempFile);
		return true;
	}

	public static String readFile( final String file ) throws IOException
	{
		if ( ! TextManager.validString(file) ) throw new IOException("Invalid file name given for reading.");
		File f = new File(file);
		return FileDirUtils.readFile(f);
	}

	public static String readFile( final File file ) throws IOException
	{
		if ( file == null ) throw new IOException("Unable to use NULL File object for source");

		// Make sure that the source exist, it might be already moved from
		// a directory and we just don't know about it
		if ( FileDirUtils.fileExists(file) ) return FileUtils.readFileToString(file);
		return null;
	}

	public static List<String> readFileAsLines( final String file ) throws IOException
	{
		if ( ! TextManager.validString(file) ) throw new IOException("Invalid file name given for reading.");
		File f = new File(file);
		return FileDirUtils.readFileAsLines(f);
	}

	public static List<String> readFileAsLines( final File file ) throws IOException
	{
		if ( file == null ) throw new IOException("Unable to use NULL File object for source");

		// Make sure that the source exist, it might be already moved from
		// a directory and we just don't know about it
		if ( file.exists() && file.canRead() ) return FileUtils.readLines(file);
		return null;
	}

	public static void copyFile( final File srcFile, final File destFile, final String suffix, final boolean overwrite ) throws IOException
	{
		if ( srcFile == null ) throw new IOException("Unable to use NULL File object for source");
		if ( destFile == null ) throw new IOException("Unable to used NULL File object for destination");
		
		// Make sure that the source exist, it might be already moved from 
		// a directory and we just don't know about it
		if ( FileDirUtils.fileExists(srcFile) )
		{
			// Next check if the destination file exists
			if ( destFile.exists() )
			{
				// If the destination exists, that means something went wrong
				// Rename the destination file under a temporary name and try to
				// move the new file instead of it
				if ( ! overwrite )
					FileDirUtils.renameToTemporaryName(destFile, null, suffix);
				else
					//noinspection ResultOfMethodCallIgnored
					destFile.delete();
			}

			// Make sure the directory exists and if not create it
			File flFolder;

			flFolder = destFile.getParentFile();
			if ((flFolder != null) && (!flFolder.exists()))
			{
				if (!flFolder.mkdirs())
				{
					// Do not throw the exception if the directory already exists
					// because it was created meanwhile for example by a different 
					// thread
					if (!flFolder.exists()) throw new IOException("Cannot create directory " + flFolder);
				}
			}

			// Now everything should exist so try to rename the file first
			// After testing, this renames files even between volumes C to H 
			// so we don't have to do anything else on Windows but we still
			// have to handle error on Unix
			FileUtils.copyFile(srcFile, destFile);
		}
	}

	public static void copyFile( final File srcFile, final File destFile, final boolean force ) throws IOException
	{
		FileDirUtils.copyFile(srcFile, destFile, null, force);
	}

	public static void copyFile( final File srcFile, final File destFile ) throws IOException
	{
		FileDirUtils.copyFile(srcFile, destFile, false);
	}
	
	/**
	 * Move file to a new location. If the destination is on different volume,
	 * this file will be copied and then original file will be deleted.
	 * If the destination already exists, this method renames it with different
	 * name and leaves it in that directory and moves the new file along side 
	 * the renamed one.
	 * 
	 * @param srcFile - file to move
	 * @param destFile - destination file
	 * @param suffix - suffix
	 * @throws java.io.IOException - error message
	 */
	public static void moveFile( final File srcFile, final File destFile, final String suffix ) throws IOException
	{
		FileDirUtils.copyFile(srcFile, destFile, suffix, false);
		if ( destFile.exists() && destFile.isFile() ) FileUtils.forceDelete(srcFile);
	}

	public static void moveFile( final File srcFile, final File destFile ) throws IOException
	{
		FileDirUtils.moveFile(srcFile, destFile, null);
	}
	
	public static void copyDirectory( final File srcDir, final File destDir, String suffix, final boolean overwrite ) throws IOException
	{
		if ( srcDir == null ) throw new IOException("Unable to use NULL File object for source");
		
		if ( destDir == null ) throw new IOException("Unable to used NULL File object for destination");
		
		if ( ! TextManager.validString(suffix) ) suffix = TimeUtils.getCurrentDateAsString();
		
		// Make sure that the source exist, it might be already moved from 
		// a directory and we just don't know about it
		if ( FileDirUtils.directoryExists(srcDir) )
		{
			// Next check if the destination directory exists
			if ( FileDirUtils.directoryExists(destDir) )
			{
				// If the destination exists, that means something went wrong
				// Rename the destination file under temporaty name and try to  
				// move the new file instead of it
				if ( ! overwrite )
					FileDirUtils.renameToTemporaryName(destDir, null, suffix);
				else
					FileDirUtils.deleteDirectory(destDir);
			}

			// Make sure the directory exists and if not create it
			File flFolder;

			flFolder = destDir.getParentFile();
			if ((flFolder != null) && (!flFolder.exists()))
			{
				if (!flFolder.mkdirs())
				{
					// Do not throw the exception if the directory already exists
					// because it was created meanwhile for example by a different 
					// thread
					if (!flFolder.exists()) throw new IOException("Cannot create directory " + flFolder);
				}
			}

			// Now everything should exist so try to rename the directory first
			// After testing, this renames files even between volumes C to H 
			// so we don't have to do anything else on Windows but we still
			// have to handle error on Unix 
			FileUtils.copyDirectory(srcDir, destDir);
			if ( ! destDir.exists() ) throw new IOException();
		}   
	}

	public static void moveDirectory( final File srcDir, final File destDir, final String suffix, final boolean overwrite ) throws IOException
	{
		FileDirUtils.copyDirectory(srcDir, destDir, suffix, overwrite);
		if ( destDir.exists() && destDir.isDirectory() ) FileUtils.deleteDirectory(srcDir);
	}
	
	/**
	 * Rename the file to temporary name with given prefix and suffix
	 * 
	 * @param srcFile - file to rename
	 * @param strPrefix - prefix to use
	 * @param strSuffix - suffix to use
	 * @throws java.io.IOException - error message
	 */
	public static File renameToTemporaryName( final File srcFile, String strPrefix, String strSuffix ) throws IOException
	{
		if ( srcFile == null ) throw new IOException("Unable to used NULL File object for source");
		if ( ! TextManager.validString(strPrefix, true) ) strPrefix = "";
		if ( ! TextManager.validString(strSuffix, true) ) strSuffix = "";
		
		if ( strPrefix.equals(strSuffix) && "".equals(strPrefix) )
		{
			strSuffix = TimeUtils.getCurrentDateAsString();
		}
		
		String       strParent;
		StringBuffer sbBuffer = new StringBuffer();
		File         flTemp;
		int          iIndex = 0;

		strParent = srcFile.getParent();

		// Generate new name for the file in a deterministic way
		do
		{
			iIndex++;
			sbBuffer.delete(0, sbBuffer.length());
			if (strParent != null) 
			{
				sbBuffer.append(strParent);
				sbBuffer.append(File.separatorChar);
			}

			sbBuffer.append(strPrefix);
			sbBuffer.append("_");
			sbBuffer.append(iIndex);
			sbBuffer.append("_");
			sbBuffer.append(srcFile.getName());
			sbBuffer.append("_");
			sbBuffer.append(strSuffix);

			flTemp = new File(sbBuffer.toString());
		}      
		while (flTemp.exists());

		// Now we should have unique name
		if (!srcFile.renameTo(flTemp))
		{
			throw new IOException("Cannot rename " + srcFile.getAbsolutePath() + " to " + flTemp.getAbsolutePath());
		}
		
		return flTemp;
	}

	/** 
	 * Delete all files and directories in directory but do not delete the
	 * directory itself.
	 * 
	 * @param strDir - string that specifies directory to delete
	 * @return boolean - success flag
	 * @throws java.io.IOException
	 */
	public static boolean deleteDirectoryContent( final String strDir ) throws IOException
	{
		return ( TextManager.validString(strDir) ) && FileDirUtils.deleteDirectoryContent(new File(strDir));
	}

	/**
	 * Delete all files and directories in directory but do not delete the
	 * directory itself.
	 *
	 * @param fDir - directory to delete
	 * @return boolean - sucess flag
	 * @throws java.io.IOException
	 */
	public static boolean deleteDirectoryContent( final File fDir ) throws IOException
	{
		boolean bRetval = false;

		if (fDir != null && fDir.isDirectory())
		{
			File[] files = fDir.listFiles();

			if (files != null)
			{
				bRetval = true;
				boolean dirDeleted;

				for (File file : files)
				{
					if (file.isDirectory())
					{
						// TODO: Performance: Implement this as a queue where you add to
						// the end and take from the beginning, it will be more efficient
						// than the recursion
						dirDeleted = FileDirUtils.deleteDirectoryContent(file);
						bRetval = dirDeleted && bRetval && FileDirUtils.deleteDirectory(file);
					}
					else
					{
						org.apache.commons.io.FileUtils.forceDelete(file);
						if ( file.exists() ) bRetval = false;
					}
				}
			}
		}

		return bRetval;
	}

	/**
	 * Deletes all files and subdirectories under the specified directory including
	 * the specified directory
	 *
	 * @param strDir - string that specifies directory to be deleted
	 * @return boolean - true if directory was successfully deleted
	 * @throws java.io.IOException
	 */
	public static boolean deleteDirectory( final String strDir ) throws IOException
	{
		return (TextManager.validString(strDir)) && FileDirUtils.deleteDirectory(new File(strDir));
	}

	/**
	 * Deletes all files and subdirectories under the specified directory including
	 * the specified directory
	 *
	 * @param fDir - directory to be deleted
	 * @return boolean - true if directory was successfully deleted
	 * @throws java.io.IOException
	 */
	public static boolean deleteDirectory( final File fDir ) throws IOException
	{
		boolean bRetval = false;
		if ( fDir != null && fDir.exists() && fDir.isDirectory() )
		{
			bRetval = FileDirUtils.deleteDirectoryContent(fDir);
			if (bRetval)
			{
				bRetval = bRetval && fDir.delete();         
			}
		}
		return bRetval;
	}

	public static boolean areFilesEqual( final File first, final File second ) throws IOException
	{
		return FileDirUtils.isFileBinaryEqual(first, second);
	}
	
	/**
	 * Compare binary files. Both files must be files (not directories) and exist.
	 * 
	 * @param first  - first file
	 * @param second - second file
	 * @return boolean - true if files are binary equal
	 * @throws java.io.IOException - error in function
	 */
	public static boolean isFileBinaryEqual( final File first, final File second ) throws IOException
	{
		boolean retval = false;

		if ( first == null || second == null ) return false;


		if ((first.exists()) && (second.exists()) 
				&& (first.isFile()) && (second.isFile()))
		{
			if (first.getCanonicalPath().equals(second.getCanonicalPath())) retval = true;
			else
			{
				FileInputStream firstInput;
				FileInputStream secondInput;
				BufferedInputStream bufFirstInput = null;
				BufferedInputStream bufSecondInput = null;

				try
				{            
					firstInput = new FileInputStream(first); 
					secondInput = new FileInputStream(second);
					bufFirstInput = new BufferedInputStream(firstInput, FileDirUtils.BUFFER_SIZE);
					bufSecondInput = new BufferedInputStream(secondInput, FileDirUtils.BUFFER_SIZE);

					int firstByte;
					int secondByte;

					while (true)
					{
						firstByte = bufFirstInput.read();
						secondByte = bufSecondInput.read();
						if (firstByte != secondByte) break;
						if ((firstByte < 0) && (secondByte < 0))
						{
							retval = true;
							break;
						}
					}
				}
				finally
				{
					try
					{
						if (bufFirstInput != null) bufFirstInput.close();
					}
					finally
					{
						if (bufSecondInput != null) bufSecondInput.close();
					}
				}
			}
		}

		return retval;
	}



	/**
	 * Get path which represents temporary directory. It is guarantee that it 
	 * ends with \ (or /).
	 * 
	 * @return String
	 */
	public static String getTemporaryDirectory()
	{
		return FileDirUtils.TEMP_DIRECTORY;
	}

	public static String copyStreamToString( final OutputStream output )
	{
		if ( output == null ) return null;
		
		StringBuffer sb = new StringBuffer();
		
	    @SuppressWarnings("resource")
		Scanner s = new Scanner((Readable) output).useDelimiter("\\A");
	    
	    while ( s.hasNext() )
	    {
	    	sb.append(s.next());
	    }
	    
	    s.close();
	    return sb.toString();
		
	}
	
	public static String copyStreamToString( final InputStream input )
	{
		if ( input == null ) return null;
		
		StringBuffer sb = new StringBuffer();
		
	    @SuppressWarnings("resource")
		Scanner s = new Scanner(input).useDelimiter("\\A");
	    
	    while ( s.hasNext() )
	    {
	    	sb.append(s.next());
	    }
	    
	    s.close();
	    return sb.toString();
	}
	
	/**
	 * Copy any input stream to output file. Once the data will be copied
	 * the stream will be closed.
	 * 
	 * @param input  - InputStream to copy from
	 * @param output - File to copy to
	 * @throws java.io.IOException - error in function
	 */
	public static void copyStreamToFile( final InputStream input, final File output ) throws IOException
	{
		FileOutputStream foutOutput;

		// open input file as stream safe - it can throw some IOException
		try
		{
			foutOutput = new FileOutputStream(output);
		}
		catch (IOException ioExec)
		{
			/*if (foutOutput != null)
			{
				try
				{
					foutOutput.close();
				}
				catch (IOException ioExec2)
				{

				}
			}*/          

			throw ioExec;
		}

		// all streams including os are closed in copyStreamToStream function 
		// in any case
		FileDirUtils.copyStreamToStream(input, foutOutput);
	}

	/**
	 * Copy any input stream to output stream. Once the data will be copied
	 * both streams will be closed.
	 * 
	 * @param input  - InputStream to copy from
	 * @param output - OutputStream to copy to
	 * @throws java.io.IOException - io error in function
	 */
	public static void copyStreamToStream( final InputStream input, final OutputStream output ) throws IOException
	{
		InputStream is = null;
		OutputStream os = null;
		int ch;

		try
		{
			if (input instanceof BufferedInputStream) is = input;
			else is = new BufferedInputStream(input);

			if (output instanceof BufferedOutputStream) os = output;
			else os = new BufferedOutputStream(output);

			while ((ch = is.read()) != -1) os.write(ch);
			os.flush();
		}
		finally
		{
			IOException exec1 = null;
			IOException exec2 = null;
			try
			{
				// because this close can throw exception we do next close in 
				// finally statement
				if (os != null)
				{
					try
					{
						os.close();
					}
					catch (IOException exec)
					{
						exec1 = exec;
					}
				}
			}
			finally
			{
				if (is != null)
				{
					try
					{
						is.close();
					}
					catch (IOException exec)
					{
						exec2 = exec;
					}
				}
			}
			if ((exec1 != null) && (exec2 != null)) throw exec1;
			else if (exec1 != null) throw exec1;
			else if (exec2 != null) throw exec2;
		}
	}

	public static String resourcePath( final String subPath ) throws IOException
	{
		String foundResource = null;

		final File jarFile = new File(FileDirUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath());

		if( jarFile.isFile() )
		{  // Run with JAR file
			final JarFile jar = new JarFile(jarFile);
			final Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
			while(entries.hasMoreElements())
			{
				final String name = entries.nextElement().getName();
				if ( TextManager.validString(subPath) )
					if ( name.startsWith(subPath + File.separator) ) foundResource = name;
			}
			jar.close();
		}
		else
		{ // Run with IDE
			final URL url = Launcher.getLauncher().getClassLoader().getResource(File.separator + subPath);
			if (url != null)
			{
				try
				{
					foundResource = url.toURI().getPath();
				}
				catch (URISyntaxException ignored)
				{}
			}
			else
			{
				foundResource = jarFile.getAbsolutePath().replaceAll("target/classes", "src");
				if ( TextManager.validString(subPath) )
					foundResource += File.separator + subPath;
			}
		}
		return foundResource;
	}

	public static String joinPath( final String ... pathSegments )
	{
		String resultantPath = null;

		if ( pathSegments.length >= 1 ) resultantPath = pathSegments[0];

		for ( int loop = 1; loop < pathSegments.length; ++loop )
		{
			resultantPath += File.separator + pathSegments[loop];
		}

		if ( resultantPath != null ) resultantPath += File.separator;
		return resultantPath;
	}
}
