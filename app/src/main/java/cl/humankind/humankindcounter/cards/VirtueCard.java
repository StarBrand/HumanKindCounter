package cl.humankind.humankindcounter.cards;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class VirtueCard {

    private List<Integer> available;
    private List<Integer> display;

    VirtueCard(){
        available = new ArrayList<>();
        display = new ArrayList<>();
        shuffleCards();
    }

    /**
     * Get next virtue and delete pass ones
     *
     * @return Value of display id and card image drawable
     */
    public CardPair nextVirtue(){
        if (available.size() == 0) {
            throw new IndexOutOfBoundsException();
        } else {
            Random rand = new Random();
            int die = rand.nextInt(available.size());
            Integer to_display = display.get(die);
            Integer chosen = available.get(die);
            display.remove(to_display);
            available.remove(chosen);
            return new CardPair(to_display, chosen);
        }
    }

    /**
     * Regenerate cards
     */
    public void shuffleCards(){
        available.clear();
        display.clear();
    };

    /**
     * Add parameter to available values of card
     *
     * @param mini_view: Id of mini view on display
     * @param drawable: Drawable object to put on card
     */
    void addAvailable(int mini_view, int drawable){
        available.add(drawable);
        display.add(mini_view);
    }

}
