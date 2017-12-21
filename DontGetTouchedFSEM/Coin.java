package DontGetTouchedFSEM;
// Coin.java
// Matt Jarnevic

/* For collectible objects such as coins, but since its near Halloween,
    I used pumpkins as collectibles

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
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import java.awt.image.ShortLookupTable;
import java.util.stream.IntStream;

public class Coin extends JFrame
{
    // dimensions
    protected int width;
    protected int height;
    protected int sheetWidth;
    protected int sheetHeight;

    // position
    protected double xPos;
    protected double yPos;

    // the tile map that the coin will be on
    protected TileMap tm;
    protected int tileSize;

    // boolean for if the coin has been collected
    private boolean uncollected;

    protected int pWidth, pHeight;   // panel dimensions

    // animation
    protected Animation animation;
    protected int currentAnimation;

    protected boolean facingLeft;

    private int blocked[];

    private BufferedImage[] idleCoin;
    private BufferedImage[] collectedCoin;

    private int spacing;

    public Coin(TileMap tm, int pW, int pH, int x, int y){
        this.tm = tm;
        tileSize = tm.getTileSize();
        pWidth = pW; pHeight = pH;

        width = (int) (tm.getTileSize() * 2/3.);
        height = (int) (tm.getTileSize() * 2/3.);
        sheetWidth = 20;
        sheetHeight = 20;

        xPos = x;
        yPos = y;

        try{
            collectedCoin = new BufferedImage[1];
            idleCoin = new BufferedImage[1];

            idleCoin[0] = ImageIO.read(getClass().getResource("/DontGetTouchedFSEM/Resources/Objects/pumpkin.png"));

            BufferedImage image = ImageIO.read(getClass().getResource("/DontGetTouchedFSEM/Resources/Sprites/spritesheets.png"));
            for (int i = 0; i < collectedCoin.length; i++) {
                collectedCoin[i] = image.getSubimage(
                        spriteSheetCalc(i+26),
                        spriteSheetCalc(4),
                        sheetWidth,
                        sheetHeight
                );
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        animation = new Animation();
        animation.setFrames(idleCoin);
        animation.setDelay(10);
        uncollected = true;
    }

    protected int spriteSheetCalc(int i) {
        return (i+1)*3 + sheetWidth*i;
    }

    public boolean isUncollected() {return uncollected;}

    public void setUncollected(boolean b) {
        uncollected = b;
        try {

            idleCoin[0] = ImageIO.read(getClass().getResource("/DontGetTouchedFSEM/Resources/Objects/pumpkin.png"));
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void setCollected() {
        uncollected = false;
        spacing = 20;
    }

    public double getxPos() { return xPos; }

    public double getyPos() { return yPos; }

    public int getWidth() { return width; }

    public int getHeight() { return height; }

    public void update() {
        // coin animation
        animation.setFrames(idleCoin);
        animation.setDelay(10);
        animation.update();
    }

    public void draw(Graphics g)
    {
        int tx = tm.getX();
        int ty = tm.getY();
        if (uncollected)
            g.drawImage(animation.getImage(), (int) (tx + xPos - width / 2 + (tileSize/2)), (int) (ty + yPos - height / 2 + (tileSize/2)), width, height, null);
        else {
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
                g.drawImage(animation.getImage(), (int) (tx + xPos - width / 2 + (tileSize/2)), (int) (ty + yPos - height / 2 + (tileSize/2)), width, height, null);
            }
            spacing = spacing - 1;
        }
    }  // end of draw()

}  // end of Enemy class
