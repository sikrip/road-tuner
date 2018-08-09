package sikrip.roaddyno.standalone.util;

import java.awt.*;

public final class FontUtil {

	private FontUtil() {
	}

	public static void changeFont(Component component, Font font) {
		component.setFont(font);
		if (component instanceof Container) {
			for (Component child : ((Container) component).getComponents()) {
				changeFont(child, font);
			}
		}
	}

	public static String htmlText(String text) {
		return "<html>" + text + "</html>";
	}
}
