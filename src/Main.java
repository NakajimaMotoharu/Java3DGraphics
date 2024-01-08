// メインクラス
public class Main {
	// メインメソッド
	public static void main(String[] args) {
		Setting.init();

		Setting.worldMatrices.addAll(WorldReader.readObjFile("teapot.obj", 0, 0, 0));
		Setting.displayMatrix = CalcMatrix.generateDisplayMatrix();
		Setting.dispImg = CalcMatrix.generateDisplayImage();
		System.out.printf("合計面数: %d\n", Setting.worldMatrices.size());

		MainCtrlThread mainCtrlThread = new MainCtrlThread();
		RepaintThread repaintThread = new RepaintThread();
		FrameRateThread frameRateThread = new FrameRateThread();
		ActionThread actionThread = new ActionThread();
		Setting.mainWindow = new MainWindow();
		if (Setting.SUB_WINDOW) {
			Setting.subWindow = new SubWindow();
		}

		mainCtrlThread.start();
		frameRateThread.start();
		actionThread.start();
		repaintThread.start();

		try {
			mainCtrlThread.join();
			frameRateThread.join();
			actionThread.join();
			repaintThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
