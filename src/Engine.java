import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JOptionPane;
import javax.swing.tree.TreePath;


public class Engine
{
	/**
	 * Saves the file paths with their TreePath object for easy reload of the
	 * tree later.
	 */
	public static HashMap<String, TreePath>		fileTreePaths;
	
	/**
	 * Saves the file list as FileObject.
	 */
	public static FileObject[]					filesList;
	
	/**
	 * The file-object related to the path. How the files are saved in the
	 * database.
	 */
	public static HashMap<String, FileObject>	dbFilesList;
	
	/**
	 * List of folders to be watched for changes.
	 */
	private static HashMap<Folder, FileWatcher>	fileWatcherMap;
	
	/**
	 * List of drives to be used to build the tree.
	 */
	public static TreeSet<String>				drives;
	
	/**
	 * Reference to the main application window.
	 */
	public static MainUI						mainUI;
	
	/**
	 * Flag for the watchers operation.
	 */
	public static boolean						watchersWorking;
	
	/**
	 * Executor for the watchers.
	 */
	private static ExecutorService				executor;
	
	/**
	 * Executor for the notification windows.
	 */
	private static ExecutorService				executorNotifications	= Executors.newCachedThreadPool();
	
	/**
	 * Number of notification windows up.
	 */
	public static int							notificationCounter		= 1;
	
	/**
	 * Number of files changed.
	 */
	public static int							changedCounter;
	
	/**
	 * Database reference.
	 */
	public static FileDatabase					database;
	
	/**
	 * Methods to help deal with data to and from the database.
	 */
	public static FileDatabaseHelper			fileDBHelper;
	
	/**
	 * Flag for database connection.
	 */
	public static boolean						databaseConnected;
	
	/**
	 * Flag for missing drives list.
	 */
	public static boolean						needDrives				= true;
	
	/**
	 * List of files initialised.
	 */
	public static boolean						listInitialised;
	
	/**
	 * List changed; need to update states.
	 */
	public static boolean						statesOutDated;
	
	/**
	 * Reference to the up-to-date state of files.
	 */
	public static NewState						newState;
	
	/**
	 * Reference to the snap-shot state of files.
	 */
	public static OldState						oldState;
	
	/**
	 * When was the snapshot taken.
	 */
	public static String						snapTime;
	
	/**
	 * Saves the list of files as objects.
	 */
	public static final String					FILES_SYS_FILE			= "data/files.ser";
	
	/**
	 * Saves the list of drives.
	 */
	public static final String					DRIVES_SYS_FILE			= "data/drives.ser";
	
	/**
	 * Database for the list of files.
	 */
	public static final String					DB_SYS_FILE				= "data/database.db";
	
	/**
	 * Saves the snapshot time.
	 */
	public static final String					SNAP_TIME_SYS_FILE		= "data/snaptime.ser";
	
	/**
	 * Program's current version.
	 */
	public static final String					VERSION					= "2.03.0050";
	
	/**
	 * Prepares data.
	 */
	public static void initialise()
	{
		File dataDir = new File( "data" );		// data folder.
		
		// check that the data folder exists, create if necessary.
		if ( !dataDir.exists() )
			if ( !dataDir.mkdir() )
			{
				JOptionPane.showMessageDialog( null, "Failed to create 'data' folder, please create it manually.", "ERROR!",
						JOptionPane.ERROR_MESSAGE );
				System.exit( 1 );		// exit the program if it doesn't exist.
			}
		
		loadData();
		
		// if the drives list is empty, set the flag.
		if ( drives.size() > 0 )
			needDrives = false;
		
		File dbFile = new File( DB_SYS_FILE );	// database file.
		
		fileDBHelper = new FileDatabaseHelper();		// methods to deal with the database data.
		
		// make sure database file exists, then read it.
		if ( dbFile.exists() )
			readDatabase();
	}
	
	/**
	 * Loads all saved data (non-database related).
	 */
	@SuppressWarnings( "unchecked" )
	public static void loadData()
	{
		fileTreePaths = new HashMap<String, TreePath>();
		drives = new TreeSet<String>();
		
		FileInputStream fileStream;		// incoming link to file.
		ObjectInputStream objectStream;		// link to objects read from file.
		
		try
		{
			
			////////////////////////////////////////////////////////////////////////////////////////
			// load list of files.
			//======================================================================================
			
			fileStream = new FileInputStream( FILES_SYS_FILE );
			objectStream = new ObjectInputStream( fileStream );
			
			fileTreePaths = (HashMap<String, TreePath>) objectStream.readObject();
			
			objectStream.close();
			
			////////////////////////////////////////////////////////////////////////////////////////
			// load list of drives.
			//======================================================================================
			
			fileStream = new FileInputStream( DRIVES_SYS_FILE );
			objectStream = new ObjectInputStream( fileStream );
			
			drives = (TreeSet<String>) objectStream.readObject();
			
			objectStream.close();
			
			////////////////////////////////////////////////////////////////////////////////////////
			// load snapshot time.
			//======================================================================================
			
			fileStream = new FileInputStream( SNAP_TIME_SYS_FILE );
			objectStream = new ObjectInputStream( fileStream );
			
			snapTime = (String) objectStream.readObject();
			
			objectStream.close();
		}
		catch ( Exception e )
		{	
			
		}
	}
	
	/**
	 * Loads files' information from database.
	 */
	public static void readDatabase()
	{
		database = new FileDatabase( DB_SYS_FILE );
		
		// convert the list read from the database into the format used in this program.
		dbFilesList = fileDBHelper.convert2DToMap( database.viewRecord( "files", new String[] { "*" }, null ) );
	}
	
	/**
	 * Creates a report of files' states.
	 */
	public static void generateReport()
	{
		updateFileList();	// make sure the list is read from the UI and up-to-date.
		
		// if the list is sync'd, and the file list has files, then create report.
		if ( !statesOutDated && ( ( filesList != null ) && ( filesList.length > 0 ) ) )
		{
			createOldState();
			createNewState();
			
			new ReportUI( oldState, newState );		// show report.
		}
	}
	
	/**
	 * Creates a current state of files.
	 */
	public static void createNewState()
	{
		FileObject[] dbFilesArray = dbFilesList.values().toArray( new FileObject[] {} );	// convert snapped file paths to file-objects to be passed to state constructor.
		
		// compare states and make object to that effect.
		newState = new NewState( dbFilesArray, filesList );
	}
	
	/**
	 * Creates a files' state at snapshot time.
	 */
	public static void createOldState()
	{
		oldState = new OldState( dbFilesList.values().toArray( new FileObject[] {} ) );		// create state from saved files list after conversion to appropriate format.
	}
	
	/**
	 * Read file-list from the GUI, and update {@link Engine#filesList}.
	 */
	public static void updateFileList()
	{
		ArrayList<FileObject> filesArrayList = new ArrayList<FileObject>();		// temp array to save file-list. (variable sized)
		
		// get list from UI and save it to array.
		for ( CheckListEntry checkListEntry : mainUI.fileList )
			filesArrayList.add( checkListEntry.getFile() );
		
		filesList = filesArrayList.toArray( new FileObject[0] );	// convert array-list to array.
	}
	
	/**
	 * Save file-list to DB.
	 * 
	 * @param fullUpdate
	 *            Makes the update an up-to-date one.
	 */
	public static void updateDatabase( boolean fullUpdate )
	{
		updateFileList();		// make sure file-list if up-to-date.
		
		File dbFile = new File( DB_SYS_FILE );		// database file.
		
		// delete database and start fresh.
		if ( dbFile.exists() )
		{
			database.disconnect();
			
			dbFile.delete();
		}
		
		// if there're no files to be reported, then no need to create empty DB.
		if ( ( filesList == null ) || ( filesList.length <= 0 ) )
			return;
		
		database = new FileDatabase( DB_SYS_FILE );		// create new DB.
		
		// use new list when needed.
		if ( fullUpdate )
			dbFilesList = fileDBHelper.updateDBList( dbFilesList, filesList );
		
		// make sure database is up, then save files after conversion to appropriate format.
		if ( database.isConnected() )
			database.batchInsertRecords( "files", fileDBHelper.convertMapTo2D( dbFilesList, fullUpdate ) );
	}
	
	/**
	 * Sets {@link Engine#snapTime} to when the snapshot was taken.
	 */
	public static void setSnapTime()
	{
		DateFormat format = new SimpleDateFormat( "dd / MMM - HH:mm:ss a" );
		
		snapTime = format.format( new Date() );
	}
	
	/**
	 * Creates watchers for a file list.
	 * 
	 * @param setFileObjects
	 *            A set of file paths to be watched.
	 */
	public static void createWatchers( Set<String> setFileObjects )
	{
		// if watchers are already working then there's nothing to do here.
		if ( watchersWorking )
			return;
		
		Folder folder;		// folder of file in list.
		FileObject file;		// file.
		FileWatcher fileWatcher;	// watcher.
		
		fileWatcherMap = new HashMap<Folder, FileWatcher>();	// keep a list of file-watchers to pass to executor.
		
		// get the folder of the file, then create a watcher using that folder.
		for ( String filePath : setFileObjects )
		{
			file = new FileObject( filePath );
			folder = file.getFolder();
			
			// make sure the a watcher for the folder wasn't created before.
			if ( fileWatcherMap.containsKey( folder ) )
				fileWatcherMap.get( folder ).addFile( file );	// add the file to the watcher of that folder.
			else
			{
				// if watcher for folder wasn't created, then create.
				fileWatcher = new FileWatcher( folder );
				fileWatcher.addFile( file );
				fileWatcherMap.put( folder, fileWatcher );
			}
		}
		
		// iterator for file watcher list.
		Iterator<FileWatcher> fileWatchersIterator = fileWatcherMap.values().iterator();
		
		// executor for the file-watchers.
		executor = Executors.newFixedThreadPool( fileWatcherMap.values().toArray().length );
		
		// iterate over the file-watcher list.
		while ( fileWatchersIterator.hasNext() )
			executor.execute( fileWatchersIterator.next() );	// run each file-watcher.
			
		watchersWorking = true;		// set flag to indicate monitoring is up.
	}
	
	/**
	 * Updates UI components concerned with monitoring.
	 * 
	 * @param file
	 *            The changed-file path.
	 * @param changeType
	 *            The kind of change done to the file (deleted, created,
	 *            modified).
	 */
	public static void updateMonitoringUI( String file, String changeType )
	{
		mainUI.updateMonitoringUI( String.format( "File %s: \"%s\".", changeType, file ), changedCounter );	// send file path, with the type of change, and the number of changed files to the UI.
		popNotification( file, changeType );	// notify the user of the change.
	}
	
	/**
	 * Shows a notification of the file change.
	 * 
	 * @param file
	 *            The changed-file path.
	 * @param changeType
	 *            The kind of change done to the file (deleted, created,
	 *            modified).
	 */
	public static void popNotification( String file, String changeType )
	{
		executorNotifications.execute( new NotificationUI( file, changeType ) );		// notification in a separate thread to continue monitoring.
	}
	
	/**
	 * Stops real-time monitoring.
	 */
	public static void disableWatchers()
	{
		// if monitoring is off, then nothing to do here.
		if ( !watchersWorking )
			return;
		
		System.out.println( "Disabling watchers!" );
		
		Iterator<FileWatcher> iterator = fileWatcherMap.values().iterator();
		
		// iterate over watcher-list.
		while ( iterator.hasNext() )
			iterator.next().terminate();	// stop each watcher.
			
		executor.shutdownNow();	// terminate executor.
		
		watchersWorking = false;		// set flag.
	}
	
	/**
	 * Saves data/database and disposes of the main application window.
	 */
	public static void exitApplication()
	{
		disableWatchers();	// stop monitoring.
		
		// make sure there's something to save.
		if ( !needDrives || ( ( fileTreePaths != null ) && ( fileTreePaths.size() > 0 ) ) )
			saveData();
		
		mainUI.frame.dispose();		// terminate UI.
		
		// update database using the list at snapshot time.
		updateDatabase( false );
		
		// make sure there's a database to disconnect.
		if ( ( database != null ) && database.isConnected() )
			database.disconnect();
		
		System.exit( 0 );		// exit.
	}
	
	/**
	 * Saves data to disc (non-database related).
	 */
	public static void saveData()
	{
		FileOutputStream fileStream;
		ObjectOutputStream objectStream;
		
		try
		{
			
			////////////////////////////////////////////////////////////////////////////////////////
			// save file-list.
			//======================================================================================
			
			fileStream = new FileOutputStream( FILES_SYS_FILE );
			objectStream = new ObjectOutputStream( fileStream );
			
			objectStream.writeObject( fileTreePaths );
			
			objectStream.close();
			
			////////////////////////////////////////////////////////////////////////////////////////
			// save drives list.
			//======================================================================================
			
			fileStream = new FileOutputStream( DRIVES_SYS_FILE );
			objectStream = new ObjectOutputStream( fileStream );
			
			objectStream.writeObject( drives );
			
			objectStream.close();
			
			////////////////////////////////////////////////////////////////////////////////////////
			// save snapshot time.
			//======================================================================================
			
			fileStream = new FileOutputStream( SNAP_TIME_SYS_FILE );
			objectStream = new ObjectOutputStream( fileStream );
			
			objectStream.writeObject( snapTime );
			
			objectStream.close();
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}
}
