import java.util.ArrayList;
import java.util.List;

public class Wall {
    private int  y;
    private int x;
    private final int size;
    private final boolean horizontal;
    private ArrayList<Position> wallArray = new ArrayList<>();


    public Wall(int y, int x, int size, boolean horizontal) {
        this.y = y;
        this.x = x;
        this.size = size;
        this.horizontal = horizontal;
    }

    public ArrayList<Position> addVerticleWall(int size){
        for(int i = 0;i<size;i++){
            wallArray.add(new Position(x, y +i));
        }
        return wallArray;
    }
    public ArrayList<Position> addHorizontalWall(int size){
        for(int i = 0;i<size;i++){
            wallArray.add(new Position(x +i, y));
        }
        return wallArray;
    }
    public List<Position> createWall() {
        if (this.horizontal)
            return addHorizontalWall(size);
        else
            return addVerticleWall(size);
    }
}
