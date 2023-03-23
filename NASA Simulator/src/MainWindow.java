

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;

import javax.swing.JFrame;

/**
 * The main windows of the application. The main window itself is actually
 * represented by the JFrame. The structure of this class was created by the GUI
 * builder. You can revamp this structure in order to implement your singleton
 * structure and to start your game in a separate main class.
 * 
 */
public class MainWindow {
	
	private static volatile MainWindow instance;
	private JFrame frame;

	
//	Private constructor to prevent external instantiation.
	private MainWindow() {
		initialize();
	}

	
//	Returns the singleton instance of MainWindow.
//	@return MainWindow instance
	public static MainWindow getInstance() {
		if (instance == null) {
			synchronized (MainWindow.class) {
				if (instance == null) {
					instance = new MainWindow();
				}
			}
		}
		return instance;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
//		frame.setBounds(100, 100, 1000, 600);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setSize(screenSize.width - 100, screenSize.height - 100);
		frame.setTitle("Nasa Simulator");
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	
//	Activates the MainWindow.
	public void activate() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					// Instance of the board.
					Board board = new Board();

					// Attach the board to the frame (the actual Swing main window)
					frame.setContentPane(board);

					// Make the window visible
					frame.setVisible(true);

					// Initialise the board (e.g., init all threads and start them, etc.)
					board.init();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
