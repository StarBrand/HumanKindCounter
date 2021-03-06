package cl.humankind.humankindcounter.points;

import java.util.LinkedList;
import java.util.Queue;

public class SanctuaryCache {

    private Queue<Sanctuary> sanctuaries;

    public SanctuaryCache(){
        sanctuaries = new LinkedList<>();
    }

    public void addSanctuary(int index, String name, int structure, int will, String faction){
        sanctuaries.add(new Sanctuary(index, name, structure, will, faction));
    }

    public Sanctuary getSanctuary(){
        return sanctuaries.poll();
    }

    public boolean cacheEmpty(){
        return sanctuaries.isEmpty();
    }

}
