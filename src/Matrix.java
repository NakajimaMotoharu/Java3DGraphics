// 行列そのものを表現するクラス
public class Matrix {
	private double[][] data;

	// コンストラクタ
	public Matrix(int x, int y){
		data = new double[y][x];
		for (int i = 0; i < getSizeY(); i = i + 1){
			for (int j = 0; j < getSizeX(); j = j + 1){
				setData(j, i, 0);
			}
		}
	}

	// データの挿入
	public void setData(int x, int y, double element){
		if (isInField(x, y)){
			data[y][x] = element;
		}
	}

	// データの取得
	public double getData(int x, int y){
		if (isInField(x, y)){
			return data[y][x];
		}
		return 0;
	}

	// 行列の列数を取得
	public int getSizeX(){
		return data[0].length;
	}

	// 行列の行数を取得
	public int getSizeY(){
		return data.length;
	}

	// 指定座標が行列内に収まっているかの判定
	private boolean isInField(int x, int y){
		return (((x >= 0)) && (x < getSizeX())) && (((y >= 0)) && (y < getSizeY()));
	}

	// 行列の積
	public static Matrix product(Matrix a, Matrix b){
		if (a.getSizeX() != b.getSizeY()){
			return null;
		}
		Matrix ans = new Matrix(b.getSizeX(), a.getSizeY());
		for (int i = 0; i < ans.getSizeY(); i = i + 1){
			for (int j = 0; j < ans.getSizeX(); j = j + 1){
				double tmp = 0;
				for (int k = 0; k < a.getSizeX(); k = k + 1){
					tmp = tmp + a.getData(k, i) * b.getData(j, k);
				}
				ans.setData(j, i, tmp);
			}
		}

		return ans;
	}
}
