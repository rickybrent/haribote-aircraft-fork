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
import java.util.List;
import mod.ymt.cmn.Utils;
import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Chunk;
import net.minecraft.src.DamageSource;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityBoat;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityMinecart;
import net.minecraft.src.IEntitySelector;
import net.minecraft.src.MathHelper;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.World;

/**
 * @author Yamato
 *
 */
public abstract class EntityAirCraft extends Entity {
	protected final AirCraftCore core = AirCraftCore.getInstance();
	/**
	 * DataWatcher -> OwnerName
	 */
	protected final int DWKEY_OWNERNAME = 11;
	protected boolean captured = false;
	
	protected double nextPosX, nextPosY, nextPosZ;
	protected float nextYaw, nextPitch;
	protected int turnProgress = 0;
	
	protected EntityAirCraft(World world) {
		super(world);
		this.preventEntitySpawning = true;
		this.setSize(1, 1);
		this.yOffset = 0;
		this.stepHeight = 0;
		this.isImmuneToFire = true; // 炎は無敵
		this.noClip = true;
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource par1DamageSource, int par2) {
		return false; // 無敵
	}
	
	@Override
	public boolean canBeCollidedWith() {
		return !isDead;
	}
	
	@Override
	public AxisAlignedBB getBoundingBox() {
		return this.boundingBox;
	}
	
	@Override
	public float getBrightness(float par1) {
		return worldObj.provider.lightBrightnessTable[15]; // TODO 15をもっと賢く
	}
	
	public String getOwnerName() {
		return dataWatcher.getWatchableObjectString(DWKEY_OWNERNAME);
	}
	
	public List<Entity> getRiddingEntities() {
		List<Entity> result = new ArrayList<Entity>();
		findRiddingEntities(result);
		return result;
	}
	
	@Override
	public float getShadowSize() {
		return 0.0F;
	}
	
	public boolean hasOwnerName() {
		return Utils.hasString(getOwnerName());
	}
	
	public boolean isEntityHitBlocks() {
		AxisAlignedBB aabb = getBoundingBox();
		return aabb != null && worldObj.getAllCollidingBoundingBoxes(aabb).size() != 0;
	}
	
	@Override
	public void onEntityUpdate() {
		if (this.ridingEntity != null && this.ridingEntity.isDead) {
			this.ridingEntity = null;
		}
		this.prevDistanceWalkedModified = this.distanceWalkedModified;
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.prevRotationPitch = this.rotationPitch;
		this.prevRotationYaw = this.rotationYaw;
		
		if (isDead) {
			return;
		}
		if (this.posY < -64) {
			this.kill();
			return;
		}
		if (captured == false && hasOwnerName()) {
			if (Utils.isServerSide(worldObj)) {
				capturePassengers();
			}
			captured = true;
		}
		if (Utils.isClientSide(worldObj)) {
			if (turnProgress > 0) {
				double x = posX + (nextPosX - posX) / turnProgress;
				double y = posY + (nextPosY - posY) / turnProgress;
				double z = posZ + (nextPosZ - posZ) / turnProgress;
				double d_angle = MathHelper.wrapAngleTo180_double(this.nextYaw - (double) this.rotationYaw);
				float yaw = (float) (rotationYaw + d_angle / turnProgress);
				float pitch = (float) (rotationPitch + (nextPitch - (double) rotationPitch) / turnProgress);
				super.setPosition(x, y, z);
				super.setRotation(yaw, pitch);
				turnProgress--;
			}
		}
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound tag) {
		String name = tag.getString("OwnerName");
		if (name != null) {
			setOwnerName(name);
		}
	}
	
	public void setNextPosition(double x, double y, double z, float yaw, float pitch, int turn) {
		nextPosX = x;
		nextPosY = y;
		nextPosZ = z;
		nextYaw = yaw;
		nextPitch = pitch;
		turnProgress = turn;
		if (turnProgress <= 0) {
			super.setPositionAndRotation(nextPosX, nextPosY, nextPosZ, nextYaw, nextPitch);
			turnProgress = 0;
		}
		else if (Utils.isServerSide(worldObj)) {
			super.setPosition(nextPosX, nextPosY, nextPosZ);
			super.setRotation(nextYaw, nextPitch);
		}
	}
	
	@Override
	public void setPositionAndRotation(double x, double y, double z, float yaw, float pitch) {
		super.setPosition(x, y, z);
		super.setRotation(yaw, pitch);
	}
	
	@Override
	public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int turn) {
		setNextPosition(x, y, z, yaw, pitch, 5);
	}
	
	public void stopImmediately() {
		// このエンティティの停止
		this.isAirBorne = true; // 強制更新
		setNextPosition(posX, posY, posZ, rotationYaw, rotationPitch, 0); // 現在位置を即座に更新
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound tag) {
		String name = getOwnerName();
		if (name != null) {
			tag.setString("OwnerName", name);
		}
	}
	
	protected void capturePassenger(Entity ent) {
		EntityMobMat mat = new EntityMobMat(worldObj, getOwnerName());
		// double y = Math.max(ent.posY, this.posY + this.height); // 位置補正
		double y = ent.posY;
		mat.setPosition(ent.posX, y, ent.posZ);
		worldObj.spawnEntityInWorld(mat);
		ent.mountEntity(mat); // ent を mat に載せる
	}
	
	protected void capturePassengers() {
		for (Entity ent: getRiddingEntities()) {
			if (isUnCapturedPassenger(ent)) {
				capturePassenger(ent);
			}
		}
	}
	
	@Override
	protected void entityInit() {
		dataWatcher.addObject(DWKEY_OWNERNAME, "");
	}
	
	protected void findRiddingEntities(List<Entity> result) {
		// 参考: World#selectEntitiesWithinAABB
		AxisAlignedBB aabb = getBoundingBox();
		if (aabb != null) {
			aabb = aabb.expand(0, 2, 0).offset(0, 2, 0);
			int c_x_min = MathHelper.floor_double((aabb.minX - 2.0D) / 16.0D);
			int c_x_max = MathHelper.floor_double((aabb.maxX + 2.0D) / 16.0D);
			int c_z_min = MathHelper.floor_double((aabb.minZ - 2.0D) / 16.0D);
			int c_z_max = MathHelper.floor_double((aabb.maxZ + 2.0D) / 16.0D);
			for (int x = c_x_min; x <= c_x_max; x++) {
				for (int z = c_z_min; z <= c_z_max; z++) {
					Chunk chunk = worldObj.getChunkFromChunkCoords(x, z);
					chunk.getEntitiesOfTypeWithinAAAB(Entity.class, aabb, result, new IEntitySelector() {
						@Override
						public boolean isEntityApplicable(Entity ent) {
							return ent instanceof EntityAirCraft == false && !ent.isDead && ent.ridingEntity == null;
						}
					});
				}
			}
		}
	}
	
	protected boolean isUnCapturedPassenger(Entity ent) {
		if (ent.ridingEntity != null)
			return false;
		return ent instanceof EntityLiving || ent instanceof EntityItem || ent instanceof EntityMinecart || ent instanceof EntityBoat;
	}
	
	protected void setOwnerName(String name) {
		if (name == null) {
			name = "";
		}
		dataWatcher.updateObject(DWKEY_OWNERNAME, name);
	}
}
