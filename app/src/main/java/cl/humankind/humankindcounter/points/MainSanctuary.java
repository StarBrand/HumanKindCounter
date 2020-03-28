package cl.humankind.humankindcounter.points;

public class MainSanctuary extends Sanctuary {

    public MainSanctuary(Sanctuary aSanctuary){
        super(aSanctuary);
    }

    public MainSanctuary(int structure, int will, String faction){
        super("main", structure, will, faction);
    }

    public void setStructurePoints(int structure){
        super.setStructurePoints(structure);
    }

    public void setWillPoints(int will){
        super.setWillPoints(will);
    }

    public void setFaction(String faction){
        super.setFaction(faction);
    }

}
