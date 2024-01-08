import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

// 3次元オブジェクトの読み込みクラス
public class WorldReader {
	// wld(独自定義形式)読み込みクラス
	public static ArrayList<WorldMatrix> readWldFile(String fileName){
		System.out.println("ワールドデータの読み込み中......");
		Path path = Paths.get(fileName);
		List<String>data;
		try {
			data = Files.readAllLines(path);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		int st = 0, sf = 1, en = 0, indexSt = 0, indexEn = 0;
		for (int i = 0; i < data.size(); i = i + 1){
			String tmp = data.get(i);
			switch (tmp) {
				case "start" -> {
					st = st + 1;
					indexSt = i;
				}
				case "next" -> sf = sf + 1;
				case "end" -> {
					en = en + 1;
					indexEn = i;
				}
			}
		}
		if (st != 1 || en != 1){
			System.out.println("format error");
			return null;
		}

		ArrayList<WorldMatrix>wm = new ArrayList<>();
		WorldMatrix wt = new WorldMatrix();
		for (int i = indexSt + 1, j = 0; i < indexEn; i = i + 1){
			if (data.get(i).equals("next")){
				j = j + 1;

				// 色ランダム
				wt.color = new int[]{(int) (Math.random() * 256), (int) (Math.random() * 256), (int) (Math.random() * 256)};

				wm.add(wt);
				wt = new WorldMatrix();
			} else {
				String[] tmp = data.get(i).split(",");
				double[] db = new double[3];
				for (int k = 0; k < 3; k = k + 1){
					db[k] = Double.parseDouble(tmp[k]);
				}
				wt.add(db[0], db[1], db[2]);
				Setting.lineCount = Setting.lineCount + 1;
			}
		}

		// 色ランダム
		wt.color = new int[]{(int) (Math.random() * 256), (int) (Math.random() * 256), (int) (Math.random() * 256)};

		wm.add(wt);

		System.out.println("ワールドデータの読み込み完了");
		return wm;
	}

	// objファイル読み込みクラス
	public static ArrayList<WorldMatrix> readObjFile(String fileName, double x, double y, double z){
		System.out.printf("オブジェクトデータ[%s]を読み込み中......\n", fileName);
		Path path = Paths.get(fileName);
		List<String>data;
		int count = 0;

		try {
			data = Files.readAllLines(path);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		ArrayList<String>vData = new ArrayList<>();
		ArrayList<String>fData = new ArrayList<>();

		for (String datum : data) {
			String str = datum.replace("  ", " ");
			if (str.isEmpty()) {
				// ignore
			} else if (str.charAt(0) == 'v' && str.charAt(1) == ' ') {
				vData.add(str);
			} else if (str.charAt(0) == 'f' && str.charAt(1) == ' ') {
				fData.add(str);
			}
		}

		double[][] v = new double[vData.size()][3];
		for (int i = 0; i < vData.size(); i = i + 1){
			String[] tmp = vData.get(i).split(" ");
			v[i][0] = Double.parseDouble(tmp[1]);
			v[i][1] = Double.parseDouble(tmp[2]);
			v[i][2] = Double.parseDouble(tmp[3]);
		}

		int[][] n = new int[fData.size()][];
		for (int i = 0; i < fData.size(); i = i + 1){
			String[] tmp = fData.get(i).split(" ");
			n[i] = new int[tmp.length - 1];
			for (int j = 1; j < tmp.length; j = j + 1){
				n[i][j - 1] = Integer.parseInt(tmp[j].split("/")[0]);
			}
		}

		ArrayList<WorldMatrix>wm = new ArrayList<>();
		for (int i = 0; i < fData.size(); i = i + 1){
			WorldMatrix wt = new WorldMatrix();

			wt.color = Setting.defColor.clone();

			for (int j = 0; j < n[i].length; j = j + 1){
				count = count + 1;
				wt.add(v[n[i][j] - 1][0] + x, v[n[i][j] - 1][1] + y, v[n[i][j] - 1][2] + z);
			}
			wm.add(wt);
		}

		Setting.lineCount = Setting.lineCount + count;
		System.out.printf("オブジェクトデータ[%s]の読み込み完了\n", fileName);
		return wm;
	}
}
