import java.util.Arrays;
import java.util.HashMap;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;


/**
 * Represents the latest state of the files.
 */
public class NewState
{
	
	/** Array of latest files in their latest state. */
	FileObject[]				filesArray;
	
	/** A visual table to list the files. */
	JTable						stateTable;
	
	/** Table's model. */
	DefaultTableModel			tableModel;
	
	/** Column names in the table. */
	String[]					columnNames;
	
	/** Table data as a 2D array. */
	String[][]					tableData;
	
	/** Changed files as a hash-map. */
	HashMap<FileObject, String>	changedFiles;
	
	/** Old files' list. */
	FileObject[]				dbFilesList;
	
	/**
	 * Instantiates a new state.
	 * 
	 * @param dbFilesList
	 *            Old files' list.
	 * @param filesArray
	 *            Latest files' list.
	 */
	public NewState( FileObject[] dbFilesList, FileObject[] filesArray )
	{
		changedFiles = new HashMap<FileObject, String>();
		
		this.filesArray = filesArray;
		
		this.dbFilesList = dbFilesList;
		
		// sort the file-list.
		Arrays.sort( this.filesArray );
		
		createTable();
	}
	
	/**
	 * Creates the table; updates files' objects properties, fill array of
	 * files' data, calculate difference in size, update list.
	 */
	public void createTable()
	{
		columnNames = new String[] { "Size", "Time-stamp", "Difference" };
		tableData = new String[filesArray.length][3];
		
		readFilesMetaData();
		
		fillFileDataArray();
		
		calculateDifference();
		
		updateChangedFilesList();
		
		// create model and table from model.
		tableModel = new DefaultTableModel( tableData, columnNames );
		stateTable = new JTable( tableModel );
		
		// set columns to be right aligned.
		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment( SwingConstants.RIGHT );
		stateTable.getColumnModel().getColumn( 0 ).setCellRenderer( rightRenderer );
		stateTable.getColumnModel().getColumn( 1 ).setCellRenderer( rightRenderer );
		stateTable.getColumnModel().getColumn( 2 ).setCellRenderer( rightRenderer );
		
		// columns can't be selected.
		stateTable.setColumnSelectionAllowed( false );
		stateTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		
		//( (DefaultTableCellRenderer) stateTable.getDefaultRenderer( tableModel.getColumnClass( 1 ) ) ).setHorizontalAlignment(SwingConstants.RIGHT);
	}
	
	/**
	 * Update files' properties from disc.
	 */
	public void readFilesMetaData()
	{
		for ( FileObject file : filesArray )
			file.readMetaData();
	}
	
	/**
	 * Fills file-data array with size and time of file.
	 */
	public void fillFileDataArray()
	{
		for ( int i = 0; i < tableData.length; i++ )
		{
			tableData[i][0] = filesArray[i].exists() ? filesArray[i].getSizeString() : "--";
			tableData[i][1] = filesArray[i].exists() ? filesArray[i].getTimeString() : "--";
		}
	}
	
	/**
	 * Calculate difference in files' sizes.
	 */
	public void calculateDifference()
	{
		long oldStateSize;
		long newStateSize;
		
		// sort old files' list.
		Arrays.sort( dbFilesList );
		
		long difference;
		
		double percentChange;
		
		// go through the files.
		for ( int i = 0; i < filesArray.length; i++ )
		{
			// get old and new sizes, and calculate difference.
			oldStateSize = dbFilesList[i].getSizeNoMeta();
			newStateSize = filesArray[i].getSizeNoMeta();
			difference = newStateSize - oldStateSize;
			
			// percentage of change in file-size.
			percentChange = ( difference / ( oldStateSize * 1.0 ) ) * 100;
			
			// set the file's percentage in the table's data array.
			if ( filesArray[i].existsNoMeta() )
				tableData[i][2] = String.format( "%.2f %%", (float) percentChange );
			else
				// if the file was deleted, display '--'.
				tableData[i][2] = String.format( "%s", "--" );
		}
	}
	
	/**
	 * Update changed files list, with the type of change.
	 */
	public void updateChangedFilesList()
	{
		long oldStateSize;
		long newStateSize;
		
		long difference;
		
		boolean oldExisted;
		boolean newExists;
		
		// set the new state of each file.
		for ( int i = 0; i < filesArray.length; i++ )
		{
			oldStateSize = dbFilesList[i].getSizeNoMeta();
			newStateSize = filesArray[i].getSizeNoMeta();
			difference = newStateSize - oldStateSize;
			
			oldExisted = dbFilesList[i].existsNoMeta();
			newExists = filesArray[i].existsNoMeta();
			
			if ( oldExisted && !newExists )
				changedFiles.put( dbFilesList[i], "deleted" );
			else
				if ( !oldExisted && newExists )
					changedFiles.put( dbFilesList[i], "created" );
				else
					if ( ( difference != 0 ) || ( dbFilesList[i].getTimeStampNoMeta() != filesArray[i].getTimeStampNoMeta() ) )
						changedFiles.put( dbFilesList[i], "modified" );
		}
	}
}
