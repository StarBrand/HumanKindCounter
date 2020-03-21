package cl.humankind.humankindcounter.points;

public class GameStatus{

    private CounterPoints structure;
    private CounterPoints will;
    private boolean onGame;

    public GameStatus(){
        onGame = true;
        structure = new CounterPoints(12, 1);
        will = new CounterPoints(4, 0);
    }

    public boolean getStatus(){
        return onGame;
    }

    public String getStructurePoints(){
        return structure.getPoints();
    }

    public String getWillPoints(){
        return will.getPoints();
    }

    public void addStructure(int points){
        structure.addPoints(points);
    }

    public void delStructure(int points){
        try {
            structure.delPoints(points);
        } catch (Exception e){
            onGame = false;
        }
    }

    public void addWill(int points){
        will.addPoints(points);
    }

    public void delWill(int points) throws Exception{
        try {
            will.delPoints(points);
        } catch (Exception e){
            throw new Exception("no more will");
        }
    }

}
