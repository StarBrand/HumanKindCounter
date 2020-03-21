package cl.humankind.humankindcounter.points;

public class CounterPoints {

    private int points;
    private int min_points;

    CounterPoints(int init_points, int min_points){
        points = init_points;
        this.min_points = min_points;
    }

    boolean isInvalid(){
        return points < min_points;
    }

    void addPoints(int plus){
        points += plus;
    }

    void delPoints(int minus) throws Exception{
        points -= minus;
        if(isInvalid()){
            points = 0;
            throw new Exception("explode");
        }
    }

    String getPoints(){
        return String.valueOf(points);
    }

}
