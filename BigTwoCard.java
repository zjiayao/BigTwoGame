/**
 * This class is used to model the a card used in a Big Two card
 * game. This class is a subclass of the Card class. It holds the Information
 * of a Big Two card and overrides the compareTo() method in order to return
 * the ordering of the cards used in the Big Two game.
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
 public class BigTwoCard extends Card
 {
/**
 * The constructor of the BigTwoCards class, constructing
 * a Big Two card with specified suit and rank.
 *	<br>
 * @param suit
 *				an integer between 0 to 3 representing the suit of the card
 *				<p>
 *				0 = Diamond, 1 = Club, 2 = Heart, 3 = Spade
 * @param rank
 *				an integer between 0 to 12 representing the rank of the card
 *				0 = 'A', 1 = '2', 2 = '3', ..., 11 = 'Q', 12 = 'K'
 */
 	public BigTwoCard(int suit, int rank)
	{
		// Call the constructor from the superclass as the parameters are the same
		super(suit,rank);
	}

/**
 * The method for comparing this Big Two card with another Big Two card using the
 * rules specified in the Big Two game. This method returns a negetive, zero, or a
 * positive integer to present this card is less than, equal to or greater than
 * the card given.
 *	<br>
 * @param card Another card given to be compared with this card.
 * @return A negative integer, zero or a positive integer as this card is less
 * than, equal to or greater than the given card
 */
 	public int compareTo(Card card)
	{
		int thisRank = this.getRank(), cardRank = card.getRank();
		if(thisRank == cardRank)
		{
			return this.getSuit() - card.getSuit();

		} else {
			return (thisRank + 11)%13 - (cardRank + 11)%13;
		}
	}

 }
