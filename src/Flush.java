import java.util.ArrayList;
/**
 * This class is used to model a hand of Flush in Big Two Game.
 * It is a subclass of the class Hand. It stores the information of
 * the player who plays his / her hand. It provides the concrete implementation
 * of two abstract methods in the superclass and overrides several methods for its use.
 * This class also provides a static method for checking whether a given hand
 * is a valid Flush.
 * <br>
 * <p>
 * A Flush is five Big Two Cards from the same suit.
 *  <br> <b> Important Note: A Straight Flush is also considered as a Flush </b>
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
 * @since 2016-10-09
 */
public class Flush extends Hand
{
	// Private variable storing the type of this hand in String
	private String type;


/**
 * This constructor construct a Flush object with specified player and list
 * of cards.
 *	<br>
 * @param player The player who plays his / her hand
 * @param cards the list cards that the player plays
 */
	public Flush(CardGamePlayer player, CardList cards)
	{
		super(player,cards);
		this.type = "Flush";

		// Storing the type(s) of hand Flush can beat
		// A Flush can beat a Pass and any Straight.
		this.beatList.add("Straight");

	}

/**
 * This method is used to check if this is a valid Flush.
 *	<br>
 * @return a boolean value specifying whether this hand is a valid Flush
 */
		public boolean isValid()
		{
			return Flush.isValid(this);
		}

/**
 * This method is used to check whether a given hand is a valid Flush hand.
 * This method is a static method.
 *	<br>
 * @param hand a hand given to be checked validity
 * @return the boolean value to specify whether the given hand is a valid
 * Flush in Big Two Game.
 */
	public static boolean isValid(CardList hand)
	{
		int numOfCards = hand.size();
		if(numOfCards == 5)
		{
			int firstSuit = hand.getCard(0).getSuit();

			// Looping through all cards
			for(int i = 0; i < numOfCards; ++i)
			{
				Card card = hand.getCard(i);
				int suit = card.getSuit();
				int rank = card.getRank();

				// Whether a valid Card
				if(!(-1 < suit && suit < 4 && -1 < rank && rank < 13 && suit == firstSuit))
				{
					return false;
				}
			}

			return true;

		}

		return false;
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
	@Override
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
			return (this.getTopCard().getSuit() - hand.getTopCard().getSuit() > 0) ? true : false;
		} else if (this.beatList.contains(hand.getType())) {
			// Otherwise the beatList should be considered.
			return true;
		}

		return false;

	}
/**
 * This method is used to return the type of this hand in
 * String representation.
 * @return the String specification of the type of this hand
 */
	public String getType()
	{
		return this.type;
	}

}
