import java.util.ArrayList;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.imageio.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.DefaultCaret;
import java.net.InetAddress;
import java.net.Inet4Address;
import java.util.regex.Pattern;
import java.util.HashMap;

/**
 * The BigTwoTable class implements the CardGameTable interface,
 * which is used to render a GUI for the game.
 * This class also handles user actions.
 *  <br>
 *	<br>
 *	<p> The following is the original GUI design of the game. At implementation, all
 *	contents showing in the playerTables are rendered directly throw the bigTwoPanel.
 *	<br> <img src="doc-files/GUI Design.svg" alt="GUI Design">
 *  <br> The following is the Use Case diagram. of this game. The use case is relatively simple at this stage.
 *  <br> <img src="doc-files/Use Case.svg" alt="Use Case">
 *
 *	<br>
 *	<p>	<b>Copyright Information</b>
 *	<br> <br> <i> COMP 2396 OOP &amp; Java Assignment #5 </i>
 * 	<br> Version 0.3 2016-11-25
 *	<br> <i> ZHANG, Jiayao Johnson / 3035233412 </i>
 *	<br> Department of Computer Science, The University of Hong Kong
 *  <br> <a href = "jyzhang@cs.hku.hk"> Contact me via email </a>
 *  <br> <a href = "http://i.cs.hku.hk/~jyzhang/"> Visit my webpage </a>
 *
 *  <br> Version 0.3 at 2016-11-25
 * @author ZHANG, Jiayao Johnson
 * @version 0.3
 * @since 2016-11-07
 */
 public class BigTwoTable implements CardGameTable
 {
	 // Private instance variables used for this class
	 // The constant parameters are declared as static final here
	 // including the game setting, file reading and dimensional parameters
	 private static final int 	MAX_SUIT_NUM = 4,
	 							MAX_CARD_NUM = 13,
	 							MAX_PLAYER = 4,
	 							MAX_CARDS_DISPLAYED = 15;
	 private static final boolean PLAYER_SWICH = false;

	 private static final char[] SUIT_IDF = {'d', 'c', 'h', 's'},
	 RANK_IDF = { 'a', '2', '3', '4', '5', '6', '7', '8', '9', 't', 'j', 'q', 'k' };

	 private static final String[]
	 	AVATAR_IDF = {"p1", "p2", "p3", "p4"},
	 	BUTTON = {"playButton", "passButton"},
	 	BUTTON_STATUS = {"", "_pressed"};
	 private static final String
	 	CARD_BACK = "back",
		CARD_PATH = "Resource/Card/",
		CARD_TYPE = ".gif",
		AVATAR_PATH = "Resource/Avatar/",
		AVATAR_TYPE = ".png",
		ICON_PATH = "Resource/Icon/",
		BUTTON_PATH = "Resource/Icon/",
		GAME_ICON = "gameIcon.png",
		BUTTON_TYPE = ".png",
		WELCOME_MSG = String.format("Big Two Game%nCopyright (c) 2016 Jiayao Zhang%n%nHotkeys:%n{PLAY} <SHIFT> + <RETURN>%n{PASS} <SPACE>%n{HINT} <CTRL> + <RETURN>%n{CHAT} <RETURN>%n");

	 private static final Font
	 	DISPLAY_FONT = new Font("Consolas", Font.PLAIN, 16),
	 	CONSOLE_FONT = new Font("Consolas", Font.PLAIN, 12);
	 private static final Color
	 	GAME_PANEL_COLOR = new Color(241, 241, 242),
	 	HAND_PANEL_COLOR = new Color(161, 190, 149),
	 	BUTTON_PANEL_COLOR = new Color(226, 223, 162),
	 	BUTTON_COLOR = new Color(161, 214, 226),
	 	BUTTON_TEXT_COLOR = new Color(35,27,18);
	 private static Color[] PANEL_COLOR;

	 private static final File DEFAULT_LOG_PATH = new File("Log/");

	 private static final Dimension 
	 	CARD_DIMENSION = new Dimension(73,97),
	 	AVATAR_DIMENSION = new Dimension(117,121);
	 private static final int
	 	HORIZONTAL_SHIFT = 30,
	 	VERTICAL_SHIFT = -25,
	 	VERTICIAL_INIT_HIGHT = 40,
	 	VERTICIAL_INIT_HIGHT_AVATAR = 30,
		HORIZONTAL_INIT_HIGHT_HAND = 10,
		VERTICIAL_INIT_HIGHT_HAND = 600,
		HORIZONTAL_INIT_LABEL = 5,
		VERTICIAL_INIT_HIGHT_LAEBL = 10,
	 	PLAYER_PANEL_HEIGHT_TOLERANCE = 10,
	 	GAME_PANEL_WIDTH_TOLERANCE = 200,
	 	CHAT_INPUT_HEIGHT = 50;

	 // The dimensional parameters directly related to GUI
	 private static final Dimension
	 	LABEL_DIMENSION = new Dimension(128,20),
		CONLABEL_DIMENSION = new Dimension(256,20),
	 	BTNPNL_DIMENSION = new Dimension(600,30),
	 	BIGTWO_DIMENSION = new Dimension(600, 600), //(int) ( (MAX_PLAYER + 1) * 
	 				//(LABEL_DIMENSION.getHeight() + AVATAR_DIMENSION.getHeight()) )),
	 	FRAME_DIMENSION = new Dimension(1000,
	 		(int) (BIGTWO_DIMENSION.getHeight() + BTNPNL_DIMENSION.getHeight())),
		PANEL_DIMENSION = new Dimension(600, (int) FRAME_DIMENSION.getHeight()),
		MSG_DIMENSION = new Dimension(395, (int) FRAME_DIMENSION.getHeight()),
		MSG_DIMENSION_MIN = new Dimension(150,(int) BIGTWO_DIMENSION.getHeight()),
		CHAT_INPUT_DIMRNSION_MIN = new Dimension((int) MSG_DIMENSION.getWidth(), CHAT_INPUT_HEIGHT),
		CHAT_DIMRNSION = new Dimension((int) MSG_DIMENSION.getWidth(), 225),
		CHAT_DIMRNSION_MIN = new Dimension((int) MSG_DIMENSION.getWidth(), 175),
		BTNPNL_DIMENSION_MIN = new Dimension(150,30),
		LABEL_DIMENSION_MIN = new Dimension(128,20);

	 private static final int
	 	GAME_PANEL_WIDTH_MIN = ((int) CARD_DIMENSION.getWidth() - HORIZONTAL_SHIFT)
	 								* MAX_CARD_NUM + (int) AVATAR_DIMENSION.getWidth()
	 								+ GAME_PANEL_WIDTH_TOLERANCE,
	 	GAME_PANEL_WIDTH_MAX = (int) BIGTWO_DIMENSION.getWidth() + GAME_PANEL_WIDTH_TOLERANCE,
	 	FRAME_WIDTH_MIN = GAME_PANEL_WIDTH_MIN + (int) MSG_DIMENSION_MIN.getWidth(),
	 	FRAME_HEIGHT_MIN = 600;// (int) ((MAX_PLAYER + 1) * (LABEL_DIMENSION.getHeight() + AVATAR_DIMENSION.getHeight()) + BTNPNL_DIMENSION_MIN.getHeight());

	 private static final Dimension
	 	BIGTWO_DIMENSION_MIN = new Dimension(150,(int) BIGTWO_DIMENSION.getHeight()),
	 	FRAME_DIMENSION_MIN = new Dimension(FRAME_WIDTH_MIN, 600),
		PANEL_DIMENSION_MIN = new Dimension(FRAME_WIDTH_MIN,(int) FRAME_DIMENSION.getHeight()),
 		FRAME_DIMENSION_MAX = new Dimension(2000,1600),
		PANEL_DIMENSION_MAX = new Dimension(GAME_PANEL_WIDTH_MAX,1600),
		CHAT_DIMENSION_MAX = new Dimension(Short.MAX_VALUE,CHAT_INPUT_HEIGHT),
		BIGTWO_DIMENSION_MAX = new Dimension(GAME_PANEL_WIDTH_MAX,1500),
		BTNPNL_DIMENSION_MAX = new Dimension(GAME_PANEL_WIDTH_MAX,30),
		LABEL_DIMENSION_MAX = new Dimension(128,20);


	 private CardGame game; // The BigTwo game associated with this table
	 private boolean[] selected; // Indicating which cards are selected
	 private int activePlayer = -1; // Specifying the index of the active player
	 private int numOfPlayers; // Number of players

	 // GUI-related variables
	 private JFrame frame; // The main window
	 private JPanel contentPanel, bigTwoPanel, buttonPanel; // The panel for showing cards of each player and the cards played on table
	 private JButton playButton, passButton; // The buttons for the active player
	 private JTextArea msgArea, chatArea;
	 private JTextField chatMsgArea; // Text area for showing the current game status and the end of game messages
	 private JScrollPane msgPane, chatMsgPane, chatPane; // Panel containing the text area
	 private BigTwoMenu menuBar; // The menubar
	 private HashMap<String, JMenuItem> gameMenuList; // Menuitem list
	 private ArrayList<InfoLabel> infoLabel; // The labels

	 // Graphics-related variables
	 private Image[][] cardImages; // 2D array for faces of the cards
	 private Image[][] buttonImages; // Buttont images
	 private Image cardBackImage, guiIcon; // Card back and GUI Icon images
	 private Image[] avatars; // Avatar images

/**
 * Public constructor of the class, used for
 * constructing the instance of the class, associated
 * the table with the BigTwo game, loading images and creating
 * a GUI frame.
 * <br>
 * @param game
 *				the reference to the card game associated with this table.
 */
public BigTwoTable(CardGame game)
{

	// Associate the game
	this.game = game;
	this.numOfPlayers = game.getNumOfPlayers();
	PANEL_COLOR = new Color[numOfPlayers];
		PANEL_COLOR[0] = new Color(68, 76, 92);
		PANEL_COLOR[1] = new Color(206, 90, 87);
		PANEL_COLOR[2] = new Color(120, 165, 163);
		PANEL_COLOR[3] = new Color(225, 177, 106);

	// Loading images
	cardImages = new Image[MAX_SUIT_NUM][MAX_CARD_NUM];
	buttonImages = new Image[2][2];
	avatars = new Image[numOfPlayers];
	this.loadImages();
	this.selected = new boolean[MAX_CARD_NUM];
	this.infoLabel = new ArrayList<InfoLabel>(numOfPlayers);

	// Create the frame
	frame = new GameFrame();
	contentPanel = new ContentPanel();
	frame.setContentPane(contentPanel);		
	this.printMsg(String.format("%s%n", WELCOME_MSG));

}

	// Overridden abstract methods

/**
 * Sets the index of the current active player;
 * <br>
 * @param activePlayer
 *				an integer specifying the index of the active player
 */
@Override
public void setActivePlayer(int activePlayer)
{
	// Verify and set the active player of the game
	if (activePlayer < 0 || activePlayer >= numOfPlayers) {
		this.activePlayer = -1;
	} else {
		this.activePlayer = activePlayer;
	}
}



/**
 * Gets the array of cards selected, as specifying in card indices.
 * This method returns null if there are no cards being selected.
 * <br>
 * @return an integer array of indices of the cards selected
 */
@Override
public int[] getSelected()
{
	CardGamePlayer player = (this.game.getPlayerList()).get(this.activePlayer);
	int[] cardIdx = null;

	// Obtain the number of cards selected
	int count = 0;
	for(int j = 0; j < this.selected.length; ++j)
	{
		if(this.selected[j])
		{
			++count;
		}
	}

	// Check whether there are cards being selected
	if(count != 0)
	{
		cardIdx = new int[count];
		count = 0;
		for(int i = 0; i < this.selected.length; ++i)
		{
			if(this.selected[i])
			{
				// Selecting cards
				cardIdx[count] = i;
				++count;
			}
		}
	}

	return cardIdx;

}

/**
 * This methods resets the list of cards selected, which is
 * useful for repainting after invalid selection.
 * <br>
 */
@Override
public void resetSelected()
{
	this.selected = new boolean[MAX_CARD_NUM];
	this.bigTwoPanel.repaint();
}

/**
 * This method is used for repainting the GUI.
 * This method should be called whenever there is a change
 * in active player. Normally after the CheckMove() method
 * in BigTwo class.
 * <br>
 */
@Override
public void repaint()
{

	// Setting message area
	String consoleMsg = "";

	// Check whether game ends.
	int currentIdx = this.game.getCurrentIdx();
	if(this.activePlayer != currentIdx)
	{
		this.disable();
	} else {
		this.enable();
	}

	if(currentIdx >=0 && currentIdx < numOfPlayers && this.activePlayer == currentIdx)
	{
		CardGamePlayer player = this.game.getPlayerList().get(this.game.getCurrentIdx());
		// Prepare message
		consoleMsg += String.format("<%s> %s", activePlayer == currentIdx ? "You" : player.getName(), player.getCardsInHand().toString());
	}

	bigTwoPanel.setBackground( (0 <= activePlayer && activePlayer < MAX_PLAYER ) ? PANEL_COLOR[activePlayer] : HAND_PANEL_COLOR);

	// Redraw the bigTwoPanel
	bigTwoPanel.repaint();

	// this.resetSelected();
	this.selected = new boolean[MAX_CARD_NUM];

	this.printMsg(consoleMsg);

	// Update labels
	for(int i = 0; game.getPlayerList() != null && i < game.getPlayerList().size(); ++i)
	{		
		CardGamePlayer player = game.getPlayerList().get(i);
		InfoLabel label = infoLabel.get(i);
		label.setText( i == activePlayer ? "You" : player.getName() );

		// Bold the lable of the current player
		if(i == currentIdx) {
			label.setFont(label.getFont().deriveFont(label.getFont().getStyle() | Font.BOLD));
		} else {
			label.setFont(label.getFont().deriveFont(label.getFont().getStyle() & ~Font.BOLD));
		}
	}

	// Latest hand played on table
	int latestIdx =  ((BigTwoClient) game).getLatestIdx();
	if(0 <= latestIdx && latestIdx < numOfPlayers)
	{
		CardGamePlayer latestPlayer = game.getPlayerList().get(latestIdx);

		infoLabel.get(infoLabel.size() - 1).updateInfo(
			(game.getHandsOnTable() == null || game.getHandsOnTable().size() == 0 ) ? 
			"" : latestPlayer.getName());		
	}

	// Check whether need to disable the "Connection" menu item
	this.gameMenuList.get("Connect").setEnabled(!((BigTwoClient) game).getConnectionStatus());

	// Repaint and revalidate the frame
	frame.repaint();
	frame.revalidate();

	// Refocus the frame for KeyListener
	frame.setFocusable(true);

}


/**
 * Prints the message string to the message area in the table.
 * A newline character will be automatically printed for
 * every message.
 * <br>
 * @param msg
 *			the string to be printed to the message area
 */
@Override
public void printMsg(String msg)
{
	this.msgArea.append(msg + String.format("%n"));
}

/**
 * Prints the chat message string to the message area in the table.
 * A newline character will be automatically printed for
 * every message.
 * <br>
 * @param msg
 *			the string to be printed to the chat message area
 */
public void printChatMsg(String msg)
{
	this.chatArea.append(msg + String.format("%n"));
}


/**
 * This method clears the message area of the table.
 * <br>
 */
@Override
public void clearMsgArea()
{
	this.msgArea.setText(WELCOME_MSG);
}

/**
 * This method clears the chat message area of the table.
 * <br>
 */
public void clearChatArea()
{
	this.chatArea.setText(null);
}

/**
 * This method is used to get the message
 * in the message area in the table, for example,
 * for the purpose of saving the game log.
 * <br>
 * @return the text in the message area
 */
public String getMessage()
{
	return this.msgArea.getText();
}

/**
 * This method is used for resetting the GUI when the connection is lost
 * <br>
 */
@Override
public void reset()
{
	// Clear the message area
	this.repaint();


}

/**
 * This method enables user interactions with GUI by enabling
 * the Play and Pass buttons.
 * <br>
 */
@Override
public void enable()
{
	this.playButton.setEnabled(true);
	this.passButton.setEnabled(true);
}

/**
 * This method disables user interactions with GUI by
 * disabling the Play and Pass buttons, which Normally
 * is called immediately after game ends.
 * <br>
 */
@Override
public void disable()
{
	this.playButton.setEnabled(false);
	this.passButton.setEnabled(false);
}

/**
 * This method load Images for this GUI.
 * Images including images for cards, avatars, utilities.
 * This method using IO package and thus may throw exception.
 * <br>
 */
public void loadImages()
{
	BufferedImage img = null;
	String path;

	// Loading card images
	for(int i = 0; i < MAX_SUIT_NUM; ++i)
	{
		for(int j = 0; j < MAX_CARD_NUM; ++j)
		{

			path = CARD_PATH + RANK_IDF[j] + SUIT_IDF[i] + CARD_TYPE;

			try {
		    	img = ImageIO.read(new File(path));
			} catch (IOException e) {
				System.out.format("FAILED LOADING %s%n", path);
				img = null;
			} finally {
				cardImages[i][j] = img;
			}
		}
	}

	// Loading card back image
	path = CARD_PATH + CARD_BACK + CARD_TYPE;
	try {

    	img = ImageIO.read(new File(path));

	} catch (IOException e) {
		System.out.format("FAILED LOADING %s%n", path);
		img = null;
	} finally {
		cardBackImage = img;
	}

	// Loading avatars
	for(int i = 0; i < numOfPlayers; ++i)
	{
		path = AVATAR_PATH + AVATAR_IDF[i] + AVATAR_TYPE;

		try {

	    	img = ImageIO.read(new File(path));

		} catch (IOException e) {
			System.out.format("FAILED LOADING %s%n", path);
			img = null;
		} finally {
			avatars[i] = img;
		}
	}

	// Loading game icon
	path = ICON_PATH + GAME_ICON;

	try {

    	img = ImageIO.read(new File(path));

	} catch (IOException e) {
		System.out.format("FAILED LOADING %s%n", path);
		img = null;
	} finally {
		guiIcon = img;
	}

	// Loading Buttons
	for(int i = 0; i < 2; ++i)
	{
		for(int j = 0; j < 2; ++j)
		{

			path = BUTTON_PATH + BUTTON[i] + BUTTON_STATUS[j] + BUTTON_TYPE;

			try {

		    	img = ImageIO.read(new File(path));

			} catch (IOException e) {
				System.out.format("FAILED LOADING %s%n", path);
				img = null;
			} finally {
				buttonImages[i][j] = img;
			}
		}
	}
}

// The inner classes for this class

/**
 * This inner class is the listener class for PlayButton
 * Playbutton should not work if no cards are selected.
 */
private class PlayButtonListener implements ActionListener
{
	public void actionPerformed(ActionEvent e)
	{
		// System.out.println(playButton.getSize());
		if( getSelected() != null )                  // || activePlayer != mainPlayer) <-- For future use
		{
			game.makeMove(activePlayer, getSelected());
		} else {
			printMsg(String.format("Please select cards to play.%nFor {Pass}, please press <Pass> Button."));
		}

	}
}

/**
 * This inner class is the listener class for PassButton
 */
private class PassButtonListener implements ActionListener
{
	public void actionPerformed(ActionEvent e)
	{
		game.makeMove(activePlayer, null);
	}
}

/**
 * This inner class is the listener class for the menu item Restart.
 */
private class RestartMenuItemListener implements ActionListener
{
	public void actionPerformed(ActionEvent e)
	{
		reset();
	}
}

private class ServerConfigurationMenuItemListener implements ActionListener
{
	public synchronized void actionPerformed(ActionEvent e)
	{
		boolean matched = false;
	    while(!matched)
		{	
			// Create dialog
			String serverIP = ((BigTwoClient) game).getServerIP();
			int serverPort = ((BigTwoClient) game).getServerPort();

			JTextField serverIPField = new JTextField(serverIP, 7);
			JTextField serverPortField = new JTextField("" + serverPort, 6);
			JPanel serverPanel = new JPanel();
			serverPanel.add(new JLabel("IP Address:"));
			serverPanel.add(serverIPField);
			serverPanel.add(Box.createVerticalStrut(5));
			serverPanel.add(new JLabel("Port:"));
			serverPanel.add(serverPortField);

			int response = JOptionPane.showConfirmDialog(frame,
				serverPanel,
				"Server Configuration",
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE);
			
			if(response == JOptionPane.OK_OPTION)
			{
				int port = Integer.parseInt(serverPortField.getText());
				String ip = serverIPField.getText();

				try {
					Inet4Address add = (Inet4Address) InetAddress.getByName(ip);
					matched = true;
				} catch (Exception ex){
					JOptionPane.showMessageDialog(null,
						"Illegal IP host address.",
						"Error",
						JOptionPane.ERROR_MESSAGE);
				}

				if( matched && (port > 1024) )
				{

					if(!ip.equals(serverIP))
					{
						int result = JOptionPane.showConfirmDialog(null,
							String.format("Re-establish in-progress connection will exit current server.%nAre you sure to continue?"),
							"Configuration changed",
							JOptionPane.OK_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE);
						if(result == JOptionPane.OK_OPTION)
						{
							((BigTwoClient) game).setServerIP(ip);
							((BigTwoClient) game).setServerPort(port);
							((BigTwoClient) game).disConnection();
							((BigTwoClient) game).makeConnection();
						}
					}
					matched = true;
				} else if(matched) {
					matched = false;
					JOptionPane.showMessageDialog(null,
						"Illegal IP host address or TCP port number.",
						"Error",
						JOptionPane.ERROR_MESSAGE);
				}				

			} else {
				matched = true;
			}
		}
	}
}

/**
 * This inner class is the listener class for the menu item Connect.
 */
private class ConnectMenuItemListener implements ActionListener
{
	public void actionPerformed(ActionEvent e)
	{
		((NetworkGame) game).makeConnection();
	}
}

/**
 * This inner class is the listener class for the menu item Clear Server Message.
 */
private class ClearServerMessageListener implements ActionListener
{
	public void actionPerformed(ActionEvent e)
	{
		clearMsgArea();
	}
}
/**
 * This inner class is the listener class for the menu item Clear Chat Message.
 */
private class ClearChatMessageListener implements ActionListener
{
	public void actionPerformed(ActionEvent e)
	{
		clearChatArea();
	}
}
/**
 * This inner class is the listener class for the menu item Quit.
 */
private class QuitMenuItemListener implements ActionListener
{
	public void actionPerformed(ActionEvent e)
	{
		int response = JOptionPane.showConfirmDialog(null,
			String.format("Are you sure to quit?%n"),
			"Quit",
			JOptionPane.YES_NO_OPTION,
			JOptionPane.QUESTION_MESSAGE);

		if(response == JOptionPane.YES_OPTION)
		{
			((BigTwoClient) game).disConnection();
			System.exit(0);
		}

	}

}

/**
 * This inner class is the listener class for the menu item SaveLog
 */
private class SaveLogMenuItemListener implements ActionListener
{
	private String path;
	private JFileChooser fileChooser;

	public void actionPerformed(ActionEvent e)
	{
		fileChooser = new JFileChooser(DEFAULT_LOG_PATH);
		fileChooser.setFileFilter( new FileNameExtensionFilter("Log / Text File", "log", "txt") );
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		
		int userAction = fileChooser.showSaveDialog(frame);

		if(userAction == JFileChooser.APPROVE_OPTION)
		{
			File log = fileChooser.getSelectedFile();

			try {
				FileWriter writer = new FileWriter(log);
				writer.write(getMessage());
				writer.close();

			} catch (Exception ex) {
				
				JOptionPane.showMessageDialog(null,
				"Error",
				String.format("Failed to write the file %s%n.", log.getName()),
				JOptionPane.ERROR_MESSAGE);
			}

		}

	}

}

/**
 * This inner class is designed to model the MenuBar used in Big Two game.
 */
private class BigTwoMenu extends JMenuBar
{
	private HashMap<String, JMenu> menuList; // The ArrayList of menulists
	private BigTwoMenu()
	{
		super();
		this.menuList = new HashMap<>();

		JMenu menu = new gameMenu("Game");
		this.menuList.put( "Game", menu );
		this.add(menu);

		menu = new msgMenu("Message");
		this.menuList.put( "Message", menu );
		this.add(menu);
	}
}

/**
 * This inner class is designed to model the game menu used in Big Two game.
 * The instance of this class will be created when initializing the BigTwoMenu class.
 */
private class msgMenu extends JMenu
{
	private HashMap<String, JMenuItem> itemList;
	private msgMenu(String itm)
	{

		super(itm);
		itemList = new HashMap<>();

		JMenuItem item = new JMenuItem("Clear Server Messsage");

		item.addActionListener( new ClearServerMessageListener() );
		this.add(item);
		gameMenuList.put( "Clear Server Messsage", item);

		item = new JMenuItem("Clear Chat Messsage");
		item.addActionListener( new ClearChatMessageListener() );
		this.add(item);
		gameMenuList.put( "Clear Chat Messsage", item);

	}
}

/**
 * This inner class is designed to model the game menu used in Big Two game.
 * The instance of this class will be created when initializing the BigTwoMenu class.
 */
private class gameMenu extends JMenu
{
	private gameMenu(String itm)
	{

		super(itm);
		gameMenuList = new HashMap<>();

		JMenuItem item = new JMenuItem("Server Configuration");

		item.addActionListener( new ServerConfigurationMenuItemListener() );
		this.add(item);
		gameMenuList.put( "Server Configuration", item);

		item = new JMenuItem("Connect");
		item.addActionListener( new ConnectMenuItemListener() );
		this.add(item);
		gameMenuList.put( "Connect", item);

		item = new JMenuItem("Save Log");
		item.addActionListener( new SaveLogMenuItemListener() );
		this.add(item);
		gameMenuList.put( "Save Log", item);

		this.addSeparator();

		item = new JMenuItem("Quit");
		item.addActionListener( new QuitMenuItemListener() );
		this.add(item);
		gameMenuList.put( "Quit", item);

	}
}

/**
 * This inner class is designed to model the message panel used in Big Two game.
 * This component uses Group Layout as Layout Manager
 */
private class MessagePanel extends JPanel
{
	private GroupLayout layout;
	public MessagePanel()
	{
		super();
		this.setPreferredSize(MSG_DIMENSION);
		this.setVisible(true);

		layout = new GroupLayout(this);
		this.setLayout(layout);

		msgArea = new MessageArea();
		chatMsgArea = new ChatMessageArea();
		chatArea = new MessageArea();

		msgPane = new MessagePane();
		chatMsgPane = new MessagePane();
		chatPane = new MessagePane();

		chatMsgArea.addKeyListener(new ChatKeyListener());

		msgPane.setViewportView(msgArea);
		chatMsgPane.setViewportView(chatMsgArea);
		chatPane.setViewportView(chatArea);

		chatMsgPane.setMaximumSize(CHAT_DIMENSION_MAX);
		chatMsgPane.setMinimumSize(CHAT_INPUT_DIMRNSION_MIN);

		chatMsgPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		chatMsgPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

		chatPane.setMinimumSize(CHAT_DIMRNSION_MIN);
		chatPane.setPreferredSize(CHAT_DIMRNSION);

		layout.setHorizontalGroup(layout.createSequentialGroup()
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(msgPane)
				.addComponent(chatPane)
				.addComponent(chatMsgPane))
		);

		layout.setVerticalGroup(layout.createSequentialGroup()
			.addComponent(msgPane)
			.addComponent(chatPane)
			.addComponent(chatMsgPane)
		);

	}
}

/**
 * This inner class is designed to model the scroll pane used in Big Two game
 * for the message area.
 * This component uses Group Layout as Layout Manager.
 */
private class MessagePane extends JScrollPane
{
	public MessagePane()
	{
		super();
		this.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		this.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		this.setWheelScrollingEnabled(true);
	}
}

/**
 * This inner class is designed to model the chat message area used in Big Two game
 */
private class ChatMessageArea extends JTextField
{
	private ChatMessageArea()
	{
		super();
		this.setVisible(true);
		// this.setLineWrap(true);
		this.setFont(CONSOLE_FONT);
		this.setDisabledTextColor(Color.BLACK);
		this.setEnabled(true);

		this.setHorizontalAlignment(JTextField.LEFT);
		// this.setVerticalAlignment(JTextField.UP);
		DefaultCaret caret = (DefaultCaret) this.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	}
}

/**
 * This inner class is designed to model the message area used in Big Two game
 */
private class MessageArea extends JTextArea
{
	private MessageArea()
	{
		super();

		this.setVisible(true);
		this.setLineWrap(true);
		this.setFont(CONSOLE_FONT);
		this.setDisabledTextColor(Color.BLACK);
		this.setEnabled(false);

		DefaultCaret caret = (DefaultCaret) this.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	}
}

/**
 * This inner class is designed to model the panel holding buttons used in Big Two game
 * This component uses Box Layout as Layout Manager.
 */
private class ButtonPanel extends JPanel
{
	BoxLayout layout;
	public ButtonPanel()
	{
		super();
		this.setPreferredSize(BTNPNL_DIMENSION);
		this.setMinimumSize(BTNPNL_DIMENSION_MIN);
		this.setMaximumSize(BTNPNL_DIMENSION_MAX);
		this.setVisible(true);
		this.setBackground(BUTTON_PANEL_COLOR);

		layout = new BoxLayout(this, BoxLayout.LINE_AXIS);
		this.setLayout(layout);

		// Adding buttons
		playButton = new JButton("PLAY");
		playButton.setVisible(true);
		playButton.addActionListener(new PlayButtonListener());
		playButton.setBackground(BUTTON_COLOR);
		playButton.setForeground(BUTTON_TEXT_COLOR);

		/* Following two lines are custome button icons, which are disabled due to poor compatibility. */
		// playButton.setIcon(new ImageIcon(buttonImages[0][0]));
		// playButton.setPressedIcon(new ImageIcon(buttonImages[0][1]));

		passButton = new JButton("PASS");
		passButton.setVisible(true);
		passButton.addActionListener(new PassButtonListener());
		passButton.setBackground(BUTTON_COLOR);
		passButton.setForeground(BUTTON_TEXT_COLOR);

		/* Following two lines are custome button icons, which are disabled due to poor compatibility. */
		// passButton.setIcon(new ImageIcon(buttonImages[1][0]));
		// passButton.setPressedIcon(new ImageIcon(buttonImages[1][1]));

		// Adding constraints
		/* Note
		 * To fasciliate the dynamimc resizing of the button panel,
		 * using of the glue is implemented.
		 * By experience, glues are added three times to the left
		 * and three times to the right.
		 */
		this.add(Box.createHorizontalGlue());
		this.add(Box.createHorizontalGlue());
		this.add(Box.createHorizontalGlue());
		this.add(playButton);
		this.add(Box.createHorizontalGlue());
		this.add(passButton);
		this.add(Box.createHorizontalGlue());
		this.add(Box.createHorizontalGlue());
		this.add(Box.createHorizontalGlue());
	}
}


/**
 * This inner class is designed to model the gaming panel of the Big Two game.
 * This component creates and holds all instances of the HandsPanel, possibly 
 * PlayPanel and adds them to itself.
 *
 * This component uses Group Layout as Layout Manager.
 */
private class BigTwoPanel extends JPanel implements MouseListener
{
	// Private variables
	private SpringLayout layout;
	private double x_resize;
	private double y_resize;

	// Constructor
	private BigTwoPanel()
	{
		// Construction
		super();
		this.setMinimumSize(BIGTWO_DIMENSION_MIN);
		this.setPreferredSize(BIGTWO_DIMENSION);
		this.setMaximumSize(BIGTWO_DIMENSION_MAX);
		this.setVisible(true);
		this.setBackground(GAME_PANEL_COLOR);


		layout = new SpringLayout();

		// Setting up layout
		this.setLayout(layout);
		this.addMouseListener(this);

		for(int i = 0; i < numOfPlayers; ++i)
		{
			// int idx = PLAYER_SWICH ? ((activePlayer + i) % numOfPlayers) : i;
			InfoLabel label = new InfoLabel();
			infoLabel.add(label);
			this.add(label);

			layout.putConstraint(SpringLayout.NORTH, label,
                     VERTICIAL_INIT_HIGHT_LAEBL + i * ((int) (AVATAR_DIMENSION.getHeight() + LABEL_DIMENSION.getHeight())),
                     SpringLayout.NORTH, this);

			layout.putConstraint(SpringLayout.WEST, label,
                     HORIZONTAL_INIT_LABEL, 
                     SpringLayout.WEST, this);
		}

		InfoLabel label = new InfoLabel(String.format("<Table>"));
		label.setPreferredSize(CONLABEL_DIMENSION);

		this.add(label);
		infoLabel.add(label);
		layout.putConstraint(SpringLayout.NORTH, label,
                 VERTICIAL_INIT_HIGHT_LAEBL + numOfPlayers * ((int) (AVATAR_DIMENSION.getHeight() + LABEL_DIMENSION.getHeight())),
                 SpringLayout.NORTH, this);	

		layout.putConstraint(SpringLayout.WEST, label,
                 HORIZONTAL_INIT_LABEL, 
                 SpringLayout.WEST, this);
	}

	/**
	 * This method is used to paint the
	 * component in the panel.
	 * <br>
	 * @param g system canvas
	 */
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		// The resizing factors make it possible for the component to 
		// resize dynamically. However, due to the poor aesthetic performance
		// of unbalanced resizing, this function is disabled.
		x_resize = 1.0; //frame.getWidth() * 1.0 / FRAME_DIMENSION_MIN.getWidth();
		y_resize = 1.0; //frame.getHeight() * 1.0 / FRAME_DIMENSION_MIN.getHeight();
		
		for(int i = 0; i < numOfPlayers; ++i)
		{
			CardGamePlayer player = game.getPlayerList().get(i);
			CardList currentCards = player.getCardsInHand();

			g.setColor( PANEL_COLOR[i] );
			g.fillRect(0, (int) ( i * ((int) ( AVATAR_DIMENSION.getHeight()
						 + LABEL_DIMENSION.getHeight()) ) * y_resize),
						Short.MAX_VALUE,
						Short.MAX_VALUE);
			
			for(int j = 0; j < currentCards.size(); ++j)
			{
				Image img = cardBackImage;
				if(i == activePlayer || game.getCurrentIdx() == -1)
				{
					Card card = currentCards.getCard(j);
					img = cardImages[card.getSuit()][card.getRank()];
				}



				g.drawImage(img, 
							(int) ((AVATAR_DIMENSION.getWidth() * x_resize + j * HORIZONTAL_SHIFT )),
							(int) ((VERTICIAL_INIT_HIGHT + i * ( (int) (AVATAR_DIMENSION.getHeight()
								+ LABEL_DIMENSION.getHeight()) ) 
								+ (selected[j] && (i == activePlayer) ? VERTICAL_SHIFT : 0)) * y_resize),
							this); 
			}
			
			Image img = avatars[i];
			// Show avatars if the avatar represents the current player OR the player has joined
			if( ( i == activePlayer ) || (( (BigTwoClient) game).getConnectionStatus() && player.getName() != "") )
			{			
				g.drawImage(img, 
						(int) (0.1 * x_resize),
						(int) ((VERTICIAL_INIT_HIGHT_AVATAR + i * ((int) ( AVATAR_DIMENSION.getHeight()
							 + LABEL_DIMENSION.getHeight()) ) ) * y_resize),
						// (int) (AVATAR_DIMENSION.getWidth() * x_resize), (int) (AVATAR_DIMENSION.getHeight() * y_resize),
						this); 
			}
		}

		g.setColor( HAND_PANEL_COLOR );
		g.fillRect(0, (int) ( (numOfPlayers) * ((int) ( AVATAR_DIMENSION.getHeight()
				 + LABEL_DIMENSION.getHeight()) ) * y_resize),
				Short.MAX_VALUE,
				Short.MAX_VALUE);
			

		ArrayList<Hand> handsOnTable = game.getHandsOnTable();

		if(handsOnTable != null && handsOnTable.size() > 0)
		{
			int sum = 0, idx = handsOnTable.size() - 1;
			while(sum < MAX_CARDS_DISPLAYED && idx > 0)
			{
				sum += handsOnTable.get(idx).size();
				--idx;
			}

			int counter = 0;
			for(int i = idx; i < handsOnTable.size(); ++i)
			{
				Hand hand = handsOnTable.get(i);

				for(int j = 0; j < hand.size(); ++j)
				{

					Card card = hand.getCard(j);
					Image img = cardImages[card.getSuit()][card.getRank()];
					g.drawImage(img, 
								HORIZONTAL_INIT_HIGHT_HAND + (counter++) * HORIZONTAL_SHIFT,
								VERTICIAL_INIT_HIGHT_HAND,
								this); 				
				}
			}

		}
		this.revalidate();

	}

	/**
	 * This method is used to update the layout for
	 * the card at given index.
	 */
	@Override
	public void mouseClicked(MouseEvent e)
	{
		CardList cards = game.getPlayerList().get(activePlayer).getCardsInHand();

		if(activePlayer != -1 && cards.size() > 0 )
		{
			int x = e.getX(), y = e.getY();
			// System.out.format("X: %d Y: %d%n", x, y);

			for(int i = 0; i < cards.size() - 1; ++i)
			{
				if(cardInside(x, y, i) && !cardInside(x, y, i + 1))
				{
					selected[i] = !selected[i];
				}

			}

			if(cardInside(x, y, cards.size() - 1))
			{
				selected[cards.size() - 1] = !selected[cards.size() - 1];
			}	

			this.repaint();		
		}

	}

	private boolean cardInside(int x, int  y, int idx)
	{
		int xL = (int) (AVATAR_DIMENSION.getWidth() * x_resize + idx * HORIZONTAL_SHIFT);
		int xR = xL + (int) CARD_DIMENSION.getWidth();
		int yU = VERTICIAL_INIT_HIGHT 
			+ (int) ( y_resize * activePlayer * ((AVATAR_DIMENSION.getHeight() 
								+ LABEL_DIMENSION.getHeight() )) 
								+ (selected[idx] ? VERTICAL_SHIFT : 0) );
		int yD = yU + (int) CARD_DIMENSION.getHeight();
		// System.out.format("LOCATIONS FOR %d%n%d %d%n%d %d%n%n", idx, xL, xR, yU, yD);
		if(x >= xL && x <= xR && y >= yU && y <= yD)
		{
			return true;
		}
		return false;

	}

	@Override
	public void mousePressed(MouseEvent e){}
	
	@Override
	public void mouseReleased(MouseEvent e){}
	
	@Override
	public void mouseEntered(MouseEvent e){}
	
	@Override
	public void mouseExited(MouseEvent e){}
}


/**
 * This inner class is designed to model a label using for
 * showing informatio in the Big Two game.
 */
private class InfoLabel extends JLabel
{
	private InfoLabel()
	{
		this.setMinimumSize(LABEL_DIMENSION);
		this.setPreferredSize(LABEL_DIMENSION);
		this.setMaximumSize(LABEL_DIMENSION_MAX);
		this.setVisible(true);
		this.setFont(DISPLAY_FONT);
		this.setForeground(Color.WHITE);
	}

	// Constructor for showing arbitary text
	private InfoLabel(String txt)
	{
		this();
		this.setText(txt);
	}

	// Constructor for showing player name
	private InfoLabel(int idx)
	{
		this();
		this.updatePlayer(idx);
	}

	private void updateInfo(String msg)
	{
		this.setText(String.format("<Table> %n %s", msg));
	}

	private void updatePlayer(int idx)
	{
		if(idx >= 0 && idx < numOfPlayers)
		{
			this.setText(game.getPlayerList().get(idx).getName());
		}
	}
}


/**
 * This inner class is designed to model the main content
 * panel used in the Big Two game.
 * This is a subclass of JPanel, it creates and holds the bigTwoPanel,
 * buttonPanel and msgPanel.
 *
 * It uses GroupLayout as Layout Manager
 */
private class ContentPanel extends JPanel
{
	GroupLayout layout;

	private ContentPanel()
	{
		this.setSize(FRAME_DIMENSION);
		this.setMinimumSize(FRAME_DIMENSION_MIN);
		this.setMaximumSize(PANEL_DIMENSION_MIN);
		this.setVisible(true);
		this.setBackground(HAND_PANEL_COLOR);

		layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setAutoCreateGaps(false);
		layout.setAutoCreateContainerGaps(false);

		bigTwoPanel = new BigTwoPanel();
		buttonPanel = new ButtonPanel();

		JPanel msgPanel = new MessagePanel();


		layout.setHorizontalGroup(layout.createSequentialGroup()
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(bigTwoPanel)
				.addComponent(buttonPanel))
			.addComponent(msgPanel)
		);

		layout.setVerticalGroup(layout.createParallelGroup()
			.addGroup(layout.createSequentialGroup()
				.addComponent(bigTwoPanel)
				.addComponent(buttonPanel))
			.addComponent(msgPanel)
		);

	}


}

/**
 * This inner class is designed to model the GUI frame for 
 * the Big Two game.
 * This is a subclass of JFrame, it creates and holds its one
 * and only one component, namely, the contentPanel.
 * It also addes the menubar and associate with the KeyListener
 * for interact with user keyboard actions.
 *
 */
private class GameFrame extends JFrame
{
	private GameFrame()
	{
		super();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.setSize(FRAME_DIMENSION);
		this.setMinimumSize(FRAME_DIMENSION_MIN);
		this.setVisible(true);
		this.setTitle(String.format("Big Two Game (%s)", ((BigTwoClient) game).getPlayerName()));

		this.setIconImage(guiIcon);

		menuBar = new BigTwoMenu();
		this.setJMenuBar(menuBar);

		this.addKeyListener( new KeyBoardListener());
	}


}

/**
 * This class implements the KeyListener,
 * it is used for sending the chat messages to
 * the server.
 */
private class ChatKeyListener implements KeyListener
{	
	@Override
	public void keyPressed(KeyEvent e)
	{	
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				if(chatMsgArea.getText() != null) {
					if(!Pattern.matches( 
						String.format("\\p{javaWhitespace}*"), chatMsgArea.getText() )) {
						CardGameMessage msg = new CardGameMessage(CardGameMessage.MSG,0,chatMsgArea.getText());
						chatMsgArea.setText("");
						((BigTwoClient) game).sendMessage(msg);
					}
					chatMsgArea.setText("");
				}
				chatMsgArea.setCaretPosition(0);
				frame.requestFocusInWindow();
		}

	}

	@Override
	public void keyTyped(KeyEvent e){}

	@Override
	public void keyReleased(KeyEvent e){}
}

/**
 * This class implements the KeyListener,
 * it is used for interact with user input,
 * which is an alternative way for controlling 
 * the game.
 */
private class KeyBoardListener implements KeyListener
{
	// private final ArrayList<Integer> pressed = new ArrayList<Integer>;
	ComposeHands composedhands;

	@Override
	public void keyPressed(KeyEvent e)
	{
		// pressed.add(e.getKeyCode());

		if(e.getKeyCode() == KeyEvent.VK_ENTER && ((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) == InputEvent.CTRL_DOWN_MASK))
		{
			if(activePlayer == game.getCurrentIdx())
			{			
				ArrayList<Hand> handsOnTable = game.getHandsOnTable();
				Hand latestHand = handsOnTable.size() == 0 ? null : handsOnTable.get(handsOnTable.size() - 1);
				composedhands = new ComposeHands(game.getPlayerList().get(activePlayer), latestHand);
				printMsg(composedhands.getAdvice());
			}
		}
		else if (e.getKeyCode() == KeyEvent.VK_ENTER && ((e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) == InputEvent.SHIFT_DOWN_MASK))
		{
			if(activePlayer == game.getCurrentIdx())
			{			
				if( getSelected() != null )
				{
					game.makeMove( activePlayer, getSelected() );
				} else {
					printMsg(String.format("Please select cards to play.%nFor {Pass}, please press <Pass> Button."));
				}
			}
		} else if (e.getKeyCode() == KeyEvent.VK_SPACE ) {
			game.makeMove( activePlayer, null );	
		}

	}	

	@Override
	public void keyTyped(KeyEvent e){}

	@Override
	public void keyReleased(KeyEvent e){}
}

}
