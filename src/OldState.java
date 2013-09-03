import java.util.Arrays;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;


/**
 * Represents the latest state of the files.
 */
public class OldState
{
	
	/** Visual table for the state. */
	JTable				stateTable;
	
	/** Table model. */
	DefaultTableModel	tableModel;
	
	/** List of column names. */
	String[]			columnNames;
	
	/** Table data as a 2D array. */
	String[][]			tableData;
	
	/** Files list. */
	FileObject[]		dbFilesList;
	
	/**
	 * Instantiates an old state.
	 * 
	 * @param dbFilesList
	 *            Files list.
	 */
	public OldState( FileObject[] dbFilesList )
	{
		this.dbFilesList = dbFilesList;
		
		Arrays.sort( this.dbFilesList );
		
		tableData = new String[dbFilesList.length][3];
		
		convertFilesArray2D();
		
		createTable();
	}
	
	/**
	 * Fills file-data array with name, size, and time of file.
	 */
	public void convertFilesArray2D()
	{
		boolean asteriskFlag = false;
		
		for ( int i = 0; i < tableData.length; i++ )
		{
			// check if the file-time has changed. If true, set flag to add an asterisk.
			if ( dbFilesList[i].getTimeStampNoMeta() != dbFilesList[i].file.lastModified() )
				asteriskFlag = true;
			
			tableData[i][0] = ( asteriskFlag ? "* " : "" ) + dbFilesList[i].getPathString();		// add an asterisk before the file if it has changed.
			tableData[i][1] = dbFilesList[i].existsNoMeta() ? dbFilesList[i].getSizeString() : "--";
			tableData[i][2] = dbFilesList[i].existsNoMeta() ? dbFilesList[i].getTimeString() : "--";
			
			// reset flag.
			asteriskFlag = false;
		}
	}
	
	/**
	 * Creates the table.
	 */
	public void createTable()
	{
		columnNames = new String[] { "Path", "Size", "Time-stamp" };
		
		tableModel = new DefaultTableModel( tableData, columnNames );
		stateTable = new JTable( tableModel );
		
		// set the columns' alignment to the right.
		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment( SwingConstants.RIGHT );
		stateTable.getColumnModel().getColumn( 1 ).setCellRenderer( rightRenderer );
		stateTable.getColumnModel().getColumn( 2 ).setCellRenderer( rightRenderer );
		
		// columns can't be selected.
		stateTable.setColumnSelectionAllowed( false );
		stateTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		
		//( (DefaultTableCellRenderer) stateTable.getDefaultRenderer( tableModel.getColumnClass( 1 ) ) ).setHorizontalAlignment(SwingConstants.RIGHT);
	}
	
}
