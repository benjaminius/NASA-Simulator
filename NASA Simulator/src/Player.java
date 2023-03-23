
public class Player extends Base{

	public Player(int x, int y, int width, int height) {
		super(x, y, width, height);
		// TODO Auto-generated constructor stub
	}
	
	public void move(int dX, int dY) {
		this.x += dX;
		this.y += dY;
	}

}
