package DontGetTouchedFSEM;
// Enemy.java
// Matt Jarnevic

/* Contains the enemy's coordinates and movement
   and code for deciding on the next position or
   if it needs to change left or right movement.

   The enemy only moves left and right and when it hits
   a wall it turns around or if it would land a space with nothing below it,
   it will turn around so it does not fall

   The public methods are:

   public Enemy(TileMap tm, int pW, int pH) // creation of a new enemy

   public void setxPos(int newX) { xPos = newX; }

   public void setyPos(int newY) { yPos = newY; }

   public double getxPos() { return xPos; }

   public double getyPos() { return yPos; }

   public int getWidth() { return width; }

   public int getHeight() { return height; }

   public void update() // determines movement of enemies
*/


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.stream.IntStream;

public class Enemy extends JFrame
{

    // dimensions
    protected int width;
    protected int height;
    protected int sheetWidth;
    protected int sheetHeight;

    // position
    protected double xPos;
    protected double yPos;
    protected double dx;
    protected double dy;

    // movement
    protected boolean left;
    protected boolean right;
    protected boolean falling;

    // stores various speeds and values for calculations
    protected double moveSpeed;
    protected double maxSpeed;
    protected double maxFallingSpeed;
    protected double stopSpeed;
    protected double gravity;

    // the tile map that the enemy will be on
    protected TileMap tm;
    protected int tileSize;

    // booleans for the corners of the screen
    private boolean topLeft;
    private boolean topRight;
    private boolean bottomLeft;
    private boolean bottomRight;

    protected int pWidth, pHeight;   // panel dimensions

    // animation
    protected Animation animation;
    protected int currentAnimation;

    //used for drawing the enemy based on the direction its facing
    protected boolean facingLeft;

    private int blocked[];

    public Enemy(){

    }

    protected int spriteSheetCalc(int i) {
        return (i+1)*3 + sheetWidth*i;
    }

    public void setxPos(int newX) { xPos = newX; }

    public void setyPos(int newY) { yPos = newY; }

    public double getxPos() { return xPos; }

    public double getyPos() { return yPos; }

    public int getWidth() { return width; }

    public int getHeight() { return height; }

    private void calculateNearbyTiles(double xPos, double yPos) {
        int leftTile = tm.getColTile( (int) (xPos - width / 2));
        int rightTile = tm.getColTile ( (int) (xPos + width / 2) - 1);
        int topTile = tm.getRowTile( (int) yPos - height / 2);
        int bottomTile = tm.getRowTile( (int) (yPos + height / 2) - 1);
        blocked = tm.getBlocked();
        // compare the tile to all of the blocked tiles
        topLeft = IntStream.of(blocked).anyMatch(a -> a == (tm.getTile(topTile, leftTile)));
        topRight = IntStream.of(blocked).anyMatch(a -> a == (tm.getTile(topTile, rightTile)));
        bottomLeft = IntStream.of(blocked).anyMatch(a -> a == (tm.getTile(bottomTile, leftTile)));
        bottomRight = IntStream.of(blocked).anyMatch(a -> a == (tm.getTile(bottomTile, rightTile)));

    }


    /**
     * The update function tells the enemy how to move.
        It is very similar to the player movement except it
        only moves in the x direction or when falling down.

        The enemy will not walk off cliffs, but will instead turn around
        if the next till would cause it to fall. If it hits a wall moving left
        or right, it will turn around and move the opposite direction till
        it hits a wall or empty space below it.
     */
    public void update() {
        //determine next position
        if (left) { // if moving left
            dx -= moveSpeed;
            if (dx < -maxSpeed) { // if moving faster than the max speed
                dx = -maxSpeed;
            }
        }
        else if (right) { // if moving right
            dx += moveSpeed;
            if(dx > maxSpeed) { // if moving faster than the max speed
                dx = maxSpeed;
            }
        }
        else {
            if (dx > 0) { // if still moving right
                dx -= stopSpeed; //slow down till
                if (dx < 0) { // come to a stop
                    dx = 0;
                }
            }
            else if (dx < 0) { // if still moving left
                dx += stopSpeed; // slow down still
                if (dx > 0) { //come to a stop
                    dx = 0;
                }
            }
        }

        if(falling) {
            dy += gravity;
            if(dy > maxFallingSpeed) {
                dy = maxFallingSpeed;
            }
        }
        else { // not falling
            dy = 0;
        }


        // check collisions with walls and empty space below the enemy

        int currCol = tm.getColTile( (int) xPos);
        int currRow = tm.getRowTile( (int) yPos);

        double goingToX = xPos + dx;
        double goingToY = yPos + dy;

        double tempX = xPos;
        double tempY = yPos;

        calculateNearbyTiles(xPos, goingToY);
        if (dy > 0 ) { // if moving down, which only happens if it is falling
            if(bottomLeft || bottomRight) { // if the bottom exists
                dy = 0; // stop falling
                falling = false;
                tempY = (currRow + 1) * tm.getTileSize() - height / 2;
            }
            else { // keep falling till you land on solid ground
                tempY += dy;
            }
        }

        calculateNearbyTiles(goingToX, yPos); // check the next x positions
        if(dx < 0) { //if going to the left
            if(topLeft || bottomLeft) { //check two left corners
                dx = 0; //if blocked, stop
                left = false; //turnaround
                right = true;
                tempX = currCol * tm.getTileSize() + width / 2;
            }
            else { // keep going left
                tempX += dx;
            }
        }
        if (dx > 0) { //moving to the right
            if(topRight || bottomRight) { //check two right corners
                dx = 0; //if blocked, stop
                right = false; //turnaround
                left = true;
                tempX = (currCol + 1) * tm.getTileSize() - width / 2;
            }
            else { // else keep moving right
                tempX += dx;
            }
        }

        if(!falling) { // if not falling
            calculateNearbyTiles(xPos, yPos + 1); //get the tiles under the enemy
            if(!bottomLeft && !bottomRight) { //if nothing below the enemy
                falling = true; //begin falling

            }
        }
        if (dx > 0) { //if moving to the right
            if(!falling) { // if not falling (important because enemies can fall when they initially spawn)
                calculateNearbyTiles(xPos + width, yPos + 1); //get the tiles to the right and under the enemy
                if(!bottomLeft && !bottomRight) { //if nothing there
                    right = false; //stop moving right to avoid falling
                    left= true;
                }
            }
        }

        if (dx < 0) {
            if(!falling) { // if not falling (important because enemies can fall when they first spawn)
                calculateNearbyTiles(xPos - width, yPos + 1); //get the tiles to the left and under the enemy
                if(!bottomLeft && !bottomRight) { //if nothing there
                    left= false; //stop moving left to avoid falling
                    right = true;
                }
            }
        }

        // set the actual xPos and yPos
        xPos = tempX;
        yPos = tempY; //only changes when the enemy is falling(it usually stays the same except at the very beginning)

    }

    public void draw(Graphics g)
    {
        int tx = tm.getX();
        int ty = tm.getY();
        if(facingLeft) {
            g.drawImage(animation.getImage(), (int) (tx + xPos - width / 2), (int) (ty + yPos - height / 2), width, height, null);
        }
        else {
            g.drawImage(animation.getImage(), (int) (tx + xPos - width / 2 + width), (int) (ty + yPos - height / 2), -width, height, null);
        }
    }  // end of draw()

}  // end of Enemy class
