package DontGetTouchedFSEM;
// Player.java
// Matt Jarnevic

/* Contains the player's coordinates and movement
   and code for deciding on the next position of the player.
   It also checks in here if the player should warp through a pipe or not

   The public methods are:

   public Player(TileMap tm, int pW, int pH)   // for instantiating the player

   public void setLeft(boolean b) { left = b; } // if the player is moving left or not

   public void setRight(boolean b) {right = b; } //if the player is moving right or not

   public void setJumping(boolean b) { if (!falling) { jumping = true;} }

   public void setxPos(int newX) { xPos = newX; }

   public void setyPos(int newY) { yPos = newY; }

   public double getxPos() { return xPos; }

   public double getyPos() { return yPos; }

   public int getWidth() { return width; }

   public int getHeight() { return height; }

   public void setLives( int n) { lives = n; }

   public void update()
*/

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.stream.IntStream;

import static DontGetTouchedFSEM.Main.TileGame.jukeBox;

public class Player extends JFrame{

    // actions the player can be performing
    protected boolean left;
    protected boolean right;
    protected boolean down;
    protected boolean jumping;
    protected boolean falling;

    // stores various speeds and values for calculations
    protected double moveSpeed;
    protected double maxSpeed;
    protected double maxFallingSpeed;
    protected double stopSpeed;
    protected double jumpStart;
    protected double gravity;

    // number of lives the player has
    public int lives;

    // coordinates of player and offsets
    protected double xPos;
    protected double yPos;
    protected double dx;
    protected double dy;

    // player width and height(currently a rectangle, will eventually be an image)
    protected int width;
    protected int height;
    protected int sheetHeight;
    protected int sheetWidth;

    // the tile map that the player will be on
    protected TileMap tm;
    protected int tileSize;

    // the panel dimensions
    protected int pWidth, pHeight;

    // booleans for the corners of the screen
    private boolean topLeft;
    private boolean topRight;
    private boolean bottomLeft;
    private boolean bottomRight;

    protected Animation animation;

    private BufferedImage[] idleSprites;
    private BufferedImage[] walkingSprites;
    private BufferedImage[] jumpingSprites;
    private BufferedImage[] fallingSprites;

    private BufferedImage[] catIdleSprites;
    private BufferedImage[] catWalkingSprites;
    private BufferedImage[] catJumpingSprites;
    private BufferedImage[] catFallingSprites;
    private BufferedImage[] catDownSprites;
    private BufferedImage[] catDyingSprites;

    protected boolean facingLeft;
    protected boolean dead;
    protected boolean awaitingRespawn;

    private int[] blocked;

    private int spacing;


    public Player(){ }

    public Player(TileMap tm, int pW, int pH) {

        this.tm = tm;
        tileSize = tm.getTileSize();
        width = (int) (tm.getTileSize() * 4/10.);
        height = (int) (tm.getTileSize() * 6/10.);
        sheetWidth = 22;
        sheetHeight = 22;

        moveSpeed = tm.getTileSize() / 17.0;
        maxSpeed = tm.getTileSize() / 13.2;
        maxFallingSpeed = 12;
        stopSpeed = tm.getTileSize() / 26.8;
        jumpStart = -tm.getTileSize() / 3.2;
        gravity = tm.getTileSize() / 86.4;

        pWidth = pW;
        pHeight = pH;
        xPos = pWidth/2;
        yPos = pHeight/2;

        lives = 1;

        try{
            idleSprites = new BufferedImage[1];
            jumpingSprites = new BufferedImage[1];
            fallingSprites = new BufferedImage[1];
            walkingSprites = new BufferedImage[6];

            catIdleSprites = new BufferedImage[4];
            catJumpingSprites = new BufferedImage[1];
            catFallingSprites = new BufferedImage[1];
            catWalkingSprites = new BufferedImage[8];
            catDownSprites = new BufferedImage[1];
            catDyingSprites = new BufferedImage[1];

            BufferedImage cats = ImageIO.read(getClass().getResource("/DontGetTouchedFSEM/Resources/Sprites/cat_sprites.png"));
            for (int i = 0; i < catIdleSprites.length; i++) {
                catIdleSprites[i] = cats.getSubimage((20 + 64*i), 24, 20, 30);
            }
            for (int i = 0; i < catJumpingSprites.length; i++) {
                catJumpingSprites[i] = cats.getSubimage((20 + 64*3), 24 + 64*2, 20, 30);
            }
            for (int i = 0; i < catFallingSprites.length; i++) {
                catFallingSprites[i] = cats.getSubimage((20 + 64*4), 24 + 64*2, 20, 30);
            }
            for (int i = 0; i < catWalkingSprites.length; i++) {
                catWalkingSprites[i] = cats.getSubimage((20 + 64*i), 24 + 64, 20, 30);
            }
            for (int i = 0; i < catDownSprites.length; i++) {
                catDownSprites[i] = cats.getSubimage((20 + 64*2), 24 + 64*5, 20, 30);
            }
            for (int i = 0; i < catDyingSprites.length; i++) {
                catDyingSprites[i] = cats.getSubimage((20 + 64*2), 24 + 64*4, 20, 30);
            }

            idleSprites[0] = ImageIO.read(getClass().getResource("/DontGetTouchedFSEM/Resources/Sprites/kirbyidle.gif"));
            jumpingSprites[0] = ImageIO.read(getClass().getResource("/DontGetTouchedFSEM/Resources/Sprites/kirbyjump.gif"));
            fallingSprites[0] = ImageIO.read(getClass().getResource("/DontGetTouchedFSEM/Resources/Sprites/kirbyfall.gif"));

            BufferedImage image = ImageIO.read(getClass().getResource("/DontGetTouchedFSEM/Resources/Sprites/kirbywalk.gif"));
            for (int i = 0; i < walkingSprites.length; i++) {
                walkingSprites[i] = image.getSubimage(
                        i*sheetWidth + i,
                        0,
                        sheetWidth,
                        sheetHeight
                );
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        animation = new Animation();
        facingLeft = false;
    }

    public void reduceLives() { lives -= 1; }

    public boolean isDead() {
        if (lives <= 0) {
            dead = true;
            return true;
        }
        else
            return false;
    }

    public void setLeft(boolean b) { left = b; }

    public void setRight(boolean b) {right = b; }

    public void setDown(boolean b) {down = b;}

    public void setJumping(boolean b) { if (!falling) { jumping = true;} }

    public void setxPos(int newX) { xPos = newX; }

    public void setyPos(int newY) { yPos = newY; }

    public double getxPos() { return xPos; }

    public double getyPos() { return yPos; }

    public int getWidth() { return width; }

    public int getHeight() { return height; }

    public void setLives( int n) { lives = n; }

    public void setDead(boolean b) {dead = b; }

    /**
     * Used for respawning the player after he dies, accepts a boolean
     * which sets the awaiting respawn to be true or false and fixes
     * images and the spacing for deleting pixels
     * @param b
     */
    public void setRespawn(boolean b) {

        animation.setFrames(catDyingSprites);
        awaitingRespawn = b;
        spacing = 100;
        try{
            BufferedImage cats = ImageIO.read(getClass().getResource("/DontGetTouchedFSEM/Resources/Sprites/cat_sprites.png"));
            for (int i = 0; i < catDyingSprites.length; i++) {
                catDyingSprites[i] = cats.getSubimage((20 + 64*2), 24 + 64*4, 20, 30);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private void calculateNearbyTiles(double x, double y) {
        int leftTile = tm.getColTile( (int) (x - width / 2));
        int rightTile = tm.getColTile ( (int) (x + width / 2) - 1);
        int topTile = tm.getRowTile( (int) y - height / 2);
        int bottomTile = tm.getRowTile( (int) (y + height / 2) - 1);
        blocked = tm.getBlocked();
        topLeft = IntStream.of(blocked).anyMatch(a -> a == (tm.getTile(topTile, leftTile)));
        topRight = IntStream.of(blocked).anyMatch(a -> a == (tm.getTile(topTile, rightTile)));
        bottomLeft = IntStream.of(blocked).anyMatch(a -> a == (tm.getTile(bottomTile, leftTile)));
        bottomRight = IntStream.of(blocked).anyMatch(a -> a == (tm.getTile(bottomTile, rightTile)));
    }

    public boolean checkWin() {
        int playerCol = tm.getColTile((int) getxPos());
        int playerRow = tm.getRowTile((int) getyPos());

        if(down) { // if player pressed down key
            if (tm.getTile(playerRow + 1, playerCol) == 2) {
                jukeBox.play("pipeWarp");
                return true;
            }
        }
        else if(right) {
            if (tm.getTile(playerRow, playerCol+1) == 20)
            {
                if(xPos + (tileSize/4.25) > ((playerCol+1) * tileSize)) {
                    jukeBox.play("pipeWarp");
                    return true;
                }
            }
        }
        else if(left) {
            if (tm.getTile(playerRow, playerCol-1) == 40)
            {
                if(xPos - (tileSize/4.25) < ((playerCol-1) * tileSize + tileSize)) {
                    jukeBox.play("pipeWarp");
                    return true;
                }
            }
        }
        return false;
    }

    public void update() {
        //determine next position for the player
        if(!awaitingRespawn) {
            if (left) {
                dx -= moveSpeed;
                if (dx < -maxSpeed) {
                    dx = -maxSpeed;
                }
            } else if (right) {
                dx += moveSpeed;
                if (dx > maxSpeed) {
                    dx = maxSpeed;
                }
            } else {
                if (dx > 0) {
                    dx -= stopSpeed;
                    if (dx < 0) {
                        dx = 0;
                    }
                } else if (dx < 0) {
                    dx += stopSpeed;
                    if (dx > 0) {
                        dx = 0;
                    }
                }
            }

            if (jumping) {
                dy = jumpStart;
                falling = true;
                jumping = false;
                jukeBox.play("jump");
            }

            if (falling) {
                dy += gravity;
                if (dy > maxFallingSpeed) {
                    dy = maxFallingSpeed;
                }
            } else {
                dy = 0;
            }


            // check collisions with walls and ground

            int currCol = tm.getColTile((int) xPos);
            int currRow = tm.getRowTile((int) yPos);

            double goingToX = xPos + dx;
            double goingToY = yPos + dy;

            double tempX = xPos;
            double tempY = yPos;

            calculateNearbyTiles(xPos, goingToY);
            if (dy < 0) {
                if (topLeft || topRight) { //check to see if head collides with top
                    dy = 0;
                    tempY = currRow * tm.getTileSize() + height / 2;
                } else {
                    tempY += dy;
                }
            }
            if (dy > 0) {
                if (bottomLeft || bottomRight) { //check to see if stuff is below the player
                    dy = 0;
                    falling = false;
                    tempY = (currRow + 1) * tm.getTileSize() - height / 2;
                } else {
                    tempY += dy;
                }
            }

            calculateNearbyTiles(goingToX, yPos);
            if (dx < 0) { //if going to the left
                if (topLeft || bottomLeft) { //check two left corners
                    dx = 0; //if blocked, stop
                    tempX = currCol * tm.getTileSize() + width / 2;
                } else {
                    tempX += dx;
                }
            }
            if (dx > 0) { //moving to the right
                if (topRight || bottomRight) { //check two right corners
                    dx = 0; //if blocked, stop
                    tempX = (currCol + 1) * tm.getTileSize() - width / 2;
                } else {
                    tempX += dx;
                }
            }

            if (!falling) { // if not falling
                calculateNearbyTiles(xPos, yPos + 1); //get the tiles under the character
                if (!bottomLeft && !bottomRight) { //if nothing below the player
                    falling = true; //set falling to true
                }
            }

            // set the actual xPos and yPos
            xPos = tempX;
            yPos = tempY;

            if (yPos > pHeight + height)
                reduceLives();

            // move the level
            tm.setX((int) (pWidth / 2 - xPos));
            //tm.setyPos((int) (pHeight / 2 - yPos)); //(if i ever need to set the y of the camera)


            // sprite animation
            if (left || right) {
                animation.setFrames(catWalkingSprites);
                animation.setDelay(5);
            } else {
                animation.setFrames(catIdleSprites);
                animation.setDelay(10);
            }
            if (dy < 0) {
                animation.setFrames(catJumpingSprites);
                animation.setDelay(-1);
            }
            if (dy > 0) {
                animation.setFrames(catFallingSprites);
                animation.setDelay(-1);
            }
            if (down) {
                animation.setFrames(catDownSprites);
                animation.setDelay(-1);
            }
            animation.update();

            if (dx < 0) {
                facingLeft = true;
            }
            if (dx > 0) {
                facingLeft = false;
            }
        }

    } //end of update


    public void draw(Graphics g)
    // draw the player
    {
        int tx = tm.getX();
        int ty = tm.getY();
        if(awaitingRespawn) {
            int imWidth = animation.getImage().getWidth();
            int imHeight = animation.getImage().getHeight();
            int [] pixels = new int[imWidth * imHeight];
            animation.getImage().getRGB(0, 0, imWidth, imHeight, pixels, 0, imWidth);
            int i = 0;
            if(spacing > 0) {
                while (i < pixels.length) {
                    pixels[i] = 0; // make transparent (or black if no alpha)
                    i = i + spacing;
                }
                animation.getImage().setRGB(0, 0, imWidth, imHeight, pixels, 0, imWidth);
                g.drawImage(animation.getImage(), (int) (tx + xPos - width / 2), (int) (ty + yPos - height / 2), width, height, null);
            }
            spacing = spacing - 1;
        }
        else if(facingLeft) {
            g.drawImage(animation.getImage(), (int) (tx + xPos - width / 2 + width), (int) (ty + yPos - height / 2), -width, height, null);
        }
        else if(!facingLeft) {
            g.drawImage(animation.getImage(), (int) (tx + xPos - width / 2), (int) (ty + yPos - height / 2), width, height, null);
        }
    }  // end of draw()




}
