/**
 * Represents a database specially tailored to this application.
 */
public class FileDatabase extends Database
{
	/**
	 * Creates an SQLite database then initialises it.
	 *
	 * @param dbFileName
	 *            The physical file-name of the database.
	 */
	public FileDatabase( String dbFileName )
	{
		super( dbFileName );	// create DB using file-name passed.
		initDB();
	}

	/**
	 * Initialises the database by creating the table.
	 */
	public void initDB()
	{
		// create table.
		createTable( "files", new String[] { "path VARCHAR(255)", "size VARCHAR(255)", "timestamp VARCHAR(255)" }, "path", null );		// make sure DB has the required table.
	}
}
