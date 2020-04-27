package cl.humankind.humankindcounter.cards;

import cl.humankind.humankindcounter.R;

/**
 * Numerical Virtue cards model
 *
 * @author Juan Saez Hidalgo
 */
public class NumericalVirtue extends VirtueCard{

    /**
     * Regenerate cards with integers
     */
    public void shuffleCards(){
        super.shuffleCards();
        addAvailable(R.drawable.minus_two, R.drawable.card_minus_two);
        addAvailable(R.drawable.minus_one, R.drawable.card_minus_one);
        addAvailable(R.drawable.zero, R.drawable.card_zero);
        addAvailable(R.drawable.plus_one, R.drawable.card_plus_one);
        addAvailable(R.drawable.plus_two, R.drawable.card_plus_two);
    }

}
