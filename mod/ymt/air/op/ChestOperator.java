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

import java.util.Set;
import mod.ymt.air.BlockData;
import mod.ymt.air.ImitationSpace;
import mod.ymt.air.Materializer;
import mod.ymt.cmn.Coord3D;
import mod.ymt.cmn.Utils;
import net.minecraft.src.Block;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.ModelChest;
import net.minecraft.src.ModelLargeChest;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.RenderManager;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntityChest;
import net.minecraft.src.World;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

/**
 * @author Yamato
 *
 */
public class ChestOperator extends InventoryBlockOperator {
	private final ModelChest modelChest = new ModelChest();
	private final ModelChest modelLargeChest = new ModelLargeChest();

	@Override
	public boolean hasSpecialRender() {
		return true;
	}

	@Override
	public void renderBlock(RenderBlocks render, BlockData data) {
		;
	}

	@Override
	public void renderBlockSpecial(RenderManager manager, RenderBlocks render, BlockData data) {
		Block block = data.block;
		int metadata = data.metadata;

		boolean adjacentChestZNeg = false;
		boolean adjacentChestZPos = false;
		boolean adjacentChestXNeg = false;
		boolean adjacentChestXPos = false;
		NBTTagCompound tag = getNBT(render.blockAccess, data.absPos);
		if (tag != null) {
			adjacentChestZNeg = tag.getBoolean("adjacentChestZNeg");
			adjacentChestZPos = tag.getBoolean("adjacentChestZPos");
			adjacentChestXNeg = tag.getBoolean("adjacentChestXNeg");
			adjacentChestXPos = tag.getBoolean("adjacentChestXPos");
		}
		if (adjacentChestZNeg || adjacentChestXNeg) {
			return;
		}

		ModelChest model;

		if (!adjacentChestXPos && !adjacentChestZPos) {
			model = modelChest;
			loadTexture(manager, "/item/chest.png");
		}
		else {
			model = modelLargeChest;
			loadTexture(manager, "/item/largechest.png");
		}

		GL11.glPushMatrix();
		{
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glTranslatef(-0.5F, 1.0F, 0.5F);
			GL11.glScalef(1.0F, -1.0F, -1.0F);
			GL11.glTranslatef(0.5F, 0.5F, 0.5F);
			short angle = 0;
			if (metadata == 2) {
				angle = 180;
			}
			if (metadata == 3) {
				angle = 0;
			}
			if (metadata == 4) {
				angle = 90;
			}
			if (metadata == 5) {
				angle = -90;
			}

			if (metadata == 2 && adjacentChestXPos) {
				GL11.glTranslatef(1.0F, 0.0F, 0.0F);
			}
			if (metadata == 5 && adjacentChestZPos) {
				GL11.glTranslatef(0.0F, 0.0F, -1.0F);
			}
			GL11.glRotatef(angle, 0.0F, 1.0F, 0.0F);
			GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

			model.chestLid.rotateAngleX = 0;
			model.renderAll();
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		}
		GL11.glPopMatrix();
	}

	@Override
	protected void addMoveableBlockIds(Set<Integer> result) {
		result.add(Block.chest.blockID);
	}

	@Override
	protected boolean canPlaceBlockAt(World world, int x, int y, int z, int blockId, int metadata) {
		Block block = Utils.getBlock(blockId);
		return block == null || block.canPlaceBlockAt(world, x, y, z); //
	}

	@Override
	protected NBTTagCompound readFromTileEntity(Materializer owner, int blockId, int metadata, Coord3D pos) {
		NBTTagCompound tag = super.readFromTileEntity(owner, blockId, metadata, pos);
		{
			TileEntity tile = owner.world.getBlockTileEntity(pos.x, pos.y, pos.z);
			if (tile instanceof TileEntityChest) {
				TileEntityChest chest = (TileEntityChest) tile;
				tag.setBoolean("adjacentChestZNeg", chest.adjacentChestZNeg != null);
				tag.setBoolean("adjacentChestZPos", chest.adjacentChestZPosition != null);
				tag.setBoolean("adjacentChestXNeg", chest.adjacentChestXNeg != null);
				tag.setBoolean("adjacentChestXPos", chest.adjacentChestXPos != null);
			}
		}
		return tag;
	}

	private static NBTTagCompound getNBT(IBlockAccess blockAccess, Coord3D pos) {
		if (blockAccess instanceof ImitationSpace) {
			ImitationSpace space = (ImitationSpace) blockAccess;
			return space.getTileEntityData(pos);
		}
		return null;
	}
}
