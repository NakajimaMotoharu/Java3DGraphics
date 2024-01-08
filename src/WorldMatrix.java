import java.util.ArrayList;

// 3時限上の線・面を扱うクラス
public class WorldMatrix {
	private ArrayList<Matrix> matrix;
	public double dist = 0;
	public int[] color = Setting.defColor.clone();

	// コンストラクタ
	public WorldMatrix(){
		matrix = new ArrayList<>();
	}

	// 点の追加
	public void add(double x, double y, double z){
		Matrix adder = new Matrix(1, 3);
		adder.setData(0, 0, x);
		adder.setData(0, 1, y);
		adder.setData(0, 2, z);
		matrix.add(adder);
	}

	// 点の取得
	public double[] getData(int n){
		if (matrix == null){
			return null;
		}
		if (n < matrix.size()){
			double[] tmp = new double[3];
			tmp[0] = matrix.get(n).getData(0, 0);
			tmp[1] = matrix.get(n).getData(0, 1);
			tmp[2] = matrix.get(n).getData(0, 2);
			return tmp;
		}
		return null;
	}

	// 登録されている点の数の取得
	public int getSize(){
		if (matrix == null){
			return 0;
		}
		return matrix.size();
	}
}
