import javax.swing.*;

// メインのウィンドウ作成クラス
public class MainWindow extends JFrame {
	// メインウィンドウ作成
	public MainWindow(){
		setTitle(Setting.MAIN_TITLE);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(Setting.MAIN_W, Setting.MAIN_H);

		getContentPane().add(new MainDraw());
		addKeyListener(Setting.keyUtil);
		addMouseListener(Setting.keyUtil);
		addMouseMotionListener(Setting.keyUtil);

		setVisible(true);
	}
}
