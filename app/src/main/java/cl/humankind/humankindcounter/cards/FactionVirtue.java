package cl.humankind.humankindcounter.cards;

import cl.humankind.humankindcounter.R;

public class FactionVirtue extends VirtueCard {

    @Override
    public void shuffleCards() {
        super.shuffleCards();
        addAvailable(R.drawable.chimera, R.drawable.card_chimera);
        addAvailable(R.drawable.abysmal, R.drawable.card_abysmal);
        addAvailable(R.drawable.corpo, R.drawable.card_corpo);
        addAvailable(R.drawable.acracia, R.drawable.card_acracia);
        addAvailable(R.drawable.blank, R.drawable.card_blank);
    }
}
