import java.util.ArrayList;
/**
 * This class is used to model a hand of cards. It is a subclass of the
 * class CardList. It stores the information of the player who plays his / her
 * hand. It also includes the methods for accessing the player of his / her
 * hand, checking whether if it beats a specified hand.
 * This class also provides a protocol of static method used to be overridden by
 * its subclasses to determine whether any  arbitrary given hand is a valid hand
 * of certain type.
 *	<br>
 *
 *
 *	<br>
 *	<p>	<b>Copyright Information</b>
 *	<br> <br> <i> COMP 2396 OOP &amp; Java Assignment #3 </i>
 *	<br> <i> ZHANG, Jiayao Johnson / 3035233412 </i>
 *	<br> Department of Computer Science, The University of Hong Kong
 *  <br> <a href = "jyzhang@cs.hku.hk"> Contact me via email </a>
 *  <br> <a href = "http://i.cs.hku.hk/~jyzhang/"> Visit my webpage </a>
 *
 * @author ZHANG, Jiayao Johnson
 * @version 0.1
 * @since 2016-10-08
 */
 public abstract class Hand extends CardList
 {
	 // Private instance variable holding the information of the player
	 private CardGamePlayer player;
	 private int numOfCards;

	 // Storing the type(s) of hand this hand can beat
	 // Since this list should be updated by the subclasses accordingly,
	 // it has an access level of protected
	 protected ArrayList<String> beatList;

/**
 * This constructor provides a protocol for constructing objects from
 * the subclasses of the class Han, with specified player and list.
 * of cards.
 *	<br>
 * @param player The player who plays his / her hand
 * @param cards the list cards that the player plays
 */
 	public Hand(CardGamePlayer player, CardList cards)
	{
		this.player = player;

		// Any hand can beat a "Pass" hand, including the "Pass"
		// hand itself
		this.beatList = new ArrayList<String>();
		this.beatList.add("Pass");

		// If the hand is empty, the cards is null
		if(cards != null)
		{
			this.numOfCards = cards.size();
			for(int i = 0; i < cards.size(); ++i)
			{
				this.addCard(cards.getCard(i));
			}
		} else {
			this.numOfCards = 0;
		}

	}

/**
 * This method is used to retreive the player of this hand.
 *	<br>
 * @return the player of this hand
 */
	public CardGamePlayer getPlayer()
	{
		return this.player;
	}

/**
 * This method is used to retrieve the top card of this hand.
 *	<br>
 * @return the top card of this hand
 */
	public Card getTopCard()
	{
		this.sort();
		return this.getCard(0);
	}

/**
 * This method is used to retrieve the number of cards of this hand.
 *	<br>
 * @return the number of cards of this hand
 */
	public int getNumOfCards()
	{
		return this.numOfCards;
	}

/**
 * This method is used for checking whether this hand beats a specified hand.
 * This method firstly checks for the match of number of cards between two hands,
 * if unmatch, returns false. Then this method returns true if the top card, as
 * obtained from the getTopCard() method, in this hand is strictly greater than
 * that in the other hand, and returns false otherwise. Finally, this hand will
 * consider whther the hand given, though of a different type can also be beaten
 * by the current hand.
 *	<br>
 * @param hand a specified hand used to be compared by this hand
 * @return the boolean value specifying whether the given hand can be beaten
 * 			by this hand
 */
	public boolean beats(Hand hand)
	{

		// Handle the edge case
		if(hand == null)
		{
			return true;
		}

		// Only the hand with the same number of cards may have
		// a chance to beat another
		if(this.getNumOfCards() != hand.getNumOfCards())
		{
			return false;
		} else if (this.getType() == hand.getType()) {
			// The comparison is meaningful only if the hands are of the same type.
			return (this.getTopCard().compareTo(hand.getTopCard()) > 0) ? true : false;
		} else if (this.beatList.contains(hand.getType())) {
			// Otherwise the beatList should be considered.
			return true;
		}

		return false;

	}

/**
 * This method is used to check if a given hand is a valid hand.
 * This is a static method. Since this method is designed to be overridden
 * by its subclasses, it always returns false.
 *	<br>
 * @param hand the hand given to be checked validity
 * @return false
 */
	public static boolean isValid(CardList hand){
		return false;
	}

/**
 * This method is used to check if this is a valid hand.
 * This is an abstract method designed to be overridden by the subclasses
 * of the Hand class, if applicable.
 *	<br>
 * @return a boolean value specifying whether this hand is a valid hand
 */
	public abstract boolean isValid();

/**
 * This method is used to return a string specifying the type of this hand.
 * This is an abstract method designed to be overridden by the subclasses
 * of the Hand class, if applicable.
 *	<br>
 * @return the string specifying the type of this hand
 */
	public abstract String getType();
 }
