package cl.humankind.humankindcounter.points;

public class MainSanctuary extends Sanctuary {

    public MainSanctuary(Sanctuary aSanctuary){
        super(aSanctuary);
    }

    public MainSanctuary(int structure, int will, String faction){
        super(-1, "main", structure, will, faction);
    }

    public MainSanctuary(int structure, int will, int faction){
        super(-1, "main", structure, will, faction);
    }

}
