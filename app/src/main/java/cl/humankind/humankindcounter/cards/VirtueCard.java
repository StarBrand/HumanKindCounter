package cl.humankind.humankindcounter.cards;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class VirtueCard {

    private List<Object> available = new ArrayList<>();

    public VirtueCard(){
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
            available.remove(die);
            return chosen;
        }
    }

    /**
     * Regenerate cards
     */
    public abstract void shuffleCards();

    /**
     * Add parameter to available values of card
     *
     * @param object: object to insert
     */
    void addAvailable(Object object){
        available.add(object);
    }

}
