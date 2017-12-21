package DontGetTouchedFSEM.Main;
// TileGame.java
// Matt Jarnevic 9/26/2017
//1366 by 768

/* Enemies move left and right and the user must
 avoid the enemies. If an enemy touches the player
 he loses a life. Currently the player only has one life
 and dies if he gets touched, so dont get touched.

 The game, rather than ending, will respawn the player at the start.
 The only way to end it is to get to the goal.
 Or, if you are a loser, you can press escape or quit....

 A score is displayed on screen at the end, saying how
 much time you managed to survive or if you beat it, how long
 it took to beat it.

 Make sure to collect as many pumpkins as you can

 -------------

 Uses full-screen exclusive mode, active rendering,
 and double buffering/page flipping.

 On-screen pause and quit buttons.

 Using Java 3D's timer: J3DTimer.getValue()
 *  nanosecs rather than millisecs for the period

 Located in /DontGetTouchedFSEM
 */

import DontGetTouchedFSEM.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class TileGame extends GameFrame {

	private static final long serialVersionUID = -2450477630768116721L;

	private static int DEFAULT_FPS = 100;

	private Player player; // the player

	private int score = 0;
	private Font font;
	private FontMetrics metrics;

	// used by quit 'button'
	private volatile boolean isOverQuitButton = false;
	private Rectangle quitArea;

	// used by the pause 'button'
	private volatile boolean isOverPauseButton = false;
	private Rectangle pauseArea;
	
    private DecimalFormat df = new DecimalFormat("0.##");  // 2 dp

	// the tile map that everything is on
	private TileMap tm;
	private int tileSize;

	// the checkpoint coordinates for the player if they die
	private int xCheckPoint;
	private int yCheckPoint;

	// keep track of all the enemies
	private ArrayList<Enemy> enemies;

	// keep track of all the coins
	private ArrayList<Coin> coins;

	// store and play sounds
	public static JukeBox jukeBox;

	private BufferedImage[] backgrounds;

	// for keeping track of the levels
	private int maxLevel;
	private int levelNum;

	private long deathTime;
	private boolean awaitingRespawn;


	public TileGame(long period) {
		super(period);
	}

	@Override
	protected void simpleInitialize() {
		// create game components
		passed = false;
		gameOver = false;

		levelNum = 1;
		maxLevel = 7;
		deathTime = 0;
		awaitingRespawn = false;

		tileSize = pHeight / 15;
		tm = new TileMap(pHeight, pWidth);

		backgrounds = new BufferedImage[2];
		try {
			backgrounds[0] = ImageIO.read(getClass().getResource("/DontGetTouchedFSEM/Resources/Backgrounds/skybg.png"));
			backgrounds[1] = ImageIO.read(getClass().getResource("/DontGetTouchedFSEM/Resources/Backgrounds/forestbg.png"));
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		// Load all the sounds/music into the jukebox
		jukeBox = new JukeBox();
		jukeBox.load("/DontGetTouchedFSEM/Resources/Music/bgmusic2.wav", "bgmusic");
		jukeBox.load("/DontGetTouchedFSEM/Resources/SFX/jump_01.wav", "jump");
		jukeBox.load("/DontGetTouchedFSEM/Resources/SFX/Mario_Warp_Pipe.wav", "pipeWarp");
		jukeBox.load("/DontGetTouchedFSEM/Resources/SFX/lose.wav", "lose");
		jukeBox.load("/DontGetTouchedFSEM/Resources/SFX/clapping.wav", "win");
		jukeBox.load("/DontGetTouchedFSEM/Resources/SFX/cartoonMunch.wav", "munch");
		jukeBox.setVolume("jump", -15);
		jukeBox.setVolume("pipeWarp", -5);
		jukeBox.setVolume("munch", -10);

		enemies = new ArrayList<>();

		coins = new ArrayList<>();

		player = new Player(tm, pWidth, pHeight);

		// set up message font
		font = new Font("Arial", Font.BOLD, 24);
		metrics = this.getFontMetrics(font);

		// specify screen areas for the buttons
		pauseArea = new Rectangle(pWidth - 100, pHeight - 45, 70, 15);
		quitArea = new Rectangle(pWidth - 100, pHeight - 20, 70, 15);

		tm.loadLevel("level" + levelNum + ".txt");
		tm.getPlayerCoords();
		xCheckPoint = tm.getSpawnX();
		yCheckPoint = tm.getSpawnY();
		player.setxPos(xCheckPoint);
		player.setyPos(yCheckPoint);
		getEnemies();
		getCoins();
		jukeBox.loop("bgmusic");

	}

	protected void getEnemies() {
		//get the enemies
		ArrayList<Integer> flyingEnemyCoords = tm.getCoords(3);
		if (flyingEnemyCoords.size() > 0) {
			int numOfEnemies = flyingEnemyCoords.size() / 2;
			for (int i = 0; i < numOfEnemies; i++) {
				FlyingBird enemy = new FlyingBird(tm, pWidth, pHeight, flyingEnemyCoords.get(i * 2), flyingEnemyCoords.get(i * 2 + 1));
				enemies.add(enemy);
			}
		}
		ArrayList<Integer> slimeEnemyCoords = tm.getCoords(4);
		if (slimeEnemyCoords.size() > 0) {
			int numOfEnemies = slimeEnemyCoords.size() / 2;
			for (int i = 0; i < numOfEnemies; i++) {
				SlimeEnemy enemy = new SlimeEnemy(tm, pWidth, pHeight, slimeEnemyCoords.get(i * 2), slimeEnemyCoords.get(i * 2 + 1));
				enemies.add(enemy);
			}
		}
	}

	protected void getCoins() {
		//get the coins
		ArrayList<Integer> coinCoords = tm.getCoords(6);
		if (coinCoords.size() > 0) {
			int numOfEnemies = coinCoords.size() / 2;
			for (int i = 0; i < numOfEnemies; i++) {
				Coin coin = new Coin(tm, pWidth, pHeight, coinCoords.get(i * 2), coinCoords.get(i * 2 + 1));
				coins.add(coin);
			}
		}
	}

	@Override
	protected void keyPress(int keyCode) {
		if (keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_LEFT) {
			player.setLeft(true);
		}
		else if ((keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_RIGHT)) {
			player.setRight(true);
		}
		else if ((keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_SPACE)) {
			player.setJumping(true);
		}
		else if ((keyCode == KeyEvent.VK_S || keyCode == KeyEvent.VK_DOWN)) {
			player.setDown(true);
		}
		else{
			// Do nothing
		}
	}

	@Override
	protected void keyRelease(int keyCode) {
		if (keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_LEFT) {
			player.setLeft(false);
		}
		else if ((keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_RIGHT)) {
			player.setRight(false);
		}
		else if ((keyCode == KeyEvent.VK_S || keyCode == KeyEvent.VK_DOWN)) {
			player.setDown(false);
		}
		else{
			// Do nothing
		}
	}

	@Override
	protected void mousePress(int x, int y) {
		if (isOverPauseButton)
			isPaused = !isPaused; // toggle pausing
		else if (isOverQuitButton)
			running = false;
		else {
			//do nothing
		}
	} // end of testPress()

	@Override
	protected void mouseMove(int x, int y) {
		if (running) { // stops problems with a rapid move after pressing 'quit'
			isOverPauseButton = pauseArea.contains(x, y) ? true : false;
			isOverQuitButton = quitArea.contains(x, y) ? true : false;
		}
	}

	@Override
	protected void simpleRender(Graphics gScr) {
		if (levelNum % 2 == 0) {
			gScr.setColor(Color.black);
			gScr.fillRect(0, 0, pWidth, pHeight);
		}
		else {
//			gScr.drawImage(backgrounds[0], 0, 0, pWidth, pHeight, 0, 0, 3072, 1244, null);
			gScr.drawImage(backgrounds[1], 0, 0, pWidth, pHeight,null);
		}
		tm.draw(gScr); //draw the tilemap

		gScr.setColor(Color.blue);
		gScr.setFont(font);
		gScr.setColor(Color.blue);
	    gScr.setFont(font);

	    // report frame count & average FPS and UPS at top left
		gScr.drawString("Average FPS/UPS: " + df.format(averageFPS) + ", " +
	                                df.format(averageUPS), 20, 25);  // was (10,55)
		
		// report time used and boxes used at bottom left
		gScr.drawString("Time Spent: " + timeSpentInGame + " secs", 10,
				pHeight - 15);
		gScr.drawString("Lives left: " + player.lives, pWidth - 250, 20);

		// draw the pause and quit 'buttons'
		drawButtons(gScr);

		gScr.setColor(Color.green);

		// draw game elements: the enemies and the player
		for (Enemy e : enemies) {
			e.draw(gScr);
		}
		for (Coin c : coins) {
			c.draw(gScr);
		}
		player.draw(gScr);
	} // end of simpleRender()

	private void drawButtons(Graphics g) {
		g.setColor(Color.blue);

		// draw the pause 'button'
		if (isOverPauseButton)
			g.setColor(Color.green);

		g.drawOval(pauseArea.x, pauseArea.y, pauseArea.width, pauseArea.height);
		if (isPaused)
			g.drawString("Paused", pauseArea.x, pauseArea.y + 10);
		else
			g.drawString("Pause", pauseArea.x + 5, pauseArea.y + 10);

		if (isOverPauseButton)
			g.setColor(Color.blue);

		// draw the quit 'button'
		if (isOverQuitButton)
			g.setColor(Color.green);

		g.drawOval(quitArea.x, quitArea.y, quitArea.width, quitArea.height);
		g.drawString("Quit", quitArea.x + 15, quitArea.y + 10);

		if (isOverQuitButton)
			g.setColor(Color.blue);
	} // drawButtons()

	@Override
	protected void gameOverMessage(Graphics g)
	// center the game-over message in the panel
	{
		String msg;

		msg = "Congrats! You beat the game in " + score + " seconds";

		int x = (pWidth - metrics.stringWidth(msg)) / 2;
		int y = (pHeight - metrics.getHeight()) / 2;
		g.setColor(Color.blue);
		g.setFont(font);
		g.drawString(msg, x, y);
	} // end of gameOverMessage()

	@Override
	protected void levelPassedMessage(Graphics g)
	// center the level-passed message in the panel
	{
		String msg = "Congrats, you beat the game in: " + score + " seconds";
		int x = (pWidth - metrics.stringWidth(msg)) / 2;
		int y = (pHeight - metrics.getHeight()) / 2;
		g.setColor(Color.green);
		g.setFont(font);
		g.drawString(msg, x, y);
	} // end of levelPassedMessage()


	@Override
	protected void loadNextLevel() {
		levelNum+=1;
		if(levelNum > maxLevel){ //the last level is this level
			levelNum+=1;
			jukeBox.stop("bgmusic");
			jukeBox.play("win");
			passed = false;
			score = (timeSpentInGame);
			gameOver = true;
			return;
		}
		tm.loadLevel("level" + levelNum + ".txt");
		tm.getPlayerCoords();
		xCheckPoint = tm.getSpawnX();
		yCheckPoint = tm.getSpawnY();
		player.setxPos(xCheckPoint);
		player.setyPos(yCheckPoint);
		enemies.clear();
		//get the enemies
		getEnemies();
		coins.clear();
		getCoins();
	}

	@Override
	protected void simpleUpdate() {
		if(!awaitingRespawn) {
			tm.update();
			player.update();
			checkGameOver();
			for (Enemy e : enemies) {
				e.update();
				checkCollision(player, e);
			}
			for (Coin c : coins) {
				c.update();
				collectCoins(player, c);
			}
			passed = player.checkWin();
		}
		if (awaitingRespawn){
			long currentTime = System.nanoTime()/1000000000L;
			player.update();
			if(deathTime+2 < currentTime){
				awaitingRespawn = false;
				player.setRespawn(false);
				resetLevel();
			}
		}
	}

	// checks to see if any of the enemies touch the player
	protected void checkCollision(Player p, Enemy e) {
		Rectangle playerRect = new Rectangle((int) p.getxPos(), (int) p.getyPos(), p.getWidth(), p.getHeight());
		Rectangle enemyRect = new Rectangle((int) e.getxPos() + 4, (int) e.getyPos(), e.getWidth() - 4, e.getHeight()-6);

		if( playerRect.intersects(enemyRect) )
			p.reduceLives();
	}

	/**
	 * Accepts a player and a coin and checks if they intersect, if they do
	 * it sets the coin to be collected and plays a sound
	 * @param p
	 * @param c
	 */
	protected void collectCoins(Player p, Coin c) {
		Rectangle playerRect = new Rectangle((int) p.getxPos(), (int) p.getyPos(), p.getWidth(), p.getHeight());
		Rectangle coinRect = new Rectangle((int) c.getxPos() + (tileSize/2), (int) c.getyPos() + (tileSize/2), c.getWidth(), c.getHeight());

		if( playerRect.intersects(coinRect) ) {
			if (c.isUncollected()) {
				jukeBox.play("munch");
				c.setCollected();
			}
		}
	}

	// checks to see if the player has any lives left then calculates the score
	protected void checkGameOver() {
		if (player.isDead()) {
			jukeBox.stop("bgmusic");
			jukeBox.play("lose");
			deathTime = System.nanoTime()/1000000000L;
			player.setRespawn(true);
			awaitingRespawn = true;
			player.setDead(false);
		}
	}

	/**
	 * Resets the level back to what it was when the player first spawned in
	 */
	private void resetLevel() {
		player.setLives(1);
		player.setxPos(xCheckPoint);
		player.setyPos(yCheckPoint);
		for (Coin c : coins) {
			c.setUncollected(true);
		}
		jukeBox.resumeLoop("bgmusic");
	}


	public static void main(String args[]) {
		int fps = DEFAULT_FPS;
		if (args.length != 0)
			fps = Integer.parseInt(args[0]);

		long period = (long) 1000.0 / fps;
		System.out.println("fps: " + fps + "; period: " + period + " ms");
		new TileGame(period * 1000000L); // ms --> nanosecs
	} // end of main()

} // end of TileGame class

