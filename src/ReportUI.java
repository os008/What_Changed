import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


@SuppressWarnings( "serial" )
public class ReportUI extends JFrame
{
	OldState	oldState;
	NewState	newState;
	
	JScrollPane	firstTableScroller;
	JScrollPane	secondTableScroller;
	
	JSplitPane	splitPane;
	
	JPanel		buttonPanel;
	JButton		closeButton;
	
	/**
	 * Create the frame.
	 */
	public ReportUI( final OldState oldState, final NewState newState )
	{
		this.oldState = oldState;
		this.newState = newState;
		
		showSummaryReport( this.newState.changedFiles );
	}
	
	private void showSummaryReport( HashMap<FileObject, String> changedFiles )
	{
		new SummaryReport( changedFiles );
	}
	
	private void showFullReport()
	{
		setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );
		setBounds( 150, 150, 800, 400 );
		setTitle( "Report" );
		getContentPane().setLayout( new BorderLayout( 0, 0 ) );
		
		splitPane = new JSplitPane();
		
		firstTableScroller = new JScrollPane( this.oldState.stateTable );
		secondTableScroller = new JScrollPane( this.newState.stateTable );
		
		splitPane.setLeftComponent( firstTableScroller );
		splitPane.setRightComponent( secondTableScroller );
		
		getContentPane().add( splitPane, BorderLayout.CENTER );
		
		////////////////////////////////////////////////////////////////////////////////////////
		// #region Close button area.
		//======================================================================================
		
		buttonPanel = new JPanel();
		
		closeButton = new JButton( "Close" );
		
		GroupLayout gl_panel = new GroupLayout( buttonPanel );
		gl_panel.setHorizontalGroup( gl_panel.createParallelGroup( Alignment.LEADING ).addGroup(
				gl_panel.createSequentialGroup().addContainerGap( 625, Short.MAX_VALUE ).addComponent( closeButton ) ) );
		gl_panel.setVerticalGroup( gl_panel.createParallelGroup( Alignment.LEADING ).addComponent( closeButton, Alignment.TRAILING,
				GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE ) );
		buttonPanel.setLayout( gl_panel );
		
		getContentPane().add( buttonPanel, BorderLayout.SOUTH );
		
		//======================================================================================
		// #endregion Close button area.
		////////////////////////////////////////////////////////////////////////////////////////
		
		Engine.mainUI.frame.setFocusableWindowState( false );
		Engine.mainUI.frame.setEnabled( false );
		
		addComponentListener( new ComponentAdapter()
		{
			@Override
			public void componentResized( ComponentEvent e )
			{
				super.componentResized( e );
				adjustColumns();
			}
			
			@Override
			public void componentShown( ComponentEvent e )
			{
				super.componentShown( e );
				adjustColumns();
			}
			
			private void adjustColumns()
			{
				splitPane.setDividerLocation( getWidth() - 370 );
				
				oldState.stateTable.getColumnModel().getColumn( 0 ).setPreferredWidth( getWidth() - 630 );
				oldState.stateTable.getColumnModel().getColumn( 1 ).setPreferredWidth( 70 );
				oldState.stateTable.getColumnModel().getColumn( 2 ).setPreferredWidth( 150 );
				
				newState.stateTable.getColumnModel().getColumn( 0 ).setPreferredWidth( 30 );
				newState.stateTable.getColumnModel().getColumn( 1 ).setPreferredWidth( 110 );
				newState.stateTable.getColumnModel().getColumn( 2 ).setPreferredWidth( 30 );
			}
		} );
		
		closeButton.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				Engine.mainUI.frame.setFocusableWindowState( true );
				Engine.mainUI.frame.setEnabled( true );
				
				dispose();
				
				Engine.mainUI.frame.toFront();
			}
		} );
		
		setVisible( true );
	}
	
	class SummaryReport extends JFrame
	{
		HashMap<FileObject, String>	changedFiles;
		TreeSet<String>				deletedFiles;
		TreeSet<String>				modifiedFiles;
		TreeSet<String>				createdFiles;
		
		JList<String>				deletedFilesList;
		JScrollPane					deletedListScrollPane;
		
		JList<String>				modifiedFilesList;
		JScrollPane					modifiedListScrollPane;
		
		JList<String>				createdFilesList;
		JScrollPane					createdListScrollPane;
		
		JButton						fullReportButton;
		JButton						closeButton;
		
		public SummaryReport( HashMap<FileObject, String> changedFiles )
		{
			Box verticalBox = Box.createVerticalBox();
			
			setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
			setBounds( 200, 200, 800, 400 );
			setTitle( "Summary" );
			
			this.changedFiles = changedFiles;
			
			if ( this.changedFiles.size() <= 0 )
			{
				JOptionPane.showMessageDialog( this, "All files are intact; nothing has changed.", "No changes!", JOptionPane.INFORMATION_MESSAGE );
				return;
			}
			
			createdFiles = new TreeSet<String>();
			modifiedFiles = new TreeSet<String>();
			deletedFiles = new TreeSet<String>();
			
			splitList();
			
			////////////////////////////////////////////////////////////////////////////////////////
			// #region Create/init lists.
			//======================================================================================
			
			createdFilesList = new JList<String>( createdFiles.toArray( new String[] {} ) );
			
			if ( createdFiles.size() > 0 )
			{
				createdListScrollPane = new JScrollPane( createdFilesList );
				createdListScrollPane.setBorder( BorderFactory.createTitledBorder( "Created files (" + createdFiles.size() + "):" ) );
				verticalBox.add( createdListScrollPane );
			}
			
			modifiedFilesList = new JList<String>( modifiedFiles.toArray( new String[] {} ) );
			
			if ( modifiedFiles.size() > 0 )
			{
				modifiedListScrollPane = new JScrollPane( modifiedFilesList );
				modifiedListScrollPane.setBorder( BorderFactory.createTitledBorder( "Modified files (" + modifiedFiles.size() + "):" ) );
				verticalBox.add( modifiedListScrollPane );
			}
			
			deletedFilesList = new JList<String>( deletedFiles.toArray( new String[] {} ) );
			
			if ( deletedFiles.size() > 0 )
			{
				deletedListScrollPane = new JScrollPane( deletedFilesList );
				deletedListScrollPane.setBorder( BorderFactory.createTitledBorder( "Deleted files (" + deletedFiles.size() + "):" ) );
				verticalBox.add( deletedListScrollPane );
			}
			
			//======================================================================================
			// #endregion Create/init lists.
			////////////////////////////////////////////////////////////////////////////////////////
			
			fullReportButton = new JButton( "Show full report" );
			closeButton = new JButton( "Close" );
			
			JPanel buttonsPanel = new JPanel();
			buttonsPanel.add( fullReportButton );
			buttonsPanel.add( closeButton );
			
			verticalBox.add( buttonsPanel );
			
			////////////////////////////////////////////////////////////////////////////////////////
			// #region Action-listeners.
			//======================================================================================
			
			createdFilesList.addListSelectionListener( new ListSelectionListener()
			{
				
				@Override
				public void valueChanged( ListSelectionEvent e )
				{
					createdFilesList.clearSelection();
				}
			} );
			
			modifiedFilesList.addListSelectionListener( new ListSelectionListener()
			{
				
				@Override
				public void valueChanged( ListSelectionEvent e )
				{
					modifiedFilesList.clearSelection();
				}
			} );
			
			deletedFilesList.addListSelectionListener( new ListSelectionListener()
			{
				
				@Override
				public void valueChanged( ListSelectionEvent e )
				{
					deletedFilesList.clearSelection();
				}
			} );
			
			fullReportButton.addActionListener( new ActionListener()
			{
				
				@Override
				public void actionPerformed( ActionEvent e )
				{
					dispose();
					showFullReport();
				}
			} );
			
			closeButton.addActionListener( new ActionListener()
			{
				
				@Override
				public void actionPerformed( ActionEvent e )
				{
					dispose();
				}
			} );
			
			//======================================================================================
			// #endregion Action-listeners.
			////////////////////////////////////////////////////////////////////////////////////////
			
			getContentPane().add( verticalBox );
			
			setVisible( true );
			pack();
		}
		
		/**
		 * Puts each file in the its change-type list.
		 */
		private void splitList()
		{
			Iterator<FileObject> iterator = changedFiles.keySet().iterator();
			FileObject file;
			
			while ( iterator.hasNext() )
			{
				file = iterator.next();
				
				switch ( changedFiles.get( file ) )
				{
					case "deleted":
						deletedFiles.add( file.getPathString() );
						break;
					
					case "modified":
						modifiedFiles.add( file.getPathString() );
						break;
					
					case "created":
						createdFiles.add( file.getPathString() );
						break;
				}
			}
		}
	}
}
