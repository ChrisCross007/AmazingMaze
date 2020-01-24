import java.util.ArrayList;
import java.util.List;

public class Walls {
    private List<Wall> walls = new ArrayList<>();

    public Walls() {
        addWall(new Wall(0,0, 51, true)); //side wall
        addWall(new Wall(0,0, 49, false)); //top wall
        addWall(new Wall(1,50, 49, false));//side wall
        addWall(new Wall(23 ,1, 47, true)); //top wall

        addWall(new Wall(1,8, 10, false));
        addWall(new Wall(1,15, 3, false));
        addWall(new Wall(1,30, 7, false));
        addWall(new Wall(10,9, 3, true));
        addWall(new Wall(8,18, 13, true));
        addWall(new Wall(8,17, 5, false));
        addWall(new Wall(10,17, 4, true));
        addWall(new Wall(13,21, 9, false));
        addWall(new Wall(3,4, 10, false));
        addWall(new Wall(15,2, 10, true));
        addWall(new Wall(15,5, 7, false));
        addWall(new Wall(12,12, 6, false));
        addWall(new Wall(18,5, 5, true));
        addWall(new Wall(2,23, 5, false));
        addWall(new Wall(5,13, 8, true));
        addWall(new Wall(15,17, 8, false));
        addWall(new Wall(20,18, 1, true));
        addWall(new Wall(20,23, 6, true));
        addWall(new Wall(22,23, 5, false));
        addWall(new Wall(10,30, 10, false));
        addWall(new Wall(10,25, 8, false));
        addWall(new Wall(15,26, 4, true));
        addWall(new Wall(15,35, 8, true));
        addWall(new Wall(4,35, 8, false));
        addWall(new Wall(5,36, 10, true));
        addWall(new Wall(10,40, 10, false));
        addWall(new Wall(21,42, 8, true));
        addWall(new Wall(17,38, 6, false));
        addWall(new Wall(9,33, 14, true));
        addWall(new Wall(17,45, 5, true));
        addWall(new Wall(12,40, 10, true));
        addWall(new Wall(16,35, 6, false));
        addWall(new Wall(12,30, 3, true));
    }

    public void addWall (Wall wall){
        walls.add(wall);
    }

    public List<Wall> getWalls() {
        return walls;
    }
}
