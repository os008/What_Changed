import javax.swing.JCheckBox;


/**
 * A class representing an entry in a JList. </br> Adds functionality: tick the
 * entry in the list, instead of depending on CTRL + cLick. </br> Comparable.
 */
@SuppressWarnings( "serial" )
public class CheckListEntry extends JCheckBox implements Comparable<CheckListEntry>
{
	/**
	 * File object for the list entry.
	 */
	private FileObject	file;
	
	/**
	 * Constructor; creates an object representing {@link FileObject} object, to
	 * be retrieved easily later.
	 * 
	 * @param file
	 *            {@link FileObject} object to be represented.
	 */
	public CheckListEntry( FileObject file )
	{
		this.file = file;		// set file to the file passed.
		
		setText( toString() );		// set label.
	}
	
	/**
	 * Returns the file in this list entry.
	 * 
	 * @return {@link FileObject} represented by this object.
	 */
	public FileObject getFile()
	{
		return file;
	}
	
	/**
	 * @return String representation of the absolute path of the file in this
	 *         list entry.
	 */
	@Override
	public String toString()
	{
		return file.getFile().getAbsolutePath();
	}
	
	/**
	 * Calls {@link FileObject#compareTo(FileObject)} of the file in this list
	 * entry. </br> Passes the file in the passed {@link CheckListEntry}.
	 * 
	 * @param o
	 *            {@link CheckListEntry} to be compared to.
	 */
	@Override
	public int compareTo( CheckListEntry o )
	{
		return file.compareTo( o.file );
	}
	
	/**
	 * Calls {@link FileObject#equals(Object)} of the file in this list entry.
	 * 
	 * @param o
	 *            Object to be compared to.
	 */
	@Override
	public boolean equals( Object o )
	{
		return file.equals( o );
	}
}
