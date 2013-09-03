

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;


@SuppressWarnings( "serial" )
public class NotificationUI extends JFrame implements Runnable
{
	JPanel		panel;
	JPanel		panelLabel;
	JLabel		label;
	JTextArea	textArea;
	
	/**
	 * Create the frame.
	 */
	public NotificationUI( String file, String changeType )
	{
		setResizable( false );
		setFocusableWindowState( false );
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = (int) screenSize.getWidth();
		int height = (int) screenSize.getHeight();
		
		int frameWidth = 400;
		int frameHeight = 200;
		
		setBounds( ( width - 10 - frameWidth ), ( height - 38 - ( ( frameHeight + 20 ) * Engine.notificationCounter ) ), frameWidth, frameHeight );
		
		// header.
		label = new JLabel( "File " + changeType + "!" );
		label.setHorizontalAlignment( SwingConstants.CENTER );
		label.setFont( new Font( "Gabriola", Font.PLAIN, 35 ) );
		
		// text-area.
		textArea = new JTextArea( String.format( "File %s: \"%s\".", changeType, file ) );
		textArea.setEditable( false );
		textArea.setLineWrap( true );
		textArea.setRows( 7 );
		textArea.setFont( new Font( "Verdana", Font.BOLD, 15 ) );
		textArea.setBorder( BorderFactory.createLineBorder( Color.BLACK, 1 ) );
		
		// layout.
		panel = new JPanel();
		SpringLayout sl_panel = new SpringLayout();
		sl_panel.putConstraint( SpringLayout.NORTH, textArea, 55, SpringLayout.NORTH, panel );
		sl_panel.putConstraint( SpringLayout.WEST, textArea, 0, SpringLayout.WEST, panel );
		sl_panel.putConstraint( SpringLayout.SOUTH, textArea, 0, SpringLayout.SOUTH, panel );
		sl_panel.putConstraint( SpringLayout.EAST, textArea, 0, SpringLayout.EAST, panel );
		panel.setLayout( sl_panel );
		
		panelLabel = new JPanel();
		sl_panel.putConstraint( SpringLayout.NORTH, panelLabel, 0, SpringLayout.NORTH, panel );
		sl_panel.putConstraint( SpringLayout.WEST, panelLabel, 0, SpringLayout.WEST, panel );
		sl_panel.putConstraint( SpringLayout.SOUTH, panelLabel, 0, SpringLayout.NORTH, textArea );
		sl_panel.putConstraint( SpringLayout.EAST, panelLabel, 0, SpringLayout.EAST, panel );
		
		// header layout.
		SpringLayout sl_panelLabel = new SpringLayout();
		sl_panelLabel.putConstraint( SpringLayout.NORTH, label, 0, SpringLayout.NORTH, panelLabel );
		sl_panelLabel.putConstraint( SpringLayout.WEST, label, 0, SpringLayout.WEST, panelLabel );
		sl_panelLabel.putConstraint( SpringLayout.SOUTH, label, 0, SpringLayout.SOUTH, panelLabel );
		sl_panelLabel.putConstraint( SpringLayout.EAST, label, 0, SpringLayout.EAST, panelLabel );
		panelLabel.setLayout( sl_panelLabel );
		
		panelLabel.add( label );
		
		panel.add( panelLabel );
		panel.add( textArea );
		panel.setBorder( BorderFactory.createLineBorder( Color.BLACK, 2 ) );
		
		getContentPane().add( panel );
		
		setType( Type.UTILITY );
		setAlwaysOnTop( true );
		setVisible( true );
	}
	
	@Override
	public void run()
	{
		try
		{
			Engine.notificationCounter++;
			Thread.sleep( 5000 );
			Engine.notificationCounter--;
			this.dispose();
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}
	
}
