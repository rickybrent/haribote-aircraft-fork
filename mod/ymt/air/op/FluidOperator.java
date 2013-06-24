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
import mod.ymt.air.Materializer;
import net.minecraft.src.Block;

/**
 * @author Yamato
 *
 */
public class FluidOperator extends AbstractOperator {
	@Override
	protected void addMoveableBlockIds(Set<Integer> result) {
		result.add(Block.waterMoving.blockID);
		result.add(Block.waterStill.blockID);
		result.add(Block.lavaMoving.blockID);
		result.add(Block.lavaStill.blockID);
	}
	
	@Override
	protected boolean setRealBlockWithRotation(Materializer owner, int blockId, int metadata, int x, int y, int z, int rotate) {
		// Fluid ÇÕÇΩÇ‘ÇÒ metadata ÇèëÇ´ä∑Ç¶Ç»Ç≠ÇƒÇ‡ÅcÅcìÆÇ≠ÅH
		return setRealBlock(owner, blockId, metadata, x, y, z);
	}
}
