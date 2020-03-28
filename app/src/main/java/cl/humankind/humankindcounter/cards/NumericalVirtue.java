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
        addAvailable(R.id.minus_two, R.drawable.card_minus_two);
        addAvailable(R.id.minus_one, R.drawable.card_minus_one);
        addAvailable(R.id.zero, R.drawable.card_zero);
        addAvailable(R.id.plus_one, R.drawable.card_plus_one);
        addAvailable(R.id.plus_two, R.drawable.card_plus_two);
    }

}
