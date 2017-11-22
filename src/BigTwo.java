import java.util.ArrayList;

/**
 * The BigTwo class is used to model a Big Two card game.
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
 *	<br> <br> <i> COMP 2396 OOP &amp; Java Assignment #4 </i>
 * 	<br> Version 0.2 2016-11-13
 *	<br> <i> ZHANG, Jiayao Johnson / 3035233412 </i>
 *	<br> Department of Computer Science, The University of Hong Kong
 *  <br> <a href = "jyzhang@cs.hku.hk"> Contact me via email </a>
 *  <br> <a href = "http://i.cs.hku.hk/~jyzhang/"> Visit my webpage </a>
 *
 *  <br> Version 0.2 at 2016-11-13
 * @author ZHANG, Jiayao Johnson
 * @version 0.2
 * @since 2016-10-07
 */
public class BigTwo implements CardGame
{
  // Creating private instance variables
  private static final int MAX_PASS = 3; // Maximum number of consecutive occurrence of the "Pass"
  private static final int NumOfPlayers = 4; // Number of Players

  private Deck deck;  // A deck of cards
  private ArrayList<CardGamePlayer> playerList; // A list of players
  private ArrayList<Hand> handsOnTable; // A list of hands played on the table
  private int mainIdx = -1, currentIdx = -1, latestIdx = -1; // The idnex of the current player (-1 for unset)
  private int passCounter;	
  private CardGameTable table; // The table associated with this game
  private boolean endOfGame = false;
  //private BigTwoConsole bigTwoConsole;  // The BigTwoConsole object providing user interface


/**
 *  The constructor for creating a Big Two card game.
 *  Four players will be initialized and stored into
 *  an ArrayList. The table is also created.
 *  <br>
 */
    public BigTwo()
    {
       // Creating a new table and associate the instance of the BigTwo object
	   this.table = new BigTwoTable(this);
	   this.playerList = new ArrayList<CardGamePlayer>();
	   for(int i = 0;i < this.NumOfPlayers; ++i)
	   {
	     this.playerList.add(new CardGamePlayer());
	     // this.playerList.add(new SimpleAI(this));
	   }
    }

/**
 * The accessor for retreiving the number of players
 * <br>
 * @return the number of players
 */
@Override
public int getNumOfPlayers()
{
	return this.NumOfPlayers;
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
 * Get the index of the main player;
 * <br>
 *	@return an integer specifying the index of the main player
 */
public int getMainIdx()
{
    return this.mainIdx;
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
 * The method for starting the game with a (shuffled) deck provided in the argument.
 * The game logic is implemented in this method.
 *  <br>
 * @param deck A (shuffled) deck for starting the game
 */
@Override
public void start(Deck deck)
{
	// Creating four players and store them into the private ArrayList variable
   this.handsOnTable = new ArrayList<Hand>();
   this.endOfGame = false;
   this.mainIdx = 0;
   this.passCounter = 0;

    this.deck = deck;
    this.currentIdx = 0;

	// Allocate cards and sort cards
	for(int i = 0; i < 52; ++i)
	{
		this.playerList.get(i / 13).addCard(this.deck.getCard(i));
	}

	for(CardGamePlayer player : playerList)
	{
		player.sortCardsInHand();
	}

	// Set the first player
	this.latestIdx = this.currentIdx = 0;


	// Start playing
	this.table.repaint();
}

/**
 * This method is used for checking whether the game ends
 * <br>
 * @return a boolean value indicating whether the game ends
 */
@Override
public boolean endOfGame()
{
	return endOfGame;
}

/**
 * This method is used to check the move made by the current player
 * <br>
 * @param cardIdx
 *			the list of cards selected by the player
 */
@Override
public void checkMove(int[] cardIdx)
{
	// Attempt to compose a hand
	CardGamePlayer currentPlayer = this.playerList.get(this.currentIdx);
	CardList cards = currentPlayer.play(cardIdx);

	Hand hand = BigTwo.composeHand(currentPlayer, cards);

	// Get latest hand
	Hand latestHand = this.handsOnTable.size() == 0 ? null : this.handsOnTable.get(this.handsOnTable.size() - 1);

	// Check whether valid
	if((hand == null)
		|| (latestHand == null && hand.getType() == "Pass")
		|| (latestHand != null && !hand.beats(latestHand)))
	{
		this.table.printMsg(currentPlayer.getName() + ": Not a legal Move!");
		this.table.resetSelected();
		// Invalid hand
		return;
	}

	String message = String.format("%n<%s> {%s} ", currentPlayer.getName(), hand.getType());


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

	if(currentPlayer.getNumOfCards() == 0)
	{
		// Game end
		this.endOfGame = true;

		this.handsOnTable.clear();

		for(int i = 0; i < NumOfPlayers; ++i)
		{
			CardGamePlayer player = this.playerList.get(i);
			int numOfCards = player.getNumOfCards();
			if(numOfCards != 0)
			{
				this.table.printMsg(String.format("%n%s has %d cards in hand.", player.getName(), numOfCards));
			} else {
				this.table.printMsg(String.format("%n%s won the game.", player.getName()));
			}
		}
		table.repaint();
		table.disable();

		return;
	}
	
	this.currentIdx = (++this.currentIdx) % NumOfPlayers;

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
     // Create a BigTwo object
     BigTwo game = new BigTwo();

     // Create a new shuffled deck
     BigTwoDeck newDeck = new BigTwoDeck();

     // Start the game with the shuffled deck
     game.start(newDeck);
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
