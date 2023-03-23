

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JPanel;


public class Board extends JPanel implements MouseMotionListener, MouseListener, ActionListener{

	/**
	 * This thread is supposed to render the frame to screen specified by the frame
	 * rate.
	 */
	private Thread renderThread;

	/**
	 * This thread is responsible for updating the screne (e.g., position of actors
	 * on screen) in specified intervals.
	 */
	private Thread animationThread;

	/**
	 * This thread is responsble for continously generating opponents that enter the
	 * stage from east.
	 */
	private Thread opponentGeneratorThread;

	/**
	 * This thread is responsible for simulating continues fire when the mouse
	 * button is held down.
	 */
	private Thread shotRepetitionThread;

	/**
	 * This variable indicates whether the player is in continued fire mode or not.
	 * Check when and where this flag is set to true in the code.
	 */
	private volatile boolean fire = false;

	/**
	 * The frame rate at which the scene is rendered onto screen. If you don't know
	 * what a frame rate is google for it. This example here would indicate 60 fps
	 * (frames per second).
	 */
	private static final int FRAME_RATE = 1000 / 60;

	/**
	 * Interval used in the animationThread.
	 */
	private static final int ANIMATION_INTERVAL = 10;

	/**
	 * Interval used in the opponentGeneratorThread.
	 */
	private static final int OPPONENT_INTERVALL = 200;

	/**
	 * Interval used in the shotRepetitionThread.
	 */
	private static final int SHOT_REPETITON_INTERVAL = 200;

	/**
	 * Size of opponents.
	 */
	private static final int OPPONENT_SIZE = 40;
	
	/**
	 * speed of the opponent
	 */
	private static final int OPPONENT_SPEED = 2;
	
	/**
	 * defines how many hits the opponent can take
	 */
	private static final int OPPONENT_LIFE = 3;
	
	/**
	 * for later implementation, useful for leveling
	 */
	private static final int OPPONENT_AMOUNT = 100;
	
	/**
	 * Size of shots.
	 */
	private static final int SHOT_SIZE = 5;

	/**
	 * Size of the player.
	 */
	private static final int PLAYER_SIZE = 40;

	/**
	 * List that stores all opponents on screen.
	 */
	private List<Opponent> opponents = new ArrayList<Opponent>();

	/**
	 * List that stores all opponents on screen.
	 */
	private List<Shot> shots = new ArrayList<Shot>();

	/**
	 * The player object.
	 */
	private Player player;
	
	/**
	 * defines how many hits a player can take divided by 10
	 */
	private static int PLAYER_LIFE = 100;
	
	/**
	 * button for resetting the game
	 */
	JButton newGameButton;
	
	/**
	 * defines the severity of the mouse/player lag
	 */
	private static final int LAG = 10;
	
	/**
	 * variable for the highscore
	 */
	int highScore = 0;
	

	/**
	 * Variables used to pass around the values of the current mouse position.
	 * Useful for implementing the mouse lag.
	 */
	private int mouseX = 0, mouseY = 0;

	public Board() {

		player = new Player(100, getHeight() / 2, PLAYER_SIZE, PLAYER_SIZE);

		addMouseMotionListener(this);

		addMouseListener(this);
	}
	
	
	/**
	 * method for resetting the game
	 */
	public void resetGame() {
		PLAYER_LIFE = 100;
		highScore = 0;
		opponents.removeAll(opponents);
		removeAll();
		repaint();
	    
	}

	/**
	 * Initialize all threads and start them.
	 */
	public void init() {
		opponentGeneratorThread = new Thread(new Runnable() {

			@Override
			public void run() {

				Random r = new Random();

				while (true) {

					opponents.add(new Opponent(getWidth(), r.nextInt(0, getHeight()), OPPONENT_SIZE, OPPONENT_SIZE, OPPONENT_SPEED, OPPONENT_LIFE));

					try {
						Thread.sleep(OPPONENT_INTERVALL);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		});
		opponentGeneratorThread.start();

		animationThread = new Thread(new Runnable() {

			@Override
			public void run() {

				while (true) {
					animate();
					try {
						Thread.sleep(ANIMATION_INTERVAL);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		});
		animationThread.start();

		renderThread = new Thread(new Runnable() {

			@Override
			public void run() {

				while (true) {
					repaint();
					try {
						Thread.sleep(FRAME_RATE);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		});
		renderThread.start();

		shotRepetitionThread = new Thread(new Runnable() {

			@Override
			public void run() {

				while (true) {

					if (fire) {

						shots.add(
								new Shot(player.x + player.width, player.y + player.height / 2, SHOT_SIZE, SHOT_SIZE));
						try {
							Thread.sleep(SHOT_REPETITON_INTERVAL);
						} catch (InterruptedException e) {

							e.printStackTrace();
						}
					}
				}

			}
		});
		shotRepetitionThread.start();
	}

	/**
	 * This method is automatically called when the scene should be rendered to
	 * screen. The method gets triggered by the repaint method. Use the search
	 * function to find the method invocation in this class.
	 */
	@Override
	public void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		
//		Background image is applied here
		Image backgroundImage = null;
		try {
		    backgroundImage = ImageIO.read(new File("src/images/space.jpg"));
		} catch (IOException e) {
		    e.printStackTrace();
		}
		g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
		
//		HIGHSCORE is painted here
		g.setColor(Color.white);
		g.setFont(new Font("ARIAL", Font.BOLD, 20));
		g.drawString("Highscore: " + highScore, getWidth() / 2 - 50, 20);
		

		// This creates a temporary clone of the opponents list. This
		// is one approach (maybe not ideal but good enough for this example) to avoid
		// the problem of having to iterate over a list while
		// other threads might try to manipulate the same list at the same time (for
		// example when an opponent is drawn to screen, but another thread tries to
		// remove the same opponent from the list because it mgiht leave the stage or
		// because it collides with
		// a shot).
		List<Opponent> opponentsCopy = new CopyOnWriteArrayList<>(opponents);

		// Same here as above.
		List<Shot> shotsCopy = new CopyOnWriteArrayList<>(shots);

		// Use the Graphics object to change the brush to red.
		g.setColor(Color.RED);
		for (Opponent o : opponentsCopy) {
			if (o != null) {
				// Paint to to the board.
				g.setColor(Color.red);
				g.fillOval(o.x, o.y, o.width, o.height);
				g.setColor(Color.blue);
				g.fillOval(o.x + (o.width/4) , o.y+ (o.height/4), o.width / 2, o.height / 2);
			}
		}
		
//		SPACESHIP coloring
		g.setColor(Color.green);
		//body
		g.fillRect(player.x - (PLAYER_SIZE / 2), player.y - (PLAYER_SIZE / 2), player.width, player.height);
		//wings
		g.setColor(Color.yellow);
		g.fillRect(player.x - (PLAYER_SIZE - 10), player.y - (PLAYER_SIZE /2), player.width , player.height / 4);
		g.fillRect(player.x - (PLAYER_SIZE - 10), player.y + (PLAYER_SIZE / 4), player.width , player.height / 4);
//		canon
		g.setColor(Color.red);
		g.fillOval(player.x + (PLAYER_SIZE / 2) - 5, player.y - 5, 10, 10);

//		SHOTS coloring
		for (Shot s : shotsCopy) {
			if (s != null) {
				g.setColor(Color.orange);
				g.fillRect(s.x, s.y - (PLAYER_SIZE / 2), s.width * 4, s.height);
			}
		}
		
//		painting the life bar of the player 
		g.setColor(Color.red);
		g.fillRect(10, 10  , 100, 10);
		g.setColor(Color.green);
		g.fillRect(10, 10  , PLAYER_LIFE, 10);
		
//		GAME OVER
		if(PLAYER_LIFE < 1) {
//			painting the game over text
			g.setColor(Color.red);
			g.setFont(new Font("ARIAL", Font.BOLD, 150));
			g.drawString("Game Over", 500, 400);
			
			
//			painting the player life bar completely red after game over
			g.setColor(Color.red);
			g.fillRect(10, 10  , 100, 10);
			
//			the new game button is added here
			newGameButton = new JButton();
			newGameButton.setText("Neues Spiel");
			newGameButton.setSize(200, 50);
			newGameButton.setBackground(Color.blue);
			newGameButton.setForeground(Color.WHITE);
//			newGameButton.setLocation(400, 350);
			newGameButton.setLocation((getWidth() / 2) - 100, getHeight() / 2);
			newGameButton.addActionListener(this);
			this.add(newGameButton);
			this.setVisible(true);
		} 

	}
	
	
	public void animate() {

		// Same motivation as above.
		List<Opponent> opponentsCopy = new CopyOnWriteArrayList<>(opponents);
		List<Shot> shotsCopy = new CopyOnWriteArrayList<>(shots);

		// Animiate opponents
		for (Opponent o : opponentsCopy) {
			if (o != null) {
				o.move();
				if (o.x < 0) {
					// Remove opponents when they leave the screen area.
					opponents.remove(o);
				}
			}
		}

		// Animate shots
		for (Shot s : shotsCopy) {
			if (s != null) {
				s.move();
				if (s.x > getWidth()) {
					// Remove shots if they leave the stage.
					shots.remove(s);
				}
			}
		}
		
		// Collision detection for shots and opponents
		for (Opponent o : opponentsCopy) {
			for (Shot s : shotsCopy) {

				if (o != null && s != null) {
					if (s.x >= o.x && s.y - (PLAYER_SIZE / 2) >= o.y && s.y <= o.y + (o.height *1.5)) {
						o.life -= 1;
						shots.remove(s);
						if (o.life <= 0) {
							opponents.remove(o);
							highScore += 1;
						}
					}
				}
			}
		}
		
		// collision detection between opponent and player
		for (Opponent o : opponentsCopy) {
			//player.x >= o.x && player.y >= o.y && player.y <= o.y + o.height * (PLAYER_SIZE / 10)&& player.y <= o.y
			if (CollisionDetection.collisionDetector(player.x, player.y, PLAYER_SIZE, o.x, o.y, OPPONENT_SIZE))  {
				// Remove both opponent and shot, when they collide with each other.
				opponents.remove(o);
				PLAYER_LIFE -= 10;
				System.out.println("Player hit!");
			}
		}
	}
	
	/**
	 * Do your own research to find out when this method has to be implemented.
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
//		player.x = e.getX();
//		player.y = e.getY();
		
//		LAG implementation
		mouseX = e.getX();
        mouseY = e.getY();
        int dx = (mouseX - player.x) / LAG;
        int dy = (mouseY - player.y) / LAG;
        player.move(dx, dy);
	}

	/**
	 * Do your own research to find out when this method has to be implemented.
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO: Here you should store the current mouse position to mouseX and mouseY
		// (instead of player.x and player.y), in order for the mouse lag to work as
		// expected. If you have another way to
		// implement this functionality, feel free to do so.
//		player.x = e.getX();
//		player.y = e.getY();
		
//		LAG implementation
		mouseX = e.getX();
        mouseY = e.getY();
        int dx = (mouseX - player.x) / LAG;
        int dy = (mouseY - player.y) / LAG;
        player.move(dx, dy);
        
	}

	/**
	 * Do your own research to find out when this method has to be implemented.
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	/**
	 * Do your own research to find out when this method has to be implemented.
	 */
	@Override
	public void mousePressed(MouseEvent e) {
//		player.x = e.getX();
//		player.y = e.getY();
		
//		LAG implementation
		mouseX = e.getX();
        mouseY = e.getY();
        int dx = (mouseX - player.x) / LAG;
        int dy = (mouseY - player.y) / LAG;
        player.move(dx, dy);
        
     // Setting continues fire to true;
		fire = true;
	}

	/**
	 * Do your own research to find out when this method has to be implemented.
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		fire = false;
	}

	/**
	 * Do your own research to find out when this method has to be implemented.
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	/**
	 * Do your own research to find out when this method has to be implemented.
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		resetGame();
	}
}
	


