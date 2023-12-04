import javax.swing.*;
import java.awt.*;

// 画面の描画領域を扱うクラス
public class MainDraw extends JPanel {
	// メイン描画メソッド
	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(Color.RED);
		for (int i = 0; i < Setting.displayMatrix.size(); i = i + 1){
			paintPolygon(Setting.displayMatrix.get(i), g);

		}

		g.setColor(Color.BLACK);
		g.drawString(String.format("fps = %f", Setting.fps), 10, 10);
		g.drawString(String.format("theta = %f, angle = %f", Setting.theta, Setting.angle), 10, 20);
		g.drawString(String.format("(x, y, z) = (%f, %f, %f)", Setting.x, Setting.y, Setting.z), 10, 30);
		g.drawString(String.format("MouseEnabled = %s", Setting.mouseEnabled), 10, 40);

		Setting.frameCount = Setting.frameCount + 1;
	}

	// DisplayMatrixを描画
	private static void paintPolygon(DisplayMatrix displayMatrix, Graphics g){
		if (displayMatrix.getLoop()){
			paintLineLoop(displayMatrix, g);
		} else {
			paintLineNonLoop(displayMatrix, g);
		}
	}

	// 指定された2点を結ぶ線分の描画
	private static void paintLineDouble(double x1, double y1, double x2, double y2, Graphics g){
		g.drawLine(moveX(x1), moveY(y1), moveX(x2), moveY(y2));
	}

	// DisplayMatrixを描画(始点と終点を結ばない)
	private static void paintLineNonLoop(DisplayMatrix displayMatrix, Graphics g){
		for (int i = 1; i < displayMatrix.getSize(); i = i + 1){
			double[] t1 = displayMatrix.getData(i - 1);
			double[] t2 = displayMatrix.getData(i);
			paintLineDouble(t1[0], t1[1], t2[0], t2[1], g);
		}
	}

	// DisplayMatrixを描画(始点と終点を結ぶ)
	private static void paintLineLoop(DisplayMatrix displayMatrix, Graphics g){
		paintLineNonLoop(displayMatrix, g);
		double[] t1 = displayMatrix.getData(displayMatrix.getSize() - 1);
		double[] t2 = displayMatrix.getData(0);
		paintLineDouble(t1[0], t1[1], t2[0], t2[1], g);
	}

	// 実数をディスプレイ上の座標に変換(X)
	private static int moveX(double x){
		x = x * 100;
		x = x + ((double) Setting.MAIN_W / 2);
		return (int) x;
	}

	// 実数をディスプレイ上の座標に変換(Y)
	private static int moveY(double y){
		y = y * -1;
		y = y * 100;
		y = y + ((double) Setting.MAIN_H / 2);
		return (int) y;
	}
}
