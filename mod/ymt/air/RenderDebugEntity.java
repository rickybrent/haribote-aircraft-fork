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

import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.Render;
import net.minecraft.src.RenderBlocks;
import org.lwjgl.opengl.GL11;

/**
 * @author Yamato
 *
 */
public class RenderDebugEntity extends Render {
	private final RenderBlocks blockRenderer = new RenderBlocks();
	private final Block block;
	
	public RenderDebugEntity(Block block) {
		this.block = block;
	}
	
	@Override
	public void doRender(Entity ent, double x, double y, double z, float yaw, float partialTickTime) {
		GL11.glPushMatrix();
		this.loadTexture("/terrain.png");
		GL11.glTranslatef((float) x, (float) y + 0.5f, (float) z); // なんで + 0.5f？
		GL11.glRotatef(-yaw, 0, 1, 0);
		this.blockRenderer.renderBlockAsItem(block, 0, ent.getBrightness(partialTickTime));
		GL11.glPopMatrix();
	}
}
