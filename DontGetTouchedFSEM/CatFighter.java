package DontGetTouchedFSEM;
/*
    All Sprite Credit goes to https://opengameart.org/content/cat-fighter-sprite-sheet
 */

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

public class CatFighter extends Player {

    private BufferedImage[] catIdleSprites;
    private BufferedImage[] catWalkingSprites;
    private BufferedImage[] catJumpingSprites;
    private BufferedImage[] catFallingSprites;
    private BufferedImage[] catDownSprites;
    private BufferedImage[] catDyingSprites;

    public CatFighter(TileMap tm, int pW, int pH)
    {
        this.tm = tm;
        width = (int) (tm.getTileSize() * 6/10.);
        height = (int) (tm.getTileSize() * 6/10.);
        sheetWidth = 22;
        sheetHeight = 22;

        moveSpeed = tm.getTileSize() / 17.0;
        maxSpeed = tm.getTileSize() / 13.2;
        maxFallingSpeed = 12;
        stopSpeed = tm.getTileSize() / 26.8;
        jumpStart = -tm.getTileSize() / 3.6;
        gravity = tm.getTileSize() / 86.4;

        pWidth = pW;
        pHeight = pH;
        xPos = pWidth/2;
        yPos = pHeight/2;

        lives = 1;

        try {

            catIdleSprites = new BufferedImage[4];
            catJumpingSprites = new BufferedImage[1];
            catFallingSprites = new BufferedImage[1];
            catWalkingSprites = new BufferedImage[8];
            catDownSprites = new BufferedImage[1];
            catDyingSprites = new BufferedImage[6];

            BufferedImage cats = ImageIO.read(getClass().getResource("/DontGetTouchedFSEM/Resources/Sprites/cat_sprites.png"));
            for (int i = 0; i < catIdleSprites.length; i++) {
                catIdleSprites[i] = cats.getSubimage((20 + 64 * i), 24, 20, 30);
            }
            for (int i = 0; i < catJumpingSprites.length; i++) {
                catJumpingSprites[i] = cats.getSubimage((20 + 64 * 3), 24 + 64 * 2, 20, 30);
            }
            for (int i = 0; i < catFallingSprites.length; i++) {
                catFallingSprites[i] = cats.getSubimage((20 + 64 * 4), 24 + 64 * 2, 20, 30);
            }
            for (int i = 0; i < catWalkingSprites.length; i++) {
                catWalkingSprites[i] = cats.getSubimage((20 + 64 * i), 24 + 64, 20, 30);
            }
            for (int i = 0; i < catDownSprites.length; i++) {
                catDownSprites[i] = cats.getSubimage((20 + 64 * 2), 24 + 64 * 5, 20, 30);
            }
            for (int i = 0; i < catDyingSprites.length; i++) {
                catDyingSprites[i] = cats.getSubimage((20 + 64 * i), 24 + 64 * 4, 20, 30);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        animation = new Animation();
        animation.setFrames(catWalkingSprites);
        animation.setDelay(10);
        facingLeft = false;

    } // end of catFighter()

    public void update() {

        // sprite animation
        if(left || right) {
            animation.setFrames(catWalkingSprites);
            animation.setDelay(5);
        }
        else {
            animation.setFrames(catIdleSprites);
            animation.setDelay(10);
        }
        if(dy < 0) {
            animation.setFrames(catJumpingSprites);
            animation.setDelay(-1);
        }
        if(dy > 0) {
            animation.setFrames(catFallingSprites);
            animation.setDelay(-1);
        }
        if(down) {
            animation.setFrames(catDownSprites);
            animation.setDelay(-1);
        }
        if(dead) {
            animation.setFrames(catDyingSprites);
            animation.setDelay(30);
        }
        animation.update();

        if(dx < 0) {
            facingLeft = true;
        }
        if(dx > 0) {
            facingLeft = false;
        }

    }

    // Draw player cat fighter
    public void draw(Graphics2D g) {
        super.draw(g);
    }
}


