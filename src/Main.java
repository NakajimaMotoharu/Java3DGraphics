// メインクラス
public class Main {
	// メインメソッド
	public static void main(String[] args) {
		Setting.init();

		Setting.worldMatrices.addAll(WorldReader.readWldFile("world.wld"));
		Setting.worldMatrices.addAll(WorldReader.readObjFile("teapot.obj", 0, 0, 0));
		Setting.displayMatrix = CalcMatrix.generateDisplayMatrix();
		System.out.printf("合計線分数: %d\n", Setting.lineCount);

		MainCtrlThread mainCtrlThread = new MainCtrlThread();
		FrameRateThread frameRateThread = new FrameRateThread();
		ActionThread actionThread = new ActionThread();
		Setting.mainWindow = new MainWindow();
		if (Setting.SUB_WINDOW) {
			Setting.subWindow = new SubWindow();
		}

		mainCtrlThread.start();
		frameRateThread.start();
		actionThread.start();

		try {
			mainCtrlThread.join();
			frameRateThread.join();
			actionThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
