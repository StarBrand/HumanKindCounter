package cl.humankind.humankindcounter.cards;

public class CardPair {

    private int display;
    private int cardImage;

    CardPair(int display, int cardImage){
        this.display = display;
        this.cardImage = cardImage;
    }

    public int getDisplay(){
        return display;
    }

    public int getCardImage(){
        return cardImage;
    }

}
