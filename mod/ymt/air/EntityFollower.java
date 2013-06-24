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

import net.minecraft.src.World;

/**
 * @author Yamato
 *
 */
public abstract class EntityFollower extends EntityAirCraft {
	protected EntityAirCraft owner = null;
	protected double offsetAngle;
	protected double offsetDistance;
	protected double offsetY;
	protected double offsetRotateYaw;
	protected int ownerMissingTime = 0;
	
	protected EntityFollower(World world) {
		super(world);
	}
	
	protected EntityFollower(World world, String ownerName) {
		super(world);
		if (ownerName != null) {
			setOwnerName(ownerName);
		}
	}
	
	public EntityAirCraft getOwner() {
		return owner;
	}
	
	@Override
	public void onEntityUpdate() {
		super.onEntityUpdate();
		if (owner == null) {
			if (600 <= ownerMissingTime++) { // ˆê’èŽžŠÔAe‚ªŒ©‚Â‚©‚ç‚È‚©‚Á‚½‚çÁ–Å‚³‚¹‚é
				core.debugPrint("owner not found %s", this);
				setDead();
				return;
			}
			setOwner(EntityCraftCore.getEntityCore(worldObj, getOwnerName()));
		}
		else if (owner.isDead) {
			setDead();
		}
	}
	
	public void updateFollowerPosition() {
		if (owner != null) {
			double angle = offsetAngle + (offsetRotateYaw - owner.rotationYaw) / 180 * Math.PI;
			double x = owner.posX + offsetDistance * Math.sin(angle);
			double y = owner.posY + offsetY;
			double z = owner.posZ + offsetDistance * Math.cos(angle);
			setNextPosition(x, y, z, (float) (owner.rotationYaw - offsetRotateYaw), 0, 1);
		}
	}
	
	protected void setOwner(EntityCraftCore owner) {
		this.owner = owner;
		if (owner != null) {
			owner.addSubEntity(this);
			double dx = this.posX - owner.posX;
			double dz = this.posZ - owner.posZ;
			offsetAngle = Math.atan2(dx, dz);
			offsetDistance = Math.sqrt(dx * dx + dz * dz);
			offsetY = this.posY - owner.posY;
			offsetRotateYaw = owner.rotationYaw;
		}
	}
}
