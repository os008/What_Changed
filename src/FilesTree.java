import it.cnr.imaa.essi.lablib.gui.checkboxtree.CheckboxTree;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.TreeCheckingEvent;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.TreeCheckingListener;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.TreeCheckingModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;


/**
 * Represents a checked JTree, tailored to deal with a directory tree.
 */
@SuppressWarnings( "serial" )
public class FilesTree extends CheckboxTree
{
	
	/** Saves using 'this' a lot. */
	public DefaultMutableTreeNode	root2;
	
	/** Tree helper methods. */
	public TreeHelper				treeHelper;
	
	/** A list of drives to use in forming the tree. */
	public DefaultMutableTreeNode[]	driveNodes;
	
	/**
	 * Instantiates a new files tree.
	 * 
	 * @param root
	 *            Root of the tree.
	 */
	public FilesTree( DefaultMutableTreeNode root )
	{	// #region
	
		super( root );
		
		this.root2 = root;
		
		treeHelper = new TreeHelper();
		treeHelper.setTree( this );		// sets the root in tree-helper to this tree.
		
		getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );		// sets to selecting single nodes.
		getCheckingModel().setCheckingMode( TreeCheckingModel.CheckingMode.PROPAGATE );		// selecting one node selects all children.
		
		// build the tree.
		initialiseDrives();
		
		// a listener to prepare a node for expansion. Also, it must be used to decide if the node needs a '+'.
		addTreeWillExpandListener( new TreeWillExpandListener()
		{
			
			@Override
			public void treeWillExpand( TreeExpansionEvent event ) throws ExpandVetoException
			{
				// the node just displayed.
				DefaultMutableTreeNode expandedNode = (DefaultMutableTreeNode) event.getPath().getLastPathComponent();
				
				treeHelper.setRoot( expandedNode );
				treeHelper.initNode();	// fill children of the node.
				
				// check if the node's children were selected before (in another session); restore selections.
				for ( TreePath path : Engine.fileTreePaths.values() )
				{
					treeHelper.setRoot( root2 );
					treeHelper.setNode( root2 );
					
					try
					{
						getCheckingModel().addCheckingPath( treeHelper.getUpdatedPath( path ) );		// get the node at the end of that path in THIS tree; the saved one is out-dated.
					}
					catch ( Exception e )
					{	
						
					}
				}
			}
			
			@Override
			public void treeWillCollapse( TreeExpansionEvent event ) throws ExpandVetoException
			{
				
			}
		} );
		
		// if a node is checked, add it to the list, if it's a file.
		addTreeCheckingListener( new TreeCheckingListener()
		{
			
			@Override
			public void valueChanged( TreeCheckingEvent e )
			{
				updateList();
			}
		} );
		
		// #endregion 'FilesTree()'
	}
	
	/**
	 * Read the drives' folders and files (1 level), using the stored drive list
	 * in {@link Engine}.
	 */
	private void initialiseDrives()
	{
		// Set the size of the drives' list.
		driveNodes = new DefaultMutableTreeNode[Engine.drives.size()];
		
		// create an iterator for the drives' list.
		Iterator<String> iterator = Engine.drives.iterator();
		
		// a counter for the array of drive nodes.
		int counter = 0;
		
		// create an node object for each drive, then add it to the tree.
		while ( iterator.hasNext() )
		{
			driveNodes[counter] = new DefaultMutableTreeNode( iterator.next() );
			root2.add( driveNodes[counter] );
			
			counter++;
		}
		
		// fill the drive nodes, then expand them.
		for ( DefaultMutableTreeNode node : driveNodes )
		{
			treeHelper.setRoot( node );
			treeHelper.setNode( node );
			treeHelper.fillNode( new File( treeHelper.pathStringNode( node ) ) );
			
			treeHelper.initNode();
			
			expandPath( new TreePath( node.getPath() ) );
		}
		
		// set the checked files, from the saved paths.
		for ( TreePath path : Engine.fileTreePaths.values() )
		{
			treeHelper.setRoot( root2 );
			treeHelper.setNode( root2 );
			
			try
			{
				getCheckingModel().addCheckingPath( treeHelper.getUpdatedPath( path ) );
			}
			catch ( Exception e )
			{	
				
			}
		}
	}
	
	/**
	 * Update list with the checked nodes.
	 */
	@SuppressWarnings( "unchecked" )
	public void updateList()
	{
		// get all the nodes in the tree.
		Enumeration<DefaultMutableTreeNode> traversal = root2.preorderEnumeration();
		
		// flag for a checked node found.
		boolean found = false;
		
		DefaultMutableTreeNode nodeTemp = traversal.nextElement();		// the current node.
		TreePath nodePath = new TreePath( nodeTemp.getPath() );		// its path.
		String file;		// string representation of the file; saved in the node.
		FileObject tempFile;		// FileObject of the file.
		
		// go through the nodes.
		while ( traversal.hasMoreElements() )
		{
			nodeTemp = traversal.nextElement();
			
			nodePath = new TreePath( nodeTemp.getPath() );
			
			// if the node is a file or folder.
			if ( nodeTemp.getUserObject() != "[Files]" )
			{
				// get the string of the file path.
				file = treeHelper.pathStringNode( nodeTemp );
				
				// go through all the checked nodes.
				for ( TreePath path : getCheckingPaths() )
					// if the node is checked.
					if ( ( path != null ) && path.equals( nodePath ) )
					{
						tempFile = new FileObject( file );		// create a new FileObject.
						
						// make sure it's a file; not a folder.
						if ( tempFile.getFile().isFile() )
						{
							// add to the main list.
							Engine.fileTreePaths.put( file, path );
							
							Engine.mainUI.addToList( tempFile );
						}
						
						found = true;
						break;		// no need to check other nodes.
					}
				
				// if the file was not checked, and it's in the main list, remove it.
				if ( !found && Engine.fileTreePaths.containsKey( file ) )
				{
					Engine.fileTreePaths.remove( file );
					Engine.mainUI.removeFromList( new FileObject( file ) );
				}
				
				found = false;		// reset flag.
			}
			
			found = false;		// reset flag.
		}
	}
	
	/**
	 * Remove nodes from the tree removed from list.
	 * 
	 * @param filesToRemove
	 *            List of files to remove.
	 */
	public void updateTree( ArrayList<CheckListEntry> filesToRemove )
	{
		TreePath[] uncheckedPaths = new TreePath[filesToRemove.size()];
		TreePath file;
		
		// build a list of all checked files' paths.
		for ( int i = 0; i < filesToRemove.size(); i++ )
		{
			file = Engine.fileTreePaths.get( filesToRemove.get( i ).getFile().getPathString() );		// get the path of the file from the main list.
			treeHelper.setRoot( root2 );
			treeHelper.setNode( root2 );
			uncheckedPaths[i] = treeHelper.getUpdatedPath( file );
		}
		
		// remove file from tree and main-list.
		for ( int i = 0; i < uncheckedPaths.length; i++ )
			if ( uncheckedPaths[i] != null )		// work-around for an exception.
				getCheckingModel().removeCheckingPath( uncheckedPaths[i] );
			else
			{
				Engine.fileTreePaths.remove( filesToRemove.get( i ).getFile().getPathString() );
				Engine.mainUI.initList();		// re-fill list.
			}
		
	}
	
}
