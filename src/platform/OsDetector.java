package platform;

public class OsDetector {

	public enum OsType {
		WINDOWS, LINUX, MAC, SOLARIS, NotDetected
	};

	private static OsType detectedOS = OsType.NotDetected;
	   

	public static final OsType getOperatingSystem() {

        String operatingSystem = System.getProperty("os.name").toLowerCase();
        if (operatingSystem.contains("win")) {
            detectedOS = OsType.WINDOWS;
        } else if (operatingSystem.contains("nix") || operatingSystem.contains("nux") || operatingSystem.contains("aix")) {
            detectedOS = OsType.LINUX;
        } else if (operatingSystem.contains("mac") || operatingSystem.contains("arwin")) {
            detectedOS = OsType.MAC;
        } else if (operatingSystem.contains("sunos")) {
            detectedOS = OsType.SOLARIS;
        } else {
        	detectedOS = OsType.NotDetected;
        }
        return detectedOS;
	}

}