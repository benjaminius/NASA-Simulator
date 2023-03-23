
public class CollisionDetection {
	
	
	public static boolean collisionDetector(int playerX, int playerY, int playerSize, int opponentX, int opponentY, int opponentSize) {
		playerX = playerX - (playerSize / 2);
		playerY = playerY - (playerSize / 2);
		boolean hit = false;

		double sumRadii = (playerSize + opponentSize) / 2.0;

		// checks if the distance between the player and the enemy is less than the sum of the radius
		if (Math.sqrt(Math.pow(playerX - opponentX, 2) + Math.pow(playerY - opponentY, 2)) < sumRadii) {
		    hit = true;
		}
		return hit;
		
	}
}
