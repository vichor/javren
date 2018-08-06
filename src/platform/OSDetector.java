package platform;

import java.util.Locale;

public class OSDetector {

	public enum OSFamily {
		WINDOWS, LINUX, MAC, SOLARIS, NotDetected
	};

	private static OSFamily detectedOS = OSFamily.NotDetected;
	   

	public static final OSFamily getOperatingSystem() {

        String operSys = System.getProperty("os.name").toLowerCase();
        if (operSys.contains("win")) {
            detectedOS = OSFamily.WINDOWS;
        } else if (operSys.contains("nix") || operSys.contains("nux") || operSys.contains("aix")) {
            detectedOS = OSFamily.LINUX;
        } else if (operSys.contains("mac") || operSys.contains("arwin")) {
            detectedOS = OSFamily.MAC;
        } else if (operSys.contains("sunos")) {
            detectedOS = OSFamily.SOLARIS;
        } else {
        	detectedOS = OSFamily.NotDetected;
        }
        return detectedOS;
	}

}