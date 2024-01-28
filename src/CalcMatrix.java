import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

// 行列変換などを行うクラス
public class CalcMatrix {
	// DisplayMatrixに対する平行移動行列の生成
	public static DisplayMatrix shift(double tx, double ty, DisplayMatrix displayMatrix){
		Matrix matrix = FormulasMatrix.shift2d(tx, ty);

		return trans(displayMatrix, matrix);
	}

	// WorldMatrixに対する平行移動行列の生成
	public static WorldMatrix shift(double tx, double ty, double tz, WorldMatrix worldMatrix){
		Matrix matrix = FormulasMatrix.shift3d(tx, ty, tz);

		return trans(worldMatrix, matrix);
	}

	// DisplayMatrixに対する拡大行列の生成
	public static DisplayMatrix zoom(double sx, double sy, DisplayMatrix displayMatrix){
		Matrix matrix = FormulasMatrix.zoom2d(sx, sy);

		return trans(displayMatrix, matrix);
	}

	// WorldMatrixに対する拡大行列の生成
	public static WorldMatrix zoom(double sx, double sy, double sz, WorldMatrix worldMatrix){
		Matrix matrix = FormulasMatrix.zoom3d(sx, sy, sz);

		return trans(worldMatrix, matrix);
	}

	// DisplayMatrixに対する回転行列の生成
	public static DisplayMatrix rotation(double theta, DisplayMatrix displayMatrix){
		Matrix matrix = FormulasMatrix.rotation2d(theta);

		return trans(displayMatrix, matrix);
	}

	// WorldMatrixに対するX軸の回転行列の生成
	public static WorldMatrix rotationX(double theta, WorldMatrix worldMatrix){
		Matrix matrix = FormulasMatrix.rotationX3d(theta);

		return trans(worldMatrix, matrix);
	}

	// WorldMatrixに対するY軸の回転行列の生成
	public static WorldMatrix rotationY(double theta, WorldMatrix worldMatrix){
		Matrix matrix = FormulasMatrix.rotationY3d(theta);

		return trans(worldMatrix, matrix);
	}

	// WorldMatrixに対するZ軸の回転行列の生成
	public static WorldMatrix rotationZ(double theta, WorldMatrix worldMatrix){
		Matrix matrix = FormulasMatrix.rotationZ3d(theta);

		return trans(worldMatrix, matrix);
	}

	// DisplayMatrixから点(ベクトル)を抽出
	private static Matrix generateVector(DisplayMatrix displayMatrix, int n){
		Matrix matrix = new Matrix(1, 3);
		double[] tmp = displayMatrix.getData(n);
		matrix.setData(0, 0, tmp[0]);
		matrix.setData(0, 1, tmp[1]);
		matrix.setData(0, 2, 1);
		return matrix;
	}

	// WorldMatrixから点(ベクトル)を抽出
	private static Matrix generateVector(WorldMatrix worldMatrix, int n){
		Matrix matrix = new Matrix(1, 4);
		double[] tmp = worldMatrix.getData(n);
		matrix.setData(0, 0, tmp[0]);
		matrix.setData(0, 1, tmp[1]);
		matrix.setData(0, 2, tmp[2]);
		matrix.setData(0, 3, 1);
		return matrix;
	}

	// DisplayMatrixに行列変換を適用
	public static DisplayMatrix trans(DisplayMatrix displayMatrix, Matrix matrix){
		DisplayMatrix ans = new DisplayMatrix(displayMatrix.getLoop());
		for (int i = 0; i < displayMatrix.getSize(); i = i + 1){
			Matrix vec = generateVector(displayMatrix, i);
			Matrix tmp = Matrix.product(matrix, vec);
			if (tmp != null){
				ans.add(tmp.getData(0, 0), tmp.getData(0, 1));
			}
		}
		return ans;
	}

	// WorldMatrixに行列変換を適用
	public static WorldMatrix trans(WorldMatrix worldMatrix, Matrix matrix){
		WorldMatrix ans = new WorldMatrix();
		for (int i = 0; i < worldMatrix.getSize(); i = i + 1){
			Matrix vec = generateVector(worldMatrix, i);
			Matrix tmp = Matrix.product(matrix, vec);
			if (tmp != null){
				ans.add(tmp.getData(0, 0), tmp.getData(0, 1), tmp.getData(0, 2));
			}
		}
		return ans;
	}

	// 右手系座標を左手系座標に変換
	public static WorldMatrix right2left(WorldMatrix wm){
		WorldMatrix ans = new WorldMatrix();
		for (int i = 0; i < wm.getSize(); i = i + 1){
			double[] tmp = wm.getData(i);
			ans.add(tmp[0], tmp[1], tmp[2] * -1);
		}
		return ans;
	}

	// 現在のdisplayMatrixから描画用の画像を生成
	public static BufferedImage generateDisplayImage(){
		BufferedImage img = new BufferedImage(Setting.MAIN_W, Setting.MAIN_H, BufferedImage.TYPE_INT_RGB);
		double[][] depth = new double[Setting.MAIN_H][Setting.MAIN_W];
		ArrayList<DisplayMatrix>dm = Setting.displayMatrix;

		// depthを最遠点に初期化
		for (double[] doubles : depth) {
			Arrays.fill(doubles, Double.MAX_VALUE);
		}

		for (int i = 0; i < dm.size(); i = i + 1){
			DisplayMatrix df = dm.get(i);
			img = generateFillTexture(img, df, depth);
		}

		return img;
	}

	// テクスチャマッピングを行うための中間クラス 処理自体はImageCalc担当
	public static BufferedImage generateFillTexture(BufferedImage img, DisplayMatrix displayMatrix, double[][] depth){
		int[] xps = new int[3], yps = new int[3];
		double dist = displayMatrix.dist;

		// Polygon生成
		for (int i = 0; i < 3; i = i + 1){
			double[] tmp = displayMatrix.getData(i);
			xps[i] = moveX(tmp[0]);
			yps[i] = moveY(tmp[1]);
		}
		Polygon pol = new Polygon(xps, yps, 3); // nps側が出力側
		Polygon src = clonePolygon(displayMatrix.texture);

		return ImageCalc.movingImageTex(Texture.getTexture(displayMatrix.texNum), img, src, pol, depth, dist, displayMatrix.color.clone());
	}

	// 現在のWorldMatrixからスクリーン情報を生成(射影変換)
	public static ArrayList<DisplayMatrix> generateDisplayMatrix(){
		ArrayList<WorldMatrix> wm = transWorldMatrix();

		ArrayList<DisplayMatrix>dm = new ArrayList<>();
		for (int i = 0; i < wm.size(); i = i + 1){
			DisplayMatrix dt = new DisplayMatrix(true);
			for (int j = 0; j < wm.get(i).getSize(); j = j + 1) {
				double[] tmp = wm.get(i).getData(j);
				dt.add(1 * tmp[0] / tmp[2], 1 * tmp[1] / tmp[2]);
			}
			dt.dist = wm.get(i).dist;
			dt.color = wm.get(i).color;
			dt.texture = clonePolygon(wm.get(i).texture);
			dt.texNum = wm.get(i).texNum;
			dm.add(dt);
		}

		ArrayList<DisplayMatrix>trans = new ArrayList<>();
		for (int i = 0; i < dm.size(); i = i + 1){
			DisplayMatrix tmp = zoom(4, 4, dm.get(i));
			tmp.dist = dm.get(i).dist;
			tmp.color = dm.get(i).color;
			tmp.texture = clonePolygon(dm.get(i).texture);
			tmp.texNum = dm.get(i).texNum;
			trans.add(tmp);
		}

		return trans;
	}

	// 現在のWorldMatrixから視野を作成(視野変換)
	private static ArrayList<WorldMatrix> transWorldMatrix(){
		ArrayList<WorldMatrix>wm = Setting.worldMatrices;
		boolean[] enb = new boolean[wm.size()];
		Arrays.fill(enb, true);

		for (int i = 0; i < wm.size(); i = i + 1){
			for (int j = 0; j < wm.get(i).getSize() && enb[i]; j = j + 1){
				double th = arc2rad(Setting.theta), ph = arc2rad(Setting.angle);
				double[] v = new double[]{-1 * Math.sin(th) * Math.cos(ph), Math.sin(ph), Math.cos(th) * Math.cos(ph)};
				double[] p = new double[]{Setting.x, Setting.y, Setting.z};
				double[] e = wm.get(i).getData(j);

				double tmp = 0;
				for (int k = 0; k < 3; k = k + 1){
					tmp = tmp + (v[k] * (e[k] - p[k]));
				}
				if (tmp < 0){
					enb[i] = false;
				}
			}
		}

		ArrayList<WorldMatrix>enabled = new ArrayList<>();
		for (int i = 0; i < wm.size(); i = i + 1){
			if (enb[i]){
				double tmp = avgDistance(wm.get(i));
				int[] cl = generateColor(wm.get(i));
				WorldMatrix adder = right2left(wm.get(i));
				adder.dist = tmp;
				adder.color = cl;
				adder.texture = clonePolygon(wm.get(i).texture);
				adder.texNum = wm.get(i).texNum;
				enabled.add(adder);
			}
		}
		wm = enabled;

		Matrix rz = FormulasMatrix.rotationZ3d(arc2rad(-180));
		Matrix view = FormulasMatrix.rotationY3d(arc2rad(Setting.theta));
		Matrix ang = FormulasMatrix.rotationX3d(arc2rad(Setting.angle));
		Matrix move = FormulasMatrix.shift3d(Setting.x, Setting.y, Setting.z);

		Matrix rot = Matrix.product(move, Objects.requireNonNull(rz));
		Matrix agr = Matrix.product(view, Objects.requireNonNull(rot));
		Matrix fnl = Matrix.product(ang, Objects.requireNonNull(agr));

		ArrayList<WorldMatrix>trans = new ArrayList<>();
		for (int i = 0; i < wm.size(); i = i + 1){
			WorldMatrix tmp = trans(wm.get(i), fnl);
			tmp.dist = wm.get(i).dist;
			tmp.color = wm.get(i).color;
			tmp.texture = clonePolygon(wm.get(i).texture);
			tmp.texNum = wm.get(i).texNum;
			trans.add(tmp);
		}

		return trans;
	}

	// 度数表現をラジアン表現に変換
	public static double arc2rad(double n){
		double tmp = (n / 180) * Math.PI;
		return cTheta(tmp);
	}

	// 与えられた角度を-pi～piに丸める
	public static double cTheta(double tmp){
		while (tmp > Math.PI){
			tmp = tmp - Math.PI * 2;
		}
		while (tmp <= Math.PI * -1){
			tmp = tmp + Math.PI * 2;
		}

		return tmp;
	}

	// WorldMatrixと視点位置の距離の平均を求める
	public static double avgDistance(WorldMatrix wm){
		double sum = 0;

		for (int i = 0; i < wm.getSize(); i = i + 1){
			double[] point = wm.getData(i);
			double dx = point[0] - Setting.x;
			double dy = point[1] - Setting.y;
			double dz = point[2] - Setting.z;
			sum = sum + Math.sqrt(dx * dx + dy * dy + dz * dz);
		}

		return sum / wm.getSize();
	}

	// 実数をディスプレイ上の座標に変換(X)
	private static int moveX(double x){
		x = x * 100; // pow
		x = x + ((double) Setting.MAIN_W / 2);
		return (int) x;
	}

	// 実数をディスプレイ上の座標に変換(Y)
	private static int moveY(double y){
		y = y * -1;
		y = y * 100; // pow
		y = y + ((double) Setting.MAIN_H / 2);
		return (int) y;
	}

	private static double removeX(int x) {
		double result = x - ((double) Setting.MAIN_W / 2);
		result = result / 100; // pow
		return result;
	}

	// 画面上の整数型y座標を実数型y座標に逆変換
	private static double removeY(int y) {
		double result = y - ((double) Setting.MAIN_H / 2);
		result = result / 100; // pow
		result = result * -1;
		return result;
	}

	// 原点と点のなす角(0 <= theta < 360)
	public static double getTheta(double x, double y){
		if (x == 0){
			return 0;
		}

		double rad = Math.atan(y / x);

		if (x < 0){
			rad = rad + Math.PI;
		}

		return rad;
	}

	// ラジアンを度数表現に変換(任意のradに対して0 <= theta < 360)
	public static double rad2arc(double r){
		double arc = (r / Math.PI) * 180;

		while (arc < 0){
			arc = arc + 360;
		}

		while (arc >= 360){
			arc = arc - 360;
		}

		return arc;
	}

	// 点pと(点1, 点2)のなす角を求める(0 <= theta < 180)
	public static double getDiffTheta(double xp, double yp, double x1, double y1, double x2, double y2){
		double t1 = rad2arc(getTheta(x1 - xp, y1 - yp));
		double t2 = rad2arc(getTheta(x2 - xp, y2 - yp));
		double t3 = Double.max(t1, t2);
		double t4 = Double.min(t1, t2);
		double t = t3 - t4;

		if (t >= 180) {
			t = 360 - t;
		}

		return t;
	}

	// 光の足し合わせ(0-255)
	public static int[] addColor(int[] c1, int[] c2){
		int[] ans = new int[3];

		for (int i = 0; i < 3; i = i + 1){
			ans[i] = c1[i] + c2[i];
			if (ans[i] > 255){
				ans[i] = 255;
			} else if (ans[i] < 0){
				ans[i] = 0;
			}
		}

		return ans;
	}

	// 光の倍率設定(0-255)
	public static int[] mulColor(int[] c, double x){
		if ((x >= 0) && (x <= 1)) {
			int[] ans = new int[3];

			for (int i = 0; i < 3; i = i + 1) {
				ans[i] = (int) (c[i] * x);
			}

			return ans;
		} else {
			return c.clone();
		}
	}

	// WorldMatrixからその面の法線ベクトルを計算する
	public static double[] getNormalVector(WorldMatrix worldMatrix){
		double[][] points = new double[][]{worldMatrix.getData(0), worldMatrix.getData(1), worldMatrix.getData(2)};
		double x = (points[1][1] - points[0][1]) * (points[2][2] - points[0][2]) - (points[2][1] - points[0][1]) * (points[1][2] - points[0][2]);
		double y = (points[1][2] - points[0][2]) * (points[2][0] - points[0][0]) - (points[2][2] - points[0][2]) * (points[1][0] - points[0][0]);
		double z = (points[1][0] - points[0][0]) * (points[2][1] - points[0][1]) - (points[2][0] - points[0][0]) * (points[1][1] - points[0][1]);

		return new double[]{x, y, z};
	}

	// WorldMatrixから色を求める
	public static int[] generateColor(WorldMatrix worldMatrix){
		double[] wv = getNormalVector(worldMatrix);
		double[] lv = Setting.lightVector.clone();

		double val = (wv[0] * lv[0] + wv[1] * lv[1] + wv[2] * lv[2]) / (Math.sqrt(wv[0] * wv[0] + wv[1] * wv[1] + wv[2] * wv[2]) * Math.sqrt(lv[0] * lv[0] + lv[1] * lv[1] + lv[2] * lv[2]));
		double theta = Math.acos(val);
		if (theta > Math.PI / 2){
			theta = (theta - (theta - Math.PI / 2));
		}

		return addColor(mulColor(Setting.ambientColor.clone(), Setting.ambientPow), mulColor(worldMatrix.color, Math.cos(theta)));
	}

	// Polygonのクローンを作成
	private static Polygon clonePolygon(Polygon p){
		return new Polygon(p.xpoints.clone(), p.ypoints.clone(), p.npoints);
	}
}
