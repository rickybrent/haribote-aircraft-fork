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
public class RailPoweredOperator extends AbstractRotationOperator {
	public RailPoweredOperator() {
		// 0 - 南から北
		// 1 - 西から東
		// 2 - 西から東(上)
		// 3 - 西から東(下)
		// 4 - 南から北(上)
		// 5 - 南から北(下)
		// 0 → 1
		// 2 → 5 → 3 → 4
		for (int metadata = 0; metadata < rotation.length; metadata++) {
			int d = metadata & 7;
			switch (d) {
				case 0:
					d = 1;
					break;
				case 1:
					d = 0;
					break;
				case 2:
					d = 5;
					break;
				case 3:
					d = 4;
					break;
				case 4:
					d = 2;
					break;
				case 5:
					d = 3;
					break;
			}
			rotation[metadata] = (metadata & 8) | (d & 7);
		}
	}

	@Override
	protected void addMoveableBlockIds(Set<Integer> result) {
		result.add(Block.railPowered.blockID);
		result.add(Block.railDetector.blockID);
		result.add(Block.railActivator.blockID); // 作動レール
	}

	@Override
	protected ScanTime getScanTime(int blockId) {
		return ScanTime.Delicate;
	}

	@Override
	protected boolean setRealBlock(Materializer owner, int blockId, int metadata, int x, int y, int z) {
		boolean result = super.setRealBlock(owner, blockId, metadata, x, y, z);
		if (result && Utils.getBlock(blockId) != null) {
			owner.world.setBlockMetadataWithNotify(x, y, z, metadata, 2); // 無視されるので重ねて metadata 設定
		}
		return result;
	}
}
