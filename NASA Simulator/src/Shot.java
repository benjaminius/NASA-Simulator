

/**
 * Class for modelling a shot.
 * 
 * @author markus
 *
 */
public class Shot extends Base{

	/**
	 * Speed of a shot as it travels through the screen.
	 */
	int speed = 4;

	public Shot(int x, int y, int width, int height) {
		super(x, y, width, height);
	}

	/**
	 * Called to change the position of the shot.
	 */
	public void move() {
		x += speed;
	}

}
