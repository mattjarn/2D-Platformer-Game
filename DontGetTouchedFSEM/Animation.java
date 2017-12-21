package DontGetTouchedFSEM;
// Matt Jarnevic
// Receives an array of images and stores them in frames along with
// a delay to loop through the frames every so often

import java.awt.image.BufferedImage;

public class Animation {
	
	private BufferedImage[] frames;
	private int currentFrame;

	private long startTime;
	private long delay;
	
	public Animation() {}

	/**
	 * Pass in the images to be stored in the frames which can then be drawn.
	 * This handles a lot of cases such as for enemies, coins, and the player
	 * @param images
	 */
	public void setFrames(BufferedImage[] images) {
		frames = images;
		if (currentFrame >= frames.length) currentFrame = 0;
	}

	/**
	 * set the delay for when to switch to the next frame
	 * @param i
	 */
	public void setDelay(long i) { delay = i; }

	/**
	 * Updates the frames if the delay has been reached.
	 * If the delay is -1, it means there is only one frame,
	 * and there is no reason to update it
	 */
	public void update() {
		
		if(delay == -1) return;

		long elapsed = (System.nanoTime() - startTime) / 10000000;
		if(elapsed > delay) {
			currentFrame++;
			startTime = System.nanoTime();
		}
		if(currentFrame == frames.length) {
			currentFrame = 0;
		}
	}

	/**
	 * Returns the current image in the frames for drawing typically
	 * @return
	 */
	public BufferedImage getImage() { return frames[currentFrame]; }
	
}