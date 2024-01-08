import javax.swing.*;
import java.awt.*;

// 画面の描画領域を扱うクラス
public class MainDraw extends JPanel {
	private long lastTime = -1;

	// メイン描画メソッド
	@Override
	protected void paintComponent(Graphics g) {
		// timer(t1, t2, t3)
		long t1 = System.currentTimeMillis();
		paintDisplay(g);
		g.setColor(Color.GREEN);
		g.drawString(String.format("fps = %f", Setting.fps), 10, 10);
		g.drawString(String.format("theta = %f, angle = %f", Setting.theta, Setting.angle), 10, 20);
		g.drawString(String.format("(x, y, z) = (%f, %f, %f)", Setting.x, Setting.y, Setting.z), 10, 30);
		g.drawString(String.format("MouseEnabled = %s", Setting.mouseEnabled), 10, 40);
		g.drawString(String.format("drawDelay = %d", lastTime), 10, 50);
		g.drawString(String.format("calcDelay1 = %d", Setting.mainCtrlThreadFirstTime), 10, 60);
		g.drawString(String.format("calcDelay2 = %d", Setting.mainCtrlThreadLastTime), 10, 70);
		long t3 = System.currentTimeMillis();
		lastTime = t3 - t1;
	}

	// Setting.displayを描画
	private static void paintDisplay(Graphics g){
		/* Settingに保存されているdispImgを直接描画 ほぼノータイム */
		g.drawImage(Setting.dispImg, 0, 0, null);
	}
}
