// 画面の描画を行うスレッド
public class MainCtrlThread extends Thread{
	//private long slpTime = 1000 / Setting.TARGET_FPS;
	private long slpTime = 1;

	// スレッドメイン部分
	@Override
	public void run() {
		try {
			while (true){
				sleep(slpTime);

				long t1 = System.currentTimeMillis();

				Setting.displayMatrix = CalcMatrix.generateDisplayMatrix();
				long t2 = System.currentTimeMillis();
				Setting.dispImg = CalcMatrix.generateDisplayImage();
				long t3 = System.currentTimeMillis();
				Setting.frameCount = Setting.frameCount + 1;
				Setting.mainCtrlThreadFirstTime = t2 - t1;
				Setting.mainCtrlThreadLastTime = t3 - t2;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
