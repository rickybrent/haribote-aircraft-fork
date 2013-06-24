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

import mod.ymt.cmn.Coord3D;
import mod.ymt.cmn.Utils;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.MathHelper;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.World;

/**
 * @author Yamato
 *
 */
public class EntityPyxis extends EntityImitator {
	/**
	 * DataWatcher -> PlayerName
	 */
	protected final int DWKEY_PLAYERNAME = 17;

	private final AirCraftMoveHandler moveHandler;

	public EntityPyxis(String playerName, World world, int blockId, int metadata, Coord3D basePoint) { // Server
		super(world);
		this.moveHandler = core.newMoveHandler(world, this, playerName);
		this.setSize(1, 0.5f);
		setOwnerName(newName());
		setPlayerName(playerName);
		setThisBlock(blockId, metadata, basePoint);
	}

	public EntityPyxis(World world) { // Client
		super(world);
		this.setSize(1, 0.5f);
		this.moveHandler = core.newMoveHandler(world, this, null);
	}

	@Override
	public int getDirectionOffset() {
		return getThisBlockMetadata() & 3;
	}

	public String getPlayerName() {
		return dataWatcher.getWatchableObjectString(DWKEY_PLAYERNAME);
	}

	@Override
	public boolean interact(EntityPlayer par1EntityPlayer) {
		if (core.tryInteractServer(worldObj)) {
			terminate();
		}
		return true;
	}

	@Override
	public void processMove(String sender, byte type) {
		if (isDead) {
			return;
		}
		moveHandler.process(sender, type);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound tag) {
		super.readEntityFromNBT(tag);
		setPlayerName(tag.getString("PlayerName"));
	}

	public void setPlayerName(String name) {
		dataWatcher.updateObject(DWKEY_PLAYERNAME, name);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound tag) {
		super.writeEntityToNBT(tag);
		tag.setString("PlayerName", getPlayerName());
	}

	private String newName() {
		return "EntityPyxis_" + entityId + "_" + System.currentTimeMillis();
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataWatcher.addObject(DWKEY_PLAYERNAME, "");
	}

	@Override
	protected void onEntityHitBlocks(EntityAirCraft ent) {
		super.onEntityHitBlocks(ent);
		moveHandler.processStop();
		if (Utils.isServerSide(worldObj)) {
			// 停止時に adjustPositionAndRotation やって、それがサーバ側だけで onEntityHitBlocks を呼び出すから、サーバ側だけの処理にする
			int x = MathHelper.floor_double(ent.posX);
			int z = MathHelper.floor_double(ent.posZ);
			int y = MathHelper.floor_double(Math.round(ent.posY));
			showMessageToMyPlayer("AirCraft hits ground at " + x + ", " + y + ", " + z);
		}
	}

	@Override
	protected void onEntityPositionUpdate() {
		if (moveHandler.getPlayerName() == null) {
			String pName = getPlayerName();
			if ("".equals(pName)) {
				return; // まだ慌てるような時間じゃない
			}
			moveHandler.setPlayerName(pName);
		}
		moveHandler.onTick();
	}

	protected void showMessageToMyPlayer(String msg) {
		for (Object ent: worldObj.loadedEntityList) {
			if (ent instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) ent;
				if (!player.isDead && getPlayerName().equals(player.getEntityName())) {
					Utils.showMessage(player, msg);
				}
			}
		}
	}
}
