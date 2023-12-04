import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// サブのウィンドウ作成クラス
public class SubWindow extends JFrame implements ActionListener {
	JButton exit;

	// サブウィンドウ作成
	public SubWindow(){
		setTitle(Setting.SUB_TITLE);
		setSize(Setting.SUB_W, Setting.SUB_H);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocation(Setting.SUB_LOCATION_W, Setting.SUB_LOCATION_H);

		exit = new JButton(Setting.SUB_BUTTON_EXIT_TITLE);
		getContentPane().add(exit);
		exit.addActionListener(this);

		setVisible(true);
	}

	// ボタンが押されたときに呼び出し
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == exit){
			System.exit(0);
		}
	}
}
