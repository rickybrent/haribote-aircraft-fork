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
package mod.ymt.air;

import java.nio.ByteBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.BaseMod;
import net.minecraft.src.ModLoader;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet250CustomPayload;

/**
 * @author Yamato
 *
 */
public class AirCraftNetHandler {
	/**
	 * 操作チャンネル(クライアント→サーバ、サーバ→クライアント)
	 */
	public static final String HAAC_CTRL = "HAAC|CTRL";
	/**
	 * 表面データ要求チャンネル(クライアント→サーバ)
	 */
	public static final String HAAC_REQSUR = "HAAC|REQSUR";
	/**
	 * 表面データ返却チャンネル(サーバ→クライアント)
	 */
	public static final String HAAC_SUR = "HAAC|SUR";
	/**
	 * 準表面データ返却チャンネル(サーバ→クライアント)
	 */
	public static final String HAAC_SEMISUR = "HAAC|SEMISUR";
	/**
	 * タイルエンティティデータ返却チャンネル(サーバ→クライアント)
	 */
	public static final String HAAC_TILE = "HAAC|TILE";
	
	protected final AirCraftCore core;
	
	public AirCraftNetHandler(AirCraftCore core) {
		this.core = core;
	}
	
	public void processClientCustomPayload(Packet250CustomPayload packet) {
		if (HAAC_CTRL.equals(packet.channel)) {
			core.processMoveClient(packet.data[0], readPlayerNameFromControl(packet));
			return;
		}
		if (HAAC_SUR.equals(packet.channel)) {
			core.processAppendSurface(readEntityIdFromSurface(packet), readDataFromSurface(packet));
			return;
		}
		if (HAAC_SEMISUR.equals(packet.channel)) {
			core.processAppendSemiSurface(readEntityIdFromSurface(packet), readDataFromSurface(packet));
			return;
		}
		if (HAAC_TILE.equals(packet.channel)) {
			core.processAppendTileEntityData(readEntityIdFromSurface(packet), readDataFromSurface(packet));
			return;
		}
		core.debugPrint("client receive unknown packet [ %s ]", packet.channel);
	}
	
	public void processServerCustomPayload(Packet250CustomPayload packet) {
		if (HAAC_CTRL.equals(packet.channel)) {
			sendToClients(packet); // サーバに届いたパケットを全クライアントへ転送
			core.processMoveServer(packet.data[0], readPlayerNameFromControl(packet));
			return;
		}
		if (HAAC_REQSUR.equals(packet.channel)) {
			core.processRequestSurfaces(readEntityIdFromSurface(packet));
			return;
		}
		core.debugPrint("server receive unknown packet [ %s ]", packet.channel);
	}
	
	public void registerPacketChannel(BaseMod baseMod) {
		ModLoader.registerPacketChannel(baseMod, HAAC_CTRL);
		ModLoader.registerPacketChannel(baseMod, HAAC_REQSUR);
		ModLoader.registerPacketChannel(baseMod, HAAC_SUR);
		ModLoader.registerPacketChannel(baseMod, HAAC_SEMISUR);
		ModLoader.registerPacketChannel(baseMod, HAAC_TILE);
	}
	
	public void sendKeyToServer(byte key) {
		sendToServer(newControl(key, clientUserName()));
	}
	
	public void sendMoveStopToServerAndClients(String playerName) {
		core.debugPrint("sendMoveStopToClients %s", playerName);
		processServerCustomPayload(newControl(AirCraftMoveHandler.PROC_STOP, playerName));
	}
	
	public void sendRequestSurfacesToServer(int entityId) {
		core.debugPrint("sendRequestSurfacesToServer: sender = %s", entityId);
		sendToServer(newRequestSurface(entityId));
	}
	
	public void sendSemiSurfaceToClient(int entityId, byte[] data) {
		core.debugPrint("sendSemiSurfaceToClient: sender = %s", entityId);
		sendToClients(newAddSurface(HAAC_SEMISUR, entityId, data));
	}
	
	public void sendSurfaceToClient(int entityId, byte[] data) {
		core.debugPrint("sendSurfaceToClient: sender = %s", entityId);
		sendToClients(newAddSurface(HAAC_SUR, entityId, data));
	}
	
	public void sendTileDataToClient(int entityId, byte[] data) {
		core.debugPrint("sendTileDataToClient: sender = %s", entityId);
		sendToClients(newAddSurface(HAAC_TILE, entityId, data));
	}
	
	protected int getInt(byte[] data, int index) {
		return ByteBuffer.wrap(data).getInt(index);
	}
	
	protected Packet250CustomPayload newAddSurface(String channel, int entityId, byte[] surface) {
		byte[] data = new byte[surface.length + 4];
		putInt(data, 0, entityId);
		System.arraycopy(surface, 0, data, 4, surface.length);
		return new Packet250CustomPayload(channel, data);
	}
	
	protected Packet250CustomPayload newControl(byte type, String name) {
		byte[] nameData = name.getBytes();
		byte[] data = new byte[nameData.length + 1];
		data[0] = type;
		System.arraycopy(nameData, 0, data, 1, nameData.length);
		return new Packet250CustomPayload(HAAC_CTRL, data);
	}
	
	protected Packet250CustomPayload newRequestSurface(int entityId) {
		return new Packet250CustomPayload(HAAC_REQSUR, ByteBuffer.allocate(4).putInt(entityId).array());
	}
	
	protected byte[] putInt(byte[] data, int index, int value) {
		ByteBuffer.wrap(data).putInt(index, value);
		return data;
	}
	
	protected byte[] readDataFromSurface(Packet250CustomPayload packet) {
		byte[] data = new byte[packet.data.length - 4];
		System.arraycopy(packet.data, 4, data, 0, data.length);
		return data;
	}
	
	protected int readEntityIdFromSurface(Packet250CustomPayload packet) {
		return getInt(packet.data, 0);
	}
	
	protected String readPlayerNameFromControl(Packet250CustomPayload packet) {
		byte[] data = packet.data;
		return data == null || data.length <= 1 ? "" : new String(data, 1, data.length - 1);
	}
	
	private static String clientUserName() {
		Minecraft mc = ModLoader.getMinecraftInstance();
		if (mc != null && mc.session != null && mc.session.username != null)
			return mc.session.username;
		return "";
	}
	
	private static void sendToClients(Packet packet) {
		MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayers(packet);
	}
	
	private static void sendToServer(Packet packet) {
		ModLoader.clientSendPacket(packet);
	}
}
