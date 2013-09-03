import java.awt.EventQueue;

import javax.swing.JOptionPane;


/* Copyright (C) 2012 by Ahmed Osama el-Sawalhy
 *		Modified MIT License (GPL v3 compatible). License terms are in a separate file.
 *
 *		Project/File: What Changed//_start.java
 *			 Version: 2.03.0050
 *
 *			 Created: 29 Nov 2012 (20:35:06)
 *			   Using: Eclipse J-EE / JDK 7 / Windows 7 x64
 *
 *		 Description: This program takes a snapshot of a list of chosen files, it includes
 *						the size and timestamp. The user can generate a report to see if
 *						the files where changed. It can also monitor in real-time, and
 *						pop-up a notification.
 *					  
 */
/**
 * 
 */

public class _start
{
	
	/**
	 * Entry point of the application.
	 */
	public static void main( String[] args )
	{
		Engine.initialise();	// load data.
		
		// load the UI in the background.
		EventQueue.invokeLater( new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					MainUI window = new MainUI();
					window.frame.setVisible( true );	// show the main app window.
					
					Engine.mainUI = window;		// give the Engine a reference to the UI.
					
					Engine.mainUI.updateGUIStuff();		// init labels and buttons status.
					
					// at this point, the program failed to get the drives needed for the tree. Pointless to continue.
					if ( Engine.needDrives )
					{
						window.frame.setVisible( false );		// hide the main window.
						
						// inform the user of the failure.
						JOptionPane.showMessageDialog( null, "Failed to start!", "ERROR!", JOptionPane.ERROR_MESSAGE );
						
						Engine.exitApplication();		// application exit procedure.
					}
				}
				catch ( Exception e )
				{
					e.printStackTrace();
				}
			}
		} );
	}
	
}

/**
 * @author Ahmed el-Sawalhy
 * 
 */
