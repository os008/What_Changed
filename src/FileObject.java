import java.io.File;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Represents a file, with methods that facilitates dealing with its meta-data,
 * and comparison.
 */
public class FileObject implements Comparable<FileObject>
{
	
	/** Name of the file. */
	private String		name;
	
	/** Size of the file. */
	private long		size;
	
	/** Date of the last file modification. */
	private long		timeStamp;
	
	/** A File object representing the physical file. */
	File				file;
	
	/** Path of the file. */
	private Path		path;
	
	/** String representation of the file path. */
	private String		pathString;
	
	/** The folder containing the file. */
	private Folder		folder;
	
	/**
	 * Array representation of the file path; every level an entry in the array.
	 */
	private String[]	pathArray;
	
	/** Flag for the existence of the file. */
	private boolean		exists;
	
	/**
	 * Instantiates a new file object.
	 * 
	 * @param file
	 *            File object passed.
	 */
	public FileObject( File file )
	{
		this.file = file;
		
		pathString = file.getAbsolutePath();
		
		path = file.toPath();
		
		name = file.getName();
		
		pathArray = pathString.split( "\\\\" );
		
		folder = new Folder( path.getParent().toString() );		// form a new 'folder' object from the path of the file's parent.
	}
	
	/**
	 * Instantiates a new file object.
	 * 
	 * @param path
	 *            Path of the file as a string.
	 */
	public FileObject( String path )
	{
		this.pathString = path;
		
		file = new File( path );
		
		this.path = file.toPath();
		
		name = file.getName();
		
		pathArray = pathString.split( "\\\\" );
		
		folder = new Folder( this.path.getParent().toString() );
	}
	
	/**
	 * Updates file properties from disc.
	 */
	public void readMetaData()
	{
		timeStamp = file.lastModified();
		size = file.length();
		
		exists = file.exists();
	}
	
	/**
	 * Gets the name of the file.
	 * 
	 * @return Name of the file
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Gets the file object stored in 'this'.
	 * 
	 * @return {@link File} object.
	 */
	public File getFile()
	{
		return file;
	}
	
	/**
	 * Gets the folder object containing this file.
	 * 
	 * @return {@link Folder} object
	 */
	public Folder getFolder()
	{
		return folder;
	}
	
	/**
	 * Gets the path of the file.
	 * 
	 * @return {@link Path} object.
	 */
	public Path getPath()
	{
		return path;
	}
	
	/**
	 * Gets the path of the file as an array.
	 * 
	 * @return Path array.
	 */
	public String[] getPathArray()
	{
		return pathArray;
	}
	
	/**
	 * Gets the path of the file as a string.
	 * 
	 * @return Path string
	 */
	public String getPathString()
	{
		return pathString;
	}
	
	/**
	 * Sets the last modified date of the file.<br>
	 * Useful for creating an object for non-existing, or old representation of
	 * files.
	 * 
	 * @param timeStamp
	 *            Time in milliseconds since 1970
	 */
	public void setTimeStamp( long timeStamp )
	{
		this.timeStamp = timeStamp;
	}
	
	/**
	 * Gets the time stamp as a string.
	 * 
	 * @return Time string
	 */
	public String getTimeString()
	{
		DateFormat format = new SimpleDateFormat( "dd / MMM / YY - HH:mm:ss a" );
		
		return format.format( new Date( timeStamp ) );
	}
	
	/**
	 * Gets the time stamp in milliseconds since 1970.<br>
	 * This method reads from disc.
	 * 
	 * @return Time stamp in milliseconds
	 */
	public long getTimeStamp()
	{
		readMetaData();
		
		return timeStamp;
	}
	
	/**
	 * Gets the time stamp in milliseconds since 1970.<br>
	 * This method does NOT read from disc.
	 * 
	 * @return Time stamp in milliseconds
	 */
	public long getTimeStampNoMeta()
	{
		return timeStamp;
	}
	
	/**
	 * Sets the size of the file.<br>
	 * Useful for creating an object for non-existing, or old representation of
	 * files.
	 * 
	 * @param size
	 *            Size in bytes.
	 */
	public void setSize( long size )
	{
		this.size = size;
	}
	
	/**
	 * Gets the size of the file.<br>
	 * This method does NOT reads from disc.
	 * 
	 * @return Size in bytes.
	 */
	public long getSize()
	{
		readMetaData();
		
		return size;
	}
	
	/**
	 * Gets the size of the file.<br>
	 * This method does NOT read from disc.
	 * 
	 * @return Size in bytes.
	 */
	public long getSizeNoMeta()
	{
		return size;
	}
	
	/**
	 * Gets the size as a string, appending 'KiB', 'MiB', ... etc.
	 * 
	 * @return The size as a string.
	 */
	public String getSizeString()
	{
		// the size suffixes.
		String[] suffix = new String[] { "ByT", "KiB", "MiB", "GiB" };
		
		// each '1' is a 1024 bytes.
		int increment = 0;
		
		// temporary location to manipulate the size freely.
		double size = this.size;
		
		// get the smallest number representation of the size above zero.
		while ( ( size / 1024 ) >= 1 )
		{
			size = size / 1024;
			increment++;
		}
		
		// format then return the size.
		return String.format( "%.2f  %s", size, suffix[increment] );
	}
	
	/**
	 * Does the file exist?
	 * 
	 * @return true, if it exists.
	 */
	public boolean exists()
	{
		return file.exists();
	}
	
	/**
	 * Returns the flag without checking the disc.<br>
	 * It also checks the size and time stamp, if they're above zero then the
	 * file exists.
	 * 
	 * @return true, if the the flag is true.
	 */
	public boolean existsNoMeta()
	{
		return ( exists || ( size > 0 ) || ( timeStamp > 0 ) );
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return file.getName();
	}
	
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo( FileObject o )
	{
		String firstFilePath = file.getAbsolutePath();
		String secondFilePath = o.file.getAbsolutePath();
		
		String[] firstPathDivided = firstFilePath.split( "\\\\" );
		String[] secondPathDivided = secondFilePath.split( "\\\\" );
		
		if ( firstFilePath.equalsIgnoreCase( secondFilePath ) )
			return 0;
		
		if ( firstPathDivided.length == secondPathDivided.length )
		{
			for ( int i = 0; i < ( firstPathDivided.length - 1 ); i++ )
				if ( !firstPathDivided[i].equals( secondPathDivided[i] ) )
					return firstPathDivided[i].compareToIgnoreCase( secondPathDivided[i] );
			
			return firstPathDivided[firstPathDivided.length - 1].compareToIgnoreCase( secondPathDivided[secondPathDivided.length - 1] );
		}
		else
		{
			for ( int i = 0; i < ( Math.min( firstPathDivided.length, secondPathDivided.length ) - 1 ); i++ )
				if ( !firstPathDivided[i].equals( secondPathDivided[i] ) )
					return firstPathDivided[i].compareToIgnoreCase( secondPathDivided[i] );
			
			return firstPathDivided.length <= secondPathDivided.length ? -1 : 1;
		}
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals( Object o )
	{
		if ( !( o instanceof FileObject ) )
			return false;
		
		String firstFilePath = file.getAbsolutePath();
		String secondFilePath = ( (FileObject) o ).file.getAbsolutePath();
		
		if ( firstFilePath.equalsIgnoreCase( secondFilePath ) )
			return true;
		
		return false;
	}
}
