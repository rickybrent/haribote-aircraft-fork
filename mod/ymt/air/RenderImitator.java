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

import java.util.Collection;
import mod.ymt.cmn.Coord3D;
import net.minecraft.src.Entity;
import net.minecraft.src.GLAllocation;
import net.minecraft.src.Render;
import org.lwjgl.opengl.GL11;

/**
 * @author Yamato
 *
 */
public class RenderImitator extends Render {
	public RenderImitator() {
		;
	}

	@Override
	public void doRender(Entity ent, double x, double y, double z, float yaw, float partialTickTime) {
		this.renderImitator((EntityImitator) ent, x, y, z, reviceYaw(ent, partialTickTime), partialTickTime);
	}

	public void renderImitator(EntityImitator imitator, double x, double y, double z, float yaw, float partialTickTime) {
		if (imitator.glDisposed) { // 消滅している場合は何もしない
			return;
		}
		this.renderBlocks.blockAccess = imitator.getImitationSpace();
		GL11.glPushMatrix();
		{
			// 通常ブロック描画
			if (imitator.glUpdateList || imitator.glCallList <= 0) {
				updateBlockList(imitator);
			}
			if (0 < imitator.glCallList) {
				GL11.glTranslatef((float) x, (float) y, (float) z);
				GL11.glRotatef(-yaw, 0, 1, 0);
				// コールリストの描画
				this.loadTexture("/terrain.png");
				GL11.glCallList(imitator.glCallList);
			}
		}
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		{
			// カスタムレンダリングブロック描画
			GL11.glTranslatef((float) x, (float) y, (float) z);
			GL11.glRotatef(-yaw, 0, 1, 0);
			renderCustomBlocks(imitator);
		}
		GL11.glPopMatrix();
	}

	protected void deleteGLCallList(EntityImitator imitator) {
		if (imitator.glDisposed) {
			return;
		}
		if (0 < imitator.glCallList) {
			GLAllocation.deleteDisplayLists(imitator.glCallList);
		}
		imitator.glCallList = -1;
		imitator.glUpdateList = false;
	}

	protected void prepareBlockList(Collection<BlockData> blocks) {
		GL11.glPushMatrix();
		{
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			AirCraftCore core = AirCraftCore.getInstance();
			for (BlockData data: blocks) {
				Coord3D relPos = data.relPos;
				GL11.glPushMatrix();
				{
					GL11.glTranslatef(relPos.x, relPos.y, relPos.z);
					Operator op = core.getBlockOperator(data.block.blockID);
					op.renderBlock(renderBlocks, data); // レンダリング
				}
				GL11.glPopMatrix();
			}
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_CULL_FACE);
		}
		GL11.glPopMatrix();
	}

	protected void renderCustomBlocks(EntityImitator imitator) {
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		AirCraftCore core = AirCraftCore.getInstance();
		for (BlockData data: imitator.space.getCustomRenderBlocks()) {
			GL11.glPushMatrix();
			{
				Coord3D relPos = data.relPos;
				GL11.glTranslatef(relPos.x, relPos.y, relPos.z);
				Operator op = core.getBlockOperator(data.block.blockID);
				op.renderBlockSpecial(renderManager, renderBlocks, data); // レンダリング
			}
			GL11.glPopMatrix();
		}
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_CULL_FACE);
	}

	protected void updateBlockList(EntityImitator imitator) {
		deleteGLCallList(imitator); // 最初にお掃除
		imitator.glCallList = GLAllocation.generateDisplayLists(1);
		if (0 < imitator.glCallList) {
			imitator.glUpdateList = false;
			GL11.glNewList(imitator.glCallList, GL11.GL_COMPILE);
			prepareBlockList(imitator.getSurfaces()); // レンダリング
			GL11.glEndList();
		}
	}

	private static float reviceYaw(Entity ent, float partialTickTime) {
		float diff = ent.rotationYaw - ent.prevRotationYaw;
		if (Math.abs(diff + 360) < Math.abs(diff)) {
			diff += 360;
		}
		else if (Math.abs(diff - 360) < Math.abs(diff)) {
			diff -= 360;
		}
		return ent.prevRotationYaw + diff * partialTickTime;
	}
}
