/**
 * Copyright 2013 Yamato
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Small class to handle the keybinding so it won't throw multiplayer 
 * servers for a loop. There is perhaps a better way to handle this...
 */
package mod.ymt.air;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.lwjgl.input.Keyboard;
import mod.ymt.air.AirCraftCore;
import net.minecraft.src.KeyBinding;

/**
 * @author Yamato
 *
 */
public class ClientKeyBinder  {
	public KeyBinding[] keys;   
	public boolean isClient = false;
	
	public void init() {
		try {
			Class.forName("net.minecraft.client.Minecraft");
			isClient = true;
		} catch(ClassNotFoundException e) {
			isClient = false;
		}	 
		
		if (isClient) {
			KeyBinding[] keys = { // MoveManager の並びと合わせる
				new KeyBinding("key.HAC_Stop", Keyboard.KEY_NUMPAD5),
				new KeyBinding("key.HAC_Forward", Keyboard.KEY_NUMPAD1),
				new KeyBinding("key.HAC_Backward", Keyboard.KEY_NUMPAD3),
				new KeyBinding("key.HAC_TurnRight", Keyboard.KEY_NUMPAD9),
				new KeyBinding("key.HAC_TurnLeft", Keyboard.KEY_NUMPAD7),
				new KeyBinding("key.HAC_Up", Keyboard.KEY_NUMPAD8),
				new KeyBinding("key.HAC_Down", Keyboard.KEY_NUMPAD2),
				new KeyBinding("key.HAC_Right", Keyboard.KEY_NUMPAD6),
				new KeyBinding("key.HAC_Left", Keyboard.KEY_NUMPAD4),
				new KeyBinding("key.HAC_Terminate", Keyboard.KEY_DIVIDE),
			};
			this.keys = keys;
		}
	}
}
