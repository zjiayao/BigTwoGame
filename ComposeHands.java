import java.util.ArrayList;

/**
 * This class is used for generating possible hands
 * to beat the last hand.
 *
 * Note that the generated hands are not exhaustive for
 * pairs and tripes when there are (triples), and / or
 * four identical cards presented.
 *
 *	<br>
 *	<p>	<b>Copyright Information</b>
 *	<br> <br> <i> COMP 2396 OOP &amp; Java Assignment #4 </i>
 * 	<br> Version 0.1 2016-11-15
 *	<br> <i> ZHANG, Jiayao Johnson / 3035233412 </i>
 *	<br> Department of Computer Science, The University of Hong Kong
 *  <br> <a href = "jyzhang@cs.hku.hk"> Contact me via email </a>
 *  <br> <a href = "http://i.cs.hku.hk/~jyzhang/"> Visit my webpage </a>
 *
 *  <br> Version 0.1 at 2016-11-15
 * @author ZHANG, Jiayao Johnson
 * @version 0.1
 * @since 2016-11-05
 */
public class ComposeHands
{
	// Instance variables
	private final int SUIT = 4, RANK = 13;
	
	private CardGamePlayer player;
	private Hand latestHand;

	private ArrayList<Hand> playableHands, playableStraight, playableFlush;
	private ArrayList<Hand> formablePairs, formableTriples;
	private CardList cardList;
	
	private String type;

	/**
	 * Constructor of the class ComposeHand
	 * This constructor initialized the object with
	 * all the possible moves it can move.
	 * <br>
	 * @param player the current player
	 * @param latestHand the latest hand palyed for composing a hand against
	 */
	public ComposeHands(CardGamePlayer player, Hand latestHand)
	{
		this.playableHands = new ArrayList<>();
		this.formablePairs = new ArrayList<>();
		this.formableTriples = new ArrayList<>();
		this.playableStraight = new ArrayList<>();
		this.playableFlush = new ArrayList<>();

		this.player = player;
		this.cardList = player.getCardsInHand();
		this.latestHand = latestHand;

		if(latestHand != null)
		{
			playableHands.add(new Pass(player, null));
		} else {
			latestHand = new Pass(null, null);
		}
		this.type = latestHand.getType();
		int size = this.cardList.size();

		this.ComposeSingle();

		if(size > 2)
				this.ComposePair();
		if(size > 3)
				this.ComposeTriple();
		if(size > 5)
		{
			this.ComposeQuad();
			this.composeFlush();
			this.composeStraight();
			this.ComposeFullHouse();
			this.composeStraightFlush();
		}
	}

	/**
	 * This method returns an ArrayList of Hand
	 * that generated valid for the current player,
	 * under current condition.
	 * <br>
	 * @return an ArrayList of Hand consisting of
	 *				all possible moves under current state.
	 */
	public ArrayList<Hand> getPlayableHand()
	{
		return this.playableHands;
	}

	/**
	  * This method generates a piece of advice, i.e., a hint
	  * for the current user to suggest what can be played under
	  * current condition.
	  * <br>
	  * @return a String message consisting of the suggestion of waht
	  *				can be played under current condition.
	  */
	public String getAdvice()
	{
		String message = String.format("Exception: Unable to give advice%n");
		if(this.playableHands != null && this.playableHands.size() > 0)
			{
			message = String.format("Possible moves for %s%n", this.player.getName());
			for(Hand hand : this.playableHands)
			{
				message += String.format("{%s}: %s%n", hand.getType(), hand.toString());
			}
		}


		return message;
	}

	private void ComposeSingle()
	{
		for(int i = 0; i < this.cardList.size(); ++i)
		{
			
			CardList wrapCard = new CardList();

			Card card = formCard(this.cardList.getCard(i));	
			wrapCard.addCard(card); 

			Hand single = new Single(player,wrapCard);
			if(single.isValid() && single.beats(latestHand))
			{
				this.playableHands.add(single);
			}
		}
	}
	
	private void ComposePair()
	{
		for(int k = 1; k < 4; ++k)
		{

			for(int i = 0; i < this.cardList.size() - k; ++i)
			{
				CardList wrapCard = new CardList();
				Card card = formCard(this.cardList.getCard(i));
				wrapCard.addCard(card); 
				card = formCard(this.cardList.getCard(i + k));
				wrapCard.addCard(card);			
				Hand pair = new Pair(player,wrapCard);

				if(pair.isValid())
				{
					this.formablePairs.add(pair);
					if(pair.beats(latestHand))
					{ this.playableHands.add(pair); }
				}
			}
		}
	}

	private void ComposeTriple()
	{
		for(int i = 0; i < this.cardList.size() - 2; ++i)
		{
			CardList wrapCard = new CardList();

			Card card = formCard(this.cardList.getCard(i));
			wrapCard.addCard(card); 

			card = formCard(this.cardList.getCard(i + 1));
			wrapCard.addCard(card);

			card = formCard(this.cardList.getCard(i + 2));
			wrapCard.addCard(card);	

			Hand triple = new Triple(player,wrapCard);

			if(triple.isValid())
			{
				this.formableTriples.add(triple);
				if(triple.beats(latestHand))
				{ this.playableHands.add(triple); }
			}
		}
	}

	private void ComposeFullHouse()
	{
		if(this.formablePairs.size() > 0 && this.formableTriples.size() > 0)
		{
			for(Hand pair : formablePairs)
			{
				for(Hand triple : formableTriples)
				{
					CardList wrapCard = new CardList();
					for(int i = 0; i < pair.size(); ++i)
					{
						if(!wrapCard.contains(pair.getCard(i)))
						{ wrapCard.addCard(formCard(pair.getCard(i))); }
					}

					for(int i = 0; i < triple.size(); ++i)
					{
						if(!wrapCard.contains(triple.getCard(i)))
						{ wrapCard.addCard(formCard(triple.getCard(i))); }
					}

					Hand hand = new FullHouse(player,wrapCard);
					if(hand.isValid() && hand.beats(latestHand))
					{
						this.playableHands.add(hand);
					}
				}
			}
		}
	}

	private void ComposeQuad()
	{
		for(int i = 0; i < this.cardList.size() - 3; ++i)
		{
			CardList wrapCard = new CardList();

			Card card = formCard(this.cardList.getCard(i));
			int rank = card.getRank();
			wrapCard.addCard(formCard(card)); 

			card = formCard(this.cardList.getCard(i + 1));
			if(rank != card.getRank()) { continue; }
			wrapCard.addCard(formCard(card)); 

			card = formCard(this.cardList.getCard(i + 2));
			if(rank != card.getRank()) { continue; }
			wrapCard.addCard(formCard(card)); 

			card = formCard(this.cardList.getCard(i + 3));
			if(rank != card.getRank()) { continue; }
			wrapCard.addCard(formCard(card)); 

			for(int j = 0; j < this.cardList.size(); ++j)
			{
				if(!wrapCard.contains(this.cardList.getCard(j)))
				{ 
					wrapCard.addCard(formCard(this.cardList.getCard(j))); 
					Hand quad = new Quad(player,wrapCard);
					this.playableHands.add(quad);
					wrapCard.removeCard(this.cardList.getCard(j));
				}
			}


		}

	}

	private void composeStraight()
	{
		for(int i = 0; i < this.cardList.size() - 4; ++i)
		{
			CardList wrapCard = new CardList();

			Card card = formCard(this.cardList.getCard(i));
			wrapCard.addCard(card); 
			card = formCard(this.cardList.getCard(i + 1));
			wrapCard.addCard(card);	
			card = formCard(this.cardList.getCard(i + 2));
			wrapCard.addCard(card);			
			card = formCard(this.cardList.getCard(i + 3));
			wrapCard.addCard(card);
			card = formCard(this.cardList.getCard(i + 4));
			wrapCard.addCard(card);	
			Hand hand = new Straight(player,wrapCard);
			if(hand.isValid() && !StraightFlush.isValid(hand) && hand.beats(latestHand))
			{
				this.playableHands.add(hand);
				this.playableStraight.add(hand);
			}
		}
	}

	private void composeFlush()
	{
		for(int i = 0; i < SUIT; ++i)
		{
			CardList wrapCard = new CardList();
			for(int j = 0; j < this.cardList.size(); ++j)
			{
				if(this.cardList.getCard(j).getSuit() == i)
				{
					wrapCard.addCard(formCard(this.cardList.getCard(j)));
				}
			}
			
			Hand hand = new Flush(player, wrapCard);

			if(hand.isValid() && !StraightFlush.isValid(hand) && hand.beats(latestHand))
			{
				this.playableHands.add(hand);
				this.playableFlush.add(hand);
			}
		}

	}

	private void composeStraightFlush()
	{
		if(this.playableStraight.size() > 0 && this.playableFlush.size() > 0)
		for(Hand straight : this.playableStraight)
		{
			for(Hand flush : this.playableFlush)
			{
				if(!straight.beats(flush) && !flush.beats(straight))
				{
					CardList wrapCard = new CardList();
					for(int i = 0; i < flush.size(); ++i)
					{
						wrapCard.addCard(flush.getCard(i));
					}
					
					Hand hand = new StraightFlush(player, wrapCard);
					
					if(hand.isValid() && hand.beats(latestHand))
					{
						this.playableHands.add(hand);
					}
				}
			}
		}
	}

	private Card formCard(Card card)
	{
		return new BigTwoCard(card.getSuit(), card.getRank());
	}
}


