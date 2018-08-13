package main;

import java.awt.*;

public class SystemTrayHandler {

    SystemTray systemTray;
    private Image img;
    private TrayIcon trayIcon;

    public SystemTrayHandler() {
        if (!SystemTray.isSupported()) {
            return;
        }
        systemTray = SystemTray.getSystemTray();
        img = Toolkit.getDefaultToolkit().getImage("tklauncher.png");
        trayIcon = new TrayIcon(img, "TkLauncher", null);

        try {
            systemTray.add(trayIcon);
        } catch (AWTException e) {
            System.err.println(e);
            systemTray = null;
        }
    }
}
