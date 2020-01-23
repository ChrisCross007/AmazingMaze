
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.util.*;

public class Main {
    static int x = 2;
    static int y = 2;
    public static void main(String[] args) throws Exception {
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        Terminal terminal = terminalFactory.createTerminal();
        terminal.setCursorVisible(false);


        final char player = 'X';
        final char block = '\u2588';
        final char bomb = 'O';
        terminal.setCursorPosition(x, y);
        terminal.putCharacter(player);


        // adding Bombs
        Bombs bombs = new Bombs(20);
        makeBombs(terminal, bomb, bombs);


        // Create obsticles array
        Position[] obsticles = new Position[100];
        for(int i = 0;i<100;i++){
            obsticles[i] = new Position(0, 0+i);
        }
        // Create obsticles array
        Walls walls = new Walls();
        walls.addWall(new Wall(1,1, 79, true));
        walls.addWall(new Wall(1,1, 49, false));
        walls.addWall(new Wall(1,79, 49, false));
        walls.addWall(new Wall(23 ,1, 76, true));
        walls.addWall(new Wall(20,20, 10, true));
        walls.addWall(new Wall(12,4, 15, false));
        walls.addWall(new Wall(1,4, 9, false));
        walls.addWall(new Wall(9,4, 9, true));
        walls.addWall(new Wall(1,13, 7, false));

        // Use obsticles array to print to lanterna
        drawWalls(terminal, block, walls);



        terminal.flush();


        // Task 12
        boolean continueReadingInput = true;
        while (continueReadingInput) {



            KeyStroke keyStroke = null;
            do {
                Thread.sleep(5); // might throw InterruptedException
                keyStroke = terminal.pollInput();
            } while (keyStroke == null);


            KeyType type = keyStroke.getKeyType();
            Character c = keyStroke.getCharacter(); // used Character instead of char because it might be null

            System.out.println("keyStroke.getKeyType(): " + type
                    + " keyStroke.getCharacter(): " + c);

            if (c == Character.valueOf('q')) {
                continueReadingInput = false;
                terminal.close();
                System.out.println("quit");
            }

            int oldX = x; // save old position x
            int oldY = y; // save old position y
            switch (keyStroke.getKeyType()) {
                case ArrowDown:
                    y += 1;
                    break;
                case ArrowUp:
                    y -= 1;
                    break;
                case ArrowRight:
                    x += 1;
                    break;
                case ArrowLeft:
                    x -= 1;
                    break;
            }


            // detect if player tries to run into obsticle
            boolean crashIntoObsticle = false;

            List<Wall> walls1 = walls.getWalls();
            for (Wall w : walls1) {
                List<Position> wall = w.createWall();
                for (Position p : wall) {
                    if (p.x == x && p.y == y) {
                        crashIntoObsticle = true;
                        break;
                    }
                }
            }


            if (crashIntoObsticle) {
                x = oldX;
                y = oldY;
            }
            else {
                terminal.setCursorPosition(oldX, oldY); // move cursor to old position
                terminal.putCharacter(' '); // clean up by printing space on old position
                terminal.setCursorPosition(x, y);
                terminal.putCharacter(player);
            }

            // check if player runs into the bomb
            for(Bomb bomber: bombs.getBombs()) {
                if (bomber.bombPosition.x == x && bomber.bombPosition.y == y) {
                    terminal.close();
                }


                terminal.flush();
            }
        }

    }

    private static void makeBombs(Terminal terminal, char bomb, Bombs bombs) throws IOException, InterruptedException {
        for (Bomb bomber : bombs.getBombs()) {
            terminal.setCursorPosition(bomber.bombPosition.x, bomber.bombPosition.y);
            terminal.putCharacter(bomb);

        }
    }

    private static void drawWalls(Terminal terminal, char block,  Walls allWalls) throws IOException {
        List<Wall> walls = allWalls.getWalls();
        for (Wall wall : walls) {
            List<Position> positions = wall.createWall();
            drawWall(terminal, block, positions);
        }
    }

    private static void drawWall(Terminal terminal, char block, List<Position> positions) throws IOException {
        for (Position position : positions) {
            terminal.setCursorPosition(position.x, position.y);
            terminal.putCharacter(block);


            }
        }
    public static class Helper extends TimerTask
    {
        public static int timers = 0;
        public void run()
        {
            System.out.println("Timer ran " + ++timers);
        }
    }
}

