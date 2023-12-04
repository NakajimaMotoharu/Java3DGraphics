// 画面の描画を行うスレッド
public class MainCtrlThread extends Thread{
	private long slpTime = 1000 / Setting.TARGET_FPS;

	@Override
	public void run() {
		try {
			while (true){
				sleep(slpTime);
				Setting.displayMatrix = CalcMatrix.generateDisplayMatrix();
				Setting.mainWindow.repaint();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
