import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.util.ArrayList;


/**
 * Represents a folder object, with some methods to deal with files inside.
 */
public class Folder
{
	
	/** Name of the folder. */
	private String		name;
	
	/** Path as a string. */
	private String		pathString;
	
	/** Path of the folder. */
	private Path		path;
	
	/** Folder {@link File} object. */
	private File		folder;
	
	/** Path as an array, each level is an entry in the array. */
	private String[]	pathArray;
	
	/**
	 * Instantiates a new folder from a {@link File} object.
	 * 
	 * @param folder
	 *            Folder {@link File} object.
	 */
	public Folder( File folder )
	{
		this.folder = folder;
		
		pathString = folder.getAbsolutePath();
		
		path = folder.toPath();
		
		name = folder.getName();
		
		pathArray = pathString.split( "\\\\" );
	}
	
	/**
	 * Instantiates a new folder from a string path.
	 * 
	 * @param path
	 *            Path as a string.
	 */
	public Folder( String path )
	{
		this.pathString = path;
		
		folder = new File( path );
		
		this.path = folder.toPath();
		
		name = folder.getName();
		
		pathArray = pathString.split( "\\\\" );
	}
	
	/**
	 * Gets the file object representing the folder.
	 * 
	 * @return {@link File} object.
	 */
	public File getFileObject()
	{
		return folder;
	}
	
	/**
	 * Gets the path of the folder.
	 * 
	 * @return Path
	 */
	public Path getPath()
	{
		return path;
	}
	
	/**
	 * Gets the path as an array.
	 * 
	 * @return Path array
	 */
	public String[] getPathArray()
	{
		return pathArray;
	}
	
	/**
	 * Gets the path as a string.
	 * 
	 * @return Path string
	 */
	public String getPathString()
	{
		return pathString;
	}
	
	/**
	 * Lists the files inside the folder.
	 * 
	 * @return A list of the files as a {@link CheckListEntry}.
	 */
	public CheckListEntry[] listFiles()
	{
		// criteria to accept the file into the list.
		FilenameFilter filter = new FilenameFilter()
		{
			
			@Override
			public boolean accept( File dir, String name )
			{
				File file = new File( dir.getAbsolutePath() + "\\" + name );
				return file.isFile() ? true : false;	// accept it if it's a file; not a folder.
			}
		};
		
		// list of files as File objects.
		File[] absoluteFileList = folder.listFiles( filter );
		
		// return an empty list of nothing is in the folder.
		if ( ( absoluteFileList == null ) || ( absoluteFileList.length == 0 ) )
			return new CheckListEntry[] {};
		
		ArrayList<CheckListEntry> fileList = new ArrayList<CheckListEntry>();
		
		// convert to CheckListEntry objects.
		for ( File file : absoluteFileList )
		{
			fileList.add( new CheckListEntry( new FileObject( file ) ) );
		}
		
		// convert the ArrayList to an array then return it.
		return fileList.toArray( new CheckListEntry[fileList.size()] );
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return name;
	}
}
