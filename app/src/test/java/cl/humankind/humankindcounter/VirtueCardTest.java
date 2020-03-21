package cl.humankind.humankindcounter;

import org.junit.Before;
import org.junit.Test;
import cl.humankind.humankindcounter.cards.FactionVirtue;
import cl.humankind.humankindcounter.cards.NumericalVirtue;
import cl.humankind.humankindcounter.cards.VirtueCard;

import static org.junit.Assert.*;

/**
 * Unit Test of Virtue Card logic.
 *
 * @see cl.humankind.humankindcounter.cards.VirtueCard
 */
public class VirtueCardTest {

    private VirtueCard numerical;
    private VirtueCard faction;
    private VirtueCard initializedNumerical;
    private VirtueCard initializedFaction;

    @Before
    public void setUp(){
        numerical = new NumericalVirtue();
        faction = new FactionVirtue();
        initializedNumerical = new NumericalVirtue();
        initializedNumerical.shuffleCards();
        initializedFaction = new FactionVirtue();
        initializedFaction.shuffleCards();
    }

}
