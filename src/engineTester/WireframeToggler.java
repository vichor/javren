package engineTester;

import org.lwjgl.input.Keyboard;

import renderEngine.MasterRenderer;

public class WireframeToggler {
	
	private static boolean wireframeKey = false;
	
	
	public static void checkAndToggle() {
		if (Keyboard.isKeyDown(Keyboard.KEY_M)) {
			if (!wireframeKey) {
				MasterRenderer.toggleWireframeMode();
				wireframeKey = true;
			}
		} else {
			wireframeKey = false;
		}
	}

}
