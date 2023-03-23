
/**
 * Class for modelling opponents.
 * 
 * @author markus
 *
 */
public class Opponent extends Base{

	/**
	 * The speed at which opponents move through the screen.
	 */
	int speed;
	int life;

	public Opponent(int x, int y, int width, int height, int speed, int life) {
		super(x, y, width, height);
		this.speed = speed;
		this.life = life;
	}

	/**
	 * Moves the position of a particular opponent.
	 */
	public void move() {
		x -= speed;
	}
}
