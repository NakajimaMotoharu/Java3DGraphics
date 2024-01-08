import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

// 各種設定クラス
public class Setting {
	public static int MAIN_W = 640;
	public static int MAIN_H = 480;
	public static int SUB_W = 300;
	public static int SUB_H = 100;
	public static int SUB_LOCATION_W = 0;
	public static int SUB_LOCATION_H = MAIN_H + 20;
	public static final String MAIN_TITLE = "MainWindow";
	public static final String SUB_TITLE = "SubWindow";
	public static final String SUB_BUTTON_EXIT_TITLE = "Exit";
	public static boolean SUB_WINDOW = true;

	public static MainWindow mainWindow;
	public static SubWindow subWindow;
	public static ArrayList<DisplayMatrix>displayMatrix = new ArrayList<>();
	public static ArrayList<WorldMatrix>worldMatrices = new ArrayList<>();

	public static KeyUtil keyUtil = new KeyUtil();
	public static double theta = 180, angle = 0;
	public static double x = 0, y = 8, z = 20;
	public static long mainCtrlThreadLastTime = -1;
	public static long mainCtrlThreadFirstTime = -1;

	public static int[][][] display = new int[MAIN_H][MAIN_W][3];
	public static BufferedImage dispImg = new BufferedImage(MAIN_W, MAIN_H, BufferedImage.TYPE_INT_RGB);
	public static int[] defColor = new int[]{255, 0, 0};
	public static int[] ambientColor = new int[]{0, 0, 255};
	public static double ambientPow = 0.2;
	public static double[] lightVector = new double[]{-0.57735026919, 0.57735026919, 0.57735026919};
	public static double lightArc = 0;

	public static double fps = 0;
	public static int frameCount = 0;
	public static int lineCount = 0;

	public static boolean mouseEnabled = false;

	// 設定ファイルを読み込み設定を初期化するクラス
	public static void init(){
		System.out.print("設定ファイルを読み込み中......\n");
		Path path = Paths.get("Setting.cfg");
		List<String> data;
		try {
			data = Files.readAllLines(path);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		for (String datum : data) {
			String[] strings = datum.split(" ");
			if (strings.length == 3) {
				switch (strings[0]) {
					case "MAIN_W" -> MAIN_W = Integer.parseInt(strings[2]);
					case "MAIN_H" -> {
						MAIN_H = Integer.parseInt(strings[2]);
						SUB_LOCATION_H = MAIN_H + 20;
					}
					case "SUB_WINDOW" -> SUB_WINDOW = strings[2].equals("true");
					case "theta" -> theta = Double.parseDouble(strings[2]);
					case "angle" -> angle = Double.parseDouble(strings[2]);
					case "x" -> x = Double.parseDouble(strings[2]);
					case "y" -> y = Double.parseDouble(strings[2]);
					case "z" -> z = Double.parseDouble(strings[2]);
					case "FLOOR_LINE" -> {
						if (strings[2].equals("true")) {
							for (int j = -50; j < 50; j = j + 10) {
								for (int k = -50; k < 50; k = k + 10) {
									WorldMatrix x = new WorldMatrix(), z = new WorldMatrix();
									x.add(j, 0, k);
									x.add(j + 10, 0, k);
									z.add(k, 0, j);
									z.add(k, 0, j + 10);
									worldMatrices.add(x);
									worldMatrices.add(z);
								}
							}
						}
					}
				}
			}
		}
		display = new int[MAIN_H][MAIN_H][3];
		System.out.print("設定ファイルの読み込み完了\n");
	}
}
