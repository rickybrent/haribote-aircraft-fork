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
 */
package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import mod.ymt.air.AirCraftCore;
import org.lwjgl.input.Keyboard;

/**
 * @author Yamato
 *
 */
public class mod_HariboteAirCraft extends BaseMod {
	public final KeyBinding[] keys = { // MoveManager の並びと合わせる
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

	@MLProp(min = 0)
	public static int IdPyxis = 209;
	@MLProp(min = 0)
	public static int blockLimit = 2000;
	@MLProp
	public static int craftBodySize = -1;
	@MLProp(min = 1)
	public static int moveKeepTime = 60; // キープタイムデフォルト 60 秒
	@MLProp
	public static String blockTarget = "";
	@MLProp
	public static String blockAppend = "";
	@MLProp
	public static String blockIgnore = "2, 3, 8, 9, 10, 11, 12, 13, 31, 32, 37, 38, 78, 87, 121"; // 芝生、土、水、溶岩、砂、砂利、草、枯れ木、花、バラ、雪、ネザーラック、エンドストーン

	@Override
	public void addRenderer(Map map) {
		AirCraftCore.getInstance().addRenderer(map);
	}

	@Override
	public void clientCustomPayload(NetClientHandler handler, Packet250CustomPayload packet) {
		AirCraftCore.getInstance().net.processClientCustomPayload(packet);
	}

	@Override
	public String getPriorities() {
		return "required-after:mod_YMTLib";
	}

	@Override
	public Packet23VehicleSpawn getSpawnPacket(Entity var1, int var2) {
		return new Packet23VehicleSpawn(var1, var2);
	}

	@Override
	public String getVersion() {
		return "152v2 ulysses";
	}

	@Override
	public void keyboardEvent(KeyBinding key) {
		for (int i = 0; i < keys.length; i++) {
			if (key == keys[i]) {
				AirCraftCore.getInstance().net.sendKeyToServer((byte) i);
				break;
			}
		}
	}

	@Override
	public void load() {
		try {
			AirCraftCore core = AirCraftCore.getInstance();
			core.setBaseMod(this);
			core.setBlockIdPyxis(IdPyxis);
			core.setBlocklimit(blockLimit);
			core.setCraftBodySize(craftBodySize);
			core.setMoveKeepTime(moveKeepTime * 20); // 20FPS
			core.targetBlockId.addAll(parseIdList(blockTarget));
			core.appendixBlockId.addAll(parseIdList(blockAppend));
			core.ignoredBlockId.addAll(parseIdList(blockIgnore));
			core.run();
		}
		catch (NoClassDefFoundError ex) {
			ex.printStackTrace();
		}

		for (KeyBinding kb: keys) {
			ModLoader.registerKey(this, kb, false);
		}
		ModLoader.addLocalization("key.HAC_Forward", "ja_JP", "はりぼて前進");
		ModLoader.addLocalization("key.HAC_Backward", "ja_JP", "はりぼて後退");
		ModLoader.addLocalization("key.HAC_TurnRight", "ja_JP", "はりぼて右旋回");
		ModLoader.addLocalization("key.HAC_TurnLeft", "ja_JP", "はりぼて左旋回");
		ModLoader.addLocalization("key.HAC_Up", "ja_JP", "はりぼて上昇");
		ModLoader.addLocalization("key.HAC_Down", "ja_JP", "はりぼて下降");
		ModLoader.addLocalization("key.HAC_Right", "ja_JP", "はりぼて右スライド");
		ModLoader.addLocalization("key.HAC_Left", "ja_JP", "はりぼて左スライド");
		ModLoader.addLocalization("key.HAC_Stop", "ja_JP", "はりぼて停止");
		ModLoader.addLocalization("key.HAC_Terminate", "ja_JP", "はりぼて終了");
	}

	@Override
	public void modsLoaded() {
		// デバッグ表示
		AirCraftCore core = AirCraftCore.getInstance();
		core.debugPrint("defaultMoveableSet: %s", core.getDefaultMoveableSet());
		core.debugPrint("targetBlockId: %s", core.targetBlockId);
		core.debugPrint("appendixBlockId: %s", core.appendixBlockId);
		core.debugPrint("ignoredBlockId: %s", core.ignoredBlockId);
		core.debugPrint("MoveableBlockIds: %s", core.getMoveableBlockIds());
	}

	@Override
	public void serverCustomPayload(NetServerHandler handler, Packet250CustomPayload packet) {
		AirCraftCore.getInstance().net.processServerCustomPayload(packet);
	}

	@Override
	public Entity spawnEntity(int entId, World world, double x, double y, double z) {
		return AirCraftCore.getInstance().spawnEntity(entId, world, x, y, z);
	}

	private static List<Integer> parseIdList(String text) {
		List<Integer> result = new ArrayList<Integer>();
		if (text != null) {
			for (String value: text.split(",")) {
				value = value.trim();
				if (0 < value.length()) {
					try {
						result.add(Integer.parseInt(value));
					}
					catch (NumberFormatException ex) {
						AirCraftCore.getInstance().debugPrint("IllegalNumberFormat[%s]", value);
					}
				}
			}
		}
		return result;
	}
}
