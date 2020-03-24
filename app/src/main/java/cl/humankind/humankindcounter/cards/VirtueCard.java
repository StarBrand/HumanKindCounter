package cl.humankind.humankindcounter.cards;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class VirtueCard {

    private List<Object> available = new ArrayList<>();

    VirtueCard(){
        shuffleCards();
    }

    /**
     * Get next virtue and delete pass ones
     *
     * @return Value at random
     */
    public Object nextVirtue(){
        if (available.size() == 0) {
            throw new IndexOutOfBoundsException();
        } else {
            Random rand = new Random();
            int die = rand.nextInt(available.size());
            Object chosen = available.get(die);
            available.remove(chosen);
            return chosen;
        }
    }

    /**
     * Regenerate cards
     */
    public void shuffleCards(){
        available.clear();
    };

    /**
     * Add parameter to available values of card
     *
     * @param object: object to insert
     */
    void addAvailable(Object object){
        available.add(object);
    }

}
