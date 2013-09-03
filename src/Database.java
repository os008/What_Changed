import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.JOptionPane;


/**
 * Represents an SQLite database file and its connection. </br> Can perform
 * common SQL operations using methods defined.
 */
public class Database
{
	/**
	 * A connection to the database, which can be used to control it.
	 */
	Connection			connection;

	/**
	 * Used to send an SQL statement to the {@link Database#connection}.
	 */
	Statement			statement;

	/**
	 * Used to form a template for an SQL statement, then fill missing
	 * parameters, then be sent to the {@link Database#connection}.
	 */
	PreparedStatement	preparedStatement;

	/**
	 * Flag for the existence of the database physical file.
	 */
	boolean				exists;

	/**
	 * Creates an SQLite database file and connection. Only creates a connection
	 * if the file already exists. </br> It also sets auto-commit to false; so
	 * that communication becomes faster. Committing is done after each large
	 * operation is finished.
	 *
	 * @param dbFileName
	 *            The desired physical file-name for the database.
	 */
	public Database( String dbFileName )
	{
		try
		{
			exists = new File( dbFileName ).exists();		// checks existence of file.

			Class.forName( "org.sqlite.JDBC" );		// initialise SQLite JDBC.

			connection = DriverManager.getConnection( "jdbc:sqlite:" + dbFileName );		// load/create database file.

			connection.setAutoCommit( false );		// make bulk operations run faster.

			statement = connection.createStatement();		// used to pass queries to db.
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog( null, e.getMessage(), "ERROR!", JOptionPane.ERROR_MESSAGE );
		}

	}

	/**
	 * Passes a default file-name (database.db) to {@link Database#Database(String)}
	 * .
	 */
	public Database()
	{
		this( "database.db" );
	}

	/**
	 * Creates a new table in the database, or makes sure the table is already
	 * created.
	 *
	 * @param tableName
	 *            name of the table to be created.
	 * @param columnsArray
	 *            an array of the names of columns in the table in the form <br>
	 *            <code>[column-name] [type]</code>
	 * @param primaryKey
	 *            primary key column
	 * @param foreignKeysArray
	 *            an array of names of columns to be used as foreign keys, in
	 *            the form<br>
	 *            <code>( [column-name] ) REFERENCES [table]( [column-name-there] )</code>
	 */
	public void createTable( String tableName, String[] columnsArray, String primaryKey, String[] foreignKeysArray )
	{
		// check if tables were created before.
		try
		{
			// String to form the columns portion of the SQL statement.
			String columns = "";

			if ( columnsArray != null )
			{
				// form the columns.
				for ( int i = 0; i < columnsArray.length; i++ )
				{
					columns += columnsArray[i] + ( ( i + 1 ) < columnsArray.length ? ", " : "" );
				}
			}

			// String to form the foreign keys portion of the SQL statement.
			String foreignKeys = "";

			if ( foreignKeysArray != null )
			{
				foreignKeys = ", FOREIGN KEY ";

				// form the columns.
				for ( int i = 0; i < foreignKeysArray.length; i++ )
				{
					foreignKeys += foreignKeysArray[i] + ( ( i + 1 ) < foreignKeysArray.length ? ", " : "" );
				}
			}

			String createStatement = "CREATE TABLE " + tableName + "( " + columns + ", PRIMARY KEY ( " + primaryKey + " )" + foreignKeys + " )";

			// read meta-data of the database.
			DatabaseMetaData dbMetaData = connection.getMetaData();

			// reads database tables.
			ResultSet result = dbMetaData.getTables( null, null, tableName, null );

			// if there's no such table.
			if ( !result.next() )
				statement.executeUpdate( createStatement );		// create.
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog( null, e.getMessage(), "ERROR!", JOptionPane.ERROR_MESSAGE );
		}
	}

	/**
	 * Uses the 'INSERT' statement to add a new row into the database under the
	 * table passed. </br> This method passes only 'VARCHAR' to the database;
	 * for now.
	 *
	 * @param table
	 *            The table the row is going to be added to.
	 * @param array
	 *            An array with column values at each index. MUST be in order of
	 *            columns of the table.
	 * @return True if operation was successful.
	 */
	public <T> boolean insertRecord( String table, T[] array )
	{
		// execute INSERT.
		try
		{
			// flag for the success of the insertion operation.
			boolean success = false;

			// String to form the 'VALUES' portion of the SQL statement.
			String values = "'";

			// form 'VALUES'.
			for ( int i = 0; i < array.length; i++ )
			{
				values += array[i] + ( ( i + 1 ) < array.length ? "', '" : "'" );
			}

			// execute the statement using the values built, and store the success.
			success = statement.executeUpdate( "INSERT INTO " + table + " VALUES( " + values + " )" ) > 0;

			connection.commit();	// make sure data is saved lest a crash occurs and data is lost.

			return success;
		}
		catch ( Exception e )
		{
			JOptionPane.showMessageDialog( null, e.getMessage(), "ERROR!", JOptionPane.ERROR_MESSAGE );
		}

		return false;
	}

	/**
	 * Uses the 'INSERT' statement to add new rows into the database under the
	 * table passed. </br> This method passes only 'VARCHAR' to the database;
	 * for now.
	 *
	 * @param table
	 *            The table the rows are going to be added to.
	 * @param array
	 *            A 2D array of the column values to be passed. Each entry in
	 *            the array is an array of values (row), IN-ORDER of columns.
	 * @return True if operation was successful.
	 */
	public <T> boolean batchInsertRecords( String table, T[][] array )
	{
		// execute INSERT.
		try
		{
			// String to form the 'VALUES' portion of the SQL statement.
			String values = "'";

			// form 'VALUES'.
			for ( T[] element : array )
			{
				for ( int i = 0; i < element.length; i++ )
				{
					values += element[i] + ( ( i + 1 ) < element.length ? "', '" : "'" );
				}

				statement.addBatch( "INSERT INTO " + table + " VALUES( " + values + " )" );

				values = "'";
			}

			connection.commit();		// not sure why, but using commit BEFORE execute here gives the 'expected' result.

			// submit the batch, then check if any rows were affected.
			return statement.executeBatch()[0] > 0;
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog( null, e.getMessage(), "ERROR!", JOptionPane.ERROR_MESSAGE );
		}

		return false;
	}

	/**
	 * Uses 'SELECT' SQL statement to query the database for rows matching the
	 * criteria.
	 *
	 * @param table
	 *            The table to search in.
	 * @param columns
	 *            An array of column names to search in. '*' can be used to
	 *            search in all columns. Either '*' or a list of columns; not
	 *            both.
	 * @param condition
	 *            An SQL formatted condition. Can be 'null'.
	 * @return A 2D array of rows. Each entry in the array is an array of values
	 *         (row).
	 */
	public String[][] viewRecord( String table, String[] columns, String condition )
	{
		// execute SELECT.
		try
		{
			// used to form the columns criteria.
			String columnsString = "";

			// form the columns criteria.
			for ( int i = 0; i < columns.length; i++ )
			{
				columnsString += columns[i] + ( ( i + 1 ) < columns.length ? ", " : "" );
			}

			// execute the statement, and assign the result.
			ResultSet result = statement.executeQuery( "SELECT " + columnsString + " FROM " + table
					+ ( condition != null ? " WHERE " + condition : "" ) );

			// used to count the number of rows returned to create the array.
			int counter = 0;

			// count the rows returned.
			while ( result.next() )
				counter++;

			// re-run the result to re-set the iterator. (need to do a clean code using RowSet)
			result = statement.executeQuery( "SELECT " + columnsString + " FROM " + table + ( condition != null ? " WHERE " + condition : "" ) );

			// saves the result. Set the array's size to the number of rows returned. Each inner arrays' size is the number of columns. Table.
			String[][] resultArray = new String[counter][result.getMetaData().getColumnCount()];

			// copy the result to the array.
			for ( int i = 0; i < resultArray.length; i++ )
			{
				result.next();

				for ( int j = 0; j < resultArray[i].length; j++ )
					resultArray[i][j] = result.getString( j + 1 );
			}

			return resultArray;
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog( null, e.getMessage(), "ERROR!", JOptionPane.ERROR_MESSAGE );
		}

		return null;
	}

	/**
	 * Checks if the database connection is still up.
	 *
	 * @return true if the database connection is still up.
	 */
	public boolean isConnected()
	{
		try
		{
			if ( !connection.isClosed() )
				return true;
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog( null, e.getMessage(), "ERROR!", JOptionPane.ERROR_MESSAGE );
		}

		return false;
	}

	/**
	 * Commits any pending operations, and closes the database connection.
	 */
	public void disconnect()
	{
		try
		{
			if ( !connection.getAutoCommit() )
				connection.commit();

			connection.close();
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog( null, e.getMessage(), "ERROR!", JOptionPane.ERROR_MESSAGE );
		}
	}
}
