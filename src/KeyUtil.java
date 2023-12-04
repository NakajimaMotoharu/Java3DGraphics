import java.awt.*;
import java.awt.event.*;

// ユーザーの操作を受け付けるクラス
public class KeyUtil implements KeyListener, MouseListener, MouseMotionListener {
	private boolean keyW = false;
	private boolean keyA = false;
	private boolean keyS = false;
	private boolean keyD = false;
	private boolean keyI = false;
	private boolean keyJ = false;
	private boolean keyK = false;
	private boolean keyL = false;
	private boolean keySP = false;
	private boolean keyCTRL = false;
	private boolean keyE = false;

	private boolean keyEConnect = false;

	private int defX = Setting.MAIN_W / 2, defY = Setting.MAIN_H / 2;

	// コンストラクタ(動作なし)
	public KeyUtil() {

	}

	// キーがタイプされた時に呼び出し
	@Override
	public void keyTyped(KeyEvent e) {

	}

	// キーが押されたときに呼び出し
	@Override
	public void keyPressed(KeyEvent e) {
		keySwitch(e, true);
	}

	// キーが離されたときに呼び出し
	@Override
	public void keyReleased(KeyEvent e) {
		keySwitch(e, false);
	}

	// キーフラグの操作
	private void keySwitch(KeyEvent e, boolean set) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_W -> keyW = set;
			case KeyEvent.VK_A -> keyA = set;
			case KeyEvent.VK_S -> keyS = set;
			case KeyEvent.VK_D -> keyD = set;
			case KeyEvent.VK_I -> keyI = set;
			case KeyEvent.VK_J -> keyJ = set;
			case KeyEvent.VK_K -> keyK = set;
			case KeyEvent.VK_L -> keyL = set;
			case KeyEvent.VK_E -> keyE = set;
			case KeyEvent.VK_SPACE -> keySP = set;
			case KeyEvent.VK_CONTROL -> keyCTRL = set;

			case KeyEvent.VK_Q -> System.exit(0);
		}
	}

	// キーフラグを元に操作を反映
	public void action() {
		double view = 1, move = 0.1;
		double rad;

		defX = Setting.MAIN_W / 2 + Setting.mainWindow.getLocation().x;
		defY = Setting.MAIN_H / 2 + Setting.mainWindow.getLocation().y;

		if (keyW && !keyS) {
			rad = CalcMatrix.arc2rad(Setting.theta);
			Setting.x = Setting.x - move * Math.sin(rad);
			Setting.z = Setting.z + move * Math.cos(rad);
		}
		if (keyA && !keyD) {
			rad = CalcMatrix.arc2rad(Setting.theta);
			rad = CalcMatrix.cTheta(rad + Math.PI / 2);
			Setting.x = Setting.x - move * Math.sin(rad);
			Setting.z = Setting.z + move * Math.cos(rad);
		}
		if (keyS && !keyW) {
			rad = CalcMatrix.arc2rad(Setting.theta);
			rad = CalcMatrix.cTheta(rad + Math.PI);
			Setting.x = Setting.x - move * Math.sin(rad);
			Setting.z = Setting.z + move * Math.cos(rad);
		}
		if (keyD && !keyA) {
			rad = CalcMatrix.arc2rad(Setting.theta);
			rad = CalcMatrix.cTheta(rad + 3 * Math.PI / 2);
			Setting.x = Setting.x - move * Math.sin(rad);
			Setting.z = Setting.z + move * Math.cos(rad);
		}

		if (keySP && !keyCTRL) {
			Setting.y = Setting.y + move;
		}
		if (keyCTRL && !keySP) {
			Setting.y = Setting.y - move;
		}

		if (keyI && !keyK) {
			Setting.angle = Setting.angle + view;
			if (Setting.angle > 89){
				Setting.angle = 89;
			}
		}
		if (keyJ && !keyL) {
			Setting.theta = Setting.theta + view;
		}
		if (keyK && !keyI) {
			Setting.angle = Setting.angle - view;
			if (Setting.angle < -89){
				Setting.angle = -89;
			}
		}
		if (keyL && !keyJ) {
			Setting.theta = Setting.theta - view;
		}

		if (keyE && !keyEConnect) {
			keyEConnect = true;
			setDefaultPosition();
			if (!Setting.mouseEnabled){
				Setting.mainWindow.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
			} else {
				Setting.mainWindow.setCursor(Cursor.getDefaultCursor());
			}
			Setting.mouseEnabled = !Setting.mouseEnabled;
		}
		if (keyEConnect && !keyE) {
			keyEConnect = false;
		}
	}

	// WASDキーの文字列出力
	@Override
	public String toString() {
		return String.format("%s, %s, %s, %s", keyW, keyA, keyS, keyD);
	}

	// マウスクリック時呼び出し
	@Override
	public void mouseClicked(MouseEvent e) {

	}

	// マウスボタンが押されたとき呼び出し
	@Override
	public void mousePressed(MouseEvent e) {

	}

	// マウスボタンが離されたとき呼び出し
	@Override
	public void mouseReleased(MouseEvent e) {

	}

	// マウスが画面内に入ったとき呼び出し
	@Override
	public void mouseEntered(MouseEvent e) {

	}

	// マウスが画面外に出たとき呼び出し
	@Override
	public void mouseExited(MouseEvent e) {

	}

	// マウスがドラッグされたときに呼び出し
	@Override
	public void mouseDragged(MouseEvent e) {

	}

	// マウスが動かされた時呼び出し
	@Override
	public void mouseMoved(MouseEvent e) {
		if (Setting.mouseEnabled) {

			int moveX = e.getXOnScreen() - defX;
			int moveY = defY - e.getYOnScreen();

			Setting.angle = Setting.angle + moveY * 0.3;
			Setting.theta = Setting.theta - moveX * 0.3;

			if (Setting.angle > 89){
				Setting.angle = 89;
			} else if (Setting.angle < -89){
				Setting.angle = -89;
			}

			setDefaultPosition();
		}
	}

	// マウス位置を定位置に移動
	private void setDefaultPosition() {
		try {
			Robot robot = new Robot();
			robot.mouseMove(defX, defY);
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
}
