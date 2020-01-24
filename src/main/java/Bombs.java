import java.util.ArrayList;
import java.util.List;

public class Bombs {
    private List<Bomb> bombs = new ArrayList<>();


    public Bombs(int numberOfBombs) {
        addBombs(new Bomb(), numberOfBombs);
    }
    private void addBombs(Bomb bomb,int numberOfBombs){
        for (int i = 0; i <numberOfBombs; i++) {
            bombs.add(new Bomb());
        }
    }
    public List<Bomb> getBombs(){
        return bombs;
    }
    public void removeBombs(){
        bombs.clear();
    }
}
