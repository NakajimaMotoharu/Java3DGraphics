// 画面再描画用のクラス
public class RepaintThread extends Thread{
	private double rate = (double) 60 / 1000;

	@Override
	public void run() {
		while (true){
			try {
				sleep((long) rate);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Setting.mainWindow.repaint();
		}
	}
}
