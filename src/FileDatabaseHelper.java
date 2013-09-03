
import java.util.Arrays;
import java.util.HashMap;


/**
 * A set of methods to help deal with data to and from the database.
 */
public class FileDatabaseHelper
{
	/**
	 * Update the database file-list with the list passed.
	 * 
	 * @param dbFilesList
	 *            Hash-map to be updated.
	 * @param filesList
	 *            Array of {@link FileObject} to use for the update.
	 * @return A Hash-map of the updated file-list.
	 */
	public HashMap<String, FileObject> updateDBList( HashMap<String, FileObject> dbFilesList, FileObject[] filesList )
	{
		dbFilesList = new HashMap<String, FileObject>();
		
		// update list with the path as a key, and the file object as the value.
		for ( FileObject file : filesList )
			dbFilesList.put( file.getPathString(), new FileObject( file.getPathString() ) );
		
		return dbFilesList;
	}
	
	/**
	 * Convert the DB file-list passed from hash-map to 2D string array.
	 * 
	 * @param dbFilesList
	 *            Hash-map to be parsed.
	 * @param fullUpdate
	 *            Flag disables getting the file info from the disk (meta-data
	 *            of files).
	 * @return A 2D array of file-info.
	 */
	public String[][] convertMapTo2D( HashMap<String, FileObject> dbFilesList, boolean fullUpdate )
	{
		// create 2D array with the rows as the file-count, and columns as the info needed to be saved.
		String[][] fileData = new String[dbFilesList.size()][3];
		
		// get the file-objects and save them into an array.
		FileObject[] dbFilesArray = dbFilesList.values().toArray( new FileObject[] {} );
		
		// sort the file-object array.
		Arrays.sort( dbFilesArray );
		
		// fill the 2D array.
		for ( int i = 0; i < fileData.length; i++ )
		{
			fileData[i][0] = dbFilesArray[i].getPathString();
			fileData[i][1] = fullUpdate ? dbFilesArray[i].getSize() + "" : dbFilesArray[i].getSizeNoMeta() + "";
			fileData[i][2] = fullUpdate ? dbFilesArray[i].getTimeStamp() + "" : dbFilesArray[i].getTimeStampNoMeta() + "";
		}
		
		return fileData;
	}
	
	/**
	 * Convert the 2D array of file-info into a hash-map.
	 * 
	 * @param fileData
	 *            A 2D array of file-info.
	 * @return A hash-map containing {@link FileObject} with their paths as key.
	 */
	public HashMap<String, FileObject> convert2DToMap( String[][] fileData )
	{
		HashMap<String, FileObject> dbFilesList = new HashMap<String, FileObject>();
		
		FileObject tempFile;	// file-object from the path in the database.
		
		// go through the rows in the array.
		for ( String[] element : fileData )
		{
			tempFile = new FileObject( element[0] );	// create file-object.
			
			// set the saved size and time-stamp.
			tempFile.setSize( Long.parseLong( element[1] ) );
			tempFile.setTimeStamp( Long.parseLong( element[2] ) );
			
			// add the file-object to the list.
			dbFilesList.put( element[0], tempFile );
		}
		
		return dbFilesList;
	}
}
