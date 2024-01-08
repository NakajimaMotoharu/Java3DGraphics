import java.util.ArrayList;

// 2次元上の線・面を扱うクラス
public class DisplayMatrix {
	private ArrayList<Matrix> matrix;
	private boolean drawLoop;
	public double dist = 0;
	public int[] color = new int[]{0, 0, 0};

	// コンストラクタ
	public DisplayMatrix(boolean loop){
		matrix = new ArrayList<>();
		drawLoop = loop;
	}

	// 点を追加
	public void add(double x, double y){
		Matrix adder = new Matrix(1, 2);
		adder.setData(0,0, x);
		adder.setData(0,1, y);
		matrix.add(adder);
	}

	// 点の取得
	public double[] getData(int n){
		if (matrix == null){
			return null;
		}
		if (n < matrix.size()){
			double[] tmp = new double[2];
			tmp[0] = matrix.get(n).getData(0, 0);
			tmp[1] = matrix.get(n).getData(0, 1);
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

	// 始点と終点を結ぶ設定の取得
	public boolean getLoop(){
		return drawLoop;
	}
}
