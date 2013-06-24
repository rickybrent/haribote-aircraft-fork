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
import mod.ymt.air.ScanTime;
import net.minecraft.src.Block;

/**
 * @author Yamato
 *
 */
public class DoorOperator extends AbstractRotationOperator {
	public DoorOperator() {
		for (int metadata = 0; metadata < 7; metadata++) { // metadata 8 以降(ドアの上半分)は値を変えない
			int d = metadata & 3;
			d++;
			rotation[metadata] = (metadata & ~3) | (d & 3);
		}
	}
	
	@Override
	protected void addMoveableBlockIds(Set<Integer> result) {
		result.add(Block.doorIron.blockID);
		result.add(Block.doorWood.blockID);
	}
	
	@Override
	protected ScanTime getScanTime(int blockId) {
		return ScanTime.RedstoneOutput;
	}
}
