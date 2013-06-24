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
import mod.ymt.cmn.Utils;
import net.minecraft.src.Block;
import net.minecraft.src.ModelChest;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.RenderManager;
import net.minecraft.src.World;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

/**
 * @author Yamato
 *
 */
public class EnderChestOperator extends AbstractRotationOperator {
	private final ModelChest modelEnderChest = new ModelChest();
	
	public EnderChestOperator() {
		super(2, 5, 3, 4);
	}
	
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
		
		final boolean adjacentChestZNeg = false;
		final boolean adjacentChestZPos = false;
		final boolean adjacentChestXNeg = false;
		final boolean adjacentChestXPos = false;
		ModelChest model;
		
		loadTexture(manager, "/item/enderchest.png");
		
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
			
			GL11.glRotatef(angle, 0.0F, 1.0F, 0.0F);
			GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
			
			modelEnderChest.chestLid.rotateAngleX = 0;
			modelEnderChest.renderAll();
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		}
		GL11.glPopMatrix();
	}
	
	@Override
	protected void addMoveableBlockIds(Set<Integer> result) {
		result.add(Block.enderChest.blockID);
	}
	
	@Override
	protected boolean canPlaceBlockAt(World world, int x, int y, int z, int blockId, int metadata) {
		Block block = Utils.getBlock(blockId);
		return block == null || block.canPlaceBlockAt(world, x, y, z);
	}
}
