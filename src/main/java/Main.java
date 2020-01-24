
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
import java.util.concurrent.ThreadLocalRandom;

public class Main {

    static int x = 2;
    static int y = 2;
    static int numberOfAdditionalBombs = 0;
    static int steps = 0;
    static int moveCounter = 1;
    static int scoreCounter = 500;


    public static void main(String[] args) throws Exception {
        //Creating terminal window

        boolean continuePlaying = true;

        while (continuePlaying) {
            DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
            Terminal terminal = terminalFactory.createTerminal();
            TextGraphics tGraphics = createTerminal(terminal);


            // defining player, walls and bombs
            final char player = '\u26C7';
            final char block = '\u2588';
            final char bomb = '\u2623';
            terminal.setCursorPosition(x, y);
            terminal.putCharacter(player);

            // Drawing Maze with random bombs and static walls
            Bombs bombs = new Bombs(20);
            makeBombs(terminal, bomb, bombs);
            Walls wallsInitial = new Walls();
            drawWalls(terminal, block, wallsInitial);
            terminal.flush();

            // While loop to keep the game running
            gameEngine(terminal, tGraphics, player, block, bomb, bombs);
        }
    }


    // METHOD CALLS IN MAIN

    private static TextGraphics createTerminal(Terminal terminal) throws IOException {
        terminal.setForegroundColor(TextColor.ANSI.GREEN);
        terminal.setCursorVisible(false);
        TextGraphics tGraphics = terminal.newTextGraphics();
        TextGraphics logo = terminal.newTextGraphics();
        logo.drawRectangle(new TerminalPosition(55,1), new TerminalSize(19,5), Symbols.DIAMOND);
        terminal.newTextGraphics().putCSIStyledString(58,3,"AMAZING MAZE!");
        terminal.newTextGraphics().putCSIStyledString(56,7,"Welcome!");
        terminal.newTextGraphics().putCSIStyledString(56,9,"Use the arrows to move.");
        terminal.newTextGraphics().putCSIStyledString(56,11,"Press q to quit");
        terminal.newTextGraphics().putCSIStyledString(60,18,"GOOD LUCK!");
        return tGraphics;
    }



    // While loop to keep the game running
    private static void gameEngine(Terminal terminal, TextGraphics tGraphics, char player, char block, char bomb, Bombs bombs) throws IOException, InterruptedException {
        boolean continueReadingInput = true;
        // GAME LOOP
        while (continueReadingInput) {

            System.out.println(steps);
            System.out.println(moveCounter);
            // Keep drawing the walls to avoid bombs inside walls
            Walls walls = new Walls();
            drawWalls(terminal, block, walls);

            KeyStroke keyStroke = null;
            do {
                Thread.sleep(5); // might throw InterruptedException
                keyStroke = terminal.pollInput();
            } while (keyStroke == null);

            KeyType type = keyStroke.getKeyType();
            Character c = keyStroke.getCharacter(); // used Character instead of char because it might be null

            // Press q or Q to exit the game
            continueReadingInput = exitGame(terminal, continueReadingInput, c);

            int oldX = x; // save old position x
            int oldY = y; // save old position y
            navigatingPlayer(keyStroke);

            // detect if player tries to run into wall
            playerCrashing(terminal, player, walls, oldX, oldY);

            // check if player runs into the bomb
            catchBomb(terminal, bombs, (AbstractTextGraphics) tGraphics);

            continueReadingInput = winningGame(terminal, tGraphics);

            bombs = addingMoreBombs(terminal, block, bomb, bombs, walls);
            terminal.flush();
            }
    }

    private static boolean exitGame(Terminal terminal, boolean continueReadingInput, Character c) throws InterruptedException, IOException {
        // A menu will be inserted in the next version of the game

        if (c == Character.valueOf('q') || c == Character.valueOf('Q')) {
            continueReadingInput = false;
            System.out.println("Quitting.....");
            Thread.sleep(1000);
            terminal.close();
            System.out.println("Quit");
        }
        return continueReadingInput;
    }

    private static Bombs addingMoreBombs(Terminal terminal, char block, char bomb, Bombs bombs, Walls walls) throws IOException, InterruptedException {

            if(steps == moveCounter) {
                moveCounter = ThreadLocalRandom.current().nextInt(1, 15 + 1);
                System.out.println("MOVE COUNTER" + moveCounter);
                numberOfAdditionalBombs = ThreadLocalRandom.current().nextInt(10, 150 + 1);
                System.out.println("Numberofbombs = " + numberOfAdditionalBombs);
                removeBombs(terminal, bombs);
                bombs.removeBombs();
                bombs = new Bombs(20 + numberOfAdditionalBombs);

                makeBombs(terminal, bomb, bombs);
                drawWalls(terminal, block, walls);
                steps = 0;
                System.out.println("IN addingBombs");

            }
        return bombs;
    }

    private static boolean winningGame(Terminal terminal, TextGraphics tGraphics) throws IOException, InterruptedException {
        boolean isWinning = (x == 48 && y == 23) ||( x == 49 && y == 23);

        if (isWinning){

            System.out.println("YOU WON!");
            terminal.clearScreen();
            tGraphics.clearModifiers();
            tGraphics.drawRectangle(new TerminalPosition(23,8), new TerminalSize(40,8), Symbols.DIAMOND);
            terminal.newTextGraphics().putCSIStyledString(30,11,"CONGRATULATIONS, YOU WON!");
            String s = "your score is: "+ scoreCounter;
            terminal.newTextGraphics().putString(33,13,s);
            return false;

        }
        return  true;
       }

    private static void navigatingPlayer(KeyStroke keyStroke) {
        switch (keyStroke.getKeyType()) {
            case ArrowDown:
                y += 1;
                steps++;
                scoreCounter --;
                break;
            case ArrowUp:
                y -= 1;
                steps++;
                scoreCounter --;
                break;
            case ArrowRight:
                x += 1;
                steps++;
                scoreCounter --;
                break;
            case ArrowLeft:
                x -= 1;
                steps++;
                scoreCounter --;
                break;
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

