package gr.bytewise.jrhythm.guitk;

import java.util.ArrayList;
import java.util.List;

import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LookAndFeelManager {

	private static final Logger logger = LoggerFactory.getLogger(LookAndFeelManager.class);

	private static Integer lookAndFeel = 0;

	public static void setLookAndFeel(Integer lnf) {
		lookAndFeel = lnf;
	}

	public static boolean updateUI() {
		try {
			javax.swing.UIManager.setLookAndFeel(getLookAndFeelList()[lookAndFeel]);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception in updateUI: " + e.getMessage());
			return false;
		}
		return true;
	}

	public static final String[] getLookAndFeelList() {
		List<String> list = new ArrayList<String>();
		try {
			for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if (!info.getName().contains("Metal") && !info.getName().contains("Motif"))
					list.add(info.getClassName());
			}
		} catch (Exception e) {
		}
		return list.toArray(new String[0]);
	}

	public static final String[] getLookndFeelNameList() {
		List<String> list = new ArrayList<String>();
		try {
			for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
				if (!info.getName().contains("Metal") && !info.getName().contains("Motif"))
					list.add(info.getName());
		} catch (Exception e) {
		}
		return list.toArray(new String[0]);
	}
}
