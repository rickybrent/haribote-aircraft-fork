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

import mod.ymt.cmn.Utils;
import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;

/**
 * @author Yamato
 *
 */
public class EntityMobMat extends EntityFollower {
	public EntityMobMat(World world) {
		super(world);
		setSize(0.5f, 0.0625f);
		this.noClip = false;
	}

	public EntityMobMat(World world, String ownerName) {
		super(world, ownerName);
		setSize(0.5f, 0.0625f);
		this.noClip = false;
	}

	@Override
	public AxisAlignedBB getBoundingBox() {
		return null;
	}

	@Override
	public boolean interact(EntityPlayer player) {
		if (core.tryInteractServer(worldObj)) {
			if (riddenByEntity != null) { // 乗っている子を下ろす
				setDead();
				return true;
			}
		}
		return false;
	}

	@Override
	public void onEntityUpdate() {
		super.onEntityUpdate();
		if (!isDead && riddenByEntity == null && Utils.isServerSide(worldObj)) { // 何も乗っていなかったら消滅
			core.debugPrint("EntityMobMat no carrier: %s", this);
			setDead();
		}
	}

	@Override
	public void setDead() {
		if (riddenByEntity != null) {
			riddenByEntity.mountEntity(null);
		}
		super.setDead();
	}

	@Override
	protected void capturePassengers() {
		; // なにもしない
	}
}
