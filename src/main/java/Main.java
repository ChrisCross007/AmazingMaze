
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.util.*;

public class Main {
    static int x = 2;
    static int y = 2;
    static int moveCounter = 0;
    static int numberOfBombs = 0;


    public static void main(String[] args) throws Exception {
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        Terminal terminal = terminalFactory.createTerminal();
        terminal.setCursorVisible(false);



        final char player = '\u1F93';
        final char block = '\u2588';
        final char bomb = '\u26D4';
        terminal.setCursorPosition(x, y);
        terminal.putCharacter(player);

        Bombs bombs = new Bombs(20);
        makeBombs(terminal, bomb, bombs);
        Walls wallsInitial = new Walls();
        drawWalls(terminal, block, wallsInitial);

        terminal.flush();


        boolean continueReadingInput = true;
        while (continueReadingInput) {
            Walls walls = new Walls();
            drawWalls(terminal, block, walls);


            KeyStroke keyStroke = null;
            do {
                Thread.sleep(5); // might throw InterruptedException
                keyStroke = terminal.pollInput();
            } while (keyStroke == null);


            KeyType type = keyStroke.getKeyType();
            Character c = keyStroke.getCharacter(); // used Character instead of char because it might be null


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
                    moveCounter++;
                    break;
                case ArrowUp:
                    y -= 1;
                    moveCounter++;
                    break;
                case ArrowRight:
                    x += 1;
                    moveCounter++;
                    break;
                case ArrowLeft:
                    x -= 1;
                    moveCounter++;
                    break;
            }


            // detect if player tries to run into obsticle
            boolean crashIntoObsticle = isCrashingIntoAWall(walls);


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
            catchBomb(terminal, bombs);


            if(moveCounter == 10){

                numberOfBombs += 7;
                removeBombs(terminal, bombs);
                bombs.removeBombs();
                bombs = new Bombs(20+ numberOfBombs);

                makeBombs(terminal, bomb, bombs);
                drawWalls(terminal, block, walls);
                moveCounter = 0;

            }
                terminal.flush();
            }
        }

    private static boolean isCrashingIntoAWall(Walls walls) {
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
        return crashIntoObsticle;
    }

    private static void catchBomb(Terminal terminal, Bombs bombs) throws IOException, InterruptedException {
        for (Bomb bomber : bombs.getBombs()) {
            if (bomber.bombPosition.x == x && bomber.bombPosition.y == y) {
                terminal.bell();
                Thread.sleep(100);
                terminal.close();
                System.exit(0);
            }
        }
    }


    private static void makeBombs(Terminal terminal, char bomb, Bombs bombs) throws IOException, InterruptedException {
            for (Bomb bomber : bombs.getBombs()) {
                terminal.setCursorPosition(bomber.bombPosition.x, bomber.bombPosition.y);
                terminal.putCharacter(bomb);
            }
        System.out.println("Bombs made");

    }
    private static void removeBombs(Terminal terminal, Bombs bombs) throws IOException, InterruptedException {
        for (Bomb bomber : bombs.getBombs()) {
            terminal.setCursorPosition(bomber.bombPosition.x, bomber.bombPosition.y);
            terminal.putCharacter(' ');
        }
        System.out.println("Bombs removed");

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

}

