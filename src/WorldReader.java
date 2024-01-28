import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

// 3次元オブジェクトの読み込みクラス
public class WorldReader {
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
		ArrayList<String>vtData = new ArrayList<>();

		for (String datum : data) {
			String str = datum.replace("  ", " ");
			if (str.isEmpty()) {
				// ignore
			} else if (str.charAt(0) == 'v' && str.charAt(1) == ' ') {
				vData.add(str);
			} else if (str.charAt(0) == 'f' && str.charAt(1) == ' ') {
				fData.add(str + String.format(" %d", Texture.getNowPointer()));
			} else if (str.contains("vt")) {
				vtData.add(str);
			} else if (str.contains("mtllib")){
				String[] tmp = str.split(" ");
				Texture.getMtlFile(tmp[tmp.length - 1]);
			} else if (str.contains("usemtl")){
				String[] tmp = str.split(" ");
				Texture.addTexture(tmp[tmp.length - 1]);
			}
		}

		double[][] v = new double[vData.size()][3];
		for (int i = 0; i < vData.size(); i = i + 1){
			String[] tmp = vData.get(i).split(" ");
			v[i][0] = Double.parseDouble(tmp[1]);
			v[i][1] = Double.parseDouble(tmp[2]);
			v[i][2] = Double.parseDouble(tmp[3]);
		}

		int[][] n = new int[fData.size()][7];
		for (int i = 0; i < fData.size(); i = i + 1){
			String[] tmp = fData.get(i).split(" ");
			String[] s1 = tmp[1].split("/");
			String[] s2 = tmp[2].split("/");
			String[] s3 = tmp[3].split("/");
			n[i][0] = Integer.parseInt(s1[0]);
			n[i][1] = Integer.parseInt(s1[1]);
			n[i][2] = Integer.parseInt(s2[0]);
			n[i][3] = Integer.parseInt(s2[1]);
			n[i][4] = Integer.parseInt(s3[0]);
			n[i][5] = Integer.parseInt(s3[1]);
			n[i][6] = Integer.parseInt(tmp[tmp.length - 1]);
			/*
			n[i] = new int[tmp.length - 1];
			for (int j = 1; j < tmp.length; j = j + 1){
				n[i][j - 1] = Integer.parseInt(tmp[j].split("/")[0]);
			}
			*/
		}

		double[][] vt = new double[vtData.size()][2];
		for (int i = 0; i < vtData.size(); i = i + 1){
			String[] tmp = vtData.get(i).split(" ");
			vt[i][0] = Double.parseDouble(tmp[1]);
			vt[i][1] = Double.parseDouble(tmp[2]);
		}

		ArrayList<WorldMatrix>wm = new ArrayList<>();
		for (int i = 0; i < fData.size(); i = i + 1){
			WorldMatrix wt = new WorldMatrix();

			// 色ランダム化
			//wt.color = new int[]{(int) (Math.random() * 256), (int) (Math.random() * 256), (int) (Math.random() * 256)};
			wt.color = Setting.defColor.clone();

			count = count + 1;
			wt.add(v[n[i][0] - 1][0] + x, v[n[i][0] - 1][1] + y, v[n[i][0] - 1][2] + z);
			wt.add(v[n[i][2] - 1][0] + x, v[n[i][2] - 1][1] + y, v[n[i][2] - 1][2] + z);
			wt.add(v[n[i][4] - 1][0] + x, v[n[i][4] - 1][1] + y, v[n[i][4] - 1][2] + z);
			wt.texNum = n[i][6];

			// テクスチャ座標の設定
			int px1, py1, px2, py2, px3, py3;
			int tf1 = n[i][1] - 1, tf2 = n[i][3] - 1, tf3 = n[i][5] - 1;

			// 実数取得
			double dx1, dy1, dx2, dy2, dx3, dy3;
			dx1 = vt[tf1][0];
			dy1 = vt[tf1][1];
			dx2 = vt[tf2][0];
			dy2 = vt[tf2][1];
			dx3 = vt[tf3][0];
			dy3 = vt[tf3][1];

			// 整数変換
			BufferedImage texture = Texture.getTexture(wt.texNum);
			if (texture != null) {
				int w = texture.getWidth(), h = texture.getHeight();
				px1 = (int) (w * (dx1));
				py1 = (int) (h * (1 - dy1));
				px2 = (int) (w * (dx2));
				py2 = (int) (h * (1 - dy2));
				px3 = (int) (w * (dx3));
				py3 = (int) (h * (1 - dy3));

				wt.texture = new Polygon(new int[]{px1, px2, px3}, new int[]{py1, py2, py3}, 3);
			} else {
				System.exit(128);
			}

			wm.add(wt);
		}

		Setting.lineCount = Setting.lineCount + count;
		System.out.printf("オブジェクトデータ[%s]の読み込み完了\n", fileName);
		return wm;
	}
}
