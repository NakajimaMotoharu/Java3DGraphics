// フレームレートを測定するクラス
public class FrameRateThread extends Thread{

	@Override
	public void run() {
		while (true){
			long st = 0, en = 0;
			int count = 0;
			try {
				Setting.frameCount = 0;
				st = System.currentTimeMillis();
				sleep(1000);
				en = System.currentTimeMillis();
				count = Setting.frameCount;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Setting.fps = (double) count / (en - st) * 1000;
		}
	}
}
