import java.util.Random;

public class Bomb {
    Position bombPosition;


    public Bomb() {
        Random r = new Random();
        this.bombPosition = new Position(r.nextInt(50), r.nextInt(23));

    }

}
