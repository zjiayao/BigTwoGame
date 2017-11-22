import java.util.ArrayList;
/**
 * This class is used to model an empty hand of Single in Big Two Game.
 * It is a subclass of the class Hand. It stores the information of
 * the player who plays his / her hand.
 * This class also provides a static method to check whether a given hand
 * is a valid hand.
 * A hand is a valid Pass if and only if it is empty and it can be played.
 * This class stores the information of the number of the consecutive Passes
 * that has been constructed. If this number is greater than three, a Pass shall
 * not be constructed according to the Big Two Game Rule.
 *	<br>
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
public class Pass extends Hand
{
	// Private variable storing the type of this hand in String
	private String type;


/**
 * This constructor construct a Pass object with specified player and list
 * of cards.
 *	<br>
 * @param player The player who plays his / her hand
 * @param cards the list cards that the player plays
 */
	public Pass(CardGamePlayer player, CardList cards)
	{
		super(player,cards);
		this.type = "Pass";
		this.beatList.add("Single");
		this.beatList.add("Pair");
		this.beatList.add("Triple");
		this.beatList.add("Straight");
		this.beatList.add("Flush");
		this.beatList.add("Quad");
		this.beatList.add("FullHouse");
		this.beatList.add("StraightFlush");

	}

/**
 * This method is used to check if this is a valid Pass.
 *	<br>
 * @return a boolean value specifying whether this hand is a valid Pass
 */
	public boolean isValid()
	{
		return Pass.isValid(this);
	}

/*
 * This method is used to check whether a given hand is a valid Pass hand.
 * This method is a static method
 * An empty hand is considered as an invalid Pass iff all other players have been
 * used passed before the current player.
 *	<br>
 * @param hand the hand given to be checked validity
 * @return the boolean value to specify whether the given hand is a Pass Single
 * Single in Big Two Game.
 */
	public static boolean isValid(CardList hand)
	{
		if(hand == null || hand.size() == 0)
		{
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

/**
 * This method is used for checking whether this hand beats a specified hand.
 * Since this hand is an empty hand, i.e., pass, this method returns true
 * for any hands valid in Big Two Game and false otherwise.
 *	<br>
 * @param hand a specified hand used to be compared by this hand
 */
	public boolean beats(Hand hand)
	{
		return true;
	}
}
