
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashSet;
import java.util.List;


/**
 * An object which monitors a folder for changed files, and reports to the
 * {@link Engine}.
 */
public class FileWatcher implements Runnable
{
	
	/** Watcher service responsible for watching the folder. */
	private WatchService	watcher;
	
	/** Path of the folder. */
	private Path			pathFolder;
	
	/** File names to be watched. */
	private HashSet<String>	fileNames;
	
	/** Key! */
	private WatchKey		key;
	
	/** Flag for the thread's status. */
	private boolean			runningFlag;
	
	/**
	 * List of types of changes to files.
	 */
	private enum ChangeType
	{
		
		/** File was created. */
		CREATE,
		/** File was deleted. */
		DELETE,
		/** File was modified. */
		MODIFY
	};
	
	/**
	 * Instantiates a new file watcher.
	 * 
	 * @param folder
	 *            Folder to be watched.
	 */
	public FileWatcher( Folder folder )
	{
		fileNames = new HashSet<String>();
		
		pathFolder = folder.getPath();		// gets the path of the folder to be watched.
		
		try
		{
			watcher = FileSystems.getDefault().newWatchService();		// create a watch service.
			
			// register the watcher to be notified of file changes.
			key = pathFolder.register( watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE,
					StandardWatchEventKinds.ENTRY_MODIFY );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Adds a file to watch for changes.
	 * 
	 * @param file
	 *            File to be watched.
	 */
	public void addFile( FileObject file )
	{
		fileNames.add( file.getName() );
	}
	
	/**
	 * @see java.lang.Runnable#run()
	 */
	@SuppressWarnings( { "unchecked", "rawtypes" } )
	@Override
	public void run()
	{
		runningFlag = true;		// thread is active.
		
		// if the thread is set to be active, keep getting changes events.
		while ( runningFlag )
		{
			WatchKey watckKey;
			List<WatchEvent<?>> events;
			
			try
			{
				watckKey = watcher.take();		// this will pause the loop until the system notifies of a file-change.
				events = watckKey.pollEvents();		// what are the changes?
				
				// go through all the changes.
				for ( WatchEvent event : events )
				{
					// ...
					if ( event.kind() == StandardWatchEventKinds.OVERFLOW )
					{
						continue;
					}
					
					// get the file-name of the file changed.
					String fileName = ( ( (WatchEvent<Path>) event ).context() ).getFileName().toString();
					
					// if the file was deleted then notify and exit.
					if ( event.kind() == StandardWatchEventKinds.ENTRY_DELETE )
					{
						fileChangeDetected( fileName, ChangeType.DELETE );
						break;
					}
					
					// if the file was created then notify and exit.
					if ( event.kind() == StandardWatchEventKinds.ENTRY_CREATE )
					{
						fileChangeDetected( fileName, ChangeType.CREATE );
						break;
					}
					
					// if the file modified then notify and exit.
					if ( event.kind() == StandardWatchEventKinds.ENTRY_MODIFY )
					{
						fileChangeDetected( fileName, ChangeType.MODIFY );
						break;
					}
				}
				
				// if there's a problem with the watcher, exit.
				if ( !key.reset() )
					break;
			}
			catch ( Exception e )
			{	
				
			}
		}
	}
	
	/**
	 * File change detected, notify {@link Engine}.
	 * 
	 * @param fileName
	 *            Name of the file changed.
	 * @param changeType
	 *            Change type from the enumeration.
	 */
	public void fileChangeDetected( String fileName, ChangeType changeType )
	{
		// if the file is in the list of 'to-be-watched'.
		if ( fileNames.contains( fileName ) )
		{
			// update changed files counter.
			Engine.changedCounter++;
			
			// get change type, and invoke UI updates.
			switch ( changeType )
			{
				case CREATE:
					Engine.updateMonitoringUI( pathFolder.resolve( fileName ).toString(), "created" );
					break;
				case DELETE:
					Engine.updateMonitoringUI( pathFolder.resolve( fileName ).toString(), "deleted" );
					break;
				case MODIFY:
					Engine.updateMonitoringUI( pathFolder.resolve( fileName ).toString(), "modified" );
					break;
			}
		}
	}
	
	/**
	 * Terminate thread.
	 */
	public void terminate()
	{
		runningFlag = false;
	}
}
