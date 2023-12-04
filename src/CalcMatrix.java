import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

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
			dm.add(dt);
		}

		ArrayList<DisplayMatrix>trans = new ArrayList<>();
		for (int i = 0; i < dm.size(); i = i + 1){
			trans.add(zoom(4, 4, dm.get(i)));
		}

		return trans;
	}

	// 現在のWorldMatrixから視野を作成(視野変換)
	private static ArrayList<WorldMatrix> transWorldMatrix(){
		ArrayList<WorldMatrix>wm = (ArrayList<WorldMatrix>) Setting.worldMatrices.clone();
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
				enabled.add(right2left(wm.get(i)));
			}
		}
		wm = enabled;

		double thetaA = arc2rad(-90);
		Matrix rz = FormulasMatrix.rotationZ3d(thetaA);
		double thetaC = arc2rad(-90);
		Matrix rx = FormulasMatrix.rotationZ3d(thetaC);
		Matrix view = FormulasMatrix.rotationY3d(arc2rad(Setting.theta));
		Matrix ang = FormulasMatrix.rotationX3d(arc2rad(Setting.angle));
		Matrix move = FormulasMatrix.shift3d(Setting.x, Setting.y, Setting.z);
		Matrix rzryrxt = Matrix.product(rz, Objects.requireNonNull(rx));

		Matrix rot = Matrix.product(move, Objects.requireNonNull(rzryrxt));
		Matrix agr = Matrix.product(view, Objects.requireNonNull(rot));
		Matrix fnl = Matrix.product(ang, Objects.requireNonNull(agr));

		ArrayList<WorldMatrix>trans = new ArrayList<>();
		for (int i = 0; i < wm.size(); i = i + 1){
			trans.add(trans(wm.get(i), fnl));
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
}
