package DontGetTouchedFSEM;
// The tile map(level) class
// Matt Jarnevic

/*
    The public methods are:

    public int getX() { return x;} //returns the x coord

    public int getY(){ return y;} //returns the y coord

    public void setX(int newX) {}

    public int getColTile(int x) { return x / tileSize; } //get the col tile corresponding to the x coord

    public int getRowTile(int y) { return y / tileSize; } //get the row tile corresponding to the y coord

    public int getTile(int row, int col) { return level[row][col]; } //get the tile based on the row and col

    public int getTileSize() { return tileSize; }

    public void update() {}

 */

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class TileMap {

    private int x;
    private int y;
    private int minX;
    private int maxX;

    // the tilesize, the level and its dimensions
    private int tileSize;
    private int [][] level;
    private int levelWidth;
    private int levelHeight;

    //screen width and height
    private int pHeight;
    private int pWidth;

    // the width and height of the sprite sheet for accessing objects in it
    private int sheetWidth;
    private int sheetHeight;

    // arrays of bufferedImages to hold all the objects and tiles
    private BufferedImage groundTiles[];
    private BufferedImage pipeTiles[];
    private BufferedImage objects[];

    // array of all the blocked tiels
    private int blocked[];

    // the players x and y coordinates to spawn in at
    private int playerX = tileSize*2;
    private int playerY = tileSize*4;



    public TileMap(int pH, int pW) {

        pHeight = pH;
        pWidth = pW;

        sheetWidth = 20;
        sheetHeight = 20;

        blocked = new  int [] {1, 2, 5, 7, 8, 9, 20, 30, 40};
        tileSize = pH / 15;
        maxX = -tileSize;
        minX = -(tileSize* levelWidth - (pW + tileSize));
        groundTiles = new  BufferedImage [5];
        pipeTiles = new BufferedImage[5];
        objects = new BufferedImage[1];

        try {

            BufferedImage image = ImageIO.read(getClass().getResource("/DontGetTouchedFSEM/Resources/Sprites/spritesheets.png"));
            groundTiles[0] = image.getSubimage(spriteSheetCalc(3), spriteSheetCalc(4), 21, 21);
            groundTiles[1] = image.getSubimage(spriteSheetCalc(2), spriteSheetCalc(5), 21, 21);
            groundTiles[2] = image.getSubimage(spriteSheetCalc(3), spriteSheetCalc(10), 21, 21);
            groundTiles[3] = image.getSubimage(spriteSheetCalc(2), spriteSheetCalc(11), 21, 21);

            pipeTiles[0] = ImageIO.read(getClass().getResource("/DontGetTouchedFSEM/Resources/Objects/pipe_entr_top.png"));
            pipeTiles[1] = ImageIO.read(getClass().getResource("/DontGetTouchedFSEM/Resources/Objects/pipe_entr_bot.png"));
            pipeTiles[2] = ImageIO.read(getClass().getResource("/DontGetTouchedFSEM/Resources/Objects/pipe_entr_right.png"));
            pipeTiles[3] = ImageIO.read(getClass().getResource("/DontGetTouchedFSEM/Resources/Objects/pipe_entr_left.png"));
            pipeTiles[4] = ImageIO.read(getClass().getResource("/DontGetTouchedFSEM/Resources/Objects/pipe.png"));

            objects[0] = ImageIO.read(getClass().getResource("/DontGetTouchedFSEM/Resources/Objects/pumpkin.png"));

        }
        catch(Exception e) {
            e.printStackTrace();
        }


    }

    private int spriteSheetCalc(int i) {
        return (i+1)*3 + sheetWidth*i;
    }

    /**
     * Loads a level from the list of levels using the level name
     * @param s
     */
    public void loadLevel(String s) {
        try {
            BufferedReader br = new BufferedReader((new InputStreamReader(getClass().getResourceAsStream ("/DontGetTouchedFSEM/Resources/Levels/" + s))));

            levelWidth = Integer.parseInt(br.readLine());
            levelHeight = Integer.parseInt(br.readLine());
            maxX = -tileSize;
            minX = -(tileSize* levelWidth - (pWidth + tileSize));

            level = new int[levelHeight][levelWidth];

            for(int row = 0; row < levelHeight; row++) {
                String line = br.readLine();
                String[] tokens = line.split(" ");
                for (int col = 0; col < levelWidth; col++) {
                    level[row][col] = Integer.parseInt(tokens[col]);
                }
            }

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public int[] getBlocked() { return blocked; }

    public int getX() { return x; }//returns the x coord

    public int getY(){ return y; }//returns the y coord

    public void setX(int newX) {
        if (newX < minX) {
            x = minX;
        }
        else if (newX > maxX){
            x = maxX;
        }
        else {
            x = newX;
        }

    }

    public int getSpawnX() {return playerX;}

    public int getSpawnY() {return playerY;}

    public int getColTile(int x) { return x / tileSize; }

    public int getRowTile(int y) { return y / tileSize; }

    public int getTile(int row, int col) { return level[row][col]; }

    public int getTileSize() { return tileSize; }

    public void update() {}

    /**
     * Basic method which accepts an integer n and loops through the level
     * and returns an array list of all the coords(x and y) for that
     * number in the level
     * @param n
     * @return
     */
    public ArrayList<Integer> getCoords(int n) {
        ArrayList<Integer> Coords = new ArrayList<>();
        for (int row = 0; row < level.length; row++) {
            for (int col = 0; col < level[row].length; col++) {
                int rc = getTile(row, col);
                if (rc == n) {
                    Coords.add(col * tileSize);
                    Coords.add(row * tileSize);
                }
            }
        }
        return Coords;
    }

    /**
     * Goes through the level and finds where the number 10 is, then
     * sets the playerX and the playerY to that spot
     */
    public void getPlayerCoords() {
        for (int row = 0; row < level.length; row++) {
            for (int col = 0; col < level[row].length; col++) {
                int rc = getTile(row, col);
                if (rc == 10) {
                    playerX = col * tileSize;
                    playerY = row * tileSize;
                }
            }
        }
    }

    public void draw(Graphics g) {
        for (int row = 0; row < level.length; row++) {
            for (int col = 0; col < level[row].length; col++) {
                int rc = getTile(row,col);

                if (rc == 1) { //blocked
                    g.drawImage(groundTiles[0], (x + col * tileSize), y + row * tileSize, tileSize, tileSize, null);
                    continue;
                }
                if (rc == 2) { //goal(pipe entr top)
                    g.drawImage(pipeTiles[0], (x + col * tileSize), y + row * tileSize, tileSize, tileSize, null);
                    continue;
                }
                if (rc == 5) { //dirt tile, blocked tile
                    g.drawImage(groundTiles[1], (x + col * tileSize), y + row * tileSize, tileSize, tileSize, null);
                    continue;
                }
                if (rc == 6) { //pumpkin(coin)
//                    g.drawImage(objects[0], (x + col * tileSize), y + row * tileSize, tileSize, tileSize, null);
                    continue;
                }
                if (rc == 7) { //underground tile, blocked tile
                    g.drawImage(groundTiles[2], (x + col * tileSize), y + row * tileSize, tileSize, tileSize, null);
                    continue;
                }
                if (rc == 8) { //underground tile, blocked tile
                    g.drawImage(groundTiles[3], (x + col * tileSize), y + row * tileSize, tileSize, tileSize, null);
                    continue;
                }
                if (rc == 9) { //pipe
                    g.drawImage(pipeTiles[4], (x + col * tileSize), y + row * tileSize, tileSize, tileSize, null);
                    continue;
                }
                if (rc == 10) {
                    playerX = col * tileSize;
                    playerY = row * tileSize;
                }
                if (rc == 20) { //goal(pipe entr left)
                    g.drawImage(pipeTiles[3], (x + col * tileSize), (y + row * tileSize),tileSize, tileSize, null);
                    continue;
                }
                if (rc == 30) { //pipe(exit down)
                    g.drawImage(pipeTiles[1], (x + col * tileSize), (y + row * tileSize),tileSize, tileSize, null);
                    continue;
                }
                if (rc == 40) { //goal(pipe entr right)
                    g.drawImage(pipeTiles[2], (x + col * tileSize), (y + row * tileSize),tileSize, tileSize, null);
                    continue;
                }
            }
        }
    }

}
