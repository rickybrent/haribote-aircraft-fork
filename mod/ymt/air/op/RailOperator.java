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
import mod.ymt.air.ScanTime;
import mod.ymt.cmn.Utils;
import net.minecraft.src.Block;

/**
 * @author Yamato
 *
 */
public class RailOperator extends AbstractRotationOperator {
	public RailOperator() {
		// 0 - ìÏÇ©ÇÁñk
		// 1 - êºÇ©ÇÁìå
		// 2 - êºÇ©ÇÁìå(è„)
		// 3 - êºÇ©ÇÁìå(â∫)
		// 4 - ìÏÇ©ÇÁñk(è„)
		// 5 - ìÏÇ©ÇÁñk(â∫)
		// 6 - ìåÇ©ÇÁìÏ
		// 7 - ìÏÇ©ÇÁêº
		// 8 - êºÇ©ÇÁñk
		// 9 - ñkÇ©ÇÁìå
		// 0 Å® 1
		// 2 Å® 5 Å® 3 Å® 4
		// 6 Å® 7 Å® 8 Å® 9
		rotation[0] = 1;
		rotation[1] = 0;
		rotation[2] = 5;
		rotation[3] = 4;
		rotation[4] = 2;
		rotation[5] = 3;
		rotation[6] = 7;
		rotation[7] = 8;
		rotation[8] = 9;
		rotation[9] = 6;
	}
	
	@Override
	protected void addMoveableBlockIds(Set<Integer> result) {
		result.add(Block.rail.blockID);
	}
	
	@Override
	protected ScanTime getScanTime(int blockId) {
		return ScanTime.RedstoneOutput;
	}
	
	@Override
	protected boolean setRealBlock(Materializer owner, int blockId, int metadata, int x, int y, int z) {
		boolean result = super.setRealBlock(owner, blockId, metadata, x, y, z);
		if (result && Utils.getBlock(blockId) != null) {
			owner.world.setBlockMetadata(x, y, z, metadata); // ñ≥éãÇ≥ÇÍÇÈÇÃÇ≈èdÇÀÇƒ metadata ê›íË
		}
		return result;
	}
}
