package DontGetTouchedFSEM;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

public class FlyingBird extends Enemy {

    private BufferedImage[] flyingBatSprites;
    private BufferedImage[] fallingSprites;

    public FlyingBird(TileMap tm, int pW, int pH, int x, int y)
    {
        this.tm = tm;
        tileSize = tm.getTileSize();
        pWidth = pW; pHeight = pH;

        width = (int) (tm.getTileSize() * 3/4.);
        height = (int) (tm.getTileSize() * 3/4.);
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
            flyingBatSprites = new BufferedImage[3];

            fallingSprites[0] = ImageIO.read(getClass().getResource("/DontGetTouchedFSEM/Resources/Sprites/kirbyfall.gif"));

            BufferedImage image = ImageIO.read(getClass().getResource("/DontGetTouchedFSEM/Resources/Sprites/spritesheets.png"));
            for (int i = 0; i < flyingBatSprites.length; i++) {
                flyingBatSprites[i] = image.getSubimage(
                        spriteSheetCalc(i+13),
                        spriteSheetCalc(14),
                        sheetWidth,
                        sheetHeight
                );
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        animation = new Animation();
        animation.setFrames(flyingBatSprites);
        animation.setDelay(10);
        facingLeft = false;

    } // end of FlyingBird()

    private void setAnimation(int i, BufferedImage[] bi, int d) {
        currentAnimation = i;
        animation.setFrames(bi);
        animation.setDelay(d);
    }

    public void update() {

        // sprite animation
        if(left || right) {
            animation.setFrames(flyingBatSprites);
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



    // Draw Enemy Flying Bird
    public void draw(Graphics2D g) {
        super.draw(g);
    }
}
