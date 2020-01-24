
import com.googlecode.lanterna.Symbols;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.AbstractTextGraphics;
import com.googlecode.lanterna.graphics.TextGraphics;
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
    static int scoreCounter = 500;


    public static void main(String[] args) throws Exception {

        //Creating terminal window
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        Terminal terminal = terminalFactory.createTerminal();
        terminal.setForegroundColor(TextColor.ANSI.GREEN);
        terminal.setCursorVisible(false);
        TextGraphics tGraphics = terminal.newTextGraphics();
        TextGraphics logo = terminal.newTextGraphics();
        logo.drawRectangle(new TerminalPosition(55,1), new TerminalSize(19,5), Symbols.DIAMOND);
        terminal.newTextGraphics().putCSIStyledString(58,3,"AMAZING MAZE!");

        //tried to change colour
        logo.setForegroundColor(TextColor.ANSI.RED);


        // defining player, walls and bombs
        final char player = '\u26C7';
        final char block = '\u2588';
        final char bomb = '\u2623';
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

            System.out.println("keyStroke.getKeyType(): " + type
                    + " keyStroke.getCharacter(): " + c);

            if (c == Character.valueOf('q') || c == Character.valueOf('Q')) {
                continueReadingInput = false;
                System.out.println("Quitting.....");
                terminal.newTextGraphics().putCSIStyledString(58,10,"You coward, are you quitting on me?!");

                System.out.println("Quitting.....");

                Thread.sleep(1000);
                terminal.close();
                System.out.println("quit");
            }

            int oldX = x; // save old position x
            int oldY = y; // save old position y
            switch (keyStroke.getKeyType()) {
                case ArrowDown:
                    y += 1;
                    moveCounter++;
                    scoreCounter --;
                    break;
                case ArrowUp:
                    y -= 1;
                    moveCounter++;
                    scoreCounter --;
                    break;
                case ArrowRight:
                    x += 1;
                    moveCounter++;
                    scoreCounter --;
                    break;
                case ArrowLeft:
                    x -= 1;
                    moveCounter++;
                    scoreCounter --;
                    break;
            }


            // detect if player tries to run into obsticle
            playerCrashing(terminal, player, walls, oldX, oldY);

            // check if player runs into the bomb
            catchBomb(terminal, bombs, (AbstractTextGraphics) tGraphics);

            boolean isWinning = (x == 48 && y == 23) ||( x == 49 && y == 23);

            if (isWinning){
                System.out.println("YOU WON!");
                terminal.clearScreen();
                tGraphics.clearModifiers();
                tGraphics.drawRectangle(new TerminalPosition(23,8), new TerminalSize(40,8), Symbols.DIAMOND);
                terminal.newTextGraphics().putCSIStyledString(30,11,"CONGRATULATIONS, YOU WON!");
                String s = "your score is: "+ scoreCounter;
                terminal.newTextGraphics().putString(33,13,s);
            }

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

    private static void playerCrashing(Terminal terminal, char player, Walls walls, int oldX, int oldY) throws IOException {
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

    private static void catchBomb(Terminal terminal, Bombs bombs, AbstractTextGraphics tGraphics) throws IOException, InterruptedException {
        for (Bomb bomber : bombs.getBombs()) {
            if (bomber.bombPosition.x == x && bomber.bombPosition.y == y) {
                terminal.bell();
                System.out.println("GAME OVER!");
                terminal.clearScreen();
                tGraphics.clearModifiers();
                tGraphics.drawRectangle(new TerminalPosition(20,8), new TerminalSize(40,8), Symbols.DIAMOND);
                terminal.newTextGraphics().putCSIStyledString(35,12,"GAME OVER!");
                Thread.sleep(5000);
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

