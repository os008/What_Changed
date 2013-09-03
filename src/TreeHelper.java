
import it.cnr.imaa.essi.lablib.gui.checkboxtree.CheckboxTree;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;


/**
 * A class containing methods to deal with the tree's nodes.
 */
public class TreeHelper
{
	
	/** Tree object. */
	private CheckboxTree			tree;
	
	/** Root node. */
	private DefaultMutableTreeNode	root;
	
	/** Node. */
	private DefaultMutableTreeNode	node;
	
	/**
	 * Sets the tree object.
	 * 
	 * @param sentTree
	 *            New tree
	 */
	public void setTree( CheckboxTree sentTree )
	{
		tree = sentTree;
	}
	
	/**
	 * Sets the root node.
	 * 
	 * @param sentRoot
	 *            New root
	 */
	public void setRoot( DefaultMutableTreeNode sentRoot )
	{
		root = sentRoot;
	}
	
	/**
	 * Sets the node.
	 * 
	 * @param sentNode
	 *            New node
	 */
	public void setNode( DefaultMutableTreeNode sentNode )
	{
		node = sentNode;
	}
	
	/**
	 * Fills the node's children with nodes.
	 */
	void initNode()
	{
		// gets the node's children.
		@SuppressWarnings( "unchecked" ) Enumeration<DefaultMutableTreeNode> children = root.children();
		
		// iterate over the children.
		while ( children.hasMoreElements() )
		{
			node = children.nextElement();
			
			// if node has children, then no need to init.
			if ( node.isLeaf() )
			{
				// call appropriate method for the 'files' node, or the folder.
				if ( node.getUserObject() == "[Files]" )
				{
					fillFilesNode( new File( pathStringAbstractNode( node ) ) );
				}
				else
					fillNode( new File( pathStringNode( node ) ) );
				
				// tree has changed.
				( (DefaultTreeModel) tree.getModel() ).nodeChanged( node );
			}
		}
	}
	
	/**
	 * Fills the node with nodes.
	 */
	void initSingleNode()
	{
		if ( node.getUserObject() == "[Files]" )
		{
			fillFilesNode( new File( pathStringAbstractNode( node ) ) );
		}
		else
			fillNode( new File( pathStringNode( node ) ) );
		
		// tree has changed.
		( (DefaultTreeModel) tree.getModel() ).nodeChanged( node );
	}
	
	/**
	 * Fill node with nodes; files/folders.
	 * 
	 * @param folder
	 *            Folder to be filled.
	 */
	void fillNode( File folder )
	{
		// make sure it's a directory.
		if ( folder.isDirectory() )
		{
			// get the files/folders inside.
			File[] list = folder.listFiles();
			
			// if there're files/folders inside.
			if ( list != null )
			{
				// iterate over them.
				for ( File entry : list )
				{
					// if it's a directory, add it to the node. If not then postpone it to be added later to a the file-node.
					if ( entry.isDirectory() )
						node.add( new DefaultMutableTreeNode( entry.getName() ) );
				}
				
				// if it's a files node.
				node.add( new DefaultMutableTreeNode( "[Files]" ) );
			}
		}
	}
	
	/**
	 * Fill 'files' node.
	 * 
	 * @param folder
	 *            Folder to read files from.
	 */
	void fillFilesNode( File folder )
	{
		// make sure it's a directory.
		if ( folder.isDirectory() )
		{
			// get files/folders in the folder passed.
			File[] list = folder.listFiles();
			
			// if there're children.
			if ( list != null )
			{
				// go through them.
				for ( File entry : list )
				{
					// make sure it's a file, then add it to the node.
					if ( entry.isFile() )
						node.add( new DefaultMutableTreeNode( entry.getName() ) );
				}
				
				// if the 'files' node is empty, delete it.
				if ( node.getChildCount() <= 0 )
				{
					( (DefaultMutableTreeNode) node.getParent() ).remove( node );
				}
			}
		}
	}
	
	/**
	 * Gets the path to the node as a string, from a concrete tree node.
	 * 
	 * @param node
	 *            Node to get the path for.
	 * @return Path as a string.
	 */
	String pathStringNode( DefaultMutableTreeNode node )
	{
		TreeNode[] pathArray = node.getPath();
		String path = "";
		
		// form the string.
		for ( int i = 1; i < pathArray.length; i++ )
		{
			// exclude the 'files' nodes.
			if ( !( ( (DefaultMutableTreeNode) pathArray[i] ).getUserObject() == "[Files]" ) )
				path = path + ( (DefaultMutableTreeNode) pathArray[i] ).getUserObject() + "\\";
		}
		
		return path;
	}
	
	/**
	 * Gets the path to the node as a string, from an abstract tree node.
	 * 
	 * @param node
	 *            Node to get the path for.
	 * @return The path as a string.
	 */
	String pathStringAbstractNode( TreeNode node )
	{
		// the parents of the node as an array, each entry is a node. In reverse order.
		ArrayList<TreeNode> parents = new ArrayList<TreeNode>();
		
		// add the node itself.
		parents.add( node );
		
		TreeNode parent = node;
		
		// keep adding the parent's parents until the root is reached.
		while ( ( ( parent = ( (DefaultMutableTreeNode) parent ).getParent() ) != null )
				&& ( ( (DefaultMutableTreeNode) parent ).getUserObject() != "root" ) )
			parents.add( parent );
		
		String path = "";
		
		// get the string form of each node, and concatenate it with '\' in the middle.
		for ( int i = parents.size() - 1; i >= 1; i-- )
		{
			if ( !( ( (DefaultMutableTreeNode) parents.get( i ) ).getUserObject() == "[Files]" ) )
				path = path + ( (DefaultMutableTreeNode) parents.get( i ) ).getUserObject() + "\\";
		}
		
		return path;
	}
	
	/**
	 * Convert {@link TreePath} into an array of strings.
	 * 
	 * @param path
	 *            {@link TreePath} object.
	 * @return String representation of the tree path to the node.
	 */
	String pathStringTreePath( TreePath path )
	{
		Object[] pathArray = path.getPath();
		String pathString = "";
		
		// get the string of the node, then concatenate it with '\' in the middel.
		for ( int i = 1; i < pathArray.length; i++ )
		{
			if ( !( ( (DefaultMutableTreeNode) pathArray[i] ).getUserObject() == "[Files]" ) )
				pathString = pathString + ( (DefaultMutableTreeNode) pathArray[i] ).getUserObject() + "\\";
		}
		
		return pathString;
	}
	
	/**
	 * Gets the tree path of the current node.
	 * 
	 * @param node
	 *            Node to get the path for.
	 * @return {@link TreePath} object.
	 */
	TreePath getTreePath( DefaultMutableTreeNode node )
	{
		return new TreePath( node.getPath() );
	}
	
	/**
	 * Gets the updated path of a path in an old similar tree in the new tree.
	 * 
	 * @param oldPath
	 *            Old path.
	 * @return Updated path
	 */
	public TreePath getUpdatedPath( TreePath oldPath )
	{
		// convert path to array.
		Object[] oldPathArray = oldPath.getPath();
		
		ArrayList<DefaultMutableTreeNode> newPathArray = new ArrayList<DefaultMutableTreeNode>();
		DefaultMutableTreeNode currentNode = root;
		
		node = root;
		
		// iterate over the old path's array.
		for ( Object object : oldPathArray )
		{
			// if the node is the root, then just add the current new root; it's already with us.
			if ( ( (String) ( (DefaultMutableTreeNode) object ).getUserObject() ).matches( "\\Qroot\\E" ) )
			{
				newPathArray.add( root );
				continue;	// no need to continue with the current iteration.
			}
			
			// look for the similar node in the new tree.
			currentNode = searchForAChild( (String) ( (DefaultMutableTreeNode) object ).getUserObject() );
			
			node = currentNode; 	// set it as the current node.
			newPathArray.add( currentNode );		// add it to the new path array.
			
			// if in any iteration a node was not found, then the rest won't exist.
			if ( node == null )
				return null;
		}
		
		return new TreePath( newPathArray.toArray() );
	}
	
	/**
	 * Search for a node given its string content.
	 * 
	 * @param content
	 *            String content.
	 * @return Node, or null if not found.
	 */
	@SuppressWarnings( "unchecked" )
	private DefaultMutableTreeNode searchForAChild( String content )
	{
		// get the children of the current node.
		Enumeration<DefaultMutableTreeNode> children = node.children();
		
		DefaultMutableTreeNode nextChild;
		
		// initialise a node if it has no children; just in-case.
		if ( !children.hasMoreElements() )
			initSingleNode();
		
		// re-read children after update.
		children = node.children();
		
		// go through the children looking for the node.
		while ( children.hasMoreElements() )
		{
			nextChild = children.nextElement();
			
			if ( ( (String) nextChild.getUserObject() ).matches( "\\Q" + content + "\\E" ) )
			{
				return nextChild;
			}
		}
		
		// nothing found.
		return null;
	}
}
