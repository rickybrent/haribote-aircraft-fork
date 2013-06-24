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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.src.Entity;
import net.minecraft.src.MathHelper;
import net.minecraft.src.World;

/**
 * @author Yamato
 *
 */
public abstract class EntityCraftCore extends EntityAirCraft {
	protected final List<EntityAirCraft> subEntity = new ArrayList<EntityAirCraft>();
	
	public EntityCraftCore(World world) {
		super(world);
	}
	
	public void addSubEntity(EntityAirCraft ent) {
		subEntity.add(ent);
	}
	
	public boolean adjustPositionAndRotation() {
		this.isAirBorne = true; // 強制更新
		double x = Math.floor(posX) + 0.5;
		double z = Math.floor(posZ) + 0.5;
		double y = Math.round(posY);
		int d = getDirection(this);
		return trySetPositionAndRotation(x, y, z, d * 90, 0);
	}
	
	public int getDirectionOffset() {
		return 0;
	}
	
	public void moveCraft(double mx, double my, double mz, float yaw, float pitch) {
		trySetPositionAndRotation(posX + mx, posY + my, posZ + mz, rotationYaw + yaw, rotationPitch + pitch);
	}
	
	public abstract void processMove(String name, byte type);
	
	@Override
	public void setPositionAndRotation(double x, double y, double z, float yaw, float pitch) {
		trySetPositionAndRotation(x, y, z, yaw, pitch);
	}
	
	@Override
	public void stopImmediately() {
		super.stopImmediately();
		// サブエンティティの停止
		for (EntityAirCraft ent: subEntity) {
			if (ent instanceof EntityFollower) {
				ent.stopImmediately();
			}
		}
	}
	
	public abstract void terminate();
	
	private boolean trySetPositionAndRotation(double x, double y, double z, float yaw, float pitch) {
		// 動かす前の値を保存
		double prevPosX = posX;
		double prevPosY = posY;
		double prevPosZ = posZ;
		float prevYaw = rotationYaw;
		float prevPitch = rotationPitch;
		// 動かす
		EntityAirCraft collided = tryUpdatePosition(x, y, z, yaw, pitch);
		if (collided == null) {
			return true; // success
		}
		else {
			onEntityHitBlocks(collided);
			// 戻す
			tryUpdatePosition(prevPosX, prevPosY, prevPosZ, prevYaw, prevPitch);
			return false; // failure
		}
	}
	
	protected void cleanSubEntity() {
		Iterator<EntityAirCraft> iter = subEntity.iterator();
		while (iter.hasNext()) {
			Entity ent = iter.next();
			if (ent == null || ent.isDead) {
				iter.remove();
			}
		}
	}
	
	protected void onEntityHitBlocks(EntityAirCraft ent) {
		core.debugPrint("onEntityHitBlocks %s", ent);
	}
	
	protected abstract void onEntityPositionUpdate();
	
	protected EntityAirCraft tryUpdatePosition(double x, double y, double z, float yaw, float pitch) {
		super.setNextPosition(x, y, z, yaw, pitch, 1); // これ setPositionAndRotation 使っちゃうと、描画のブレが大きくなっちゃうんだね……
		if (isEntityHitBlocks()) {
			return this;
		}
		for (EntityAirCraft ent: subEntity) {
			if (ent instanceof EntityFollower) {
				EntityFollower follower = (EntityFollower) ent;
				follower.updateFollowerPosition();
				if (follower.isEntityHitBlocks()) {
					return follower;
				}
			}
		}
		return null;
	}
	
	public static EntityCraftCore getEntityCore(World world, String name) {
		if (name != null) {
			for (Object obj: world.loadedEntityList) {
				if (obj instanceof EntityCraftCore) {
					EntityCraftCore entity = (EntityCraftCore) obj;
					if (name.equals(entity.getOwnerName())) {
						return entity;
					}
				}
			}
		}
		return null;
	}
	
	protected static int getDirection(Entity ent) {
		return MathHelper.floor_double((ent.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
	}
}
