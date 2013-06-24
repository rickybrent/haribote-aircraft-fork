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
import net.minecraft.src.Block;

/**
 * @author Yamato
 *
 */
public class StairsOperator extends AbstractRotationOperator {
	public StairsOperator() {
		for (int metadata = 0; metadata < rotation.length; metadata++) {
			// 2 → 1 → 3 → 0
			int d = metadata & 3;
			switch (d) {
				case 2:
					d = 1;
					break;
				case 1:
					d = 3;
					break;
				case 3:
					d = 0;
					break;
				case 0:
					d = 2;
					break;
			}
			rotation[metadata] = (metadata & ~3) | (d & 3);
		}
	}

	@Override
	protected void addMoveableBlockIds(Set<Integer> result) {
		result.add(Block.stairsWoodOak.blockID);
		result.add(Block.stairsCobblestone.blockID);
		result.add(Block.stairsBrick.blockID);
		result.add(Block.stairsStoneBrick.blockID);
		result.add(Block.stairsNetherBrick.blockID);
		result.add(Block.stairsSandStone.blockID);
		result.add(Block.stairsWoodSpruce.blockID);
		result.add(Block.stairsWoodBirch.blockID);
		result.add(Block.stairsWoodJungle.blockID);
		result.add(Block.stairCompactNetherQuartz.blockID); // ネザー水晶の階段
	}
}
