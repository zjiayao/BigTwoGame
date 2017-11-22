import java.util.ArrayList;
import java.util.HashMap;
/**
 * This class is used to model a hand of Full House in Big Two Game.
 * It is a subclass of the class Hand. It stores the information of
 * the player who plays his / her hand. It provides concrete implementations of
 * two abstract methods in the superclass and overrides several methods for its use.
 * This class also provides a static method for checking whether a given hand is
 * a Full House.
 *	<br>
 *  <p>
 * A Full House is five Big Two Cards with three of the same rank and another
 * two of the same rank.
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
public class FullHouse extends Hand
{
	// Private variable storing the type of this hand in String
	private String type;


/**
 * This constructor construct a FullHouse object with specified player and list
 * of cards.
 *	<br>
 * @param player The player who plays his / her hand
 * @param cards the list cards that the player plays
 */
	public FullHouse(CardGamePlayer player, CardList cards)
	{
		// Call super constructor first
		super(player,cards);
		this.type = "FullHouse";

		// Storing the type(s) of hand FullHouse can beat
		// A Full House can beat, apart from a Pass,
		// any Straight and any Flush.
		this.beatList.add("Straight");
		this.beatList.add("Flush");


	}

/**
 * This method is used to check if this is a valid FullHouse.
 *	<br>
 * @return a boolean value specifying whether this hand is a valid FullHouse
 */
		public boolean isValid()
		{
			return FullHouse.isValid(this);
		}

/**
 * This method is used to check whether a given hand is a valid FullHouse hand.
 * This method is a static method.
 *	<br>
 * @param hand a given hand to be checked validity
 * @return the boolean value to specify whether the given hand is a valid
 * FullHouse in Big Two Game.
 */
	public static boolean isValid(CardList hand)
	{
		int numOfCards = hand.size();

		// First check the number of cards
		if(numOfCards == 5)
		{
			// Using a HashMap to associate the number of occurrence of a
			// particular suit to this particular suit
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

				// Add or update the occurrence of the rank
				if(!cardsRank.containsKey(rank))
				{
					cardsRank.put(rank,1);
				} else {
					cardsRank.replace(rank,cardsRank.get(rank) + 1);
				}
			}

			// A Full House has two different ranks and the occurrence of the ranks
			// is either (2,3) or (3,2). Thus conditioning whether the product of
			// the number of occurrence is six to determine whether this is a
			// valid Full House given the total number of cards is five.
			if((cardsRank.size() == 2)
					&& (((int) cardsRank.values().toArray()[0] * (int) cardsRank.values().toArray()[1]) == 6))
			{
				return true;
			}

		}
		return false;
	}

/**
 * This method is used to retrieve the top card of this Full House.
 * <b> Important Note: This method assumes this hand is a valid Full House. </b>
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
