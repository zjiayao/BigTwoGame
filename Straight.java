import java.util.ArrayList;
import java.util.Comparator;
/**
 * This class is used to model a hand of Straight in Big Two Game.
 * It is a subclass of the class Hand. It stores the information of
 * the player who plays his / her hand. It provides concrete implementations of
 * two abstract methods in the superclass and overrides several methods for its use.
 * This class also provides a static method to check whether a given hand is a
 * valid Straight.
 *	<br>
 *  <br> <b> Important Note: A Straight Flush is also considered as a Straight </b>
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
public class Straight extends Hand
{
	// Private variable storing the type of this hand in String
	private String type;

/**
 * This constructor construct a Straight object with specified player and list
 * of cards.
 *	<br>
 * @param player The player who plays his / her hand
 * @param cards the list cards that the player plays
 */
	public Straight(CardGamePlayer player, CardList cards)
	{
		super(player,cards);
		this.type = "Straight";
	}

/**
 * This method is used to check if this is a valid Straight.
 *	<br>
 * @return a boolean value specifying whether this is a valid Straight
 */
	public boolean isValid()
	{
		return Straight.isValid(this);
	}

/**
 * This method is used to check whether a given hand is a valid Straight hand.
 * This method is a static method.
 *	<br>
 * @param hand a given hand to be checked validity
 * @return the boolean value to specify whether the given hand is a valid
 * Straight in Big Two Game.
 */
	public static boolean isValid(CardList hand)
	{
		int numOfCards = hand.size();
		if(numOfCards == 5)
		{
			ArrayList<Integer> cardsRank = new ArrayList<Integer>(numOfCards);

			// Looping through all cards
			for(int i = 0; i < numOfCards; ++i)
			{
				Card card = hand.getCard(i);
				int suit = card.getSuit();
				int rank = card.getRank();

				// Whether a valid Card
				if(!(-1 < suit && suit < 4 && -1 < rank && rank < 13))
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
			// but not with '3' as stated in the Big Two Game Rules
			cardsRank.sort(new Comparator<Integer>(){
				public int compare(Integer first, Integer second)
				{
					return (first + 11)%13 - (second + 11)%13;
				}
			});

			// Whether five consecutive cards
			if(cardsRank.size() == 5)
			{
				// The biggest card and the smallest card, when sorted by
				// Big Two Card method, should have a differece of 5 - 1 = 4.
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
