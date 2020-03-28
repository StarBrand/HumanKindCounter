package cl.humankind.humankindcounter.cards;

import cl.humankind.humankindcounter.R;

public class FactionVirtue extends VirtueCard {

    @Override
    public void shuffleCards() {
        super.shuffleCards();
        addAvailable(R.id.chimera, R.drawable.card_chimera);
        addAvailable(R.id.abysmal, R.drawable.card_abysmal);
        addAvailable(R.id.corpo, R.drawable.card_corpo);
        addAvailable(R.id.acracia, R.drawable.card_acracia);
        addAvailable(R.id.none, R.drawable.card_blank);
    }
}
