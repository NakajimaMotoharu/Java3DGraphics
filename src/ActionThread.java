// キー操作や視点操作の定期実行用スレッド
public class ActionThread extends Thread{
	@Override
	public void run() {
		while (true){
			try {
				sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Setting.keyUtil.action();
		}
	}
}
