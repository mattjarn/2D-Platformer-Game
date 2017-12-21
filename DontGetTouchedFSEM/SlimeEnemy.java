package DontGetTouchedFSEM;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

public class SlimeEnemy extends Enemy {

    private BufferedImage[] slimeMovingSprites;
    private BufferedImage[] fallingSprites;

    public SlimeEnemy(TileMap tm, int pW, int pH, int x, int y)
    {
        this.tm = tm;
        tileSize = tm.getTileSize();
        pWidth = pW; pHeight = pH;

        width = tileSize;
        height = tileSize;
        sheetWidth = 20;
        sheetHeight = 20;

        moveSpeed = tm.getTileSize() / 24.8;
        maxSpeed = tm.getTileSize() / 18.6;
        maxFallingSpeed = 12;
        stopSpeed = tm.getTileSize() / 26.8;
        gravity = tm.getTileSize() / 24.4;

        dy = 1.2;
        dx = 1.2;
        xPos = x;
        yPos = y;
        left = true;
        right = false;

        try{
            fallingSprites = new BufferedImage[1];
            slimeMovingSprites = new BufferedImage[3];

            BufferedImage image = ImageIO.read(getClass().getResource("/DontGetTouchedFSEM/Resources/Sprites/spritesheets.png"));

            fallingSprites[0] = image.getSubimage( spriteSheetCalc(15), spriteSheetCalc(12), sheetWidth, sheetHeight);

            for (int i = 0; i < slimeMovingSprites.length; i++) {
                slimeMovingSprites[i] = image.getSubimage(spriteSheetCalc(i+13), spriteSheetCalc(12), sheetWidth, sheetHeight);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        animation = new Animation();
        animation.setFrames(slimeMovingSprites);
        animation.setDelay(10);
        facingLeft = false;

    } // end of SlimeEnemy()

    private void setAnimation(int i, BufferedImage[] bi, int d) {
        currentAnimation = i;
        animation.setFrames(bi);
        animation.setDelay(d);
    }

    public void update() {

        // sprite animation
        if(left || right) {
            animation.setFrames(slimeMovingSprites);
            animation.setDelay(10);
        }
        if(dy > 0) {
            animation.setFrames(fallingSprites);
            animation.setDelay(-1);
        }
        animation.update();

        if(dx < 0) {
            facingLeft = true;
        }
        if(dx > 0) {
            facingLeft = false;
        }

        super.update();}



    // Draw enemyy slime
    public void draw(Graphics2D g) {
        super.draw(g);
    }
}

