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

import net.minecraft.src.Tessellator;
import org.lwjgl.opengl.GL11;

/**
 * @author Yamato
 *
 */
public class RenderPyxis extends RenderImitator {
	@Override
	public void renderImitator(EntityImitator imitator, double x, double y, double z, float yaw, float partialTickTime) {
		GL11.glPushMatrix();
		{
			GL11.glTranslatef((float) x, (float) y, (float) z);
			{
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glDisable(GL11.GL_CULL_FACE);
				GL11.glDisable(GL11.GL_BLEND);
				final Tessellator tessellator = Tessellator.instance;
				tessellator.startDrawingQuads();
				tessellator.setBrightness(15 << 20 | 15 << 4);
				tessellator.setColorRGBA(255, 255, 255, 32);
				this.loadTexture("/mod/ymt/air/py_rotor.png");
				tessellator.addVertexWithUV(+0.0, 0.6, -0.8, 0, 0); // TODO 揺らす
				tessellator.addVertexWithUV(+0.8, 0.6, +0.0, 0, 1);
				tessellator.addVertexWithUV(+0.0, 0.6, +0.8, 1, 1);
				tessellator.addVertexWithUV(-0.8, 0.6, +0.0, 1, 0);
				tessellator.draw();
				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glEnable(GL11.GL_CULL_FACE);
				GL11.glEnable(GL11.GL_BLEND);
			}
		}
		GL11.glPopMatrix();
		super.renderImitator(imitator, x, y, z, yaw, partialTickTime);
	}
}
