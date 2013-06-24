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

import java.util.Comparator;
import mod.ymt.cmn.Utils;
import net.minecraft.src.Block;

/**
 * @author Yamato
 *
 */
class BlockDataRenderingComparator implements Comparator<BlockData> {
	private final int[] renderBlockPass = new int[Block.blocksList.length];
	{
		for (int i = 0; i < Block.blocksList.length; i++) {
			if (Block.blocksList[i] != null) {
				renderBlockPass[i] = Block.blocksList[i].getRenderBlockPass();
			}
		}
	}
	
	@Override
	public int compare(BlockData o1, BlockData o2) {
		int result = 0;
		if (result == 0)
			result = Utils.compare(renderBlockPass[o1.block.blockID], renderBlockPass[o2.block.blockID]);
		if (result == 0)
			result = Utils.compare(o1.relPos.y, o2.relPos.y);
		if (result == 0)
			result = Utils.compare(o1.relPos.x, o2.relPos.x);
		if (result == 0)
			result = Utils.compare(o1.relPos.z, o2.relPos.z);
		return result;
	}
}
