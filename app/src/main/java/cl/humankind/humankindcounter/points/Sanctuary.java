package cl.humankind.humankindcounter.points;

import java.util.HashMap;

import cl.humankind.humankindcounter.R;

public class Sanctuary {

    private int structurePoints;
    private int willPoints;
    private int index;
    private String name;
    private HashMap<String, Integer> colorMap;
    private HashMap<String, Integer> backgroundMap;
    private Integer color;
    private Integer background;

    private Sanctuary(int index, String name, int structure, int will){
        this.index = index;
        this.name = name;
        structurePoints = structure;
        willPoints = will;
        createColorMap();
        createBackgroundMap();
    }

    public Sanctuary(Sanctuary aSanctuary){
        this(aSanctuary.index, aSanctuary.name, aSanctuary.structurePoints, aSanctuary.willPoints);
        color = aSanctuary.color;
        background = aSanctuary.background;
    }

    public Sanctuary(int index, String name, int structure, int will, String faction){
        this(index, name, structure, will);
        color = colorMap.get(faction);
        background = backgroundMap.get(faction);
    }

    public Sanctuary(int index, String name, int structure, int will, int faction)
            throws IndexOutOfBoundsException{
        this(index, name, structure, will);
        if (faction > 5 || faction < 0){
            throw new IndexOutOfBoundsException("No faction associates with integer provided");
        }
        String[] factions = new String[]{"chimera", "abysmal",
                "corpo", "acracia", "none"};
        color = colorMap.get(factions[faction]);
        background = backgroundMap.get(factions[faction]);
    }

    private void createColorMap(){
        colorMap = new HashMap<>();
        colorMap.put("chimera", R.drawable.color_blue);
        colorMap.put("abysmal", R.drawable.color_green);
        colorMap.put("corpo", R.drawable.color_red);
        colorMap.put("acracia", R.drawable.color_yellow);
        colorMap.put("none", R.drawable.color_white);
        colorMap.put("chimera/acracia", R.drawable.color_blueyellow);
    }

    private void createBackgroundMap(){
        backgroundMap = new HashMap<>();
        backgroundMap.put("chimera", R.drawable.back_chimera);
        backgroundMap.put("abysmal", R.drawable.back_abysmal);
        backgroundMap.put("corpo", R.drawable.back_corpo);
        backgroundMap.put("acracia", R.drawable.back_acracia);
        backgroundMap.put("none", R.color.colorBack);
        backgroundMap.put("chimera/acracia", R.drawable.back_special);
    }

    public int getIndex(){
        return index;
    }

    public String getName(){
        return name;
    }

    public int getStructurePoints(){
        return structurePoints;
    }

    public int getWillPoints(){
        return willPoints;
    }

    public Integer getColor() {
        return color;
    }

    public Integer getBackground(){
        return background;
    }

}
