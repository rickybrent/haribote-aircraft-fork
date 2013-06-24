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
import mod.ymt.air.ClientKeyBinder;
import mod.ymt.cmn.Utils;


/**
 * @author Yamato
 *
 */
public class mod_HariboteAirCraft extends BaseMod {
	public ClientKeyBinder keybinder;

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
	@MLProp(name="blockAppend", info="Comma-separated list of additional block IDs to treat as movable.")
	public static String blockAppend = "";
	@MLProp(name = "blockIgnore", info = "Comma-separated list of block IDs to ignore (that will never be part of the craft)")
	public static String blockIgnore = "2, 3, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 18, 21, 30, 31, 32, 37, 38, 39, 40, 56, 73, 78, 79, 81, 82, 83, 87, 99, 100, 106, 110, 111, 127, 129, 88, 97, 121 "; 
// Added nether, ice, leaves etc
	//public static String blockIgnore = "2, 3, 8, 9, 10, 11, 12, 13, 31, 32, 37, 38, 78, 87, 121"; // 芝生、土、水、溶岩、砂、砂利、草、枯れ木、花、バラ、雪、ネザーラック、エンドストーン

	@MLProp(name = "blockFly", info = "Comma-separated list of block IDs that enable an aircraft to fly; e.g. 35 for wool.")
	public static String blockFly = "35";
	@MLProp(name = "blockFlyPercent", info = "Minimum percentage of flying blocks needed for liftoff; e.g. 60 to match Movecraft (Less than 0 means everything can fly.)", min=-1, max=101)
	public static float blockFlyPercent = -1;

	@MLProp(name = "blockFloat", info = "Comma-separated list of block IDs that enable an aircraft to float.")
	public static String blockFloat = "5, 17, 20, 53, 72, 125, 126, 134, 135, 136";
	@MLProp(name = "blockFloatPercent", info = "Minimum percentage of flying blocks needed for a ship to float. (Less than 0 means everything will sink; exactly 0 means everything will float.)", min=-1, max=100)
	public static float blockFloatPercent = -1;

	

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
		return "151v3 voyager";
	}

	@Override
	public void keyboardEvent(KeyBinding key) {
		if (keybinder.isClient) {
			for (int i = 0; i < keybinder.keys.length; i++) {
				if (key == keybinder.keys[i]) {
					AirCraftCore.getInstance().net.sendKeyToServer((byte) i);
					break;
				}
			}
		}
	}
	
	// For multiplayer:
	public void mod_HariboteAirCraft() {
		load();
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

			core.flyBlockId.addAll(parseIdList(blockFly));
			core.floatBlockId.addAll(parseIdList(blockFloat));
			core.setFloatBlockPercent(blockFloatPercent);
			core.setFlyBlockPercent(blockFlyPercent);

			core.run();
			
			keybinder = new ClientKeyBinder(); 
			keybinder.init();
			if (keybinder.isClient) {
				for (KeyBinding kb: keybinder.keys) {
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
		}
		catch (NoClassDefFoundError ex) {
			ex.printStackTrace();
		}
	

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
