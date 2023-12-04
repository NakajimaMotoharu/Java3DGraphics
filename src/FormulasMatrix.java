// アフィン変換行列の生成を行うクラス
public class FormulasMatrix {
	// 2次元の平行移動行列
	public static Matrix shift2d(double tx, double ty){
		Matrix matrix = new Matrix(3, 3);
		matrix.setData(0, 0, 1);
		matrix.setData(1, 0, 0);
		matrix.setData(2, 0, tx);
		matrix.setData(0, 1, 0);
		matrix.setData(1, 1, 1);
		matrix.setData(2, 1, ty);
		matrix.setData(0, 2, 0);
		matrix.setData(1, 2, 0);
		matrix.setData(2, 2, 1);

		return matrix;
	}

	// 3次元の平行移動行列
	public static Matrix shift3d(double tx, double ty, double tz){
		Matrix matrix = new Matrix(4, 4);
		matrix.setData(0, 0, 1);
		matrix.setData(1, 0, 0);
		matrix.setData(2, 0, 0);
		matrix.setData(3, 0, tx);
		matrix.setData(0, 1, 0);
		matrix.setData(1, 1, 1);
		matrix.setData(2, 1, 0);
		matrix.setData(3, 1, ty);
		matrix.setData(0, 2, 0);
		matrix.setData(1, 2, 0);
		matrix.setData(2, 2, 1);
		matrix.setData(3, 2, tz);
		matrix.setData(0, 3, 0);
		matrix.setData(1, 3, 0);
		matrix.setData(2, 3, 0);
		matrix.setData(3, 3, 1);

		return matrix;
	}

	// 2次元の拡大行列
	public static Matrix zoom2d(double sx, double sy){
		Matrix matrix = new Matrix(3, 3);
		matrix.setData(0, 0, sx);
		matrix.setData(1, 0, 0);
		matrix.setData(2, 0, 0);
		matrix.setData(0, 1, 0);
		matrix.setData(1, 1, sy);
		matrix.setData(2, 1, 0);
		matrix.setData(0, 2, 0);
		matrix.setData(1, 2, 0);
		matrix.setData(2, 2, 1);

		return matrix;
	}

	// 3次元の拡大行列
	public static Matrix zoom3d(double sx, double sy, double sz){
		Matrix matrix = new Matrix(4, 4);
		matrix.setData(0, 0, sx);
		matrix.setData(1, 0, 0);
		matrix.setData(2, 0, 0);
		matrix.setData(3, 0, 0);
		matrix.setData(0, 1, 0);
		matrix.setData(1, 1, sy);
		matrix.setData(2, 1, 0);
		matrix.setData(3, 1, 0);
		matrix.setData(0, 2, 0);
		matrix.setData(1, 2, 0);
		matrix.setData(2, 2, sz);
		matrix.setData(3, 2, 0);
		matrix.setData(0, 3, 0);
		matrix.setData(1, 3, 0);
		matrix.setData(2, 3, 0);
		matrix.setData(3, 3, 1);

		return matrix;
	}

	// 2次元の回転行列
	public static Matrix rotation2d(double theta){
		Matrix matrix = new Matrix(3, 3);
		matrix.setData(0, 0, Math.cos(theta));
		matrix.setData(1, 0, Math.sin(theta) * -1);
		matrix.setData(2, 0, 0);
		matrix.setData(0, 1, Math.sin(theta));
		matrix.setData(1, 1, Math.cos(theta));
		matrix.setData(2, 1, 0);
		matrix.setData(0, 2, 0);
		matrix.setData(1, 2, 0);
		matrix.setData(2, 2, 1);

		return matrix;
	}

	// 3次元のX軸に対する回転行列
	public static Matrix rotationX3d(double theta){
		Matrix matrix = new Matrix(4, 4);
		matrix.setData(0, 0, 1);
		matrix.setData(1, 0, 0);
		matrix.setData(2, 0, 0);
		matrix.setData(3, 0, 0);
		matrix.setData(0, 1, 0);
		matrix.setData(1, 1, Math.cos(theta));
		matrix.setData(2, 1, Math.sin(theta) * -1);
		matrix.setData(3, 1, 0);
		matrix.setData(0, 2, 0);
		matrix.setData(1, 2, Math.sin(theta));
		matrix.setData(2, 2, Math.cos(theta));
		matrix.setData(3, 2, 0);
		matrix.setData(0, 3, 0);
		matrix.setData(1, 3, 0);
		matrix.setData(2, 3, 0);
		matrix.setData(3, 3, 1);

		return matrix;
	}

	// 3次元のY軸に対する回転行列
	public static Matrix rotationY3d(double theta){
		Matrix matrix = new Matrix(4, 4);
		matrix.setData(0, 0, Math.cos(theta));
		matrix.setData(1, 0, 0);
		matrix.setData(2, 0, Math.sin(theta));
		matrix.setData(3, 0, 0);
		matrix.setData(0, 1, 0);
		matrix.setData(1, 1, 1);
		matrix.setData(2, 1, 0);
		matrix.setData(3, 1, 0);
		matrix.setData(0, 2, Math.sin(theta) * -1);
		matrix.setData(1, 2, 0);
		matrix.setData(2, 2, Math.cos(theta));
		matrix.setData(3, 2, 0);
		matrix.setData(0, 3, 0);
		matrix.setData(1, 3, 0);
		matrix.setData(2, 3, 0);
		matrix.setData(3, 3, 1);

		return matrix;
	}

	// 3次元のZ軸に対する回転行列
	public static Matrix rotationZ3d(double theta){
		Matrix matrix = new Matrix(4, 4);
		matrix.setData(0, 0, Math.cos(theta));
		matrix.setData(1, 0, Math.sin(theta) * -1);
		matrix.setData(2, 0, 0);
		matrix.setData(3, 0, 0);
		matrix.setData(0, 1, Math.sin(theta));
		matrix.setData(1, 1, Math.cos(theta));
		matrix.setData(2, 1, 0);
		matrix.setData(3, 1, 0);
		matrix.setData(0, 2, 0);
		matrix.setData(1, 2, 0);
		matrix.setData(2, 2, 1);
		matrix.setData(3, 2, 0);
		matrix.setData(0, 3, 0);
		matrix.setData(1, 3, 0);
		matrix.setData(2, 3, 0);
		matrix.setData(3, 3, 1);

		return matrix;
	}

	// 右手系座標を左手系座標に変換する行列
	public static Matrix right2left(){
		Matrix matrix = new Matrix(3, 3);
		matrix.setData(0, 0, 1);
		matrix.setData(1, 0, 0);
		matrix.setData(2, 0, 0);
		matrix.setData(0, 1, 0);
		matrix.setData(1, 1, 1);
		matrix.setData(2, 1, 0);
		matrix.setData(0, 2, 0);
		matrix.setData(1, 2, 0);
		matrix.setData(2, 2, -1);

		return matrix;
	}
}
