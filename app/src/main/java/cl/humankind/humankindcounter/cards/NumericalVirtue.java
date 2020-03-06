package cl.humankind.humankindcounter.cards;

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
        for (int i = -2; i <= +2; i++){
            addAvailable(i);
        }
    }

}
