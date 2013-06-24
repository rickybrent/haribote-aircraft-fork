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
package mod.ymt.air.op;

import java.util.Random;
import java.util.Set;
import mod.ymt.air.BlockData;
import mod.ymt.air.Materializer;
import mod.ymt.cmn.Coord3D;
import mod.ymt.cmn.Utils;
import net.minecraft.src.Block;
import net.minecraft.src.EntityItem;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

/**
 * @author Yamato
 *
 */
public class InventoryBlockOperator extends AbstractRotationOperator {
	public InventoryBlockOperator() {
		super(7, 2, 5, 3, 4);
	}
	
	@Override
	protected void addMoveableBlockIds(Set<Integer> result) {
		result.add(Block.stoneOvenIdle.blockID);
		result.add(Block.stoneOvenActive.blockID);
		result.add(Block.brewingStand.blockID);
		result.add(Block.dispenser.blockID);
	}
	
	protected void dropItemFromNBT(World world, NBTTagCompound tag, double x, double y, double z) {
		if (Utils.isClientSide(world)) {
			return;
		}
		ItemStack item = ItemStack.loadItemStackFromNBT(tag);
		if (item != null) {
			Random rand = world.rand;
			while (0 < item.stackSize) {
				int stackSize = rand.nextInt(21) + 10;
				if (stackSize > item.stackSize) {
					stackSize = item.stackSize;
				}
				item.stackSize -= stackSize;
				x += rand.nextFloat() * 0.8 + 0.1;
				y += rand.nextFloat() * 0.8 + 0.1;
				z += rand.nextFloat() * 0.8 + 0.1;
				EntityItem ent = new EntityItem(world, x, y, z, new ItemStack(item.itemID, stackSize, item.getItemDamage()));
				float motionRate = 0.05F;
				ent.motionX = ((float) rand.nextGaussian() * motionRate);
				ent.motionY = ((float) rand.nextGaussian() * motionRate + 0.2F);
				ent.motionZ = ((float) rand.nextGaussian() * motionRate);
				if (item.hasTagCompound()) {
					ent.getEntityItem().setTagCompound((NBTTagCompound) item.getTagCompound().copy());
				}
				world.spawnEntityInWorld(ent);
			}
		}
	}
	
	@Override
	protected void onCancelSetRealBlock(Materializer owner, BlockData data, Coord3D target) {
		super.onCancelSetRealBlock(owner, data, target);
		// 臓物をぶち撒けろ
		NBTTagCompound tag = owner.space.getTileEntityData(data.absPos);
		if (tag != null) {
			NBTTagList list = tag.getTagList("Items");
			Random rand = owner.world.rand;
			for (int i = 0; i < list.tagCount(); i++) {
				dropItemFromNBT(owner.world, (NBTTagCompound) list.tagAt(i).copy(), target.x + 0.5, target.y + 0.5, target.z + 0.5);
			}
		}
	}
	
	@Override
	protected NBTTagCompound readFromTileEntity(Materializer owner, int blockId, int metadata, Coord3D pos) {
		NBTTagCompound tag = super.readFromTileEntity(owner, blockId, metadata, pos);
		// 読み取ったら Inventory 初期化してブロック破壊に備える
		TileEntity tile = owner.world.getBlockTileEntity(pos.x, pos.y, pos.z);
		clearInventory(tile);
		return tag;
	}
	
	@Override
	protected boolean setRealBlock(Materializer owner, int blockId, int metadata, int x, int y, int z) {
		boolean result = super.setRealBlock(owner, blockId, metadata, x, y, z);
		if (result && Utils.getBlock(blockId) != null) {
			owner.world.setBlockMetadata(x, y, z, metadata); // 無視されるので重ねて metadata 設定
		}
		return result;
	}
	
	public static void clearInventory(IInventory inventory) {
		if (inventory != null) {
			for (int i = inventory.getSizeInventory() - 1; 0 <= i; i--) {
				inventory.setInventorySlotContents(i, null);
			}
		}
	}
	
	public static void clearInventory(TileEntity tile) {
		if (tile instanceof IInventory) {
			clearInventory((IInventory) tile);
		}
	}
}
