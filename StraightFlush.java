import java.util.ArrayList;
import java.util.Comparator;
/**
 * This class is used to model a hand of Straight Flush in Big Two Game.
 * It is a subclass of the class Hand. It stores the information of
 * the player who plays his / her hand. It provides concrete implementations of
 * two abstract methods in the suerclass and overrides several methods for its use.
 * This class also provides a static method to check whether a given hand is
 * a valid Straight Flush.
 *  <br>
 *  <br> <b> Important Note: A Straight Flush is also considered as a Straight
 *           or a Flush </b>
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
public class StraightFlush extends Hand
{
	// Private variable storing the type of this hand in String
	private String type;

/**
 * This constructor construct a Straight object with specified player and list
 * of cards.
 *	<br>
 * @param player the player who plays his / her hand
 * @param cards the list cards that the player plays
 */
	public StraightFlush(CardGamePlayer player, CardList cards)
	{
		super(player,cards);
		this.type = "StraightFlush";

		// Storing the type(s) of hand StraightFlush can beat, which are all
		// other five-cards types.
		this.beatList.add("Straight");
		this.beatList.add("Flush");
		this.beatList.add("FullHouse");
		this.beatList.add("Quad");

	}

/**
 * This method is used to check if this is a valid StraightFlush.
 *	<br>
 * @return a boolean value specifying whether this hand is a valid StraightFlush
 */
	public boolean isValid()
	{
		return StraightFlush.isValid(this);
	}

/**
 * This method is used to check whether a given is a valid Straight Flush hand.
 * This method is a static method.
 *	<br>
 * @param hand the hand given to be checked validity
 * @return the boolean value to specify whether the given hand is a StraightFlush
 * in Big Two Game.
 */
	public static boolean isValid(CardList hand)
	{
		int numOfCards = hand.size();
		if(numOfCards == 5)
		{
			ArrayList<Integer> cardsRank = new ArrayList<Integer>(numOfCards);
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

				if(!cardsRank.contains(rank))
				{
					cardsRank.add(rank);
				}
			}

			// Sort the ranks in the order: '3', '4', ..., 'K', 'A', '2'
			// In this way '2' and 'A' can only form a straight with 'K'
			// but not with '3'
			cardsRank.sort(new Comparator<Integer>(){
				public int compare(Integer first, Integer second)
				{
					return (first + 11)%13 - (second + 11)%13;
				}
			});

			// Whether five consecutive cards
			if(cardsRank.size() == 5)
			{
				if ((cardsRank.get(numOfCards - 1) + 11)%13 - (cardsRank.get(0) + 11)%13 == numOfCards - 1)
				{
					return true;
				}
			}
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
