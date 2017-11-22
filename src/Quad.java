import java.util.ArrayList;
import java.util.HashMap;
/**
 * This class is used to model a hand of Quad in Big Two Game.
 * It is a subclass of the class Hand. It stores the information of
 * the player who plays his / her hand. It provides concrete implementation of
 * two abstract methods in the superclass and overrides several methods for its use.
 * This class also provides a static method for checking whether a given hand
 * is a valid Quad.
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
 * @since 2016-10-09
 */
public class Quad extends Hand
{
	// Private variable storing the type of this hand in String
	private String type;


/**
 * This constructor construct a Quad object with specified player and list
 * of cards.
 *	<br>
 * @param player The player who plays his / her hand
 * @param cards the list cards that the player plays
 */
	public Quad(CardGamePlayer player, CardList cards)
	{
		super(player,cards);
		this.type = "Quad";

		// Storing the type(s) of hand FullHouse can beat
		this.beatList.add("Straight");
		this.beatList.add("Flush");
		this.beatList.add("FullHouse");

	}

/**
 * This method is used to check if this is a valid Quad.
 *	<br>
 * @return a boolean value specifying whether this hand is a valid Quad
 */
	public boolean isValid()
	{
		return Quad.isValid(this);
	}

/**
 * This method is used to check whether a given hand is a valid Quad hand.
 * This method is a static method.
 *	<br>
 * @param hand a given hand to be checked validity
 * @return the boolean value to specify whether the given hand is a valid
 * Quad in Big Two Game.
 */
	public static boolean isValid(CardList hand)
	{
		int numOfCards = hand.size();
		if(numOfCards == 5)
		{
			// Using the same method in the Full House, i.e.,
			// using a Hash Map to associate the number of occurrence of the
			// same rank to the particular rank.

			HashMap<Integer,Integer> cardsRank = new HashMap<Integer,Integer>();

			for(int i = 0; i < numOfCards; ++i)
			{
				Card card = hand.getCard(i);
				int suit = card.getSuit();
				int rank = card.getRank();
				if(!(-1 < suit && suit < 4 && -1 < rank && rank < 13))
				{
					return false;
				}

				// Add key or update the value
				if(!cardsRank.containsKey(rank))
				{
					cardsRank.put(rank,1);
				} else {
					cardsRank.replace(rank,cardsRank.get(rank) + 1);
				}
			}

			// The same logic as in the FullHouse
			// Only two different ranks and the products of their numbers of
			// occurrence should be four, thus given five cards in total, this is
			// sufficient and necessary to guarantee the hand is a valid Quad.
			if((cardsRank.size() == 2)
					&& (((int) cardsRank.values().toArray()[0] * (int) cardsRank.values().toArray()[1]) == 4))
			{
				return true;
			}

		}
		return false;
	}

/**
 * This method is used to retrieve the top card of this Quad.
 * <b> Important Note: This method assumes this hand is a valid Quad. </b>
 *	<br>
 * @return the top card of this hand
 */
	public Card getTopCard()
	{
		// Create two lists to hold cards according to rank
		CardList firstList = new CardList();
		CardList secondList = new CardList();

		// Get one rank for comparison
		int firstRank = this.getCard(0).getRank();

		// Divide the cards into two lists according to the rank obtained
		for(int i = 0; i < this.getNumOfCards(); ++i)
		{
			Card card = this.getCard(i);
			int rank = card.getRank();
			if(rank == firstRank)
			{
				firstList.addCard(card);
			} else {
				secondList.addCard(card);
			}
		}

		// Get the triplet in Full House
		CardList finalList = firstList.size() > secondList.size() ? firstList : secondList;

		// Get the top card in the triplet, which is the top card in this Full House
		finalList.sort();
		return finalList.getCard(0);

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
