package platform;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Library {
	
	
	public static void LibraryPathConfiguration(Path libraryPath) {

		// Get data depending on OS
		String separator = null;
		String osFolder = null;
		OsDetector.OsType osType = OsDetector.getOperatingSystem();
		if (osType == OsDetector.OsType.WINDOWS) {
			separator = ";";
			osFolder = "windows";
		} else if (osType == OsDetector.OsType.LINUX) {
			separator = ":";
			osFolder = "linux";
		} else {
			System.err.println("Unsupported OS!");
			System.exit(-1);
		}
		Path targetLibraryPath = Paths.get(System.getProperty("user.dir"),libraryPath.toString(), osFolder);
		
		// Search the library folder on java.library.path
		boolean found = false;
		for(String javaLibraryPathDirectory : System.getProperty("java.library.path").split(separator)) {
			if (targetLibraryPath.compareTo(Paths.get(javaLibraryPathDirectory)) == 0) {
				found = true;
				break;
			}
		}
		
		// Set user path
		if (!found) {
			try {
				addLibraryDirectory(targetLibraryPath.toString());
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Unable to set environment.");				
				System.exit(-1);
			}
		}
	}
	
	
	private static void addLibraryDirectory(String s) throws Exception {
	    try {
	        Field field = ClassLoader.class.getDeclaredField("usr_paths");
	        field.setAccessible(true);
	        // check if already present
	        String[] paths = (String[])field.get(null);
	        for (int i = 0; i < paths.length; i++) {
	            if (s.equals(paths[i])) {
	                return;
	            }
	        }
	        // add it
	        String[] tmp = new String[paths.length+1];
	        System.arraycopy(paths,0,tmp,0,paths.length);
	        tmp[paths.length] = s;
	        field.set(null,tmp);
	        System.setProperty("java.library.path", System.getProperty("java.library.path") + File.pathSeparator + s);
	    } catch (IllegalAccessException e) {
	        throw new IOException("Failed to get permissions to set library path");
	    } catch (NoSuchFieldException e) {
	        throw new IOException("Failed to get field handle to set library path");
	    }

	}	
	
}
