import java.util.ArrayList;
/**
 * This class is used to model the a deck of cards used in a Big Two card
 * game. This class is a subclass of the Deck class. It holds the Information
 * of a Big Two card and overrides the initialize() method in order to create
 * a deck of Big Two cards.
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
public class BigTwoDeck extends Deck
{

/**
 * This method if for initializing a deck of Big Two cards. It will remove
 * all cards from the deck, create 52 Big two cards and add them to the deck.
 *	<br>
 */
	public void initialize()
	{
		// Remove current deck
		this.removeAllCards();

		// Fill in the current deck
		for(int i = 0; i < 4; ++i)
		{
			for(int j = 0; j < 13; ++j)
			{
				BigTwoCard card = new BigTwoCard(i,j);
				this.addCard(card);
			}
		}

		// Shuffle the current deck
		this.shuffle();
	}

}
