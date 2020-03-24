package cl.humankind.humankindcounter.cards;

public class FactionVirtue extends VirtueCard {

    @Override
    public void shuffleCards() {
        super.shuffleCards();
        for(String faction: new String[]{"qm", "ab", "co", "ac", "none"}){
            addAvailable(faction);
        }
    }
}
