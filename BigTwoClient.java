import java.util.ArrayList;
import java.util.regex.Pattern;

import java.io.*;


import java.net.Socket;
import java.net.InetAddress;
import java.net.Inet4Address;

import javax.swing.*;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.Box;
import javax.swing.filechooser.FileNameExtensionFilter;


/**
 * The BigTwoClient class is used to model a Big Two card game client.
 * It stores the information of the deck of cards, list of players,
 * list of hands played on the table, the index of the current player,
 * and a console for providing the user interface.
 *  <br>
 *  <p> The implementation of the game can be better presented by the
 * diagram below:
 *  <br> <img src="doc-files/Class Overview.svg" alt="Class Overview">
 *	<p> The detailed heirarchy of classes can be found at the "doc-files" folder
 *
 *	<br>
 *	<p>	<b>Copyright Information</b>
 *	<br> <br> <i> COMP 2396 OOP &amp; Java Assignment #5</i>
 * 	<br> Version 0.1 2016-11-19
 *	<br> <i> ZHANG, Jiayao Johnson / 3035233412 </i>
 *	<br> Department of Computer Science, The University of Hong Kong
 *  <br> <a href = "jyzhang@cs.hku.hk"> Contact me via email </a>
 *  <br> <a href = "http://i.cs.hku.hk/~jyzhang/"> Visit my webpage </a>
 *
 *  <br> Version 0.1 at 2016-11-19
 * @author ZHANG, Jiayao Johnson
 * @version 0.1
 * @since 2016-10-07
 */
public class BigTwoClient implements CardGame , NetworkGame
{
  // Creating private instance variables
  private static final int MAX_PASS = 3; // Maximum number of consecutive occurrence of the "Pass"
  private static final int numOfPlayers = 4; // Number of Players
  private static final int WAITING_TIME_JOIN = 500; // Time waited for server reply
  private static final String DEFAULT_IP = "127.0.0.1";
  private static final int DEFAULT_PORT = 2396;


  private Deck deck;  // A deck of cards
  private ArrayList<CardGamePlayer> playerList; // A list of players
  private ArrayList<Hand> handsOnTable; // A list of hands played on the table
  private int playerID = -1, currentIdx = -1, latestIdx = -1; // The idnex of the current player (-1 for unset)
  private int passCounter;

  private String playerName = "New Player"; // The name of the local player
  private String serverIP; // The IP address of the game server
  private int serverPort; // The TCP port of the game server
  private Socket sock; // The socket connection to the game server
  private ObjectOutputStream oos; // An ObjectOutputStream for sending messages to the server

  private CardGameTable table; // The table associated with this game
  private boolean gameJoined = false, gameInProgress = false;
  //private BigTwoConsole bigTwoConsole;  // The BigTwoConsole object providing user interface


/**
 *  The constructor for creating a Big Two card game client.
 *  Four players will be initialized and stored into
 *  an ArrayList. The console is also created.
 *  <br>
 */
public BigTwoClient()
{
   // Creating a new table and associate the instance of the BigTwo object
   this.playerList = new ArrayList<CardGamePlayer>();
   for(int i = 0;i < this.numOfPlayers; ++i)
   {
	 this.playerList.add(new CardGamePlayer());
	 this.playerList.get(i).setName(null);
   }

   this.handsOnTable = new ArrayList<Hand>();


	// Create dialog
	JTextField playerNameField = new JTextField(playerName, 8);
	JPanel playerNamePanel = new JPanel();
	playerNamePanel.add(new JLabel("Your Name:"));
	playerNamePanel.add(playerNameField);

	JOptionPane.showConfirmDialog(null,
		playerNamePanel,
		"Welcome to Big Two Game",
		JOptionPane.OK_OPTION,
		JOptionPane.PLAIN_MESSAGE);

	this.setPlayerName(playerNameField.getText());

   this.table = new BigTwoTable(this);
   table.repaint();

   boolean matched = false;
   while(!matched)
	{	
		// Create dialog
		JTextField serverIPField = new JTextField(DEFAULT_IP, 7);
		JTextField serverPortField = new JTextField("" + DEFAULT_PORT, 6);
		JPanel serverPanel = new JPanel();
		serverPanel.add(new JLabel("IP Address:"));
		serverPanel.add(serverIPField);
		serverPanel.add(Box.createVerticalStrut(5));
		serverPanel.add(new JLabel("Port:"));
		serverPanel.add(serverPortField);

		int response = JOptionPane.showConfirmDialog(null,
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
				this.serverIP = ip;
				this.serverPort = port;
				matched = true;
			} else if(matched) {
				matched = false;
				JOptionPane.showMessageDialog(null,
					"Illegal IP host address or TCP port number.",
					"Error",
					JOptionPane.ERROR_MESSAGE);
			}				

		} else {
			this.serverIP = DEFAULT_IP;
			this.serverPort = DEFAULT_PORT;
			matched = true;
		}
	}

	System.out.format("IP: %s Port: %s%n", serverIP, serverPort);


   this.makeConnection();

}

/**
 * The accessor for retreiving the number of players
 * <br>
 * @return the number of players
 */
@Override
public int getNumOfPlayers()
{
	return this.numOfPlayers;
}

/**
 * The accessor method for retreiving the deck of cards being used.
 *  <br>
 *  @return The deck of cards being used
 */
@Override
public Deck getDeck()
{
   return this.deck;
}

/**
 * The accessor method for retreiving the list of the players.
 * <br>
 * @return The list of players
 */
@Override
public ArrayList<CardGamePlayer> getPlayerList()
{
	return this.playerList;
}

/**
 * The accessor method for retreiving the list of hands played on the table.
 * <br>
 * @return The list of hands played on the table
 */
@Override
public ArrayList<Hand> getHandsOnTable()
{
	return this.handsOnTable;
}

/**
 * The accessor method for retreiving the index of the current player.
 * <br>
 * @return The index of current player
 */
@Override
public int getCurrentIdx()
{
	return this.currentIdx;
}

/**
 * Get the index of the local player
 * <br>
 *	@return an integer specifying the index of the local player
 */
@Override
public int getPlayerID()
{
	return this.playerID;
}

/**
 * Set the index of the local player
 * <br>
 * @param playerID an integer specifying the local player ID
 */
@Override
public void setPlayerID(int playerID)
{
	this.playerID = playerID;
}

/**
 * Get the name of the local player
 * <br>
 *	@return a String specifying the name of the local player
 */
@Override
public String getPlayerName()
{
	return this.playerName;
}

/**
 * Set the name of the local player
 * <br>
 * @param playerName a String specifying the local player name
 */
@Override
public void setPlayerName(String playerName)
{
	this.playerName = playerName == "" ? "Anonymous" : playerName;
}

/**
 * Get the server IP of the game server
 * <br>
 *	@return a String specifying the IP address of the game server
 */
@Override
public String getServerIP()
{
	return this.serverIP;
}

/**
 * Set the server IP of the game server
 * <br>
 *	@param serverIP a String specifying the IP address of the game server, default
 *				value is used should the address be illegal.
 */
@Override
public void setServerIP(String serverIP)
{
	try {
		Inet4Address add = (Inet4Address) InetAddress.getByName(serverIP);
	} catch (Exception ex){
		JOptionPane.showMessageDialog(null,
			"Illegal IP host address.",
			"Error",
			JOptionPane.ERROR_MESSAGE);
		this.serverIP = DEFAULT_IP;
	}

	this.serverIP = serverIP;
}

/**
 * Get the TCP port of the game server
 * <br>
 *	@return a String specifying the TCP port of the game server
 */
@Override
public int getServerPort()
{
	return this.serverPort;
}

/**
 * Set the server TCP port of the game server
 * <br>
 *	@param serverPort an integer specifying the TCP port of the game server, default
 *				value is used should the address be illegal.
 */
@Override
public void setServerPort(int serverPort)
{
	this.serverPort = (serverPort > 1024) ? serverPort : DEFAULT_PORT;
}

/**
 * Making a socket connection with the game server
 * <br>
 */
@Override
public void makeConnection()
{
	try {
		this.sock = new Socket(InetAddress.getByName(serverIP), serverPort);
		oos = new ObjectOutputStream(sock.getOutputStream());
		PrintWriter writer = new PrintWriter(oos);
		
		Thread serverHandler = new Thread(new ServerHandler(sock));
		serverHandler.start();

		System.out.println("Connection established");
		System.out.println("Joining game...");

		// Send JOIN message
		this.sendMessage(new CardGameMessage(CardGameMessage.JOIN, -1, this.playerName));

		Thread.sleep(WAITING_TIME_JOIN);

		if(this.gameJoined)
		{
			this.setServerPort(this.sock.getPort());
			this.setServerIP(this.sock.getInetAddress().getHostAddress());
			System.out.format("Joined %s:%d successfully, waiting game to start...", this.serverIP, this.serverPort);
			this.sendMessage(new CardGameMessage(CardGameMessage.READY, -1, null));
		} else {
			System.out.println("Joined uncessfully.");
		}

	} catch(Exception ex) {
		System.out.println("Server non-responding, please check connetion.");
		JOptionPane.showConfirmDialog(null,
		"Server is not responding, please re-connect.",
		"Error",
		JOptionPane.OK_OPTION,
		JOptionPane.ERROR_MESSAGE);

		this.table.printMsg("Unable to connect the server.");
		// ex.printStackTrace();

	} 
}

/**
 * This method is used to get the connection status
 * <br>
 * @return a bool value specifying whether the client has
 *			successfully connected to the server
 */
public boolean getConnectionStatus()
{
	return this.gameJoined;
}

private class ServerHandler implements Runnable
{
	private Socket mySock;
	private ObjectInputStream ois;

	private ServerHandler(Socket sock)
	{
		try{
			mySock = sock;
			ois = new ObjectInputStream(mySock.getInputStream());

			System.out.println("Server Handler standing by.");

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	/**
	 * This method overrides the method from
	 * the Runnable interface, and provide the
	 * concrete implementation for the Thread to handle
	 * Server side messsages/.
	 * <br>
	 */
	@Override
	public void run()
	{
		CardGameMessage message;
		try{
			while( (message = (CardGameMessage) ois.readObject()) != null)
			{
				parseMessage(message);
			}
		} catch(Exception ex) {
			
			System.out.println("Connection lost, please re-connect");

			// disConnection();
			// Create dialog
			int response = JOptionPane.showConfirmDialog(null,
				String.format("Connection lost, would you like to reconnect?"),
				"Reconnect",
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);
			

			if(response == JOptionPane.OK_OPTION)
			{
				makeConnection();
			}
			ex.printStackTrace();
		}
	}
}

/**
 * Used to parse the messages received from the game server.
 * <br>
 * @param message a GameMessage received from the server
 */
@Override
public synchronized void parseMessage(GameMessage message)
{
	int id = message.getPlayerID();
	String name;
	System.out.format("[MESSAGE RECIVED] TYPE: %d >>> PARSING%n", message.getType());

	// Parse messages according to type
	switch(message.getType())
	{
		case CardGameMessage.PLAYER_LIST:
			this.setPlayerID( message.getPlayerID() );
			this.table.setActivePlayer(this.getPlayerID());
			this.playerList.get(this.getPlayerID()).setName(this.getPlayerName());
			String[] names = (String[]) message.getData();
			for(int i = 0; i < numOfPlayers; ++i)
			{
				// Explicitly set the name to an empty string
				// Otherwise null will be converted to the default player
				// nameby the getName() method
				if(this.getPlayerID() != i)
					this.playerList.get(i).setName(names[i] == null ? "" : names[i]);

				System.out.println(this.playerList.get(i).getName());

			}

			this.table.repaint();

			break;

		case CardGameMessage.JOIN:
			if(id == this.getPlayerID())
			{
				this.gameJoined = true;
				table.printMsg(String.format("Joined game at %s.", this.serverIP));
			} else if(this.gameJoined) {
				if(id >= 0 && id < numOfPlayers) {
					this.playerList.get(id).setName((String) message.getData());
					table.printMsg(String.format("%s joined game.", (String) message.getData()));
				}
			}
			this.table.repaint();
			break;

		case CardGameMessage.FULL:
			JOptionPane.showConfirmDialog(null,
				/* Server may send this information on some undefined behaviors of the
				   client. To be more robust, this message doe not merely state the case
				   where server is full. */
				String.format("Failed joining game: server is unreachable or full."),
				"Failed joining game",
				JOptionPane.OK_OPTION);
			break;

		case CardGameMessage.QUIT:

			if(id >= 0 && id < numOfPlayers)
			{
				name = this.playerList.get(id).getName();
				this.playerList.get(id).setName("");

				// If the game is still in progress, the current state need resetting
				// And a new message of type READY will be sent
				if(!endOfGame())
				{
					for(CardGamePlayer player : playerList)
					{
						player.removeAllCards();
						this.handsOnTable = new ArrayList<Hand>();
					}
					this.sendMessage(new CardGameMessage(CardGameMessage.READY, -1, null));
				}

				this.gameInProgress = false;

				this.table.repaint();
				this.table.printMsg(String.format("%s quited the game.",name));

				// Create dialog
				int response = JOptionPane.showConfirmDialog(null,					
					String.format("%s quited the game.%nWaiting for new player.",name),
					"Game stops",
					JOptionPane.OK_OPTION,
					JOptionPane.PLAIN_MESSAGE);			
			}
			break;

		case CardGameMessage.READY:
			if(id >= 0 && id < numOfPlayers)
			{
				name = this.playerList.get(id).getName();
				this.table.printMsg(String.format("%s is ready.",name));
			}
			break;

		case CardGameMessage.START:
			this.gameInProgress = true;
			this.table.printMsg("Game started");
			this.start((Deck) message.getData());
			break;

		case CardGameMessage.MOVE:
			if(0 <= id && id < numOfPlayers)
			{
				this.checkMove(id, (int[]) message.getData());
			}
			break;

		case CardGameMessage.MSG:
			((BigTwoTable) this.table).printChatMsg( (String) message.getData() );
			break;

		default:
			System.out.println("Received unparsable message.");

	}


}

/**
 * Used to send a GameMessage to the server.
 * <br>
 * @param message a GameMessage to be sent to server
 */
@Override
public synchronized void sendMessage(GameMessage message)
{
	try
	{
		System.out.format("[MESSAGE SUCCESSFULLY SENT] TYPE: %d%n", message.getType());
		oos.writeObject(message);
		oos.flush();
	} catch (Exception ex) {
		System.out.println("[MESSAGE SENT FAILED]");
		ex.printStackTrace();
	}
}
/**
 * The accessor method for retreiving the index of the latest player.
 * <br>
 * @return The index of the latest player
 */
public int getLatestIdx()
{
	return this.latestIdx;
}

/**
 * Disconnect the current connection
 * <br>
 */
public synchronized void disConnection()
{
	try
	{
		// Try to close the current socket
		if(this.sock != null)
		{		
			this.sock.close();
			this.sock = null;
		}
		
		for(CardGamePlayer player : playerList)
		{
			player.removeAllCards();
			if(playerID == -1 ? true : player != playerList.get(playerID))
			{ player.setName(""); }
		}
		this.gameInProgress = false;
		this.gameJoined = false;
		this.playerID = -1;
		this.handsOnTable = new ArrayList<Hand>();
		this.table.reset();
		this.table.printMsg("Disconnected");
		
	} catch (IOException ex) {
		System.out.println("Disconnection failed.");
	}
}

/**
 * The method for starting the game with a (shuffled) deck provided in the argument.
 * The game logic is implemented in this method.
 *  <br>
 * @param deck A (shuffled) deck for starting the game
 */
@Override
public void start(Deck deck)
{
	this.deck = deck;
	this.passCounter = 0;

	// Remove all cards from the player and the hand on table
	for(CardGamePlayer player : this.playerList)
	{
		player.removeAllCards();
	}

	// Remove the hands from the table
	this.handsOnTable = new ArrayList<Hand>();

	// Allocate cards and sort cards
	for(int i = 0; i < 52; ++i)
	{
		this.playerList.get(i / 13).addCard(this.deck.getCard(i));
	}

	for(int i = 0; i < numOfPlayers; ++i)
	{
		CardGamePlayer player = this.playerList.get(i);
		player.sortCardsInHand();

		// Find and assign the first player
		Card minCard = player.getCardsInHand().getCard(0);
		if(minCard.getSuit() == 0 && minCard.getRank() == 2)
		{
			this.currentIdx = i;
		}
	}
	
	// Set the first player
	this.latestIdx = this.currentIdx;
	if(this.playerID != this.currentIdx)
	{
		this.table.printMsg(String.format("%s's turn.", this.playerList.get(currentIdx).getName()));
	} else {
		this.table.printMsg("Your turn.");
	}

	// Start playing
	this.table.repaint();
}

/**
 * This method is used for making a move by a
 * player with the specified playerID using the cards specified.
 * <br>
 * @param playerID an interger specifying the player idnex
 * @param cardIdx an integer array of indexes of cards selected by the player
 */
@Override
public void makeMove(int playerID, int[] cardIdx)
{
	CardGameMessage message = new CardGameMessage(CardGameMessage.MOVE, -1, cardIdx);
	this.sendMessage(message);
}

/**
 * This method is used for checking whether the game ends
 * <br>
 * @return a boolean value indicating whether the game ends
 */
@Override
public boolean endOfGame()
{
	// The logic of this method is as follows:
	// (a) the end of game can only occur after the game starts
	// (b) with (a) satisfied, game ends when some player has no more
	//	   cards to play
	boolean cardCheck = false;
	for(CardGamePlayer player : this.playerList)
	{
		cardCheck = cardCheck || (player.getNumOfCards() == 0);
	}

	return (currentIdx != -1) && cardCheck;
}

/**
 * This method is used to check the move made by the current player
 * This method is used when an message is parsed to have a type of MOVE
 * This method also repsonse to the situation where game ends.
 * <br>
 * @param cardIdx
 *			the list of cards selected by the player
 */
@Override
public void checkMove(int playerID, int[] cardIdx)
{
	// Attempt to compose a hand
	CardGamePlayer currentPlayer = this.playerList.get(playerID);
	CardList cards = currentPlayer.play(cardIdx);

	Hand hand = BigTwoClient.composeHand(currentPlayer, cards);

	// Get latest hand
	Hand latestHand = this.handsOnTable.size() == 0 ? null : this.handsOnTable.get(this.handsOnTable.size() - 1);

	// Check whether valid
	if((hand == null)
		|| (latestHand == null && hand.getType() == "Pass")
		|| (latestHand != null && !hand.beats(latestHand)))
	{ return; }

	String message = String.format("<%s> {%s} ", playerID == this.playerID ? "You" : currentPlayer.getName(), hand.getType());

	if(hand.getType() != "Pass")
	{
		message += hand.toString();
		this.passCounter = 0;
		currentPlayer.removeCards(cards);
		this.handsOnTable.add(hand);
		this.latestIdx = this.currentIdx;
	} else {
		++this.passCounter;
	}

	this.table.printMsg(message);

	if(this.passCounter == MAX_PASS)
	{
		this.handsOnTable.clear();
		this.passCounter = 0;
	}

	// Check whether game ends

	if(this.endOfGame())
	{
		// Game end
		this.handsOnTable.clear();
		this.currentIdx = -1;

		String msg = "";

		table.repaint();
		table.disable();

		for(CardGamePlayer player : this.playerList)
		{
			int numOfCards = player.getNumOfCards();
			if(numOfCards != 0)
			{
				msg += String.format("%n%s has %d cards in hand.%n", player.getName(), numOfCards);
			} else {
				msg += String.format("%n%s won the game.%n", player.getName());
			}
		}

		this.table.printMsg(msg);

		int result = JOptionPane.showConfirmDialog(null,
			msg,
			"Game ends",
			JOptionPane.OK_OPTION,
			JOptionPane.PLAIN_MESSAGE);

		// The following code segment is commented as it violates the framework of the assignment sheet
	/*	int response = JOptionPane.showConfirmDialog(null,
		"Would you want to save the game log?",
		"Game log",
		JOptionPane.YES_NO_OPTION,
		JOptionPane.QUESTION_MESSAGE);

		if(response == JOptionPane.YES_OPTION)
		{
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileFilter( new FileNameExtensionFilter("Log / Text File", "log", "txt") );
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			
			int userAction = fileChooser.showSaveDialog(null);

			if(userAction == JFileChooser.APPROVE_OPTION)
			{
				File log = fileChooser.getSelectedFile();

				try {
					FileWriter writer = new FileWriter(log);
					writer.write( ((BigTwoTable) this.table).getMessage());
					writer.close();

				} catch (Exception ex) {
					
					JOptionPane.showMessageDialog(null,
					"Error",
					String.format("Failed to write the file %s%n.", log.getName()),
					JOptionPane.ERROR_MESSAGE);
				}
			}
		}

		JOptionPane.showConfirmDialog(null,
			"Are you read for the new game?",
			"New game",
			JOptionPane.YES_OPTION,
			JOptionPane.QUESTION_MESSAGE);*/

		for(CardGamePlayer player : this.playerList)
		{
			player.removeAllCards();
		}
		table.repaint();

		// Send ready message
		// Note: The user will have trouble in connecting the game if the player
		// does not click OK. However, this is implemented as otherwise the framework
		// of the assignment sheet is violated.
		if(result == JOptionPane.OK_OPTION)
		{ this.sendMessage(new CardGameMessage(CardGameMessage.READY, -1, null)); }
		
		this.table.clearMsgArea();		
		return;
	}
	
	this.currentIdx = (++this.currentIdx) % numOfPlayers;

	if(this.playerID != this.currentIdx)
	{
		this.table.printMsg(String.format("%s's turn.", this.playerList.get(currentIdx).getName()));
	} else {
		this.table.printMsg("Your turn.");
	}

	table.repaint();

}

// Public static methods

/**
 * The method for starting a Big Tww card game.
 * One instance of the game is created, and then is started by a shuffle deck of cards.
 *  <br>
 * @param args This argument will not be used.
 */
public static void main(String[] args)
{
	 // Create a BigTwoClient object
	 BigTwoClient game = new BigTwoClient();

	 // Create a new shuffled deck
	 BigTwoDeck newDeck = new BigTwoDeck();

	 // Start the game with the shuffled deck
	 // game.start(newDeck);
}

/**
 * The method for returning a valid hand from the list of cards of the player.
 * This method returns null if there is no valid hand. An empty hand, if valid
 * will be returned with type "Pass".
 *  <br>
 * @param player the player holding a hand of cards
 * @param cards the cards the player is holding
 * @return a specific hand of cards that is valid for this hand
 */
public static Hand composeHand(CardGamePlayer player, CardList cards)
{
	Hand hand = null;

	// Empty hand?
	if(cards == null)
	{
		if(Pass.isValid(cards))
		{
			hand = new Pass(player, cards);
		}

	} else {
		int numOfCards = cards.size();

		if(numOfCards == 1 && Single.isValid(cards))
		{
			hand = new Single(player, cards);
		} else if (numOfCards == 2 && Pair.isValid(cards)) {
			hand = new Pair(player, cards);
		} else if (numOfCards == 3 && Triple.isValid(cards)) {
			hand = new Triple(player, cards);
		} else if (numOfCards == 5){
			// Check from the top of the hierarchy
			if(StraightFlush.isValid(cards)) {
				hand = new StraightFlush(player, cards);
			} else if (FullHouse.isValid(cards)) {
				hand = new FullHouse(player, cards);
			} else if (Quad.isValid(cards)) {
				hand = new Quad(player, cards);
			} else if (Flush.isValid(cards)) {
				hand = new Flush(player, cards);
			} else if (Straight.isValid(cards)) {
				hand = new Straight(player, cards);
			}
		}
	}

	return hand;
}

}
