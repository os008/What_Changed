import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.tree.DefaultMutableTreeNode;


public class MainUI
{
	////////////////////////////////////////////////////////////////////////////////////////
	// #region Attributes.
	//======================================================================================
	
	public JFrame					frame;
	
	private JSplitPane				splitPaneBrowser;
	
	private JScrollPane				scrollPaneFolders;
	public FilesTree				foldersTree;
	
	private JScrollPane				scrollPaneFiles;
	private CheckBoxList			listFiles;
	public TreeSet<CheckListEntry>	fileList;
	
	private JPanel					panelActions;
	private JPanel					panelFilesButtons;
	
	private JButton					buttonSnapshot;
	private JLabel					snapShotTimeLabel;
	private JButton					buttonRefresh;
	private JButton					buttonChangeDrives;
	private JButton					buttonRemoveFiles;
	
	private JPanel					panelBottom;
	
	private JTextArea				textAreaLog;
	private JScrollPane				textAreaScrollPane;
	
	private JPanel					panelFilesCount;
	private JLabel					labelFilesChanged;
	private JLabel					labelCount;
	
	private JPanel					panelReportMonitorButtons;
	private JButton					buttonReport;
	private JButton					buttonMonitoring;
	
	private JButton					buttonExit;
	
	private JFileChooser			fileChooser;
	
	private SystemTray				systemTray;
	private TrayIcon				trayIcon;
	
	private Image					appIcon;
	private JPanel					panelExitButton;
	private JButton					buttonAbout;
	private Component				horizontalGlue;
	private Component				horizontalGlue_1;
	private Component				horizontalStrut;
	private Component				horizontalStrut_1;
	private Component				horizontalStrut_2;
	private Component				horizontalStrut_3;
	private Component				verticalStrut;
	
	//======================================================================================
	// #endregion Attributes.
	////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Create the frame.
	 */
	public MainUI()
	{
		initialize();
	}
	
	/**
	 * Initialise the contents of the frame.
	 */
	private void initialize()
	{	// #region
		URL tempURL = this.getClass().getResource( "res/icon.png" );
		
		if ( tempURL == null )
			appIcon = new ImageIcon( "res/icon.png" ).getImage();
		else
			appIcon = new ImageIcon( tempURL ).getImage();
		
		frame = new JFrame();
		frame.setTitle( "What Changed!" );
		frame.setBounds( 100, 100, 800, 400 );
		frame.setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );
		frame.setIconImage( appIcon );
		frame.getContentPane().setLayout( new BorderLayout( 0, 0 ) );
		
		fileList = new TreeSet<CheckListEntry>();
		
		panelActions = new JPanel();
		panelActions.setLayout( new BorderLayout( 0, 0 ) );
		
		////////////////////////////////////////////////////////////////////////////////////////
		// #region File-browser panel.
		//======================================================================================
		
		splitPaneBrowser = new JSplitPane();
		
		getDrives();
		
		foldersTree = new FilesTree( new DefaultMutableTreeNode( "root" ) );
		
		scrollPaneFolders = new JScrollPane( foldersTree );
		
		listFiles = new CheckBoxList();
		listFiles.setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
		
		scrollPaneFiles = new JScrollPane( listFiles );
		
		splitPaneBrowser.setLeftComponent( scrollPaneFolders );
		splitPaneBrowser.setRightComponent( scrollPaneFiles );
		
		frame.getContentPane().add( splitPaneBrowser, BorderLayout.CENTER );
		
		//======================================================================================
		// #endregion File-browser panel.
		////////////////////////////////////////////////////////////////////////////////////////
		
		////////////////////////////////////////////////////////////////////////////////////////
		// #region Files buttons panel.
		//======================================================================================
		
		panelFilesButtons = new JPanel();
		
		buttonSnapshot = new JButton( "Snapshot" );
		
		snapShotTimeLabel = new JLabel( Engine.snapTime );
		
		buttonRemoveFiles = new JButton( "Remove files" );
		
		buttonRefresh = new JButton( "Refresh" );
		
		buttonChangeDrives = new JButton( "Change drives" );
		
		panelActions.add( panelFilesButtons, BorderLayout.NORTH );
		panelFilesButtons.setLayout( new BoxLayout( panelFilesButtons, BoxLayout.X_AXIS ) );
		
		//--------------------------------------------------------------------------------------
		// #region Buttons-panel layout.
		
		verticalStrut = Box.createVerticalStrut( buttonSnapshot.getPreferredSize().height + 8 );
		verticalStrut.setMaximumSize( new Dimension( 0, 33 ) );
		panelFilesButtons.add( verticalStrut );
		
		horizontalStrut_2 = Box.createHorizontalStrut( 5 );
		panelFilesButtons.add( horizontalStrut_2 );
		panelFilesButtons.add( buttonRefresh );
		
		horizontalStrut_1 = Box.createHorizontalStrut( 10 );
		panelFilesButtons.add( horizontalStrut_1 );
		panelFilesButtons.add( buttonChangeDrives );
		
		horizontalGlue = Box.createHorizontalGlue();
		panelFilesButtons.add( horizontalGlue );
		panelFilesButtons.add( buttonSnapshot );
		
		horizontalStrut = Box.createHorizontalStrut( 10 );
		panelFilesButtons.add( horizontalStrut );
		panelFilesButtons.add( snapShotTimeLabel );
		
		horizontalGlue_1 = Box.createHorizontalGlue();
		panelFilesButtons.add( horizontalGlue_1 );
		panelFilesButtons.add( buttonRemoveFiles );
		
		horizontalStrut_3 = Box.createHorizontalStrut( 5 );
		panelFilesButtons.add( horizontalStrut_3 );
		
		// #endregion Buttons-panel layout.
		//--------------------------------------------------------------------------------------
		
		//======================================================================================
		// #endregion Files buttons panel.
		////////////////////////////////////////////////////////////////////////////////////////
		
		////////////////////////////////////////////////////////////////////////////////////////
		// #region Bottom panel (textArea, and report/monitoring).
		//======================================================================================
		
		panelBottom = new JPanel();
		panelBottom.setLayout( new BorderLayout( 0, 0 ) );
		
		panelFilesCount = new JPanel();
		labelFilesChanged = new JLabel( "File changes:" );
		labelCount = new JLabel( "0" );
		
		panelFilesCount.add( labelFilesChanged );
		panelFilesCount.add( labelCount );
		panelBottom.add( panelFilesCount, BorderLayout.WEST );
		
		panelReportMonitorButtons = new JPanel();
		buttonReport = new JButton( "Generate report" );
		buttonMonitoring = new JButton( "Enable real-time monitoring" );
		
		panelReportMonitorButtons.add( buttonReport );
		panelReportMonitorButtons.add( buttonMonitoring );
		panelBottom.add( panelReportMonitorButtons, BorderLayout.CENTER );
		
		textAreaLog = new JTextArea();
		textAreaLog.setRows( 5 );
		textAreaLog.setEditable( false );
		
		textAreaScrollPane = new JScrollPane( textAreaLog );
		
		panelExitButton = new JPanel();
		panelBottom.add( panelExitButton, BorderLayout.EAST );
		
		buttonAbout = new JButton( "About" );
		panelExitButton.add( buttonAbout );
		
		buttonExit = new JButton( "Exit" );
		panelExitButton.add( buttonExit );
		
		panelActions.add( textAreaScrollPane, BorderLayout.CENTER );
		panelActions.add( panelBottom, BorderLayout.SOUTH );
		
		frame.getContentPane().add( panelActions, BorderLayout.SOUTH );
		
		//======================================================================================
		// #endregion Bottom panel (textArea, and report/monitoring).
		////////////////////////////////////////////////////////////////////////////////////////
		
		////////////////////////////////////////////////////////////////////////////////////////
		// #region Action-listeners.
		//======================================================================================
		
		buttonSnapshot.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				Engine.updateDatabase( true );
				
				Engine.statesOutDated = false;
				
				Engine.setSnapTime();
				
				updateGUIStuff();
			}
		} );
		
		buttonMonitoring.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				toggleMonitoring();
			}
		} );
		
		buttonReport.addActionListener( new ActionListener()
		{
			
			@Override
			public void actionPerformed( ActionEvent e )
			{
				Engine.generateReport();
			}
			
		} );
		
		listFiles.addKeyListener( new KeyAdapter()
		{
			@Override
			public void keyPressed( KeyEvent e )
			{
				if ( ( e.getKeyCode() == KeyEvent.VK_ENTER ) || ( e.getKeyCode() == KeyEvent.VK_SPACE ) )
				{
					for ( int index : listFiles.getSelectedIndices() )
						( (CheckListEntry) listFiles.getModel().getElementAt( index ) ).setSelected( e.getKeyCode() == KeyEvent.VK_SPACE ? true
								: false );
					
					listFiles.repaint();
				}
			}
		} );
		
		buttonRemoveFiles.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				ArrayList<CheckListEntry> checkedFiles = new ArrayList<CheckListEntry>();
				
				for ( CheckListEntry checkListEntry : fileList )
					if ( checkListEntry.isSelected() )
						checkedFiles.add( checkListEntry );
				
				foldersTree.updateTree( checkedFiles );
				
				if ( fileList.size() > 0 )
					Engine.statesOutDated = true;
				else
					Engine.statesOutDated = false;
				
				updateGUIStuff();
			}
		} );
		
		buttonRefresh.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				foldersTree = new FilesTree( new DefaultMutableTreeNode( "root" ) );
				
				scrollPaneFolders = new JScrollPane( foldersTree );
				
				splitPaneBrowser.setLeftComponent( scrollPaneFolders );
				
				resetDivider();
			}
		} );
		
		buttonChangeDrives.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				Engine.needDrives = true;
				
				getDrives();
				
				foldersTree = new FilesTree( new DefaultMutableTreeNode( "root" ) );
				
				scrollPaneFolders = new JScrollPane( foldersTree );
				
				splitPaneBrowser.setLeftComponent( scrollPaneFolders );
				
				resetDivider();
			}
		} );
		
		buttonAbout.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				new About();
				
				Engine.mainUI.frame.setFocusableWindowState( false );
				Engine.mainUI.frame.setEnabled( false );
			}
		} );
		
		buttonExit.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				Engine.exitApplication();
			}
		} );
		
		frame.addComponentListener( new ComponentAdapter()
		{
			@Override
			public void componentResized( ComponentEvent e )
			{
				super.componentResized( e );
				
				resetDivider();
			}
			
		} );
		
		frame.addWindowStateListener( new WindowAdapter()
		{
			@Override
			public void windowStateChanged( WindowEvent e )
			{
				super.windowStateChanged( e );
				
				if ( Engine.watchersWorking && ( e.getNewState() == Frame.ICONIFIED ) )
					minimiseToTray();
			}
		} );
		
		//======================================================================================
		// #endregion Action-listeners.
		////////////////////////////////////////////////////////////////////////////////////////
		
		initList();
		
		resetDivider();
		// #endregion 'initialize()'
	}
	
	/**
	 * Sets buttons status, and updates the snapshot time.
	 */
	public void updateGUIStuff()
	{
		boolean reportReady = ( ( Engine.dbFilesList != null ) && ( Engine.dbFilesList.size() > 0 ) ) && buttonRemoveFiles.isEnabled()
				&& !Engine.statesOutDated;
		
		boolean fileListHasFiles = ( fileList != null ) && ( fileList.size() > 0 );
		
		buttonRemoveFiles.setEnabled( fileListHasFiles );
		
		buttonReport.setEnabled( reportReady );
		
		buttonSnapshot.setEnabled( fileListHasFiles );
		
		updateMonitoringButton();
		
		buttonMonitoring.setEnabled( fileListHasFiles );
		
		buttonExit.setEnabled( !Engine.statesOutDated );
		
		if ( reportReady )
			snapShotTimeLabel.setText( Engine.snapTime );
		else
			snapShotTimeLabel.setText( null );
	}
	
	/**
	 * Minimises the app to the system-tray.
	 */
	private void minimiseToTray()
	{	// #region
		if ( !SystemTray.isSupported() )
		{
			Engine.exitApplication();
			return;
		}
		
		systemTray = SystemTray.getSystemTray();
		
		PopupMenu trayMenu = new PopupMenu();
		MenuItem menuItem;
		
		menuItem = new MenuItem( "Restore" );
		
		menuItem.addActionListener( new ActionListener()
		{
			
			@Override
			public void actionPerformed( ActionEvent e )
			{
				restoreWindow();
			}
		} );
		
		trayMenu.add( menuItem );
		
		trayIcon = new TrayIcon( appIcon, "What Changed!", trayMenu );
		
		trayIcon.addMouseListener( new MouseAdapter()
		{
			@Override
			public void mouseClicked( MouseEvent e )
			{
				if ( e.getClickCount() >= 2 )
					restoreWindow();
			}
		} );
		
		try
		{
			systemTray.add( trayIcon );
		}
		catch ( Exception e1 )
		{
			e1.printStackTrace();
		}
		
		frame.setVisible( false );
		// #endregion 'minimiseToTray()'
	}
	
	/**
	 * Restores the app from the system-tray, and brings it to the front.
	 */
	private void restoreWindow()
	{
		frame.setVisible( true );
		frame.setExtendedState( Frame.NORMAL );
		frame.toFront();
		systemTray.remove( trayIcon );
		
		resetDivider();
	}
	
	/**
	 * Resets the position of the divider between the tree and the files' list.
	 * 
	 * @param position
	 *            position of the divider.
	 */
	public void resetDivider( int... position )
	{
		if ( position.length == 0 )
			splitPaneBrowser.setDividerLocation( frame.getWidth() / 4 );
		else
			splitPaneBrowser.setDividerLocation( position[0] );
	}
	
	/**
	 * Adds a log entry for the changed file, and sets the counter's value to
	 * the one passed.
	 * 
	 * @param fileString
	 *            File changed.
	 * @param counter
	 *            Count of files changed so-far.
	 */
	public void updateMonitoringUI( String fileString, int counter )
	{
		DateFormat format = new SimpleDateFormat( "'['dd MMM HH:mm:ss a']'" );
		
		String timeStamp = format.format( new Date() );
		
		String text = String.format( "%s %s\n", timeStamp, fileString );
		
		textAreaLog.append( text );
		
		textAreaLog.setCaretPosition( textAreaLog.getCaretPosition() + text.length() );
		
		labelCount.setText( "" + counter );
	}
	
	/**
	 * Sets the button text to reflect the action appropriate for the state of
	 * monitoring; 'enable monitoring' if it's disabled, or 'disable monitoring'
	 * if it's enabled.<br>
	 * Also, resets the file-changes counter if the workers are disabled.
	 */
	private void updateMonitoringButton()
	{
		if ( Engine.watchersWorking )
			buttonMonitoring.setText( "Disable monitoring" );
		else
			buttonMonitoring.setText( "Enable real-time monitoring" );
	}
	
	/**
	 * Toggles the state of real-time monitoring between 'enabled' and
	 * 'disabled'.<br>
	 * Also, resets the file-changes counter if the workers are disabled.
	 */
	private void toggleMonitoring()
	{
		if ( !Engine.watchersWorking )
		{
			if ( Engine.fileTreePaths.keySet().isEmpty() )
				return;
			
			Engine.changedCounter = 0;
			labelCount.setText( "" + 0 );
			textAreaLog.setText( null );
			
			Engine.createWatchers( Engine.fileTreePaths.keySet() );
			
			updateMonitoringButton();
		}
		else
		{
			Engine.disableWatchers();
			updateMonitoringButton();
		}
	}
	
	/**
	 * Opens a file-choose dialog to choose the drives to add to the tree, then
	 * saves them to the list in {@link Engine}.
	 */
	private void getDrives()
	{	// #region
		synchronized ( Engine.drives )
		{
			if ( ( Engine.drives.size() <= 0 ) || Engine.needDrives )
			{
				fileChooser = new JFileChooser( getRoot() );
				fileChooser.changeToParentDirectory();
				
				fileChooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
				fileChooser.setMultiSelectionEnabled( true );
				
				fileChooser.showOpenDialog( frame );
			}
			else
				return;
			
			File[] drives = fileChooser.getSelectedFiles();
			
			if ( ( drives != null ) && ( drives.length > 0 ) )
				Engine.needDrives = false;
			else
				return;
			
			Engine.drives = new TreeSet<String>();
			
			for ( File drive : drives )
				Engine.drives.add( drive.getName() );
		}
		// #endregion 'getDrives()'
	}
	
	/**
	 * Gets the drive/root of the folder of the application.
	 * 
	 * @return Drive/root.
	 */
	private File getRoot()
	{
		File startDir = new File( System.getProperty( "user.dir" ) );
		
		while ( startDir.getParentFile() != null )
			startDir = startDir.getParentFile();
		
		return startDir;
	}
	
	/**
	 * Sets the list of files to the files' list in {@link Engine}, then
	 * refreshes the visual list.
	 */
	@SuppressWarnings( "unchecked" )
	public void initList()
	{
		Iterator<String> iterator = Engine.fileTreePaths.keySet().iterator();
		
		fileList.clear();
		
		while ( iterator.hasNext() )
			addToList( new FileObject( iterator.next() ) );
		
		listFiles.setListData( fileList.toArray() );
		listFiles.repaint();
		
		Engine.listInitialised = true;
	}
	
	/**
	 * Adds a {@link CheckListEntry} to the list of files used in the GUI.
	 * 
	 * @param file
	 *            {@link FileObject} to create a list entry for.
	 */
	@SuppressWarnings( "unchecked" )
	public void addToList( FileObject file )
	{
		// create a clone of the file-list to prevent unnecessary updates to the GUI list, which causes bugs.
		TreeSet<CheckListEntry> tempFileList = (TreeSet<CheckListEntry>) fileList.clone();
		
		fileList.add( new CheckListEntry( file ) );
		
		// if the list has changed.
		if ( !tempFileList.equals( fileList ) )
		{
			Engine.disableWatchers();
			
			refreshList();
		}
	}
	
	/**
	 * Removes a {@link CheckListEntry} from the list of files used in the GUI.
	 * 
	 * @param file
	 *            {@link FileObject} to remove.
	 */
	public void removeFromList( FileObject file )
	{
		fileList.remove( new CheckListEntry( file ) );
		
		Engine.disableWatchers();
		
		refreshList();
	}
	
	/**
	 * Repaints the visual list, and sets the general state as out-dated.
	 */
	@SuppressWarnings( "unchecked" )
	private void refreshList()
	{
		listFiles.setListData( fileList.toArray() );
		listFiles.repaint();
		
		if ( Engine.listInitialised )
			Engine.statesOutDated = true;
		
		updateGUIStuff();
	}
}


/**
 * Class that creates a GUI to know info about the application.
 */
@SuppressWarnings( "serial" )
class About extends JFrame
{
	public About()
	{	// #region
		setBounds( 150, 150, 800, 400 );
		setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
		setTitle( "About" );
		
		String headerText = "What Changed v" + Engine.VERSION + "\n";
		
		JLabel header = new JLabel( headerText );
		
		header.setFont( new Font( "Gabriola", Font.PLAIN, 35 ) );
		header.setBorder( BorderFactory.createLineBorder( Color.BLACK ) );
		header.setHorizontalAlignment( SwingConstants.CENTER );
		
		String text;
		String license = "\nCopyright (C) 2012 by Ahmed Osama el-Sawalhy.\n\n"
				+ "          Modified MIT License (GPL v3 compatible):\n"
				+ "\tPermission is hereby granted, free of charge, to any person obtaining a copy\n\tof this software and associated documentation files (the \"Software\"), to deal\n\tin the Software without restriction, including without limitation the rights\n\tto use, copy, modify, merge, publish, distribute, sub-license, and/or sell\n\tcopies of the Software, and to permit persons to whom the Software is\n\tfurnished to do so, subject to the following conditions:\n\n"
				+ "\tThe above copyright notice and this permission notice shall be included in\n\tall copies or substantial portions of the Software.\n\n"
				+ "\tExcept as contained in this notice, the name(s) of the above copyright\n\tholders shall not be used in advertising or otherwise to promote the sale, use\n\tor other dealings in this Software without prior written authorization.\n\n"
				+ "\tThe end-user documentation included with the redistribution, if any, must\n\tinclude the following acknowledgment: \"This product includes software developed\n\tby Ahmed el-Sawalhy (http://ahmedosama.net) and his contributors\", in\n\tthe same place and form as other third-party acknowledgments. Alternately, this\n\tacknowledgment may appear in the software itself, in the same form and location\n\tas other such third-party acknowledgments.\n\n"
				+ "\tTHE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR\n\tIMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,\n\tFITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE\n\tAUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER\n\tLIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,\n\tOUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN\n\tTHE SOFTWARE.\n";
		String authorsHeader = "\nAuthor(s):\n";
		String authors = "\tAhmed Osama\n";
		String creditsHeader = "\nCredits:\n";
		String credits = "\tCheckBoxTree: copyright 2007-2010 Enrico Boldrini, Lorenzo Bigagli. GNU GPL v2 license.\n"
				+ "\tChecBoxList: Trevor Harmon at devx.com.\n";
		
		text = license + authorsHeader + authors + creditsHeader + credits;
		
		JTextArea textArea = new JTextArea( text );
		
		JScrollPane textAreaScrollPane = new JScrollPane( textArea );
		
		textArea.setFont( new Font( "Verdana", Font.BOLD, 13 ) );
		textArea.setEditable( false );
		
		JButton closeButton = new JButton( "Close" );
		
		this.getContentPane().add( header, BorderLayout.NORTH );
		this.getContentPane().add( textAreaScrollPane, BorderLayout.CENTER );
		this.getContentPane().add( closeButton, BorderLayout.SOUTH );
		
		closeButton.addActionListener( new ActionListener()
		{
			
			@Override
			public void actionPerformed( ActionEvent e )
			{
				dispose();
				
				Engine.mainUI.frame.setFocusableWindowState( true );
				Engine.mainUI.frame.setEnabled( true );
				Engine.mainUI.frame.toFront();
			}
		} );
		
		setVisible( true );
		// #endregion 'About()'
	}
}
